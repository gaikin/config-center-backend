package com.configcenter.backend.infrastructure.db.runtime.record;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.runtime.record.model.PromptTriggerLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromptTriggerLogMapper extends BaseMapper<PromptTriggerLogDO> {
}
