INSERT INTO page_menu (id, region_id, menu_code, menu_name, url_pattern, status, create_time, created_by, update_time, updated_by)
VALUES (1, 'trade', 'loan-apply', 'Loan Apply', '/loan/apply', 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO page_menu (id, region_id, menu_code, menu_name, url_pattern, status, create_time, created_by, update_time, updated_by)
VALUES
(2, 'business', 'loan-process', 'Loan Process', '/business/loan-process', 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(3, 'business', 'manual-review', 'Manual Review', '/business/manual-review', 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO page_resource (id, menu_code, page_name, page_code, owner_org_id, status, current_version_id, create_time, created_by, update_time, updated_by)
VALUES
(100, 'loan-process', 'Loan Apply Page', 'loan.apply.page', '100001', 'ACTIVE', 1, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(101, 'manual-review', 'Loan Review Page', 'loan.review.page', '100001', 'ACTIVE', 1, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO page_element (id, page_resource_id, logic_name, selector, selector_type, frame_location, create_time, created_by, update_time, updated_by)
VALUES
(6001, 100, 'customer_name', '//*[@id="customer_name"]', 'XPATH', 'main-frame', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(6002, 100, 'risk_level', '#risk-level', 'CSS', 'main-frame', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO business_field (id, code, name, scope, page_resource_id, value_type, required, description, owner_org_id, status, current_version, aliases_json, create_time, created_by, update_time, updated_by)
VALUES
(6101, 'field_customer_name', '客户姓名', 'GLOBAL', NULL, 'STRING', TRUE, '用于客户身份信息展示', 'head-office', 'ACTIVE', 1, '["customer_name"]', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(6102, 'field_risk_level', '风险等级', 'PAGE_RESOURCE', 100, 'STRING', FALSE, '页面风险等级字段', '100001', 'DRAFT', 1, '["risk_level"]', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO page_field_binding (id, page_resource_id, business_field_code, page_element_id, required, create_time, created_by, update_time, updated_by)
VALUES
(6201, 100, 'field_customer_name', 6001, TRUE, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO interface_definition (
  id, name, description, method, test_path, prod_path, path, url, owner_org_id, status, current_version_id,
  timeout_ms, retry_times, body_template_json, input_config_json, output_config_json, param_source_summary,
  response_path, mask_sensitive, create_time, created_by, update_time, updated_by
)
VALUES (
  200,
  'Customer Profile API',
  '查询客户资料',
  'POST',
  '/internal/customer/profile/test',
  '/internal/customer/profile',
  '/internal/customer/profile',
  '/internal/customer/profile',
  '100001',
  'ACTIVE',
  1,
  3000,
  0,
  '{"customerId":"1001"}',
  '{"headers":[],"query":[],"path":[],"body":[]}',
  '[{"id":"output-1","name":"customerId","path":"$.data.customerId","pathMode":"AUTO","description":"客户编号","valueType":"STRING","children":[]}]',
  'Header 0 / Query 0 / Path 0 / Body 0',
  '$.data.customerId',
  1,
  CURRENT_TIMESTAMP,
  'system.seed',
  CURRENT_TIMESTAMP,
  'system.seed'
);

UPDATE interface_definition
SET
  name = 'Customer Profile API',
  description = '查询客户资料',
  method = 'POST',
  test_path = '/internal/customer/profile/test',
  prod_path = '/internal/customer/profile',
  path = '/internal/customer/profile',
  url = '/internal/customer/profile',
  owner_org_id = '100001',
  status = 'ACTIVE',
  current_version_id = 1,
  timeout_ms = 3000,
  retry_times = 0,
  body_template_json = '',
  input_config_json = '{}',
  output_config_json = '[]',
  param_source_summary = 'Header 0 / Query 0 / Path 0 / Body 0',
  response_path = '$.data.customerId',
  mask_sensitive = 1
WHERE id = 200;

INSERT INTO rule_definition (id, rule_name, page_resource_id, owner_org_id, trigger_mode, status, current_version_id, content_json, create_time, created_by, update_time, updated_by)
VALUES (300, 'Large Amount Prompt', 100, '100001', 'AUTO', 'ACTIVE', 3000, '{"id":300,"name":"Large Amount Prompt","ruleScope":"PAGE_RESOURCE","ruleSetCode":"loan_high_risk_prompt","pageResourceId":100,"pageResourceName":"Loan Apply Page","shareMode":"PRIVATE","sharedOrgIds":[],"priority":950,"promptMode":"FLOATING","closeMode":"MANUAL_CLOSE","promptContentConfigJson":"{\\\"version\\\":1,\\\"titleSuffix\\\":\\\"贷款高风险客户\\\",\\\"bodyTemplate\\\":\\\"检测到客户 {{customer_id}} 风险等级为 {{riskLevel}}，请核对证件信息并确认是否继续办理。\\\"}","hasConfirmButton":true,"sceneId":9001,"sceneName":"贷款申请自动查数预填","effectiveStartAt":"2026-03-01 00:00","effectiveEndAt":"2026-12-31 23:59","status":"ACTIVE","currentVersion":1,"ownerOrgId":"100001","listLookupConditions":[],"updatedAt":"2026-03-22T00:00:00"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO rule_condition_group (id, rule_id, logic_type, parent_group_id, create_time, created_by, update_time, updated_by)
VALUES (301, 300, 'AND', NULL, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO rule_condition (id, rule_id, group_id, left_json, operator, right_json, create_time, created_by, update_time, updated_by)
VALUES (401, 300, 301, '{"sourceType":"PAGE_FIELD","fieldKey":"risk_level","selector":"#risk-level"}', 'EQ', '{"sourceType":"CONST","constValue":"HIGH"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO data_processor (id, name, param_count, function_code, status, used_by_count, create_time, created_by, update_time, updated_by)
VALUES
(3511, '字符串去空格', 1, 'function transform(input) { return String(input ?? '''').trim(); }', 'ACTIVE', 22, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(3512, '日期转 yyyy-MM-dd', 1, 'function transform(input) { const d = new Date(String(input ?? '''')); return Number.isNaN(d.getTime()) ? '''' : d.toISOString().slice(0, 10); }', 'ACTIVE', 14, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(3513, '手机号脱敏', 1, 'function transform(input) { const digits = String(input ?? '''').replace(/\\D/g, ''''); return digits.length >= 7 ? `${digits.slice(0, 3)}****${digits.slice(-4)}` : String(input ?? ''''); }', 'DRAFT', 4, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO context_variable (id, variable_key, label, value_source, static_value, script_content, status, owner_org_id, create_time, created_by, update_time, updated_by)
VALUES
(3601, 'org_id', '机构ID', 'STATIC', 'branch-east', NULL, 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(3602, 'operator_role', '操作员角色', 'SCRIPT', NULL, 'return context.operator_role || context.user_role || ''客户经理'';', 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(3603, 'channel', '办理渠道', 'SCRIPT', NULL, 'return context.channel || ''柜面'';', 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(3604, 'user_role', '用户角色', 'STATIC', '新员工', NULL, 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO publish_operation_log (id, action, resource_type, resource_id, resource_name, operator, effective_scope_type, effective_org_ids_json, effective_scope_summary, approval_ticket_id, approval_source, approval_status, create_time, created_by, update_time, updated_by)
VALUES (4001, 'PUBLISH', 'PAGE_RESOURCE', 100, 'Loan Apply Page', 'system.seed', 'ALL_ORGS', '["100001"]', '全部机构', 'APPR-DEFAULT', 'SYSTEM', 'PRE_APPROVED', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO job_scene_node (id, scene_id, node_type, name, order_no, enabled, config_json, create_time, created_by, update_time, updated_by)
VALUES
(90011, 9001, 'page_get', 'Page Get 1', 1, TRUE, '{"field":"customer_name"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(90012, 9001, 'api_call', 'API Call 1', 2, TRUE, '{"schemaVersion":2,"interfaceId":200,"inputBindings":{},"outputPaths":["$.data.score"]}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(90013, 9001, 'js_script', 'Script Node 1', 3, TRUE, '{"schemaVersion":2,"scriptId":"risk_transform","scriptCode":"return { scoreTag: String(input.riskRef) + ''_HIGH'' };","inputBindings":{"riskRef":{"sourceType":"REFERENCE","value":"{{node_90012_score}}"}},"outputKeys":["scoreTag"],"script":"risk_transform"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(90014, 9001, 'page_set', 'Page Set 1', 4, TRUE, '{"target":"risk_level","valueType":"REFERENCE","value":"{{node_90013_scoretag}}"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO job_scene (
  id, name, owner_org_id, share_mode, shared_org_ids_json, shared_by, shared_at, source_scene_id, source_scene_name,
  page_resource_id, page_resource_name, execution_mode, preview_before_execute, floating_button_enabled,
  floating_button_label, floating_button_x, floating_button_y, status, manual_duration_sec, risk_confirmed,
  create_time, created_by, update_time, updated_by
)
VALUES (
  9001, 'Loan Apply Assistant', '100001', 'PRIVATE', '[]', 'system.seed', CURRENT_TIMESTAMP, NULL, NULL,
  100, 'Loan Apply Page', 'AUTO_AFTER_PROMPT', FALSE, FALSE,
  '重新执行', 86, 78, 'ACTIVE', 30, TRUE,
  CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'
);

INSERT INTO job_execution (id, scene_id, scene_name, trigger_source, result, fallback_to_manual, detail, started_at, finished_at, create_time, created_by, update_time, updated_by)
VALUES (91001, 9001, '脚本链路测试', 'MANUAL_RETRY', 'SUCCESS', FALSE, '全部节点执行成功', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO prompt_trigger_log (
  id, rule_id, rule_name, page_resource_id, page_resource_name, org_id, org_name, prompt_mode,
  prompt_content_summary, scene_id, scene_name, trigger_result, reason, trigger_at,
  create_time, created_by, update_time, updated_by
)
VALUES
(93001, 300, 'Large Amount Prompt', 100, 'Loan Apply Page', '100001', '演示机构', 'FLOATING', '检测到客户风险等级较高，请确认是否继续办理。', 9001, '贷款申请自动查数预填', 'HIT', '风险评分 >= 80', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(93002, 300, 'Large Amount Prompt', 100, 'Loan Apply Page', '100001', '演示机构', 'FLOATING', '检测到客户风险等级较高，请确认是否继续办理。', 9001, '贷款申请自动查数预填', 'MISS', '未满足风险阈值', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(93003, 301, '开户完整性提醒', 100, 'Loan Apply Page', 'branch-south', '南分行', 'SILENT', '开户资料缺少必填项，请补充。', 9001, '贷款申请自动查数预填', 'FAILED', '页面元素缺失', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

UPDATE page_resource
SET owner_org_id = '100001'
WHERE id IN (100, 101);

UPDATE business_field
SET owner_org_id = '100001'
WHERE id = 6102;

UPDATE interface_definition
SET owner_org_id = '100001'
WHERE id = 200;

UPDATE rule_definition
SET owner_org_id = '100001',
    content_json = REPLACE(content_json, '"ownerOrgId":"org.demo"', '"ownerOrgId":"100001"')
WHERE id = 300;

UPDATE rule_condition
SET left_json = '{"sourceType":"PAGE_FIELD","fieldKey":"risk_level","selector":"#risk-level"}',
    operator = 'EQ',
    right_json = '{"sourceType":"CONST","constValue":"HIGH"}'
WHERE id = 401;

UPDATE publish_operation_log
SET effective_org_ids_json = '["100001"]'
WHERE id = 4001;

UPDATE job_scene
SET owner_org_id = '100001'
WHERE id = 9001;

UPDATE prompt_trigger_log
SET org_id = '100001',
    org_name = '演示机构'
WHERE id IN (93001, 93002);

INSERT INTO permission_resource (id, resource_code, resource_name, resource_type, resource_path, page_path, status, order_no, description, create_time, created_by, update_time, updated_by)
VALUES
(91001, 'menu_dashboard', '看板菜单', 'MENU', '/menu/dashboard', NULL, 'ACTIVE', 10, 'dashboard menu', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(91002, 'page_dashboard_list', '看板页面', 'PAGE', '/page/dashboard/list', '/dashboard', 'ACTIVE', 20, 'dashboard page', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(91003, 'action_roles_manage', '角色管理动作', 'ACTION', '/action/roles/list/manage', NULL, 'ACTIVE', 30, 'manage roles', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO cc_role (id, name, role_type, status, org_scope_id, create_time, created_by, update_time, updated_by)
VALUES
(7002, '总行权限管理员', 'PERMISSION_ADMIN', 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(7003, '总行配置管理员', 'CONFIG_OPERATOR', 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO role_resource_grant (id, role_id, resource_code, create_time, created_by, update_time, updated_by)
VALUES
(92001, 7003, 'menu_dashboard', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(92002, 7003, 'page_dashboard_list', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(92003, 7002, 'action_roles_manage', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO user_role_binding (id, user_id, role_id, status, create_time, created_by, update_time, updated_by)
VALUES
(93001, 'person-head-admin-a', 7003, 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
(93002, 'person-head-admin-a', 7002, 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');


INSERT INTO general_config_item (id, group_key, item_key, item_value, description, status, order_no, create_time, created_by, update_time, updated_by)
VALUES
  (1, 'platform-runtime', 'promptStableVersion', '1.3.0', '智能提示正式版本', 'ACTIVE', 1, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
  (2, 'platform-runtime', 'promptGrayDefaultVersion', '1.3.1-rc1', '智能提示默认灰度版本', 'ACTIVE', 2, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
  (3, 'platform-runtime', 'jobStableVersion', '2.1.0', '智能作业正式版本', 'ACTIVE', 3, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
  (4, 'platform-runtime', 'jobGrayDefaultVersion', '2.1.1-rc1', '智能作业默认灰度版本', 'ACTIVE', 4, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
  (5, 'region', 'trade', '交易中心', '交易中心专区', 'ACTIVE', 1, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
  (6, 'region', 'task', '任务中心', '任务中心专区', 'ACTIVE', 2, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
  (7, 'region', 'manage', '管理中心', '管理中心专区', 'ACTIVE', 3, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed'),
  (8, 'region', 'business', '业务入口', '业务入口专区', 'ACTIVE', 4, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');

INSERT INTO menu_sdk_policy (id, menu_code, menu_name, prompt_gray_enabled, prompt_gray_version, prompt_gray_org_ids_json, job_gray_enabled, job_gray_version, job_gray_org_ids_json, effective_start, effective_end, status, create_time, created_by, update_time, updated_by)
VALUES (94001, 'dashboard', '看板', 1, '1.3.1-rc1', '["head-office"]', 0, NULL, '[]', CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed');
