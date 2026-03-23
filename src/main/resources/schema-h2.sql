CREATE TABLE IF NOT EXISTS page_menu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  region_id VARCHAR(64) NOT NULL,
  menu_code VARCHAR(64) NOT NULL,
  menu_name VARCHAR(128) NOT NULL,
  url_pattern VARCHAR(255) NOT NULL,
  status VARCHAR(32) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  UNIQUE KEY uk_page_menu_menu_code (menu_code)
);

CREATE TABLE IF NOT EXISTS page_resource (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  menu_code VARCHAR(64) NOT NULL,
  page_name VARCHAR(128) NOT NULL,
  page_code VARCHAR(128) NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL,
  current_version_id BIGINT,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  KEY idx_page_resource_menu_code (menu_code),
  KEY idx_page_resource_owner_status (owner_org_id, status)
);

CREATE TABLE IF NOT EXISTS page_element (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  page_resource_id BIGINT NOT NULL,
  logic_name VARCHAR(128) NOT NULL,
  selector VARCHAR(1024) NOT NULL,
  selector_type VARCHAR(16) NOT NULL,
  frame_location VARCHAR(255) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  KEY idx_page_element_page (page_resource_id)
);

CREATE TABLE IF NOT EXISTS business_field (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(128) NOT NULL,
  name VARCHAR(128) NOT NULL,
  scope VARCHAR(32) NOT NULL,
  page_resource_id BIGINT,
  value_type VARCHAR(32) NOT NULL,
  required BOOLEAN NOT NULL DEFAULT FALSE,
  description CLOB,
  owner_org_id VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL,
  current_version INT NOT NULL DEFAULT 1,
  aliases_json CLOB,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  UNIQUE KEY uk_business_field_code (code),
  KEY idx_business_field_scope_page (scope, page_resource_id)
);

CREATE TABLE IF NOT EXISTS page_field_binding (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  page_resource_id BIGINT NOT NULL,
  business_field_code VARCHAR(128) NOT NULL,
  page_element_id BIGINT NOT NULL,
  required BOOLEAN NOT NULL DEFAULT FALSE,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  KEY idx_page_field_binding_page (page_resource_id),
  KEY idx_page_field_binding_element (page_element_id)
);

CREATE TABLE IF NOT EXISTS interface_definition (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(255),
  method VARCHAR(16) NOT NULL,
  test_path VARCHAR(255),
  prod_path VARCHAR(255),
  path VARCHAR(255) NOT NULL,
  url VARCHAR(255),
  owner_org_id VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL,
  current_version_id BIGINT,
  timeout_ms INT,
  retry_times INT,
  body_template_json CLOB,
  input_config_json CLOB,
  output_config_json CLOB,
  param_source_summary CLOB,
  response_path CLOB,
  mask_sensitive BOOLEAN,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);
ALTER TABLE interface_definition ADD COLUMN IF NOT EXISTS path VARCHAR(255);
UPDATE interface_definition
SET path = TRIM(prod_path)
WHERE prod_path IS NOT NULL
  AND CHAR_LENGTH(TRIM(prod_path)) > 0
  AND (path IS NULL OR CHAR_LENGTH(TRIM(path)) = 0 OR TRIM(path) <> TRIM(prod_path));
UPDATE interface_definition
SET path = COALESCE(
    NULLIF(TRIM(path), ''),
    NULLIF(TRIM(prod_path), ''),
    NULLIF(TRIM(url), ''),
    NULLIF(TRIM(test_path), ''),
    CONCAT('/legacy/interface/', CAST(id AS VARCHAR))
)
WHERE path IS NULL OR CHAR_LENGTH(TRIM(path)) = 0;
ALTER TABLE interface_definition ALTER COLUMN path SET NOT NULL;

CREATE TABLE IF NOT EXISTS rule_definition (
  id BIGINT PRIMARY KEY,
  rule_name VARCHAR(128) NOT NULL,
  page_resource_id BIGINT NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  trigger_mode VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL,
  current_version_id BIGINT,
  content_json CLOB,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

ALTER TABLE rule_definition ADD COLUMN IF NOT EXISTS content_json CLOB;

CREATE TABLE IF NOT EXISTS rule_condition_group (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rule_id BIGINT NOT NULL,
  logic_type VARCHAR(16) NOT NULL,
  parent_group_id BIGINT,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS rule_condition (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rule_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  left_json CLOB NOT NULL,
  operator VARCHAR(32) NOT NULL,
  right_json CLOB,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS job_scene_node (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  scene_id BIGINT NOT NULL,
  node_type VARCHAR(32) NOT NULL,
  name VARCHAR(128) NOT NULL,
  order_no INT NOT NULL,
  enabled BOOLEAN NOT NULL,
  config_json CLOB NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS job_scene (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  share_mode VARCHAR(32) NOT NULL,
  shared_org_ids_json CLOB NOT NULL,
  shared_by VARCHAR(64),
  shared_at VARCHAR(64),
  source_scene_id BIGINT,
  source_scene_name VARCHAR(128),
  page_resource_id BIGINT NOT NULL,
  page_resource_name VARCHAR(128) NOT NULL,
  execution_mode VARCHAR(32) NOT NULL,
  preview_before_execute BOOLEAN NOT NULL,
  floating_button_enabled BOOLEAN NOT NULL,
  floating_button_label VARCHAR(64) NOT NULL,
  floating_button_x INT NOT NULL,
  floating_button_y INT NOT NULL,
  status VARCHAR(32) NOT NULL,
  manual_duration_sec INT NOT NULL,
  risk_confirmed BOOLEAN NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS job_execution (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  scene_id BIGINT NOT NULL,
  scene_name VARCHAR(128) NOT NULL,
  trigger_source VARCHAR(32) NOT NULL,
  result VARCHAR(32) NOT NULL,
  fallback_to_manual BOOLEAN NOT NULL,
  detail VARCHAR(255) NOT NULL,
  started_at TIMESTAMP NOT NULL,
  finished_at TIMESTAMP NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS prompt_trigger_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rule_id BIGINT NOT NULL,
  rule_name VARCHAR(128) NOT NULL,
  page_resource_id BIGINT NOT NULL,
  page_resource_name VARCHAR(128) NOT NULL,
  org_id VARCHAR(64) NOT NULL,
  org_name VARCHAR(128) NOT NULL,
  prompt_mode VARCHAR(32) NOT NULL,
  prompt_content_summary CLOB NOT NULL,
  scene_id BIGINT,
  scene_name VARCHAR(128),
  trigger_result VARCHAR(32) NOT NULL,
  reason VARCHAR(255) NOT NULL,
  trigger_at TIMESTAMP NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS publish_operation_log (
  id BIGINT PRIMARY KEY,
  action VARCHAR(64) NOT NULL,
  resource_type VARCHAR(64) NOT NULL,
  resource_id BIGINT,
  resource_name VARCHAR(128) NOT NULL,
  operator VARCHAR(64) NOT NULL,
  effective_scope_type VARCHAR(32),
  effective_org_ids_json CLOB,
  effective_scope_summary VARCHAR(255),
  effective_start_at VARCHAR(32),
  effective_end_at VARCHAR(32),
  approval_ticket_id VARCHAR(64),
  approval_source VARCHAR(64),
  approval_status VARCHAR(32),
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS data_processor (
  id BIGINT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  param_count INT NOT NULL,
  function_code CLOB NOT NULL,
  status VARCHAR(32) NOT NULL,
  used_by_count INT NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS context_variable (
  id BIGINT PRIMARY KEY,
  variable_key VARCHAR(128) NOT NULL,
  label VARCHAR(128) NOT NULL,
  value_source VARCHAR(32) NOT NULL,
  static_value CLOB,
  script_content CLOB,
  status VARCHAR(32) NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  CONSTRAINT uk_context_variable_key UNIQUE (variable_key)
);

DROP TABLE IF EXISTS page_resource_version;
DROP TABLE IF EXISTS interface_version;
DROP TABLE IF EXISTS rule_version;
DROP TABLE IF EXISTS page_site;
DROP TABLE IF EXISTS platform_runtime_config;
DROP TABLE IF EXISTS runtime_snapshot;

CREATE TABLE IF NOT EXISTS permission_resource (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  resource_code VARCHAR(128) NOT NULL,
  resource_name VARCHAR(128) NOT NULL,
  resource_type VARCHAR(16) NOT NULL,
  resource_path VARCHAR(255) NOT NULL,
  page_path VARCHAR(255),
  status VARCHAR(32) NOT NULL,
  order_no INT NOT NULL,
  description VARCHAR(255),
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_permission_resource_code ON permission_resource(resource_code);
CREATE UNIQUE INDEX IF NOT EXISTS idx_permission_resource_path ON permission_resource(resource_path);

CREATE TABLE IF NOT EXISTS cc_role (
  id BIGINT PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  role_type VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL,
  org_scope_id VARCHAR(64) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS role_resource_grant (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_id BIGINT NOT NULL,
  resource_code VARCHAR(128) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_role_binding (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  role_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS general_config_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  group_key VARCHAR(64) NOT NULL,
  item_key VARCHAR(128) NOT NULL,
  item_value LONGTEXT NOT NULL,
  description VARCHAR(255),
  status VARCHAR(32) NOT NULL,
  order_no INT NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  UNIQUE KEY uk_general_config_item_group_key_item_key (group_key, item_key),
  KEY idx_general_config_item_group_status (group_key, status)
);

CREATE TABLE IF NOT EXISTS menu_sdk_policy (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  menu_code VARCHAR(64) NOT NULL,
  menu_name VARCHAR(128) NOT NULL,
  prompt_gray_enabled TINYINT NOT NULL,
  prompt_gray_version VARCHAR(64),
  prompt_gray_org_ids_json CLOB,
  job_gray_enabled TINYINT NOT NULL,
  job_gray_version VARCHAR(64),
  job_gray_org_ids_json CLOB,
  effective_start TIMESTAMP NOT NULL,
  effective_end TIMESTAMP NOT NULL,
  status VARCHAR(32) NOT NULL,
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_inf (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  yst_id VARCHAR(64),
  user_name VARCHAR(128) NOT NULL,
  pwd VARCHAR(255) NOT NULL,
  uuid VARCHAR(128),
  open_id VARCHAR(128),
  dpt_id VARCHAR(64),
  dpt_name VARCHAR(128),
  path_id VARCHAR(128),
  path_name VARCHAR(255),
  user_type VARCHAR(32),
  platform_user_type VARCHAR(32),
  status VARCHAR(32) NOT NULL,
  rsp_id VARCHAR(64),
  rsp_name VARCHAR(128),
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_inf_user_id ON user_inf(user_id);
CREATE INDEX IF NOT EXISTS idx_user_inf_dpt_id ON user_inf(dpt_id);
CREATE INDEX IF NOT EXISTS idx_user_inf_status ON user_inf(status);

CREATE TABLE IF NOT EXISTS dpt_inf (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dpt_id VARCHAR(64) NOT NULL,
  dpt_name VARCHAR(128) NOT NULL,
  prt_dpt_id VARCHAR(64),
  path_id VARCHAR(128),
  path_name VARCHAR(255),
  status VARCHAR(32) NOT NULL,
  is_leaf TINYINT NOT NULL,
  modify_name VARCHAR(64),
  create_time TIMESTAMP NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time TIMESTAMP NOT NULL,
  updated_by VARCHAR(64) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dpt_inf_dpt_id ON dpt_inf(dpt_id);
CREATE INDEX IF NOT EXISTS idx_dpt_inf_parent ON dpt_inf(prt_dpt_id);
CREATE INDEX IF NOT EXISTS idx_dpt_inf_path_id ON dpt_inf(path_id);
