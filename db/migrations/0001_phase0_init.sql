CREATE TABLE IF NOT EXISTS page_menu (
  id BIGINT NOT NULL AUTO_INCREMENT,
  region_id VARCHAR(64) NOT NULL,
  menu_code VARCHAR(64) NULL,
  menu_name VARCHAR(128) NOT NULL,
  url_pattern VARCHAR(255) NOT NULL,
  status VARCHAR(32) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_page_menu_region_status (region_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单定义';

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_menu'
        AND COLUMN_NAME = 'region_id'
    ) THEN 'DO 0'
    ELSE 'ALTER TABLE page_menu ADD COLUMN region_id VARCHAR(64) NULL AFTER id'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_menu'
        AND COLUMN_NAME = 'site_id'
    ) THEN 'UPDATE page_menu SET region_id = CAST(site_id AS CHAR) WHERE region_id IS NULL'
    ELSE 'DO 0'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE page_menu MODIFY COLUMN region_id VARCHAR(64) NOT NULL;

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.STATISTICS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_menu'
        AND INDEX_NAME = 'idx_page_menu_region_status'
    ) THEN 'DO 0'
    ELSE 'ALTER TABLE page_menu ADD KEY idx_page_menu_region_status (region_id, status)'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_menu'
        AND COLUMN_NAME = 'site_id'
    ) THEN 'ALTER TABLE page_menu DROP COLUMN site_id'
    ELSE 'DO 0'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS page_resource (
  id BIGINT NOT NULL AUTO_INCREMENT,
  menu_code VARCHAR(64) NULL,
  business_process_code VARCHAR(64) NULL,
  page_name VARCHAR(128) NOT NULL,
  page_code VARCHAR(128) NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL,
  current_version_id BIGINT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_page_resource_menu_process (menu_code, business_process_code),
  KEY idx_page_resource_owner_status (owner_org_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面资源主表';

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_resource'
        AND COLUMN_NAME = 'menu_code'
    ) THEN 'DO 0'
    ELSE 'ALTER TABLE page_resource ADD COLUMN menu_code VARCHAR(64) NULL AFTER id'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_resource'
        AND COLUMN_NAME = 'business_process_code'
    ) THEN 'DO 0'
    ELSE 'ALTER TABLE page_resource ADD COLUMN business_process_code VARCHAR(64) NULL AFTER menu_code'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_resource'
        AND COLUMN_NAME = 'menu_id'
    ) AND EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'page_menu'
        AND COLUMN_NAME = 'menu_code'
    ) THEN 'UPDATE page_resource pr JOIN page_menu pm ON pr.menu_id = pm.id SET pr.menu_code = pm.menu_code WHERE pr.menu_code IS NULL'
    ELSE 'DO 0'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE page_resource MODIFY COLUMN menu_code VARCHAR(64) NULL;

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'page_resource'
      AND COLUMN_NAME = 'menu_id'
    ) THEN 'ALTER TABLE page_resource DROP COLUMN menu_id'
    ELSE 'DO 0'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS business_process (
  id BIGINT NOT NULL AUTO_INCREMENT,
  process_code VARCHAR(64) NOT NULL,
  process_name VARCHAR(128) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_business_process_code (process_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务流程主表';

CREATE TABLE IF NOT EXISTS interface_definition (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(255) NULL,
  method VARCHAR(16) NOT NULL,
  test_path VARCHAR(255) NULL,
  prod_path VARCHAR(255) NULL,
  path VARCHAR(255) NOT NULL,
  url VARCHAR(255) NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL,
  current_version_id BIGINT NULL,
  timeout_ms INT NULL,
  retry_times INT NULL,
  body_template_json TEXT NULL,
  input_config_json TEXT NULL,
  output_config_json TEXT NULL,
  param_source_summary TEXT NULL,
  response_path TEXT NULL,
  mask_sensitive TINYINT(1) NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_interface_owner_status (owner_org_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接口定义主表';

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'interface_definition'
        AND COLUMN_NAME = 'path'
    ) THEN 'DO 0'
    ELSE 'ALTER TABLE interface_definition ADD COLUMN path VARCHAR(255) NULL AFTER prod_path'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

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
    CONCAT('/legacy/interface/', id)
)
WHERE path IS NULL OR CHAR_LENGTH(TRIM(path)) = 0;

ALTER TABLE interface_definition MODIFY COLUMN path VARCHAR(255) NOT NULL;

CREATE TABLE IF NOT EXISTS rule_definition (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rule_name VARCHAR(128) NOT NULL,
  page_resource_id BIGINT NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  trigger_mode VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL,
  current_version_id BIGINT NULL,
  content_json JSON NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_rule_owner_status (owner_org_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则主表';

SET @sql := (
  SELECT CASE
    WHEN EXISTS (
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'rule_definition'
        AND COLUMN_NAME = 'content_json'
    ) THEN 'DO 0'
    ELSE 'ALTER TABLE rule_definition ADD COLUMN content_json JSON NULL'
  END
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS rule_condition_group (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rule_id BIGINT NOT NULL,
  logic_type VARCHAR(16) NOT NULL,
  parent_group_id BIGINT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_rule_condition_group_rule (rule_id, parent_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则条件组表';

CREATE TABLE IF NOT EXISTS rule_condition (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rule_id BIGINT NOT NULL,
  group_id BIGINT NOT NULL,
  left_json JSON NOT NULL,
  operator VARCHAR(32) NOT NULL,
  right_json JSON NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_rule_condition_group (rule_id, group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则条件表';

CREATE TABLE IF NOT EXISTS job_scene_node (
  id BIGINT NOT NULL AUTO_INCREMENT,
  scene_id BIGINT NOT NULL,
  node_type VARCHAR(32) NOT NULL,
  name VARCHAR(128) NOT NULL,
  order_no INT NOT NULL,
  enabled TINYINT(1) NOT NULL,
  config_json JSON NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_job_scene_node_scene (scene_id, order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业场景节点表';

CREATE TABLE IF NOT EXISTS job_scene (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  share_mode VARCHAR(32) NOT NULL,
  shared_org_ids_json JSON NOT NULL,
  shared_by VARCHAR(64) NULL,
  shared_at VARCHAR(64) NULL,
  source_scene_id BIGINT NULL,
  source_scene_name VARCHAR(128) NULL,
  page_resource_id BIGINT NOT NULL,
  page_resource_name VARCHAR(128) NOT NULL,
  execution_mode VARCHAR(32) NOT NULL,
  preview_before_execute TINYINT(1) NOT NULL,
  floating_button_enabled TINYINT(1) NOT NULL,
  floating_button_label VARCHAR(64) NOT NULL,
  floating_button_x INT NOT NULL,
  floating_button_y INT NOT NULL,
  status VARCHAR(32) NOT NULL,
  manual_duration_sec INT NOT NULL,
  risk_confirmed TINYINT(1) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_job_scene_page (page_resource_id),
  KEY idx_job_scene_owner (owner_org_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业场景主表';

CREATE TABLE IF NOT EXISTS job_execution (
  id BIGINT NOT NULL AUTO_INCREMENT,
  scene_id BIGINT NOT NULL,
  scene_name VARCHAR(128) NOT NULL,
  trigger_source VARCHAR(32) NOT NULL,
  result VARCHAR(32) NOT NULL,
  fallback_to_manual TINYINT(1) NOT NULL,
  detail VARCHAR(255) NOT NULL,
  started_at DATETIME(3) NOT NULL,
  finished_at DATETIME(3) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_job_execution_scene (scene_id, started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业执行记录表';

CREATE TABLE IF NOT EXISTS prompt_trigger_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rule_id BIGINT NOT NULL,
  rule_name VARCHAR(128) NOT NULL,
  page_resource_id BIGINT NOT NULL,
  page_resource_name VARCHAR(128) NOT NULL,
  org_id VARCHAR(64) NOT NULL,
  org_name VARCHAR(128) NOT NULL,
  prompt_mode VARCHAR(32) NOT NULL,
  prompt_content_summary TEXT NOT NULL,
  scene_id BIGINT NULL,
  scene_name VARCHAR(128) NULL,
  trigger_result VARCHAR(32) NOT NULL,
  reason VARCHAR(255) NOT NULL,
  trigger_at DATETIME(3) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_prompt_trigger_rule (rule_id, trigger_at),
  KEY idx_prompt_trigger_page (page_resource_id, trigger_at),
  KEY idx_prompt_trigger_org (org_id, trigger_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示触发日志表';

CREATE TABLE IF NOT EXISTS publish_operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  action VARCHAR(64) NOT NULL,
  resource_type VARCHAR(64) NOT NULL,
  resource_id BIGINT NULL,
    resource_name VARCHAR(128) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    effective_scope_type VARCHAR(32) NULL,
    effective_org_ids_json JSON NULL,
    effective_scope_summary VARCHAR(255) NULL,
    effective_start_at VARCHAR(32) NULL,
    effective_end_at VARCHAR(32) NULL,
    approval_ticket_id VARCHAR(64) NULL,
    approval_source VARCHAR(64) NULL,
    approval_status VARCHAR(32) NULL,
    create_time DATETIME(3) NOT NULL,
    created_by VARCHAR(64) NOT NULL,
    update_time DATETIME(3) NOT NULL,
    updated_by VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
  KEY idx_publish_operation_log_resource (resource_type, resource_id),
  KEY idx_publish_operation_log_action (action, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发布操作日志表';

CREATE TABLE IF NOT EXISTS data_processor (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  param_count INT NOT NULL,
  function_code LONGTEXT NOT NULL,
  status VARCHAR(32) NOT NULL,
  used_by_count INT NOT NULL DEFAULT 0,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_data_processor_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据处理函数表';

CREATE TABLE IF NOT EXISTS context_variable (
  id BIGINT NOT NULL AUTO_INCREMENT,
  variable_key VARCHAR(128) NOT NULL,
  label VARCHAR(128) NOT NULL,
  value_source VARCHAR(32) NOT NULL,
  static_value LONGTEXT NULL,
  script_content LONGTEXT NULL,
  status VARCHAR(32) NOT NULL,
  owner_org_id VARCHAR(64) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_context_variable_key (variable_key),
  KEY idx_context_variable_owner_status (owner_org_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上下文变量表';

DROP TABLE IF EXISTS page_resource_version;
DROP TABLE IF EXISTS interface_version;
DROP TABLE IF EXISTS rule_version;
DROP TABLE IF EXISTS page_site;
DROP TABLE IF EXISTS platform_runtime_config;
DROP TABLE IF EXISTS runtime_snapshot;

CREATE TABLE IF NOT EXISTS user_inf (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id VARCHAR(64) NOT NULL,
  yst_id VARCHAR(64) NULL,
  user_name VARCHAR(128) NOT NULL,
  pwd VARCHAR(255) NOT NULL,
  uuid VARCHAR(128) NULL,
  open_id VARCHAR(128) NULL,
  dpt_id VARCHAR(64) NULL,
  dpt_name VARCHAR(128) NULL,
  path_id VARCHAR(128) NULL,
  path_name VARCHAR(255) NULL,
  user_type VARCHAR(32) NULL,
  platform_user_type VARCHAR(32) NULL,
  status VARCHAR(32) NOT NULL,
  rsp_id VARCHAR(64) NULL,
  rsp_name VARCHAR(128) NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_inf_user_id (user_id),
  KEY idx_user_inf_dpt_id (dpt_id),
  KEY idx_user_inf_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

CREATE TABLE IF NOT EXISTS dpt_inf (
  id BIGINT NOT NULL AUTO_INCREMENT,
  dpt_id VARCHAR(64) NOT NULL,
  dpt_name VARCHAR(128) NOT NULL,
  prt_dpt_id VARCHAR(64) NULL,
  path_id VARCHAR(128) NULL,
  path_name VARCHAR(255) NULL,
  status VARCHAR(32) NOT NULL,
  is_leaf TINYINT(1) NOT NULL,
  modify_name VARCHAR(64) NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_dpt_inf_dpt_id (dpt_id),
  KEY idx_dpt_inf_parent (prt_dpt_id),
  KEY idx_dpt_inf_path_id (path_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门信息表';

CREATE TABLE IF NOT EXISTS permission_resource (
  id BIGINT NOT NULL AUTO_INCREMENT,
  resource_code VARCHAR(128) NOT NULL,
  resource_name VARCHAR(128) NOT NULL,
  resource_type VARCHAR(16) NOT NULL,
  resource_path VARCHAR(255) NOT NULL,
  page_path VARCHAR(255),
  status VARCHAR(32) NOT NULL,
  order_no INT NOT NULL,
  description VARCHAR(255),
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY idx_permission_resource_code (resource_code),
  UNIQUE KEY idx_permission_resource_path (resource_path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限资源表';

CREATE TABLE IF NOT EXISTS cc_role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  role_type VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL,
  org_scope_id VARCHAR(64) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色定义表';

CREATE TABLE IF NOT EXISTS role_resource_grant (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  resource_code VARCHAR(128) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色资源授权表';

CREATE TABLE IF NOT EXISTS user_role_binding (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id VARCHAR(64) NOT NULL,
  role_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色绑定表';

CREATE TABLE IF NOT EXISTS general_config_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  group_key VARCHAR(64) NOT NULL,
  item_key VARCHAR(128) NOT NULL,
  item_value LONGTEXT NOT NULL,
  description VARCHAR(255) NULL,
  status VARCHAR(32) NOT NULL,
  order_no INT NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_general_config_item_group_key_item_key (group_key, item_key),
  KEY idx_general_config_item_group_status (group_key, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用配置项表';

CREATE TABLE IF NOT EXISTS menu_sdk_policy (
  id BIGINT NOT NULL AUTO_INCREMENT,
  menu_code VARCHAR(64) NOT NULL,
  menu_name VARCHAR(128) NOT NULL,
  prompt_gray_enabled TINYINT NOT NULL,
  prompt_gray_version VARCHAR(64),
  prompt_gray_org_ids_json JSON,
  job_gray_enabled TINYINT NOT NULL,
  job_gray_version VARCHAR(64),
  job_gray_org_ids_json JSON,
  effective_start DATETIME(3) NOT NULL,
  effective_end DATETIME(3) NOT NULL,
  status VARCHAR(32) NOT NULL,
  create_time DATETIME(3) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  update_time DATETIME(3) NOT NULL,
  updated_by VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单 SDK 策略表';


