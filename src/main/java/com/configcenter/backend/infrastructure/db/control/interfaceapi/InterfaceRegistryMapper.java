package com.configcenter.backend.infrastructure.db.control.interfaceapi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.interfaceapi.model.InterfaceDefinitionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InterfaceRegistryMapper extends BaseMapper<InterfaceDefinitionDO> {
}
