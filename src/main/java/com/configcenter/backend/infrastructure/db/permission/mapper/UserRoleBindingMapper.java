package com.configcenter.backend.infrastructure.db.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.configcenter.backend.infrastructure.db.permission.model.UserRoleBindingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleBindingMapper extends BaseMapper<UserRoleBindingDO> {
}
