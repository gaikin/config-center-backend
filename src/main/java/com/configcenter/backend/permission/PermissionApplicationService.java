package com.configcenter.backend.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.configcenter.backend.common.context.RequestContextHolder;
import com.configcenter.backend.infrastructure.db.permission.mapper.PermissionResourceMapper;
import com.configcenter.backend.infrastructure.db.permission.mapper.RoleMapper;
import com.configcenter.backend.infrastructure.db.permission.mapper.RoleResourceGrantMapper;
import com.configcenter.backend.infrastructure.db.permission.mapper.UserRoleBindingMapper;
import com.configcenter.backend.infrastructure.db.permission.model.PermissionResourceDO;
import com.configcenter.backend.infrastructure.db.permission.model.RoleDO;
import com.configcenter.backend.infrastructure.db.permission.model.RoleResourceGrantDO;
import com.configcenter.backend.infrastructure.db.permission.model.UserRoleBindingDO;
import com.configcenter.backend.permission.dto.PermissionResourceUpsertRequest;
import com.configcenter.backend.permission.dto.PermissionResourceView;
import com.configcenter.backend.permission.dto.RoleResourceGrantView;
import com.configcenter.backend.permission.dto.RoleUpdateCountView;
import com.configcenter.backend.permission.dto.RoleUpsertRequest;
import com.configcenter.backend.permission.dto.RoleView;
import com.configcenter.backend.permission.dto.SessionMeView;
import com.configcenter.backend.permission.dto.UserRoleBindingView;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PermissionApplicationService {

    private final PermissionResourceMapper permissionResourceMapper;
    private final RoleMapper roleMapper;
    private final RoleResourceGrantMapper roleResourceGrantMapper;
    private final UserRoleBindingMapper userRoleBindingMapper;

    public PermissionApplicationService(
            PermissionResourceMapper permissionResourceMapper,
            RoleMapper roleMapper,
            RoleResourceGrantMapper roleResourceGrantMapper,
            UserRoleBindingMapper userRoleBindingMapper
    ) {
        this.permissionResourceMapper = permissionResourceMapper;
        this.roleMapper = roleMapper;
        this.roleResourceGrantMapper = roleResourceGrantMapper;
        this.userRoleBindingMapper = userRoleBindingMapper;
    }

    public List<PermissionResourceView> listResources() {
        return permissionResourceMapper.selectList(new LambdaQueryWrapper<PermissionResourceDO>()
                        .orderByAsc(PermissionResourceDO::getOrderNo, PermissionResourceDO::getId))
                .stream()
                .map(this::toResourceView)
                .toList();
    }

    public PermissionResourceView upsertResource(Long id, PermissionResourceUpsertRequest payload) {
        PermissionResourceDO target = id == null
                ? new PermissionResourceDO()
                : permissionResourceMapper.selectById(id);
        if (target == null) {
            target = new PermissionResourceDO();
            target.setId(id);
        }

        target.setResourceCode(payload.resourceCode());
        target.setResourceName(payload.resourceName());
        target.setResourceType(payload.resourceType());
        target.setResourcePath(payload.resourcePath());
        target.setPagePath(payload.pagePath());
        target.setStatus(defaultText(payload.status(), "ACTIVE"));
        target.setOrderNo(payload.orderNo() == null ? 0 : payload.orderNo());
        target.setDescription(payload.description());

        if (target.getId() == null) {
            permissionResourceMapper.insert(target);
        } else {
            permissionResourceMapper.updateById(target);
        }

        return toResourceView(permissionResourceMapper.selectById(target.getId()));
    }

    public List<RoleView> listRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<RoleDO>()
                        .orderByAsc(RoleDO::getId))
                .stream()
                .map(this::toRoleView)
                .toList();
    }

    public RoleView upsertRole(Long id, RoleUpsertRequest payload) {
        RoleDO role = id == null ? new RoleDO() : roleMapper.selectById(id);
        if (role == null) {
            role = new RoleDO();
            role.setId(id);
        }
        if (role.getId() == null) {
            role.setId(nextRoleId());
        }
        role.setName(payload.name());
        role.setRoleType(defaultText(payload.roleType(), "CONFIG_OPERATOR"));
        role.setStatus(defaultText(payload.status(), "ACTIVE"));
        role.setOrgScopeId(defaultText(payload.orgScopeId(), "org.demo"));

        if (roleMapper.selectById(role.getId()) == null) {
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
        return toRoleView(roleMapper.selectById(role.getId()));
    }

    public RoleView cloneRole(Long roleId) {
        RoleDO source = requireRole(roleId);
        RoleDO cloned = new RoleDO();
        cloned.setId(nextRoleId());
        cloned.setName(source.getName() + "-copy");
        cloned.setRoleType(source.getRoleType());
        cloned.setStatus(source.getStatus());
        cloned.setOrgScopeId(source.getOrgScopeId());
        roleMapper.insert(cloned);

        List<RoleResourceGrantDO> sourceGrants = roleResourceGrantMapper.selectList(new LambdaQueryWrapper<RoleResourceGrantDO>()
                .eq(RoleResourceGrantDO::getRoleId, source.getId())
                .orderByAsc(RoleResourceGrantDO::getId));
        for (RoleResourceGrantDO grant : sourceGrants) {
            RoleResourceGrantDO nextGrant = new RoleResourceGrantDO();
            nextGrant.setRoleId(cloned.getId());
            nextGrant.setResourceCode(grant.getResourceCode());
            roleResourceGrantMapper.insert(nextGrant);
        }

        List<UserRoleBindingDO> sourceBindings = userRoleBindingMapper.selectList(new LambdaQueryWrapper<UserRoleBindingDO>()
                .eq(UserRoleBindingDO::getRoleId, source.getId())
                .orderByAsc(UserRoleBindingDO::getId));
        for (UserRoleBindingDO binding : sourceBindings) {
            UserRoleBindingDO nextBinding = new UserRoleBindingDO();
            nextBinding.setRoleId(cloned.getId());
            nextBinding.setUserId(binding.getUserId());
            nextBinding.setStatus(binding.getStatus());
            userRoleBindingMapper.insert(nextBinding);
        }

        return toRoleView(roleMapper.selectById(cloned.getId()));
    }

    public RoleView toggleRoleStatus(Long roleId) {
        RoleDO role = requireRole(roleId);
        role.setStatus("ACTIVE".equals(role.getStatus()) ? "DISABLED" : "ACTIVE");
        roleMapper.updateById(role);
        return toRoleView(roleMapper.selectById(roleId));
    }

    public List<RoleResourceGrantView> listRoleResourceGrants(Long roleId) {
        return roleResourceGrantMapper.selectList(new LambdaQueryWrapper<RoleResourceGrantDO>()
                        .eq(RoleResourceGrantDO::getRoleId, roleId)
                        .orderByAsc(RoleResourceGrantDO::getId))
                .stream()
                .map(this::toGrantView)
                .toList();
    }

    public RoleUpdateCountView replaceRoleResourceGrants(Long roleId, List<String> resourceCodes) {
        RoleDO role = requireRole(roleId);
        roleResourceGrantMapper.delete(new LambdaUpdateWrapper<RoleResourceGrantDO>()
                .eq(RoleResourceGrantDO::getRoleId, roleId));

        Set<String> uniqueCodes = new LinkedHashSet<>(resourceCodes);
        for (String resourceCode : uniqueCodes) {
            RoleResourceGrantDO grantDO = new RoleResourceGrantDO();
            grantDO.setRoleId(roleId);
            grantDO.setResourceCode(resourceCode);
            roleResourceGrantMapper.insert(grantDO);
        }

        return new RoleUpdateCountView(roleId, uniqueCodes.size());
    }

    public List<UserRoleBindingView> listRoleMembers(Long roleId) {
        return userRoleBindingMapper.selectList(new LambdaQueryWrapper<UserRoleBindingDO>()
                        .eq(UserRoleBindingDO::getRoleId, roleId)
                        .orderByAsc(UserRoleBindingDO::getId))
                .stream()
                .map(this::toBindingView)
                .toList();
    }

    public RoleUpdateCountView replaceRoleMembers(Long roleId, List<String> userIds) {
        requireRole(roleId);
        userRoleBindingMapper.delete(new LambdaUpdateWrapper<UserRoleBindingDO>()
                .eq(UserRoleBindingDO::getRoleId, roleId));

        Set<String> uniqueUsers = new LinkedHashSet<>(userIds);
        for (String userId : uniqueUsers) {
            UserRoleBindingDO binding = new UserRoleBindingDO();
            binding.setRoleId(roleId);
            binding.setUserId(userId);
            binding.setStatus("ACTIVE");
            userRoleBindingMapper.insert(binding);
        }

        return new RoleUpdateCountView(roleId, uniqueUsers.size());
    }

    public SessionMeView sessionMe(String userId, String orgId) {
        List<UserRoleBindingDO> bindings = userRoleBindingMapper.selectList(new LambdaQueryWrapper<UserRoleBindingDO>()
                .eq(UserRoleBindingDO::getStatus, "ACTIVE")
                .eq(UserRoleBindingDO::getUserId, userId));

        List<Long> roleIds = bindings.stream().map(UserRoleBindingDO::getRoleId).distinct().toList();
        List<RoleDO> roles = roleIds.isEmpty()
                ? List.of()
                : roleMapper.selectList(new LambdaQueryWrapper<RoleDO>()
                .eq(RoleDO::getStatus, "ACTIVE")
                .in(RoleDO::getId, roleIds));

        List<RoleResourceGrantDO> grants = roleIds.isEmpty()
                ? List.of()
                : roleResourceGrantMapper.selectList(new LambdaQueryWrapper<RoleResourceGrantDO>()
                .in(RoleResourceGrantDO::getRoleId, roleIds));

        Set<String> resourceCodes = grants.stream()
                .map(RoleResourceGrantDO::getResourceCode)
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        List<PermissionResourceDO> resources = resourceCodes.isEmpty()
                ? List.of()
                : permissionResourceMapper.selectList(new LambdaQueryWrapper<PermissionResourceDO>()
                .eq(PermissionResourceDO::getStatus, "ACTIVE")
                .in(PermissionResourceDO::getResourceCode, resourceCodes));

        List<String> resourcePaths = resources.stream()
                .map(PermissionResourceDO::getResourcePath)
                .filter(StringUtils::hasText)
                .toList();

        return new SessionMeView(
                userId,
                orgId,
                roles.stream().map(this::toRoleView).toList(),
                List.copyOf(resourceCodes),
                resourcePaths
        );
    }

    public String currentUserId() {
        return RequestContextHolder.currentUserId();
    }

    private RoleDO requireRole(Long roleId) {
        RoleDO role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在，无法保存授权。");
        }
        return role;
    }

    private PermissionResourceView toResourceView(PermissionResourceDO item) {
        return new PermissionResourceView(
                item.getId(),
                item.getResourceCode(),
                item.getResourceName(),
                item.getResourceType(),
                item.getResourcePath(),
                item.getPagePath(),
                item.getStatus(),
                item.getOrderNo(),
                item.getDescription(),
                item.getUpdateTime()
        );
    }

    private RoleView toRoleView(RoleDO roleDO) {
        return new RoleView(
                roleDO.getId(),
                roleDO.getName(),
                roleDO.getRoleType(),
                roleDO.getStatus(),
                roleDO.getOrgScopeId(),
                getRoleMemberUserIds(roleDO.getId()).size(),
                roleDO.getUpdateTime()
        );
    }

    private RoleResourceGrantView toGrantView(RoleResourceGrantDO grantDO) {
        return new RoleResourceGrantView(
                grantDO.getId(),
                grantDO.getRoleId(),
                grantDO.getResourceCode(),
                grantDO.getCreateTime()
        );
    }

    private UserRoleBindingView toBindingView(UserRoleBindingDO bindingDO) {
        return new UserRoleBindingView(
                bindingDO.getId(),
                bindingDO.getUserId(),
                bindingDO.getRoleId(),
                bindingDO.getStatus(),
                bindingDO.getCreateTime()
        );
    }

    private List<String> getRoleMemberUserIds(Long roleId) {
        return userRoleBindingMapper.selectList(new LambdaQueryWrapper<UserRoleBindingDO>()
                        .eq(UserRoleBindingDO::getRoleId, roleId)
                        .eq(UserRoleBindingDO::getStatus, "ACTIVE"))
                .stream()
                .map(UserRoleBindingDO::getUserId)
                .distinct()
                .toList();
    }

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private Long nextRoleId() {
        AtomicLong max = new AtomicLong(7000);
        roleMapper.selectList(new LambdaQueryWrapper<RoleDO>()
                        .select(RoleDO::getId)
                        .orderByDesc(RoleDO::getId)
                        .last("LIMIT 1"))
                .stream()
                .findFirst()
                .map(RoleDO::getId)
                .ifPresent(max::set);
        return max.get() + 1;
    }
}
