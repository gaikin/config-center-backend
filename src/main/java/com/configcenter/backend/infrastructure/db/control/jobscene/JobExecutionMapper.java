package com.configcenter.backend.infrastructure.db.control.jobscene;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobExecutionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobExecutionMapper extends BaseMapper<JobExecutionDO> {
}
