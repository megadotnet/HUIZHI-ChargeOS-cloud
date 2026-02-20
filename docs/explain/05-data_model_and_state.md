# 05. 核心数据模型与状态流转 (Data Model & State Transition)

> **文档受众**: 架构师、资深开发人员
> **核心目标**: 规范系统的静态数据结构（类图）与动态行为逻辑（状态机），确保业务概念的一致性。

## 1. 领域模型类图 (Domain Class Diagram)

本图展示了核心业务实体及其关联关系。

**设计亮点**:
- **账单分离**: `SettlementSheet` 独立于订单存在，支持按周期汇总多个订单。
- **动账流水**: `TransactionLog` 记录每一笔资金变动，确保财务可追溯。
- **快照机制**: `OrderItem` 冗余存储下单时的价格 (`dealPrice`)，防止商品改价影响历史订单。

```mermaid
classDiagram
    %% 门店实体 Store
    class Store {
        +String storeId 门店ID
        +String name 名称
        +Enum type 类型[DIRECT/FRANCHISE]
        +Account account 资金账户
        +List~Order~ orders 历史订单
    }

    %% 资金账户 Account
    class Account {
        +Decimal balance 可用余额
        +Decimal frozen 冻结金额
        +Decimal creditLimit 信用额度
        +freeze(amount) 冻结
        +unfreeze(amount) 解冻
        +deduct(amount) 扣款
    }

    %% 交易流水 TransactionLog
    class TransactionLog {
        +String txId 流水号
        +Enum type 类型[PAY/REFUND/RECHARGE]
        +Decimal amount 变动金额
        +String refNo 关联单号
        +Date time 发生时间
    }

    %% 订货单 PurchaseOrder
    class PurchaseOrder {
        +String orderNo 订单号
        +Enum status 状态
        +Decimal totalAmount 订单总额
        +Decimal actualAmount 实付金额
        +String lemengNo 乐檬单号
        +Date createTime 创建时间
        +List~OrderItem~ items 明细
    }

    %% 订单明细 OrderItem
    class OrderItem {
        +String sku 商品编码
        +Integer quantity 数量
        +Decimal dealPrice 成交单价
        +Decimal subTotal 小计
    }

    %% 退货单 ReturnOrder
    class ReturnOrder {
        +String returnNo 退货单号
        +String originalOrderNo 原订单号
        +Enum status 状态
        +Decimal refundAmount 退款金额
        +String reason 原因
    }

    %% 结算单 SettlementSheet
    class SettlementSheet {
        +String sheetNo 结算单号
        +Date periodStart 周期开始
        +Date periodEnd 周期结束
        +Decimal totalDebit 应扣
        +Decimal totalCredit 应退
        +Enum status 状态
    }

    %% 关系
    Store "1" -- "1" Account : 拥有
    Account "1" -- "*" TransactionLog : 产生
    Store "1" -- "*" PurchaseOrder : 发起
    Store "1" -- "*" ReturnOrder : 申请
    Store "1" -- "*" SettlementSheet : 结算

    PurchaseOrder "1" *-- "*" OrderItem : 包含
    PurchaseOrder "1" -- "0..1" ReturnOrder : 关联
```

## 2. 核心状态机 (Core State Machines)

状态机定义了单据生命周期的合法流转路径，严禁非法跳变。

### 2.1. 订货单状态机 (Order State Machine)

```mermaid
stateDiagram-v2
    [*] --> CREATED: 下单/冻结资金

    CREATED --> AUDITED: 总部审核通过
    CREATED --> REJECTED: 总部驳回/解冻
    CREATED --> CANCELLED: 门店取消/解冻

    AUDITED --> SYNCED: 同步乐檬成功
    AUDITED --> SYNC_FAILED: 同步失败(可重试)

    SYNC_FAILED --> SYNCED: 重试成功
    SYNC_FAILED --> CANCELLED: 重试失败取消

    SYNCED --> SHIPPED: 乐檬发货回调
    SHIPPED --> COMPLETED: 确认收货/扣款完成

    REJECTED --> [*]
    CANCELLED --> [*]
    COMPLETED --> [*]

    note right of CREATED
        资金状态: 已冻结
    end note

    note right of SHIPPED
        资金状态: 已扣款
    end note
```

### 2.2. 退货单状态机 (Return State Machine)

```mermaid
stateDiagram-v2
    [*] --> PENDING_AUDIT: 提交申请

    PENDING_AUDIT --> AUDITED: 总部初审通过
    PENDING_AUDIT --> REJECTED: 初审驳回

    AUDITED --> SYNCED: 同步乐檬成功

    SYNCED --> RECEIVED: 乐檬收货确认
    RECEIVED --> REFUNDED: 财务退款完成

    RECEIVED --> PARTIAL_REFUNDED: 部分退款(有损耗)

    REFUNDED --> [*]
    REJECTED --> [*]
    PARTIAL_REFUNDED --> [*]
```

### 2.3. 结算单状态机 (Settlement State Machine)

```mermaid
stateDiagram-v2
    [*] --> OPEN: 账期内记录流水

    OPEN --> GENERATED: 账期结束/生成账单
    GENERATED --> CONFIRMED: 门店确认无误
    GENERATED --> DISPUTED: 门店提出异议

    DISPUTED --> GENERATED: 总部修正后重新生成

    CONFIRMED --> SETTLED: 财务打款/结清
    SETTLED --> [*]
```

## 3. 关键业务规则 (Business Rules)

1.  **不可逆规则**: 订单一旦进入 `SHIPPED`（发货）状态，不可再直接取消，必须走 `ReturnOrder`（退货）流程。
2.  **资金强一致**:
    - `Account.balance`（余额） + `Account.frozen`（冻结） = 总资产。
    - 任何 `frozen` 的增加必须对应一笔 `PurchaseOrder`。
    - 任何 `frozen` 的减少必须对应 `PurchaseOrder` 的完成（扣款）或取消（解冻）。
3.  **幂等性**: 所有涉及资金变动的接口（扣款、退款）必须支持幂等调用，防止网络重试导致重复扣款。

---
*下一篇：请阅读 `06-exception_compensation.md` 了解异常处理与补偿机制。*
