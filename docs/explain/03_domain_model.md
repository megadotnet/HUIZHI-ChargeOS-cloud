# 03. 领域数据模型 (Domain Data Model)

> **文档受众**: 架构师、开发人员
> **核心目标**: 定义核心业务实体及其属性，明确数据间的引用关系。

## 1. 核心实体类图 (Core Entity Class Diagram)

```mermaid
classDiagram
    %% 门店实体
    class Store {
        +String storeId 门店ID
        +String storeName 门店名称
        +Enum storeType 门店类型[直营/加盟]
        +Decimal balance 账户余额
        +Decimal creditLimit 信用额度
        +createOrder() 发起订货
        +requestReturn() 申请退货
    }

    %% 商品实体 (自研系统镜像)
    class Goods {
        +String sku 商品编码
        +String goodsName 商品名称
        +Decimal price 销售单价
        +Integer stock 镜像库存
        +String category 商品分类
    }

    %% 订货单
    class PurchaseOrder {
        +String orderNo 订单号
        +String storeId 门店ID
        +Enum status 订单状态
        +Decimal totalAmount 订单总额
        +Date createTime 创建时间
        +Date auditTime 审核时间
        +pay() 支付冻结
        +ship() 确认发货
    }

    %% 订单明细
    class OrderItem {
        +String sku 商品编码
        +Integer quantity 订购数量
        +Decimal price 成交单价
        +Decimal subTotal 小计金额
    }

    %% 退货单
    class ReturnOrder {
        +String returnNo 退货单号
        +String originalOrderNo 原订单号
        +Enum status 退货状态
        +Decimal refundAmount 退款金额
        +String reason 退货原因
    }

    %% 资金结算单
    class SettlementSheet {
        +String sheetNo 结算单号
        +String storeId 门店ID
        +Date periodStart 账期开始
        +Date periodEnd 账期结束
        +Decimal totalDebit 应扣金额
        +Decimal totalCredit 应补金额
        +Enum status 结算状态[待确认/已确认/已支付]
    }

    %% 关系定义
    Store "1" -- "*" PurchaseOrder : 发起
    Store "1" -- "*" ReturnOrder : 申请
    Store "1" -- "*" SettlementSheet : 关联

    PurchaseOrder "1" *-- "*" OrderItem : 包含
    PurchaseOrder "1" -- "0..1" ReturnOrder : 关联退货

    Goods "1" -- "*" OrderItem : 引用
```

## 2. 关键字段说明

1.  **Store.storeType (门店类型)**:
    - `DIRECT`: 直营店，无需冻结资金，按月结算。
    - `FRANCHISE`: 加盟店，需预冻结资金，余额不足无法下单。

2.  **PurchaseOrder.status (订单状态)**:
    - `CREATED`: 已创建，待审核。
    - `AUDITED`: 总部已审核，待同步乐檬。
    - `SYNCED`: 已同步乐檬，待发货。
    - `SHIPPED`: 乐檬已发货 (部分/全部)。
    - `COMPLETED`: 订单完成 (已扣款)。
    - `CANCELLED`: 已取消 (解冻资金)。

3.  **SettlementSheet (结算单)**:
    - 用于周期性对账，汇总一段时间内的 `PurchaseOrder` (扣款) 和 `ReturnOrder` (退款)。
    - 对于加盟店，实时扣款为主，结算单用于核对差异。
    - 对于直营店，结算单是打款依据。
