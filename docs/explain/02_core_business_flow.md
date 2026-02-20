# 02. 核心业务流程 (Core Business Flows)

> **文档受众**: 产品经理、研发人员
> **核心目标**: 梳理跨端业务流转逻辑，明确关键节点的状态变更与系统交互。

## 1. 门店订货与履约流程 (Procurement Flow)

此流程描述门店发起订货到乐檬发货的全过程，包含资金冻结逻辑。

```mermaid
flowchart TD
    %% 泳道定义
    subgraph StoreLane [门店端 Store]
        StartOrder[开始订货]
        SelectGoods[选择商品]
        SubmitOrder[提交订单]
        CheckStatus[查看订单状态]
        ReceiveGoods[确认收货]
    end

    subgraph HQLane [总部运营 HQ]
        AuditOrder[审核订单]
        RejectOrder[驳回订单]
    end

    subgraph SystemLane [自研系统 System]
        CheckStock[校验库存]
        CheckBalance[校验余额/信用]
        FreezeFund[冻结资金]
        SyncLemeng[同步至乐檬]
        UpdateStatus[更新订单状态]
        DeductFund[正式扣款]
        UnfreezeFund[解冻资金]
    end

    subgraph LemengLane [乐檬供应链 Lemeng]
        ReceiveAPI[接收订单API]
        Logistics[仓库发货]
        Callback[发货回调/状态同步]
    end

    %% 流程逻辑
    StartOrder --> SelectGoods
    SelectGoods --> SubmitOrder
    SubmitOrder --> CheckStock
    CheckStock -- 库存不足 --> SelectGoods
    CheckStock -- 库存充足 --> CheckBalance

    CheckBalance -- 余额不足 --> SubmitOrder
    CheckBalance -- 余额充足 --> FreezeFund
    FreezeFund --> AuditOrder

    AuditOrder -- 驳回 --> RejectOrder
    RejectOrder --> UnfreezeFund
    UnfreezeFund --> CheckStatus

    AuditOrder -- 通过 --> SyncLemeng
    SyncLemeng --> ReceiveAPI
    ReceiveAPI --> Logistics
    Logistics --> Callback
    Callback --> UpdateStatus
    UpdateStatus --> DeductFund
    DeductFund --> ReceiveGoods
```

## 2. 门店退货流程 (Return Flow)

此流程描述门店发起退货申请，经审核后由乐檬进行回收的逆向流程。

```mermaid
flowchart TD
    %% 泳道定义
    subgraph StoreLane [门店端 Store]
        StartReturn[发起退货申请]
        ShipReturn[退货发出]
    end

    subgraph HQLane [总部运营 HQ]
        AuditReturn[审核退货]
        RejectReturn[驳回退货]
    end

    subgraph SystemLane [自研系统 System]
        ValidateReturn[校验退货规则]
        SyncReturnLemeng[同步退货单至乐檬]
        RefundFund[退还资金]
    end

    subgraph LemengLane [乐檬供应链 Lemeng]
        ReceiveReturnAPI[接收退货API]
        ConfirmReceipt[仓库确认收货]
        ReturnCallback[收货回调]
    end

    %% 流程逻辑
    StartReturn --> ValidateReturn
    ValidateReturn -- 不符合规则 --> StartReturn
    ValidateReturn -- 符合规则 --> AuditReturn

    AuditReturn -- 驳回 --> RejectReturn
    AuditReturn -- 通过 --> SyncReturnLemeng

    SyncReturnLemeng --> ReceiveReturnAPI
    ReceiveReturnAPI --> ShipReturn
    ShipReturn --> ConfirmReceipt
    ConfirmReceipt --> ReturnCallback
    ReturnCallback --> RefundFund
```

## 3. 资金结算逻辑 (Fund Settlement)

资金结算贯穿订货与退货的全生命周期。

1.  **下单时**: 系统预冻结订单金额（含运费）。
2.  **发货后**: 系统根据实际发货数量进行正式扣款（解冻并扣除）。
3.  **差异处理**: 若乐檬缺货，仅扣除实际发货金额，剩余冻结资金自动解冻。
4.  **退货时**: 乐檬确认收货数量后，按原价（或折损价）退还至门店余额。

```mermaid
flowchart LR
    Orders[订单生成] --> Freeze[资金冻结]
    Freeze --> Shipping[乐檬发货]
    Shipping --> Settlement[实际扣款]
    Settlement --> Balance[门店余额减少]

    Returns[退货申请] --> ReturnAudit[审核通过]
    ReturnAudit --> GoodsBack[乐檬收货]
    GoodsBack --> Refund[资金退还]
    Refund --> BalanceIncrease[门店余额增加]
```
