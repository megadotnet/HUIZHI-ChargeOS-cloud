# 05-异常处理与对账补偿 (Exception & Reconciliation)

本章节涵盖**系统健壮性设计**，重点分析“如果出错了怎么办”。这是任何商业系统（尤其是涉及资金和硬件的系统）的生命线。

## 场景一：启动失败的补偿流程 (Start Failure)

### 1. 场景描述
用户点击启动，钱已冻结，但设备因网络波动或硬件故障未启动成功。

### 2. 补偿逻辑 (Sequence Diagram)

```mermaid
sequenceDiagram
    participant App as 小程序
    participant Svr as 后台服务
    participant Dev as 充电设备

    App->>Svr: 1. 请求启动充电
    Svr->>Svr: 2. 冻结余额/创建订单
    Svr->>Dev: 3. 下发启动指令 [Time: T0]

    rect rgb(255, 200, 200)
        Note right of Svr: 等待响应超时 [Time > T0 + 30s]
        Svr->>Svr: 4. 判定启动失败
    end

    Svr->>Svr: 5. 执行回滚 [Rollback]
    Svr->>Svr: 6. 解冻余额/取消订单
    Svr-->>App: 7. 返回启动失败提示
```

### 3. 处理策略
*   **超时机制**：设置30-60秒的指令等待时间。
*   **自动退款**：一旦判定失败，必须原子性地执行余额解冻。
*   **错误码透传**：将设备返回的错误码（如“急停被按下”）清晰展示给用户。

---

## 场景二：停止失败与挂单处理 (Stop Failure)

### 1. 场景描述
用户点击停止，但设备没反应，依然在通电计费。这是最严重的客诉场景。

### 2. 强制结算逻辑 (Force Stop)

```mermaid
graph TD
    UserStop[用户点击停止] --> SendCmd[下发停止指令]

    SendCmd --> DevAck{设备响应?}
    DevAck --无响应--> Retry{重试3次?}
    Retry --是--> SendCmd

    Retry --否--> ForceEnd[后台强制结束订单]
    ForceEnd --> LogWarn[标记异常挂单]
    LogWarn --> Settle[按当前读数结算]
    Settle --> NotifyAdmin[通知运维人工断电]

    DevAck --响应成功--> NormalEnd[正常结算]

    style ForceEnd fill:#ffcdd2
    style LogWarn fill:#ffccbc
```

---

## 场景三：资金对账 (Reconciliation)

### 1. 对账目标
确保“用户支付的钱”等于“设备实际输出电量的应收费用”。

### 2. 对账流程
系统每日凌晨（T+1）自动执行对账任务。

*   **数据源A**：`ChargingOrder` 表中的 `amount` (实收金额)。
*   **数据源B**：`MenberBalance` 表中的流水变动。
*   **数据源C**：设备上报的最终电表读数 (Meter Value)。

### 3. 差异处理
*   **长款 (多收)**：系统自动发起退款流程，退回用户余额。
*   **短款 (少收)**：标记坏账，记录用户信用分，下次充电前补缴。
