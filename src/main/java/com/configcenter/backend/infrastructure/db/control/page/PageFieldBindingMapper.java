package com.configcenter.backend.infrastructure.db.control.page;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.page.model.PageFieldBindingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PageFieldBindingMapper extends BaseMapper<PageFieldBindingDO> {
}

