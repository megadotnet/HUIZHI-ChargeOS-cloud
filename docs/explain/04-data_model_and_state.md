# 04-场景：数据模型与状态流转 (Data Model & State Transition)

## 1. 核心数据模型 (Core Data Model)

本章节定义了业务的核心实体及其关系。

### 1.1 类图 (Class Diagram)

```mermaid
classDiagram
    class User {
        +Long userId
        +String username
        +MenberBalance wallet
    }

    class MenberBalance {
        +Long userId
        +BigDecimal amount "余额"
        +Date updateTime
    }

    class ChargingOrder {
        +String orderId "主键"
        +String orderNumber "业务单号"
        +Long userId "下单用户"
        +String pileId "桩编号"
        +String portId "枪口编号"
        +OrderState orderState "订单状态"
        +ChargeStatus chargeStatus "充电状态"
        +Date startTime "下单时间"
        +Date endTime "结束时间"
        +BigDecimal totalAmount "总金额"
        +BigDecimal consumePower "总电量"
        +String stopReason "停止原因"
    }

    class HlhtChargeOrderInfo {
        +String orderNo "互联互通订单号"
        +BigDecimal startSoc "开始电量%"
        +BigDecimal endSoc "结束电量%"
        +BigDecimal cuspElect "尖时电量"
        +BigDecimal peakElect "峰时电量"
        +BigDecimal flatElect "平时电量"
        +BigDecimal valleyElect "谷时电量"
        +String vin "车辆识别码"
    }

    class Station {
        +String stationId "站点ID"
        +String stationName "站点名称"
        +List~Pile~ piles
    }

    class Pile {
        +String pileId "设备ID"
        +List~Port~ ports
    }

    User "1" --> "1" MenberBalance : 拥有
    User "1" --> "*" ChargingOrder : 发起
    ChargingOrder "1" --> "1" Pile : 关联
    ChargingOrder "1" --> "0..1" HlhtChargeOrderInfo : 同步记录
    Station "1" --> "*" Pile : 包含
```

### 1.2 关键字段解释 (Field Explanation)

*   **`OrderState` (订单状态)**: 描述整个交易的生命周期（下单 -> 支付 -> 完成）。
*   **`ChargeStatus` (充电状态)**: 描述物理设备的运行状态（准备 -> 充电中 -> 已停止）。
*   **`soc` (State of Charge)**: 电池剩余电量百分比。`startSoc` 和 `endSoc` 的差值反映了充电效果。
*   **分时电量 (`cusp`, `peak`, `flat`, `valley`)**: 根据不同时间段统计的电量。不同时间段电价不同（尖峰平谷），系统需分别计算费用后求和。
*   **`stopReason`**: 停止原因码（如：用户手动停止、充满自停、设备故障、欠费停止）。

---

## 2. 状态流转机 (State Machine)

### 2.1 订单状态流转 (Order Lifecycle)

```mermaid
stateDiagram-v2
    [*] --> PLACE : 用户下单(Created)

    state "充电进行中" as Process {
        PLACE --> CHARGING : 启动成功
        CHARGING --> SETTLE : 收到停止信号
    }

    PLACE --> FAIL : 启动失败/超时
    SETTLE --> PAYED : 扣款成功
    SETTLE --> UNPAID : 余额不足

    PAYED --> [*]
    FAIL --> [*]
    UNPAID --> [*] : 追缴流程
```

### 2.2 充电物理状态流转 (Physical Charge Status)

```mermaid
stateDiagram-v2
    [*] --> PRE_CHARGE : 下发启动指令
    PRE_CHARGE --> CHARGING : 继电器闭合(ACK)
    PRE_CHARGE --> IDLE : 启动失败(NACK)

    CHARGING --> FINISH_CHARGE : 正常结束
    CHARGING --> FAULT_STOP : 故障中断

    FINISH_CHARGE --> IDLE : 拔枪/重置
    FAULT_STOP --> IDLE : 故障恢复
```

---

## 3. 数据流转全景 (End-to-End Data Flow)

从用户下单到最终生成报表的数据流向：

```mermaid
graph LR
    User[用户操作] -->|创建订单| OrderDB[(订单库)]
    OrderDB -->|指令| Hardware[硬件设备]

    Hardware -->|实时数据| Cache[(Redis缓存)]
    Cache -->|WebSocket| User

    Hardware -->|结束结算| OrderDB
    OrderDB -->|扣费| WalletDB[(余额库)]

    OrderDB -->|归档| ReportDB[(报表/大数据)]
    OrderDB -->|同步| GovPlatform[政府监管平台(互联互通)]
```

## 4. 特殊业务逻辑说明 (Business Rules)

1.  **分时计费逻辑**:
    *   费用 = `尖时电量 * 尖时电价` + `峰时电量 * 峰时电价` + ... + `服务费`。
    *   如果一次充电跨越多个时段（例如从平段充到峰段），系统需将电量按时间段拆分计算。
2.  **精度处理**:
    *   金额保留 2 位小数（`BigDecimal`），采用“四舍五入”或“截断”策略（具体看配置）。
    *   电量通常保留 2-3 位小数。
