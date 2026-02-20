# 02. 核心业务流程与泳道图 (Core Business Flow)

> **文档受众**: 业务方、架构师
> **核心目标**: 明确核心业务（充电全生命周期）在各系统间的流转路径，包含正向流程与异常处理。

## 1. 核心业务全景 (Business Overview)

本平台最核心的业务闭环为 **"充电服务交易闭环"**。
业务主线围绕 `订单(Order)` 展开，涉及 `设备(Pile)` 控制与 `资金(Fund)` 结算。

### 1.1 关键业务节点
1.  **用户认证与充值**: 必须先登录并保证账户余额充足（预充值模式）。
2.  **扫码启动**: 校验设备状态与用户资格。
3.  **充电中监控**: 实时接收设备上报的电压、电流、SOC及累计费用。
4.  **结束结算**: 自动生成账单，扣除余额，多退少补（针对预冻结场景）。

## 2. 充电全流程泳道图 (Charging Swimlane Diagram)

本图展示了从用户扫码到结算完成的完整业务流转。

```mermaid
flowchart TD
    %% 泳道定义
    subgraph User [C端车主 - User]
        ScanQR[扫码/输入桩号]:::action
        ClickStart[点击启动充电]:::action
        Monitor[查看实时进度]:::action
        ClickStop[点击停止充电]:::action
    end

    subgraph MP [小程序端 - Client]
        ReqPileInfo[请求桩详情]:::process
        ReqStart[请求启动充电]:::process
        ReqStop[请求停止充电]:::process
        ShowBill[展示账单详情]:::view
    end

    subgraph Platform [云平台 - Order Service]
        CheckPile[校验桩状态]:::decision
        CreateOrder[创建预订单 - PreCharge]:::process
        CheckBalance{余额充足?}:::decision
        SendStartCmd[下发启动指令]:::process
        UpdateOrderCharging[更新订单: 充电中]:::process
        PushStatus[推送实时状态]:::process
        SendStopCmd[下发停止指令]:::process
        CalcBill[计算最终费用]:::process
        SettleOrder[结算扣款]:::process
        FinishOrder[更新订单: 已支付]:::process
    end

    subgraph IoT [设备网关 - IoT Gateway]
        ForwardCmd[转发指令]:::process
        RecvHeartbeat[接收实时数据]:::process
        RecvResult[接收启停结果]:::process
    end

    subgraph Pile [充电桩 - Hardware]
        CheckSelf[自检硬件状态]:::decision
        StartOutput[闭合继电器输出]:::hardware
        Reporting[上报电压/电流/SOC]:::hardware
        StopOutput[断开继电器]:::hardware
        ReportFinal[上报最终消费数据]:::hardware
    end

    subgraph Fund [资金结算 - Wallet]
        Freeze[冻结预估金额 - 可选]:::fund
        Deduct[扣除实际金额]:::fund
        Refund[退还剩余冻结 - 可选]:::fund
    end

    %% 流程连线 - 启动阶段
    ScanQR --> ReqPileInfo
    ReqPileInfo --> CheckPile
    CheckPile -- 空闲 --> ReqPileInfo
    CheckPile -- 占用/故障 --> ShowError[提示设备不可用]:::error

    ClickStart --> ReqStart
    ReqStart --> CreateOrder
    CreateOrder --> CheckBalance
    CheckBalance -- 不足 --> PromptTopUp[提示充值]:::error
    CheckBalance -- 充足 --> Freeze
    Freeze --> SendStartCmd

    SendStartCmd --> ForwardCmd
    ForwardCmd --> Pile
    Pile --> CheckSelf
    CheckSelf -- 失败 --> ReportStartFail[上报启动失败]:::error
    ReportStartFail --> RecvResult --> CloseOrder[关闭订单/解冻]:::process

    CheckSelf -- 成功 --> StartOutput
    StartOutput --> ReportStartSuccess[上报启动成功]:::process
    ReportStartSuccess --> RecvResult --> UpdateOrderCharging

    %% 流程连线 - 充电中
    StartOutput --> Reporting
    Reporting --> RecvHeartbeat --> PushStatus
    PushStatus --> Monitor

    %% 流程连线 - 停止与结算
    ClickStop --> ReqStop
    ReqStop --> SendStopCmd
    SendStopCmd --> ForwardCmd
    ForwardCmd --> Pile
    Pile --> StopOutput
    StopOutput --> ReportFinal
    ReportFinal --> RecvResult --> CalcBill
    CalcBill --> Deduct
    Deduct --> FinishOrder
    FinishOrder --> Refund
    Refund --> ShowBill

    %% 样式类定义
    classDef action fill:#fff3e0,stroke:#ff9800,stroke-width:2px;
    classDef process fill:#e3f2fd,stroke:#2196f3,stroke-width:2px;
    classDef decision fill:#fff9c4,stroke:#fbc02d,stroke-width:2px,rhombus;
    classDef hardware fill:#e8f5e9,stroke:#4caf50,stroke-width:2px;
    classDef fund fill:#fce4ec,stroke:#e91e63,stroke-width:2px;
    classDef view fill:#f3e5f5,stroke:#9c27b0,stroke-width:2px;
    classDef error fill:#ffebee,stroke:#ef5350,stroke-width:2px,stroke-dasharray: 5 5;
```

## 3. 异常与逆向流程 (Exception Flows)

### 3.1 启动失败 (Start Charge Failed)
- **场景**: 设备离线、枪头未插好、硬件故障。
- **处理**:
    1.  平台接收到设备返回的 `StartResult=Fail` 或超时未响应。
    2.  订单状态更新为 `启动失败 (StartFail)`。
    3.  **资金回滚**: 如有预冻结资金，立即触发解冻流程，原路退回用户余额。

### 3.2 异常停止 (Abnormal Stop)
- **场景**: 用户按下急停按钮、设备过温保护、网络中断。
- **处理**:
    1.  设备主动上报 `StopReason` (如: 急停、拔枪)。
    2.  平台接收后立即结算，生成订单账单。
    3.  若存在未结算金额（如网络中断导致数据丢失），平台根据最后一次心跳数据进行**兜底结算**，并在后续人工核对。

### 3.3 充值退款 (Refund)
- **场景**: 用户余额提现。
- **流程**: 用户发起申请 -> 平台运营审核 -> 财务打款 -> 扣除余额。
- **注意**: 需校验是否存在“进行中”的订单，防止恶意提现后逃单。
