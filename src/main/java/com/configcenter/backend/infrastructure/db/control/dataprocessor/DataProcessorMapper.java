package com.configcenter.backend.infrastructure.db.control.dataprocessor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.dataprocessor.model.DataProcessorDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataProcessorMapper extends BaseMapper<DataProcessorDO> {
}
