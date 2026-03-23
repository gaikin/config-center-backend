package com.configcenter.backend.infrastructure.db.control.jobscene;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobSceneNodeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobSceneNodeMapper extends BaseMapper<JobSceneNodeDO> {
}
