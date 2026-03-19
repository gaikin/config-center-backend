# 配置中心后端服务最终文档

> 文档版本：v1.0  
> 生成日期：2026-03-19  
> 适用仓库：`config-center-backend`

## 1. 文档目的

本文件用于统一后端服务口径，沉淀以下内容：

1. 后端目标架构与分层职责。
2. 当前代码实际落地能力（以仓库代码为准）。
3. 控制面、运行面、权限治理面的 API 现状。
4. 数据模型与运行配置基线。
5. 已知差距与下一阶段迁移建议。

## 2. 信息来源与基线

本文件综合以下资料与代码现状整理：

1. `README.md`
2. `spec/config-center-product/architecture.md`
3. `spec/config-center-product/api-design.md`
4. `spec/config-center-product/permission-model.md`
5. `spec/config-center-product/decision-ledger.md`
6. `docs/superpowers/specs/2026-03-18-backend-full-rearchitecture-design.md`
7. `src/main/java/**`（Controller / Service / Infrastructure）
8. `src/main/resources/schema-h2.sql`、`data-h2.sql`
9. `db/migrations/0001_phase0_init.sql`
10. `src/test/java/**`

## 3. 后端定位与目标架构

根据产品与架构文档，后端定位为三类平面统一承载：

1. 控制面（Control Plane）：页面资源、规则、API 注册、发布校验与发布任务。
2. 运行面（Runtime Plane）：页面上下文识别、运行时快照下发、运行链路承接。
3. 管理与审计面（Management/Audit Plane）：待处理摘要、审计日志、触发日志、执行日志、指标汇总。

工程结构采用 Maven 标准目录形态：

1. `src/main/java`：启动类、Controller、应用服务与基础设施实现。
2. `src/main/resources`：Spring Profile 与 SQL 初始化资源。
3. `src/test/java`：集成测试与冒烟测试。

## 4. 技术栈与运行基线

### 4.1 技术栈

1. Java 17
2. Spring Boot 3.4.4
3. MyBatis-Plus 3.5.7
4. H2（默认本地）
5. MySQL（生产接入基线）
6. Maven

### 4.2 启动与环境

1. 默认 Profile：`h2`
2. MySQL Profile：`mysql`
3. 服务端口：`8080`（可通过 `SERVER_PORT` 覆盖）
4. 健康检查：`GET /healthz`
5. H2 Console：`/h2-console`

### 4.3 请求上下文约定

由 `RequestContextFilter` 统一处理以下头部（缺省值会自动补齐）：

1. `Authorization`
2. `X-Trace-Id`
3. `X-User-Id`
4. `X-Org-Id`
5. `X-Role-Ids`

响应头统一回写 `X-Trace-Id`。

## 5. 通用响应与错误模型

### 5.1 成功响应

统一结构：

```json
{
  "returnCode": "OK",
  "errorMsg": "success",
  "body": {}
}
```

### 5.2 失败响应

统一结构：

```json
{
  "returnCode": "BIZ_CODE",
  "errorMsg": "error message",
  "body": {
    "details": []
  }
}
```

异常处理入口：`GlobalExceptionHandler`。

## 6. API 能力清单（当前代码实况）

说明：以下“实现状态”按当前 Service 是否真实访问数据库区分。

- `DB`：通过 Mapper 持久化/查询。
- `DEMO`：通过 `DemoDataFactory` 返回模拟数据。

### 6.1 控制面 API

| 方法 | 路径 | 说明 | 实现状态 |
| --- | --- | --- | --- |
| GET | `/api/control/page-sites` | 站点列表 | DEMO |
| GET | `/api/control/page-menus` | 菜单列表 | DEMO |
| GET | `/api/control/page-resources` | 页面资源分页 | DEMO |
| GET | `/api/control/page-resources/{pageId}` | 页面资源详情 | DEMO |
| POST | `/api/control/page-resources` | 新建页面资源 | DEMO |
| POST | `/api/control/page-resources/{pageId}/versions` | 新建页面版本 | DEMO |
| PUT | `/api/control/page-resources/{pageId}/versions/{versionId}` | 更新页面版本 | DEMO |
| GET | `/api/control/interfaces` | API 注册分页 | DEMO |
| GET | `/api/control/interfaces/{interfaceId}` | API 注册详情 | DEMO |
| POST | `/api/control/interfaces` | 新建 API 注册 | DEMO |
| POST | `/api/control/interfaces/{interfaceId}/versions` | 新建 API 版本 | DEMO |
| PUT | `/api/control/interfaces/{interfaceId}/versions/{versionId}` | 更新 API 版本 | DEMO |
| GET | `/api/control/rules` | 规则分页 | DEMO |
| GET | `/api/control/rules/{ruleId}` | 规则详情 | DEMO |
| POST | `/api/control/rules` | 新建规则 | DEMO |
| POST | `/api/control/rules/{ruleId}/versions` | 新建规则版本 | DEMO |
| PUT | `/api/control/rules/{ruleId}/versions/{versionId}` | 更新规则版本 | DEMO |
| POST | `/api/control/rules/{ruleId}/versions/{versionId}/preview` | 规则预览 | DEMO |
| POST | `/api/control/publish/validate` | 发布前校验 | DEMO |
| POST | `/api/control/publish/tasks` | 创建发布任务 | DEMO |
| GET | `/api/control/publish/tasks/{taskId}` | 发布任务详情 | DEMO |

### 6.2 运行面 API

| 方法 | 路径 | 说明 | 实现状态 |
| --- | --- | --- | --- |
| POST | `/api/runtime/page-context/resolve` | 页面上下文识别 | DEMO |
| GET | `/api/runtime/pages/{pageId}/bundle` | 运行时快照包 | DEMO |

### 6.3 权限与治理 API

| 方法 | 路径 | 说明 | 实现状态 |
| --- | --- | --- | --- |
| GET | `/api/permissions/resources` | 资源列表 | DB |
| POST | `/api/permissions/resources` | 新建资源 | DB |
| PUT | `/api/permissions/resources/{id}` | 更新资源 | DB |
| GET | `/api/permissions/roles` | 角色列表 | DB |
| POST | `/api/permissions/roles` | 新建角色 | DB |
| PUT | `/api/permissions/roles/{id}` | 更新角色 | DB |
| GET | `/api/permissions/roles/{roleId}/resource-grants` | 角色资源授权列表 | DB |
| PUT | `/api/permissions/roles/{roleId}/resource-grants` | 覆盖角色资源授权 | DB |
| GET | `/api/permissions/roles/{roleId}/members` | 角色成员列表 | DB |
| PUT | `/api/permissions/roles/{roleId}/members` | 覆盖角色成员 | DB |
| GET | `/api/permissions/session/me` | 当前会话权限快照 | DB |
| GET | `/api/governance/platform-runtime-config` | 平台运行配置 | DB |
| PUT | `/api/governance/platform-runtime-config` | 更新平台运行配置 | DB |
| GET | `/api/governance/menu-sdk-policies` | 菜单 SDK 策略列表 | DB |
| POST | `/api/governance/menu-sdk-policies` | 新建菜单 SDK 策略 | DB |
| PUT | `/api/governance/menu-sdk-policies/{id}` | 更新菜单 SDK 策略 | DB |
| GET | `/api/governance/pending-summary` | 待处理摘要 | DEMO |
| GET | `/api/governance/audit-logs` | 审计日志 | DEMO |
| GET | `/api/governance/trigger-logs` | 触发日志 | DEMO |
| GET | `/api/governance/execution-logs` | 执行日志 | DEMO |
| GET | `/api/governance/metrics/overview` | 指标总览 | DEMO |

### 6.4 基础可用性 API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/healthz` | 健康检查 |

## 7. 数据模型与存储现状

### 7.1 已建表（H2 本地基线）

核心表包含：

1. 控制面：`page_site`、`page_menu`、`page_resource`、`page_resource_version`、`interface_definition`、`interface_version`、`rule_definition`、`rule_version`、`publish_task`。
2. 运行面：`runtime_snapshot`。
3. 权限：`permission_resource`、`cc_role`、`role_resource_grant`、`user_role_binding`。
4. 治理：`platform_runtime_config`、`menu_sdk_policy`。

### 7.2 持久化真实落地区域

已明确使用 Mapper + DB 的区域：

1. 权限资源与角色治理。
2. 菜单 SDK 策略与平台运行配置。

其余控制面/运行面主链路当前仍以模拟返回为主，尚未进入真实仓储读写。

## 8. 与目标设计的一致性与差距

### 8.1 已对齐项

1. Maven 标准目录（`src/main` + `src/test`）已成为当前运行主结构。
2. 公共基座（统一响应、异常、请求上下文、审计字段自动填充）已落地。
3. 权限资源化模型（资源/角色/授权/成员/会话快照）已落地。
4. 平台治理模型（平台稳定版本 + 菜单灰度策略）已落地。

### 8.2 关键差距

1. 控制面核心域（页面/规则/API 注册/发布）仍为 DEMO 数据，未完成业务持久化。
2. 运行面仅实现 `resolve + bundle` 入口，未覆盖数据代理、名单检索、执行实例等运行链路。
3. 管理审计面当前路径前缀为 `/api/governance/*`，与产品设计中 `/api/management/*` 存在口径差异。
4. 文档目标中的管理审计能力目前多数为模拟输出，尚未形成可运营的日志查询与指标聚合闭环。

## 9. 测试与验证结果

### 9.1 自动化测试

执行命令：`mvn test`  
执行日期：2026-03-19

结果：

1. 总计 10 个测试用例。
2. 失败 0，错误 0，跳过 0。
3. 构建结果：`BUILD SUCCESS`。

### 9.2 当前测试覆盖重点

1. 服务启动与健康检查。
2. 控制面/运行面/治理面接口可访问性冒烟。
3. 权限与治理配置关键接口（创建、更新、查询）基础行为。

## 10. 后续落地建议（按优先级）

1. 将控制面四大域（Page/Rule/API 注册/Publish）从 `DemoDataFactory` 切换为真实仓储实现。
2. 补齐运行面核心接口：数据代理、名单检索、执行实例与事件上报。
3. 统一管理审计面 API 命名与前缀，明确是否保留 `/api/governance/*` 或收敛至 `/api/management/*`。
4. 基于真实日志与指标表补齐管理审计查询链路，替换当前 DEMO 返回。
5. 按域补齐应用层与集成测试，逐步从“可访问”升级到“可验证业务正确性”。

## 11. 附录：快速启动命令

```bash
mvn spring-boot:run
```

```bash
mvn -Dspring-boot.run.profiles=h2 spring-boot:start
```

```bash
mvn -Dspring-boot.run.profiles=mysql spring-boot:run
```

```bash
mvn test
```
