# 01. 角色与核心用例分析 (Roles & Use Cases)

> **文档受众**: 产品经理、业务方
> **核心目标**: 明确各端业务角色、系统权限边界及核心交互场景。

## 1. 业务角色定义 (Business Roles)

| 角色名称 | 英文标识 | 职责描述 | 系统入口 |
| :--- | :--- | :--- | :--- |
| **门店店长** | `Store Manager` | 负责门店日常运营，包括发起订货、申请退货、查看库存及对账单。 | 门店管理后台 (Store Admin) |
| **加盟商** | `Franchisee` | 负责门店资金往来，查看结算单据、发起充值或提现申请。 | 门店管理后台 (Store Admin) |
| **总部运营** | `HQ Operations` | 负责审核门店订单、退货申请，监控供应链异常，管理商品上下架。 | 总部管理后台 (HQ Admin) |
| **总部财务** | `HQ Finance` | 负责审核资金结算单，处理加盟商提现与充值，监控整体资金流。 | 总部管理后台 (HQ Admin) |
| **乐檬系统** | `Lemeng System` | 外部集成系统，负责实际的供应链履约[发货/退货/库存管理]。 | 开放接口 (Open API) |

## 2. 核心用例视图 (Core Use Cases)

以下图表展示了核心角色与系统各功能模块的交互关系。

```mermaid
flowchart LR
    %% 样式定义
    classDef actor fill:#f9f,stroke:#333,stroke-width:2px;
    classDef module fill:#e1f5fe,stroke:#0277bd,stroke-width:2px;
    classDef ext fill:#fff9c4,stroke:#fbc02d,stroke-width:2px,stroke-dasharray: 5 5;

    %% 角色定义
    StoreMgr[门店店长 Store Manager]:::actor
    Franchisee[加盟商 Franchisee]:::actor
    HQOps[总部运营 HQ Operations]:::actor
    HQFin[总部财务 HQ Finance]:::actor
    Lemeng[乐檬供应链系统 Lemeng API]:::ext

    %% 模块定义 - 门店端
    subgraph StoreSystem [门店管理系统]
        CreateOrder[发起订货申请]:::module
        ReturnOrder[发起退货申请]:::module
        ViewStock[查看实时库存]:::module
        CheckBill[查看资金对账单]:::module
        Recharge[账户充值提现]:::module
    end

    %% 模块定义 - 总部端
    subgraph HQSystem [总部管理系统]
        AuditOrder[审核订货单]:::module
        AuditReturn[审核退货单]:::module
        ManageGoods[商品上下架管理]:::module
        Settlement[资金结算审核]:::module
        FinReport[财务报表查看]:::module
    end

    %% 关系连线
    StoreMgr --> CreateOrder
    StoreMgr --> ReturnOrder
    StoreMgr --> ViewStock

    Franchisee --> CheckBill
    Franchisee --> Recharge

    HQOps --> AuditOrder
    HQOps --> AuditReturn
    HQOps --> ManageGoods

    HQFin --> Settlement
    HQFin --> FinReport
    HQFin --> Recharge

    %% 跨系统交互
    CreateOrder -. 推送订单 .-> Lemeng
    ReturnOrder -. 推送退货 .-> Lemeng
    ViewStock -. 同步库存 .-> Lemeng
    AuditOrder -. 确认发货 .-> Lemeng

```

## 3. 权限边界说明

1.  **门店与总部边界**:
    - **门店**: 仅负责发起业务单据（订货、退货）和查看自身数据。
    - **总部**: 负责审核单据，具有全局商品管理和资金结算权限。

2.  **自研与乐檬边界**:
    - **自研系统**: 负责业务流转（订单状态、审批流）、资金结算（账户余额、扣款）、用户权限。
    - **乐檬系统**: 负责实物库存管理、物流发货、实际退货入库。自研系统仅通过 API 同步乐檬的状态。

3.  **资金结算权限**:
    - 加盟商充值进入平台资金池。
    - 订货时，系统预冻结资金；发货确认后，实际扣款。
    - 退货时，乐檬确认收货后，系统自动生成退款单，财务审核后退回余额。
