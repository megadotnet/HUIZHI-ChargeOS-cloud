# 01. 整体业务蓝图 (Overall Business Blueprint)

> **文档受众**: 产品经理、业务方、系统架构师
> **核心目标**: 建立全局业务视角，明确系统边界、参与角色及核心业务场景划分。

## 1. 业务背景与系统定位

本系统旨在构建一套连接**线下门店**、**总部运营**与**外部供应链（乐檬）**的零售管理平台。

- **核心价值**: 实现门店订货的资金自动管控、订单流转的自动化处理，以及与供应链系统的无缝对接。
- **关键差异**:
    - **加盟店**: 需预充值，订货时冻结资金，发货后扣款（信用管控）。
    - **直营店**: 信用额度管理，按月结算（额度管控）。

## 2. 核心业务场景划分 (Business Scenarios)

为了降低系统复杂度，我们将业务拆解为以下核心场景进行详细分析：

| 场景编号 | 场景名称 | 涉及角色 | 核心目标 | 对应文档 |
| :--- | :--- | :--- | :--- | :--- |
| **A** | **订货与支付** | 门店、总部、系统 | 完成下单、库存校验、资金/额度冻结。 | `02-scenario_ordering_payment.md` |
| **B** | **履约与售后** | 乐檬、系统、门店 | 处理发货回调、收货确认、退货申请与退款。 | `03-scenario_logistics_aftersales.md` |
| **C** | **财务与对账** | 财务、系统、乐檬 | 每日资金对账、差异处理、风控拦截。 | `04-scenario_finance_reconciliation.md` |

## 3. 全局角色与用例视图 (Global Use Case Diagram)

本图展示了系统中的所有参与者及其核心职责。

```mermaid
usecaseDiagram
    actor "门店店长 Store Manager" as StoreMgr
    actor "总部运营 HQ Ops" as HQOps
    actor "总部财务 HQ Finance" as HQFin
    actor "乐檬供应链 Lemeng API" as Lemeng

    package "自研零售管理平台 Core System" {
        usecase "发起订货申请" as UC_Order
        usecase "查看库存/状态" as UC_ViewStock
        usecase "确认收货" as UC_Receive
        usecase "发起退货申请" as UC_Return

        usecase "审核订货单" as UC_AuditOrder
        usecase "审核退货单" as UC_AuditReturn
        usecase "管理商品资料" as UC_ManageGoods

        usecase "资金账户充值" as UC_Recharge
        usecase "资金结算对账" as UC_Reconcile
        usecase "查看财务报表" as UC_Report
    }

    %% 门店侧
    StoreMgr --> UC_Order
    StoreMgr --> UC_ViewStock
    StoreMgr --> UC_Receive
    StoreMgr --> UC_Return
    StoreMgr --> UC_Recharge

    %% 总部侧
    HQOps --> UC_AuditOrder
    HQOps --> UC_AuditReturn
    HQOps --> UC_ManageGoods

    HQFin --> UC_Recharge
    HQFin --> UC_Reconcile
    HQFin --> UC_Report

    %% 系统与外部交互 (边界)
    UC_Order ..> Lemeng : 推送订单
    UC_Return ..> Lemeng : 推送退货
    UC_Receive ..> Lemeng : 状态同步
    UC_Reconcile ..> Lemeng : 获取账单
```

## 4. 系统边界与职责 (System Boundaries)

**为什么需要明确边界？**
明确边界可以避免功能重复开发，确保数据源的唯一性。

1.  **自研系统（本平台）职责**:
    - **业务流转中枢**: 管理订单状态、审批流程、退货逻辑。
    - **资金管理中心**: 管理门店余额、冻结、扣款、退款、充值。
    - **用户交互界面**: 提供门店 App 和总部管理后台。

2.  **乐檬供应链（外部系统）职责**:
    - **实物管理**: 仓库管理、物流发货、实际库存扣减。
    - **基础数据源**: 商品信息（SKU、名称、条码）的源头。

3.  **交互原则**:
    - **库存**: 自研系统仅做“镜像库存”参考，最终以乐檬下单结果为准。
    - **发货**: 乐檬发货后，通过回调通知自研系统扣款。
    - **对账**: 以乐檬的发货/退货记录为结算依据。

## 5. 关键假设 (Assumptions)

- **假设 1**: 乐檬系统提供标准的 HTTP API 接口，并支持 Webhook 回调。
- **假设 2**: 门店分为“加盟”和“直营”两种类型，且加盟店必须预充值。
- **假设 3**: 系统网络环境稳定，但需设计重试机制以应对偶发的网络波动。

---
*下一篇：请阅读 `02-scenario_ordering_payment.md` 了解订货与支付的详细逻辑。*
