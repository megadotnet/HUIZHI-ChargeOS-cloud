# 01-整体业务蓝图 (Overall Business Blueprint)

本文档旨在为非技术背景的产品经理梳理核心业务逻辑。我们将以标准的**零售电商与物流配送**模型来类比当前的充电业务系统，以便更直观地理解各模块的职责。

## 1. 业务全景图 (Business Panorama)

本系统本质上是一个**能源零售与即时配送平台**。

*   **商品**：电力（Energy）。
*   **下单**：用户启动充电（Place Order）。
*   **发货/物流**：充电桩输出电力（Delivery）。
*   **收货**：用户结束充电（Receive Goods）。
*   **结算**：根据实际发货量（度数）进行扣款（Settlement）。

### 核心映射关系表

| 业务概念 (零售/物流) | 系统实体 (代码映射) | 说明 |
| :--- | :--- | :--- |
| **消费者** (Consumer) | `User` / `Member` | C端用户，通过小程序操作 |
| **门店前台** (Storefront) | `hcp-mp` (Mini Program) | 用户下单、支付的入口 |
| **仓储管理系统** (WMS) | `hcp-operator` | 核心后台，管理订单、库存(桩)、结算 |
| **物流配送商** (Logistics) | `hcp-simulator` / Station | 负责实际的电力传输和硬件控制 |
| **销售订单** (Sales Order) | `ChargingOrder` | 记录交易的核心单据 |
| **钱包/账户** (Wallet) | `MenberBalance` | 用户的预存款账户 |

## 2. 参与角色 (Actors)

我们在系统中识别出以下核心角色：

1.  **C端消费者 (End User)**
    *   目标：购买电力，快速完成充电。
    *   关注点：价格透明、操作便捷、账单准确。

2.  **平台运营方 (Platform Operator)**
    *   目标：管理站点、监控订单、处理异常。
    *   关注点：设备在线率、订单转化率、资金安全。

3.  **物流/设备方 (Device/Logistics Provider)**
    *   目标：执行充电指令，上报实时数据。
    *   关注点：指令响应速度、数据准确性、安全保护。

## 3. 系统边界与交互 (System Boundaries)

为了清晰界定各系统的职责，我们使用以下上下文图（Context Diagram）来展示。

### 系统上下文图

```mermaid
graph TD
    User[C端消费者] -->|1. 下单与支付| MP[hcp-mp 小程序前台]
    Admin[平台运营人员] -->|2. 管理与监控| Operator[hcp-operator 运营后台]

    subgraph 核心业务域
        MP -->|3. 提交订单请求| Operator
        Operator -->|4. 下发配送指令| Simulator[hcp-simulator 设备模拟器]
        Simulator -.->|5. 实时物流状态| Operator
        Operator -.->|6. 订单状态推送| MP
    end

    style User fill:#f9f,stroke:#333,stroke-width:2px
    style Admin fill:#f9f,stroke:#333,stroke-width:2px
    style MP fill:#bbf,stroke:#333,stroke-width:2px
    style Operator fill:#bfb,stroke:#333,stroke-width:4px
    style Simulator fill:#ddd,stroke:#333,stroke-width:2px
```

## 4. 核心业务用例 (Core Use Cases)

以下用例图展示了不同角色在系统中的主要活动。

### 业务用例图

```mermaid
graph LR
    subgraph 消费者域
        User[C端消费者] --> PlaceOrder[下单/启动充电]
        User --> StopOrder[结束订单/停止充电]
        User --> ViewOrder[查看订单轨迹]
        User --> Recharge[钱包充值]
    end

    subgraph 运营管理域
        Admin[运营人员] --> AuditOrder[订单审核/查询]
        Admin --> ManageStation[站点/库存管理]
        Admin --> Reconcile[资金对账]
        Admin --> HandleException[异常处理]
    end

    subgraph 设备物流域
        Device[充电桩/模拟器] --> ReportStatus[上报心跳/物流状态]
        Device --> ExecuteCommand[执行启停指令]
    end

    PlaceOrder -.->|触发| ExecuteCommand
    StopOrder -.->|触发| ExecuteCommand
    ReportStatus -.->|更新| ViewOrder

    style User fill:#fff,stroke:#333
    style Admin fill:#fff,stroke:#333
    style Device fill:#fff,stroke:#333
    style PlaceOrder fill:#e1f5fe
    style StopOrder fill:#e1f5fe
    style AuditOrder fill:#fff9c4
    style ReportStatus fill:#f3e5f5
```

## 5. 设计原则与假设

1.  **异步优先**：由于涉及硬件交互（物流配送），大部分状态更新是异步的。
2.  **最终一致性**：订单状态可能与设备状态存在短暂延迟，但最终必须一致。
3.  **预付费模式**：假设当前业务主要支持余额扣款，即“先充值，后消费”。

接下来，我们将分场景详细拆解上述业务逻辑。
