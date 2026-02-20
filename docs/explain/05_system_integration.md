# 05. 系统集成与部署架构 (System Integration & Deployment)

> **文档受众**: 架构师、运维工程师
> **核心目标**: 明确自研系统与外部乐檬供应链系统的集成边界与部署架构。

## 1. 系统组件架构图 (Component Architecture)

系统采用微服务架构，通过适配层与乐檬系统交互。

```mermaid
flowchart TB
    %% 外部系统
    subgraph External [外部系统 External Systems]
        LemengAPI[乐檬开放平台 API]:::ext
        WeChat[微信平台]:::ext
    end

    %% 自研系统
    subgraph Internal [自研微服务集群 Self-Developed Microservices]
        Gateway[API 网关 hcp-gateway]

        subgraph Services [核心服务]
            StoreSvc[门店服务 Store Service]
            OrderSvc[订单服务 Order Service]
            FinanceSvc[资金服务 Finance Service]
            UserSvc[用户服务 User Service]
        end

        subgraph Integration [集成适配层]
            LemengAdapter[乐檬适配器 Lemeng Adapter]
        end

        DB[(MySQL Cluster)]
        Redis[(Redis Cache)]
    end

    %% 前端应用
    subgraph Clients [客户端应用]
        StoreApp[门店小程序/App]
        HQWeb[总部管理后台]
    end

    %% 交互关系
    StoreApp --> Gateway
    HQWeb --> Gateway
    Gateway --> StoreSvc
    Gateway --> OrderSvc
    Gateway --> FinanceSvc

    OrderSvc --> LemengAdapter
    LemengAdapter -- REST/HTTP --> LemengAPI

    OrderSvc --> DB
    FinanceSvc --> DB
    StoreApp -- Auth --> WeChat

    classDef ext fill:#fff9c4,stroke:#fbc02d,stroke-width:2px,stroke-dasharray: 5 5;
```

## 2. 核心接口时序交互 (Interface Sequence Diagram)

以下时序图展示了订货单从自研系统同步至乐檬系统的详细交互过程。

```mermaid
sequenceDiagram
    participant Store as 门店端 (Store)
    participant OrderSvc as 订单服务 (OrderSvc)
    participant FinanceSvc as 资金服务 (FinanceSvc)
    participant Adapter as 乐檬适配器 (Adapter)
    participant Lemeng as 乐檬系统 (Lemeng)

    %% 下单流程
    Store ->> OrderSvc: 1. 提交订单 (Submit Order)
    activate OrderSvc

    OrderSvc ->> FinanceSvc: 2. 请求冻结资金 (Freeze Fund)
    activate FinanceSvc
    FinanceSvc -->> OrderSvc: 资金冻结成功 (Success)
    deactivate FinanceSvc

    OrderSvc ->> OrderSvc: 3. 更新订单状态 (AUDITED)

    %% 同步乐檬
    OrderSvc ->> Adapter: 4. 推送订单请求 (Push Order)
    activate Adapter
    Adapter ->> Lemeng: 5. 调用创建订单接口 (POST /order/create)
    activate Lemeng
    Lemeng -->> Adapter: 6. 返回乐檬订单号 (Lemeng OrderNo)
    deactivate Lemeng

    Adapter -->> OrderSvc: 7. 同步成功 (Sync Success)
    deactivate Adapter

    OrderSvc ->> OrderSvc: 8. 更新订单状态 (SYNCED)
    OrderSvc -->> Store: 9. 下单成功 (Order Created)
    deactivate OrderSvc

    %% 发货回调
    note over Lemeng, OrderSvc: 异步发货通知
    Lemeng ->> Adapter: 10. 发货回调 (Shipping Callback)
    activate Adapter
    Adapter ->> OrderSvc: 11. 更新发货状态 (Update Status)
    activate OrderSvc
    OrderSvc ->> FinanceSvc: 12. 实际扣款 (Deduct Fund)
    activate FinanceSvc
    FinanceSvc -->> OrderSvc: 扣款成功
    deactivate FinanceSvc
    deactivate OrderSvc
    deactivate Adapter
```

## 3. 集成要点说明

1.  **接口认证**:
    - 自研系统访问乐檬 API 需携带 `AppKey` 和 `Signature`。
    - 乐檬回调自研系统需验证签名，防止伪造回调。

2.  **数据一致性**:
    - **库存**: 自研系统仅做 *镜像库存* 展示，实际下单时需校验乐檬实时库存（或依赖乐檬下单接口返回的库存不足错误）。
    - **状态**: 订单状态以乐檬回调为准，自研系统需实现 *定时任务* 主动查询乐檬订单状态，防止回调丢失。

3.  **异常处理**:
    - 若同步乐檬失败（网络超时等），订单状态置为 `SYNC_FAILED`，支持人工或自动重试。
    - 若乐檬发货数量与订单不符（缺货），按实际发货数量扣款，剩余冻结资金解冻。
