package com.configcenter.backend.infrastructure.db.control.publish;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PublishTaskMapper {

    Map<String, Object> selectPublishTaskById(@Param("taskId") Long taskId);
}
