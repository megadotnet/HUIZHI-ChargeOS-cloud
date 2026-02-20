# 04. 核心单据状态机 (Core State Machines)

> **文档受众**: 测试人员、后端开发
> **核心目标**: 明确核心业务单据（订单、设备）的状态流转逻辑及触发条件，特别是异常分支。

## 1. 充电订单状态机 (Order State Machine)

订单是整个充电交易的核心载体，其状态必须与设备物理状态保持强一致。

### 1.1 状态枚举 (States)

| 状态码 | 英文标识 | 描述 |
| :--- | :--- | :--- |
| **0** | `PLACE` | 预下单/创建中。 |
| **1** | `PRE_CHARGE` | 启动指令已下发，等待设备响应。 |
| **2** | `CHARGING` | 充电进行中，收到设备心跳。 |
| **3** | `SETTLE` | 充电结束，正在计算费用。 |
| **4** | `PAYED` | 费用已结清，订单完成。 |
| **-1** | `FAIL` | 启动失败或设备故障。 |
| **-2** | `CANCEL` | 用户主动取消（未启动前）。 |

### 1.2 状态流转图 (State Diagram)

```mermaid
stateDiagram-v2
    [*] --> PLACE : 用户扫码下单
    PLACE --> PRE_CHARGE : 下发启动指令

    state PRE_CHARGE {
        [*] --> WaitingDevice : 等待设备响应
        WaitingDevice --> DeviceAck : 收到启动成功报文
        WaitingDevice --> DeviceNack : 收到启动失败报文
        WaitingDevice --> Timeout : 超时未响应
    }

    PRE_CHARGE --> CHARGING : 启动成功
    PRE_CHARGE --> FAIL : 启动失败/超时
    FAIL --> REFUND : 触发预冻结退款
    REFUND --> [*]

    CHARGING --> SETTLE : 用户/设备停止充电

    state SETTLE {
        [*] --> CalcAmount : 计算电费+服务费
        CalcAmount --> DeductBalance : 扣除余额
    }

    SETTLE --> PAYED : 扣款成功/结算完成
    SETTLE --> EXCEPTION : 扣款失败(余额不足)

    PAYED --> [*]
    EXCEPTION --> [*] : 转入欠费追缴流程
```

## 2. 充电枪状态机 (Connector/Gun State Machine)

设备物理状态决定了是否可以创建新订单。

### 2.1 状态枚举 (States)

| 状态码 | 英文标识 | 描述 |
| :--- | :--- | :--- |
| **0** | `IDLE` | 空闲，可使用。 |
| **1** | `INSERTED` | 枪头已插入车辆，未启动。 |
| **2** | `CHARGING` | 正在充电。 |
| **3** | `FAULT` | 设备故障/离线。 |
| **4** | `OCCUPIED` | 占用中（如充电结束未拔枪）。 |

### 2.2 状态流转图 (State Diagram)

```mermaid
stateDiagram-v2
    [*] --> IDLE : 设备上线/初始化

    IDLE --> INSERTED : 用户插枪
    IDLE --> FAULT : 自检失败/离线

    INSERTED --> CHARGING : 订单启动成功
    INSERTED --> IDLE : 用户拔枪(未启动)

    CHARGING --> OCCUPIED : 充电结束(未拔枪)
    CHARGING --> FAULT : 充电中故障(急停)

    OCCUPIED --> IDLE : 用户拔枪归位
    OCCUPIED --> FAULT : 发生硬件故障

    FAULT --> IDLE : 故障修复/重新上线
```

## 3. 并发与一致性保障

1.  **状态防重**: 订单状态流转必须单向不可逆（除特殊回滚流程）。例如，一旦进入 `PAYED`，绝不可再回到 `CHARGING`。
2.  **分布式锁**: 在 `SETTLE` 阶段，建议对 `OrderId` 加锁，防止重复结算（如设备重发结束报文）。
3.  **最终一致性**: 若设备突然离线导致订单卡在 `CHARGING`，需有定时任务（Job）扫描长时间无心跳的订单，主动触发 `Status Check` 或强制结算。
