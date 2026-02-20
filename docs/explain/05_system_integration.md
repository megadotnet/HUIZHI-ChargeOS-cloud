# 05. 系统集成与外部边界 (System Integration & Architecture)

> **文档受众**: 架构师、运维工程师、第三方集成方
> **核心目标**: 清晰划分系统模块、技术栈、部署架构及外部系统边界，特别是第三方集成部分。

## 1. 总体架构图 (System Architecture)

本系统采用 Spring Cloud 微服务架构，以 **充电运营(hcp-operator)** 为核心，通过 **Netty网关** 接入海量充电设备。

```mermaid
flowchart TB
    %% 样式定义
    classDef core fill:#e1f5fe,stroke:#0277bd,stroke-width:2px;
    classDef infra fill:#f5f5f5,stroke:#bdbdbd,stroke-width:1px;
    classDef ext fill:#fff9c4,stroke:#fbc02d,stroke-width:2px,stroke-dasharray: 5 5;
    classDef client fill:#e8f5e9,stroke:#4caf50,stroke-width:2px;

    %% 客户端层
    subgraph Client [客户端 - Client Layer]
        MP[微信小程序]:::client
        H5[H5运营后台]:::client
        App[运维App - 可选]:::client
    end

    %% 网关层
    subgraph Gateway [网关层 - API Gateway]
        ApiGW[Spring Cloud Gateway]:::infra
        Nginx[Nginx LB]:::infra
    end

    %% 核心业务层 (微服务)
    subgraph Core [核心服务层 - Business Layer]
        Auth[统一认证中心 - hcp-auth]:::core
        Operator[运营管理服务 - hcp-operator]:::core
        MPService[小程序后端服务 - hcp-mp]:::core
        Job[分布式调度 - hcp-job]:::core
        System[系统管理服务 - hcp-system]:::core
    end

    %% 接入层 (设备与消息)
    subgraph Access [接入层 - IoT Access]
        Netty[Netty TCP网关]:::core
        Protocol[多协议适配器 - 104/102/云快充]:::core
    end

    %% 数据存储层
    subgraph Data [存储与中间件 - Data Layer]
        MySQL[MySQL - 业务数据]:::infra
        Redis[Redis - 缓存/锁/队列]:::infra
        MinIO[OSS/MinIO - 文件存储]:::infra
        MQ[RocketMQ/RabbitMQ - 异步解耦]:::infra
        InfluxDB[时序数据库 - 充电曲线]:::infra
    end

    %% 外部系统集成 (重点边界)
    subgraph External [外部集成系统 - 3rd Party]
        WeChat[微信支付/授权]:::ext
        AliSms[短信服务]:::ext
        MapAPI[高德/百度地图]:::ext

        %% 供应链集成 (非自研)
        subgraph SupplyChain [供应链与物流 - Integrated]
            Lemeng[乐檬开放平台 - Lemeng API]:::ext
            SCM_Order[采购订单同步]:::ext
            SCM_Stock[库存查询]:::ext
        end
    end

    %% 连线关系
    Client --> Nginx
    Nginx --> ApiGW
    ApiGW --> Auth
    ApiGW --> Operator
    ApiGW --> MPService

    MPService --> Operator : Feign调用
    Operator --> Access
    Access --> Protocol
    Protocol --> Netty

    Operator --> MySQL
    Operator --> Redis
    Access --> InfluxDB

    MPService --> WeChat
    Operator --> AliSms
    Operator --> MapAPI

    %% 供应链集成逻辑
    Operator -- HTTP/OpenAPI --> Lemeng
    Lemeng -- 采购数据 --> SCM_Order
    Lemeng -- 物料库存 --> SCM_Stock
```

## 2. 外部集成详细说明 (Integration Details)

### 2.1 供应链与物流 (Supply Chain & Logistics)
- **集成方式**: **非自研**，完全集成 **乐檬开放平台 (Lemeng Open API)**。
- **业务场景**:
    1.  **备件采购**: 当运维人员在系统中提交“充电模块损坏”工单时，系统通过API自动向乐檬发起采购申请。
    2.  **物流追踪**: 备件发货后，通过物流单号查询接口实时同步物流轨迹至运营后台。
    3.  **库存查询**: 在派单维修前，先查询乐檬库存，确认是否有备件可用。
- **数据流向**: `Operator Service` -> `OpenAPI Gateway` -> `Lemeng System`。
- **鉴权方式**: AppKey / AppSecret 签名认证。

### 2.2 支付与清算 (Payment)
- **微信支付**:
    - **预支付**: `UnifiedOrder` 接口，获取 `prepay_id`。
    - **回调**: 支付成功后，微信回调系统接口，需校验签名防止篡改。
- **分账 (Profit Sharing)**:
    - 若涉及多商户分账（如场站方、平台方），需调用微信分账接口，按比例分配资金。

### 2.3 设备互联 (IoT Protocols)
- **协议支持**:
    - **云快充 V1.5/V1.6**: 行业标准协议，支持绝大多数品牌桩。
    - **中电联 102**: 互联互通协议。
- **心跳机制**: 设备每 15-30 秒上报一次心跳，包含枪头状态、电压电流等。若连续 3 次心跳丢失，判定设备离线。
