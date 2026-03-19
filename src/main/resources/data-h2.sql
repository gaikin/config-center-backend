INSERT INTO page_site (id, site_code, name, status, remark, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (1, 'crm', 'CRM', 'ACTIVE', 'H2 seed', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO page_menu (id, site_id, menu_code, menu_name, url_pattern, status, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (1, 1, 'loan-apply', 'Loan Apply', '/loan/apply', 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO page_resource (id, site_id, menu_id, page_name, page_code, owner_org_id, status, current_version_id, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (100, 1, 1, 'Loan Apply Page', 'loan.apply.page', 'org.demo', 'ACTIVE', 1000, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO page_resource_version (id, page_resource_id, version_no, status, content_json, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (1000, 100, 1, 'ACTIVE', '{"pageTitle":"Loan Apply","urlPattern":"/loan/apply"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO interface_definition (id, name, method, path, owner_org_id, status, current_version_id, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (200, 'Customer Profile API', 'POST', '/internal/customer/profile', 'org.demo', 'ACTIVE', 2000, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO interface_version (id, interface_id, version_no, status, content_json, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (2000, 200, 1, 'ACTIVE', '{"authStrategy":"INTRANET_SESSION"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO rule_definition (id, rule_name, page_resource_id, owner_org_id, trigger_mode, status, current_version_id, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (300, 'Large Amount Prompt', 100, 'org.demo', 'AUTO', 'ACTIVE', 3000, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO rule_version (id, rule_id, version_no, status, content_json, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (3000, 300, 1, 'ACTIVE', '{"conditionLogic":"AND"}', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO publish_task (id, resource_type, resource_id, version_id, publish_type, status, scope_org_ids_json, effective_at, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (4001, 'PAGE_RESOURCE', 100, 1000, 'IMMEDIATE', 'SUCCEEDED', '["org.demo"]', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO runtime_snapshot (id, page_resource_id, page_version_id, owner_org_id, snapshot_version, snapshot_json, status, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (5001, 100, 1000, 'org.demo', 'snapshot-1', '{"manifest":{"pageId":100}}', 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO permission_resource (id, resource_code, resource_name, resource_type, resource_path, page_path, status, order_no, description, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES
(91001, 'menu_dashboard', '看板菜单', 'MENU', '/menu/dashboard', NULL, 'ACTIVE', 10, 'dashboard menu', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0),
(91002, 'page_dashboard_list', '看板页面', 'PAGE', '/page/dashboard/list', '/dashboard', 'ACTIVE', 20, 'dashboard page', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0),
(91003, 'action_roles_manage', '角色管理动作', 'ACTION', '/action/roles/list/manage', NULL, 'ACTIVE', 30, 'manage roles', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO cc_role (id, name, role_type, status, org_scope_id, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES
(7002, '总行权限管理员', 'PERMISSION_ADMIN', 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0),
(7003, '总行配置管理员', 'CONFIG_OPERATOR', 'ACTIVE', 'head-office', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO role_resource_grant (id, role_id, resource_code, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES
(92001, 7003, 'menu_dashboard', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0),
(92002, 7003, 'page_dashboard_list', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0),
(92003, 7002, 'action_roles_manage', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO user_role_binding (id, user_id, role_id, status, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES
(93001, 'person-head-admin-a', 7003, 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0),
(93002, 'person-head-admin-a', 7002, 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO platform_runtime_config (id, prompt_stable_version, prompt_gray_default_version, job_stable_version, job_gray_default_version, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (1, '1.3.0', '1.3.1-rc1', '2.1.0', '2.1.1-rc1', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);

INSERT INTO menu_sdk_policy (id, menu_code, menu_name, prompt_gray_enabled, prompt_gray_version, prompt_gray_org_ids_json, job_gray_enabled, job_gray_version, job_gray_org_ids_json, effective_start, effective_end, status, created_at, created_by, updated_at, updated_by, is_deleted)
VALUES (94001, 'dashboard', '看板', 1, '1.3.1-rc1', '["head-office"]', 0, NULL, '[]', CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), 'ACTIVE', CURRENT_TIMESTAMP, 'system.seed', CURRENT_TIMESTAMP, 'system.seed', 0);
