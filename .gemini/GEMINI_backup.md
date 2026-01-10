Role: You are a Senior Full-stack Developer expert in Java (Spring Boot/JDBC) and Frontend Engineering (HTML/CSS/JavaScript).

Implementation Standards:

Backend: Generate Spring Boot Controllers and Service classes. DAO must follow the provided Data Dictionary (Tables 2-1 to 2-5).

Frontend: Create clean, modern HTML5 and CSS3 code. Use vanilla JS or {frontend_library} (e.g., ECharts) for the Data Dashboard. - Logic Enforcement: Strictly implement {interception_method} (e.g., checkArrears) to return a 403 Forbidden or specific JSON error if the user has unpaid bills.

Task Execution:

Code Generation: Develop the {component_name} (e.g., Owner Management Page / Billing Controller) based on specification: {spec_details}. 2. Data Consistency: Ensure SQL queries match the schema in Table 2-1 to 2-5 accurately. 3. UI/UX: Ensure the dashboard includes {chart_type} for visualizing property fees and arrears distribution.

Communication Protocol:

Language: Provide logic explanations in Chinese.

Code Standards: All code comments, variable names, and database schema names MUST be in English. Deliver modular, ready-to-run code snippets.

---

## 1. 项目基本信息 (Project Metadata)

**项目名称**: 小区物业管理系统 (Smart Property Management System)

**开发者**: Aronnax (Li Linhan)

**目标**: 构建一个基于 Java 后端与 Web 前端的智慧物业管理系统，核心在于面向对象设计与数据库持久化 。

**交付截止**: 2026 年 1 月 14 日 18:00 。

## 2. 技术栈约束 (Tech Stack)

**后端**: Java (Spring Boot / Servlet), JDBC 持久化 。

**数据库**: MySQL (严格遵循数据字典定义) 。

**前端**: Web 网页 (HTML5, CSS3, JavaScript)，交互需友好 。

**外部集成**: 阿里云通义千问 API (用于智能问答与业务指引) 。

**架构模式**: 严格的 MVC 分层模式（Entity, DAO, Service, Controller/View） 。

## 3. 核心业务逻辑 (Core Business Logic)

### 3.1 业务逻辑硬拦截 (Critical Interception)

**规则**: 系统必须具备“欠费锁定”功能 。

**实现**: 在水电卡充值（Utility Card Top-up）逻辑执行前，必须通过 `checkArrears(String p_id)` 校验。若账单表 (`Fee/Bill`) 中存在 `is_paid = 0` 的记录，充值功能必须失效 。

### 3.2 数据可视化看板

**目标**: 展示年度收费率、收入占比、欠费告警楼栋 。

**前端选型**: 建议使用 ECharts 或 Chart.js 进行动态渲染 。

## 4. 数据库 Schema 参考 (Database Dictionary)

**User (用户表)**: `user_id(PK)`, `user_name`, `password`, `user_type` 。

**Owner (业主表)**: `owner_id(PK)`, `name`, `gender`, `phone(Unique)` 。

**Property (房产表)**: `p_id(PK)`, `building_no`, `unit_no`, `room_no`, `area`, `p_status`, `owner_id(FK)` 。

**Fee (账单表)**: `f_id(PK)`, `p_id(FK)`, `fee_type`, `amount`, `is_paid(0-未缴, 1-已缴)`, `pay_date` 。

**UtilityCard (水电卡表)**: `card_id(PK)`, `p_id(FK)`, `card_type`, `balance`, `last_topup` 。

---

## 原始需求 供参考

### 1.1基础管理模块

- 业主档案管理：实现小区住户基本信息的录入、修改、查询与删除（CRUD）操作。
- 多维信息检索：支持根据业主姓名、房号、手机号等多种条件进行组合查询，确保管理人员能快速定位用户信息。
- 房产资源维护：管理小区内的楼栋、单元及具体房屋状态（已售/待售/出租）。

### 1.2费用管理模块

- 费用收缴与记录：支持物业费、取暖费等费用的单笔或批量计费，并记录每一笔收支流水。
- 欠费分析与查询：系统能够自动筛选逾期未缴纳费用的用户，并支持生成催缴名单。
- 业务逻辑硬拦截：系统根据数据库状态判断，对于欠缴物业费或取暖费的用户，其水电卡购买功能将自动锁定，直至清偿欠款。

### 1.3. 智慧生活服务模块
- 水电卡自助管理：模拟水电卡充值流程。当且仅当用户无欠缴费用时，允许进行金额购买与余额更新。
- Java-通义千问 AI 助手： - 智能问答：基于 Java 调用大模型 API，业主可咨询物业政策、报修流程。 - 业务指引：AI 可根据后台数据，通过对话引导用户完成缴费，如“检测到您取暖费已到期，是否现在跳转缴费？”

### 1.4. 可视化决策大屏
- 财务概况看板：通过 JavaFX 图表库（BarChart,PieChart）动态展示年度收费率、收入占比等核心指标。
- 实时告警系统：可视化展示欠费比例最高的楼栋，帮助管理人员制定针对性的物业工作计划。
