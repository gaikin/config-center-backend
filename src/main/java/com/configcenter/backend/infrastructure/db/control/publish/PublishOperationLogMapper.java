package com.configcenter.backend.infrastructure.db.control.publish;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.publish.model.PublishOperationLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PublishOperationLogMapper extends BaseMapper<PublishOperationLogDO> {
}
