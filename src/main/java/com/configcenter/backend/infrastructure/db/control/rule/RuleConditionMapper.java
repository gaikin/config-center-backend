package com.configcenter.backend.infrastructure.db.control.rule;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.rule.model.RuleConditionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuleConditionMapper extends BaseMapper<RuleConditionDO> {
}
