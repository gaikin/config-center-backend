package com.configcenter.backend.infrastructure.db.control.contextvariable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.contextvariable.model.ContextVariableDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContextVariableMapper extends BaseMapper<ContextVariableDO> {
}
