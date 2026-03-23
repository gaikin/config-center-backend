package com.configcenter.backend.infrastructure.db.control.page;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.control.page.model.BusinessFieldDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessFieldMapper extends BaseMapper<BusinessFieldDO> {
}

