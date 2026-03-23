package com.configcenter.backend.infrastructure.db.control.rule;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.rule.model.RuleDefinitionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuleMapper extends BaseMapper<RuleDefinitionDO> {
}
