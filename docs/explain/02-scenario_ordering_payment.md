# 02-场景：下单与支付 (Ordering & Payment)

本章节详细拆解**C端用户下单（启动充电）**与**支付结算**的完整业务流程。我们将此过程类比为**用户在电商平台下单购买即时配送商品**。

## 场景一：下单（启动充电）

### 1. 场景概述
*   **场景目标**：用户通过小程序成功创建订单，并触发设备开始供电。
*   **参与角色**：消费者 (User)、前台应用 (MP)、订单中心 (Operator)、物流设备 (Simulator)。
*   **触发条件**：用户在小程序点击“开始充电”按钮。
*   **前置条件**：用户已登录，账户余额充足，且所选充电桩（库存）状态为空闲。

### 2. 正向流程 (Happy Path)

这是最理想的路径，所有校验通过，设备成功启动。

#### 业务活动图 (Activity Diagram - Forward)

```mermaid
graph TD
    Start[用户点击开始充电] --> CheckLogin{检查登录状态}
    CheckLogin --已登录--> CheckBalance{检查余额充足?}
    CheckLogin --未登录--> End[跳转登录页]

    CheckBalance --余额不足--> EndFail[提示充值]
    CheckBalance --充足--> CreateOrder[创建订单记录: ChargingOrder]

    CreateOrder --> SetState[设置状态: PLACE/PRE_CHARGE]
    SetState --> Dispatch[向设备下发启动指令]

    Dispatch --> DeviceAck{设备响应成功?}
    DeviceAck --是--> UpdateStatus[更新订单状态: CHARGING]
    UpdateStatus --> ShowSuccess[前端提示启动成功]
    ShowSuccess --> EndSuccess[流程结束]

    DeviceAck --否--> Rollback[订单标记失败]
    Rollback --> ShowError[前端提示设备故障]
    ShowError --> EndFail

    style Start fill:#e1f5fe
    style CreateOrder fill:#fff9c4
    style Dispatch fill:#f3e5f5
    style EndSuccess fill:#dcedc8
    style EndFail fill:#ffcdd2
```

### 3. 逆向流程 (Exception Path)

当遇到库存不足（桩被占用）或系统异常时的处理逻辑。

#### 业务活动图 (Activity Diagram - Reverse)

```mermaid
graph TD
    Start[接收下单请求] --> ValidatePort{校验端口状态}

    ValidatePort --端口故障/占用--> Refuse[拒绝下单: 返回错误码]
    Refuse --> LogError[记录错误日志]
    LogError --> NotifyUser[通知用户: 设备不可用]
    NotifyUser --> End[流程终止]

    ValidatePort --正常--> CallDevice[调用设备接口]
    CallDevice --超时/失败--> Retry{重试次数 < 3?}

    Retry --是--> CallDevice
    Retry --否--> CancelOrder[取消订单]
    CancelOrder --> UnlockResource[释放资源]
    UnlockResource --> NotifyUserFail[通知用户: 启动失败]
    NotifyUserFail --> End

    style Refuse fill:#ffcdd2
    style CancelOrder fill:#ffcdd2
    style End fill:#eceff1
```

### 4. 系统交互时序图 (Sequence Diagram)

展示各系统间的数据流转。

```mermaid
sequenceDiagram
    participant User as 消费者
    participant MP as 小程序前台
    participant Operator as 运营后台-OMS
    participant Simulator as 模拟器-物流

    User->>MP: 1. 点击启动充电
    MP->>Operator: 2. 请求创建订单 [OrderController.saveOrder]
    activate Operator

    Operator->>Operator: 3. 校验用户与设备
    Operator->>Operator: 4. 生成订单 [Status: PLACE]

    Operator->>Simulator: 5. 发送启动指令 [startCharge]
    activate Simulator
    Simulator-->>Operator: 6. 返回指令接收确认 [ACK]
    deactivate Simulator

    Operator->>Operator: 7. 更新订单状态 [Status: CHARGING]
    Operator-->>MP: 8. 返回下单成功
    deactivate Operator

    MP-->>User: 9. 展示充电监控页面
```

---

## 场景二：支付结算 (Payment & Settlement)

### 1. 场景概述
*   **场景目标**：根据实际充电量计算费用，并从用户余额中扣除。
*   **触发条件**：充电结束（用户主动停止 或 充满自停）。
*   **数据流转**：设备上报最终读数 -> 后台计算 -> 扣款 -> 更新订单。

### 2. 状态变化 (State Machine)

订单在支付环节的状态流转。

```mermaid
stateDiagram-v2
    [*] --> CHARGING: 充电中
    CHARGING --> SETTLE: 停止充电/开始结算
    SETTLE --> PAYED: 余额扣款成功
    SETTLE --> EXCEPTION: 扣款失败/余额不足
    PAYED --> [*]: 订单完成
```

### 3. 核心数据流

*   **输入数据**：开始时间、结束时间、总电量 (kWh)。
*   **计算公式**：`总费用 = (电费单价 * 电量) + (服务费单价 * 电量)`。
*   **输出数据**：实扣金额、订单最终状态。

### 4. 优化建议

1.  **预冻结机制**：当前流程是结束后扣款，存在坏账风险。建议在下单时冻结预估金额（如50元）。
2.  **分时计费**：支持尖峰平谷电价计算，需在订单创建时锁定费率快照。
