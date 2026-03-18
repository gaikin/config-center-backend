package com.configcenter.backend.infrastructure.db.governance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.governance.model.PlatformRuntimeConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlatformRuntimeConfigMapper extends BaseMapper<PlatformRuntimeConfigDO> {
}
