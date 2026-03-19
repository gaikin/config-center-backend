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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

    public List<Map<String, Object>> listResources() {
        return permissionResourceMapper.selectList(new LambdaQueryWrapper<PermissionResourceDO>()
                        .eq(PermissionResourceDO::getIsDeleted, 0)
                        .orderByAsc(PermissionResourceDO::getOrderNo, PermissionResourceDO::getId))
                .stream()
                .map(this::toResourceMap)
                .toList();
    }

    public Map<String, Object> upsertResource(Long id, Map<String, Object> payload) {
        PermissionResourceDO target = id == null
                ? new PermissionResourceDO()
                : permissionResourceMapper.selectById(id);

        if (target == null) {
            target = new PermissionResourceDO();
            target.setId(id);
        }

        target.setResourceCode(asText(payload.get("resourceCode")));
        target.setResourceName(asText(payload.get("resourceName")));
        target.setResourceType(asText(payload.get("resourceType")));
        target.setResourcePath(asText(payload.get("resourcePath")));
        target.setPagePath(asText(payload.get("pagePath")));
        target.setStatus(defaultText(asText(payload.get("status")), "ACTIVE"));
        target.setOrderNo(asInt(payload.get("orderNo"), 0));
        target.setDescription(asText(payload.get("description")));

        if (target.getId() == null) {
            permissionResourceMapper.insert(target);
        } else {
            permissionResourceMapper.updateById(target);
        }

        return toResourceMap(permissionResourceMapper.selectById(target.getId()));
    }

    public List<Map<String, Object>> listRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<RoleDO>()
                        .eq(RoleDO::getIsDeleted, 0)
                        .orderByAsc(RoleDO::getId))
                .stream()
                .map(this::toRoleMap)
                .toList();
    }

    public Map<String, Object> upsertRole(Long id, Map<String, Object> payload) {
        RoleDO role = id == null ? new RoleDO() : roleMapper.selectById(id);
        if (role == null) {
            role = new RoleDO();
            role.setId(id);
        }
        if (role.getId() == null) {
            role.setId(nextRoleId());
        }
        role.setName(asText(payload.get("name")));
        role.setRoleType(defaultText(asText(payload.get("roleType")), "CONFIG_OPERATOR"));
        role.setStatus(defaultText(asText(payload.get("status")), "ACTIVE"));
        role.setOrgScopeId(defaultText(asText(payload.get("orgScopeId")), "org.demo"));

        if (roleMapper.selectById(role.getId()) == null) {
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
        return toRoleMap(roleMapper.selectById(role.getId()));
    }

    public List<Map<String, Object>> listRoleResourceGrants(Long roleId) {
        return roleResourceGrantMapper.selectList(new LambdaQueryWrapper<RoleResourceGrantDO>()
                        .eq(RoleResourceGrantDO::getIsDeleted, 0)
                        .eq(RoleResourceGrantDO::getRoleId, roleId)
                        .orderByAsc(RoleResourceGrantDO::getId))
                .stream()
                .map(this::toGrantMap)
                .toList();
    }

    public Map<String, Object> replaceRoleResourceGrants(Long roleId, List<String> resourceCodes) {
        roleResourceGrantMapper.delete(new LambdaUpdateWrapper<RoleResourceGrantDO>()
                .eq(RoleResourceGrantDO::getRoleId, roleId));

        Set<String> uniqueCodes = new LinkedHashSet<>(resourceCodes);
        for (String resourceCode : uniqueCodes) {
            RoleResourceGrantDO grantDO = new RoleResourceGrantDO();
            grantDO.setRoleId(roleId);
            grantDO.setResourceCode(resourceCode);
            roleResourceGrantMapper.insert(grantDO);
        }

        return Map.of(
                "roleId", roleId,
                "updatedCount", uniqueCodes.size()
        );
    }

    public List<Map<String, Object>> listRoleMembers(Long roleId) {
        return userRoleBindingMapper.selectList(new LambdaQueryWrapper<UserRoleBindingDO>()
                        .eq(UserRoleBindingDO::getIsDeleted, 0)
                        .eq(UserRoleBindingDO::getRoleId, roleId)
                        .orderByAsc(UserRoleBindingDO::getId))
                .stream()
                .map(this::toBindingMap)
                .toList();
    }

    public Map<String, Object> replaceRoleMembers(Long roleId, List<String> userIds) {
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

        return Map.of(
                "roleId", roleId,
                "updatedCount", uniqueUsers.size()
        );
    }

    public Map<String, Object> sessionMe(String userId, String orgId) {
        List<UserRoleBindingDO> bindings = userRoleBindingMapper.selectList(new LambdaQueryWrapper<UserRoleBindingDO>()
                .eq(UserRoleBindingDO::getIsDeleted, 0)
                .eq(UserRoleBindingDO::getStatus, "ACTIVE")
                .eq(UserRoleBindingDO::getUserId, userId));

        List<Long> roleIds = bindings.stream().map(UserRoleBindingDO::getRoleId).distinct().toList();
        List<RoleDO> roles = roleIds.isEmpty()
                ? List.of()
                : roleMapper.selectList(new LambdaQueryWrapper<RoleDO>()
                .eq(RoleDO::getIsDeleted, 0)
                .eq(RoleDO::getStatus, "ACTIVE")
                .in(RoleDO::getId, roleIds));

        List<RoleResourceGrantDO> grants = roleIds.isEmpty()
                ? List.of()
                : roleResourceGrantMapper.selectList(new LambdaQueryWrapper<RoleResourceGrantDO>()
                .eq(RoleResourceGrantDO::getIsDeleted, 0)
                .in(RoleResourceGrantDO::getRoleId, roleIds));

        Set<String> resourceCodes = grants.stream()
                .map(RoleResourceGrantDO::getResourceCode)
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

        List<PermissionResourceDO> resources = resourceCodes.isEmpty()
                ? List.of()
                : permissionResourceMapper.selectList(new LambdaQueryWrapper<PermissionResourceDO>()
                .eq(PermissionResourceDO::getIsDeleted, 0)
                .eq(PermissionResourceDO::getStatus, "ACTIVE")
                .in(PermissionResourceDO::getResourceCode, resourceCodes));

        List<String> resourcePaths = resources.stream()
                .map(PermissionResourceDO::getResourcePath)
                .filter(StringUtils::hasText)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        return Map.of(
                "userId", userId,
                "orgId", orgId,
                "roles", roles.stream().map(this::toRoleMap).toList(),
                "resourceCodes", List.copyOf(resourceCodes),
                "resourcePaths", resourcePaths
        );
    }

    public String currentUserId() {
        return RequestContextHolder.currentUserId();
    }

    private Map<String, Object> toResourceMap(PermissionResourceDO item) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getId());
        row.put("resourceCode", item.getResourceCode());
        row.put("resourceName", item.getResourceName());
        row.put("resourceType", item.getResourceType());
        row.put("resourcePath", item.getResourcePath());
        row.put("pagePath", item.getPagePath());
        row.put("status", item.getStatus());
        row.put("orderNo", item.getOrderNo());
        row.put("description", item.getDescription());
        row.put("updatedAt", item.getUpdatedAt());
        return row;
    }

    private Map<String, Object> toRoleMap(RoleDO roleDO) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", roleDO.getId());
        row.put("name", roleDO.getName());
        row.put("roleType", roleDO.getRoleType());
        row.put("status", roleDO.getStatus());
        row.put("orgScopeId", roleDO.getOrgScopeId());
        row.put("updatedAt", roleDO.getUpdatedAt());
        return row;
    }

    private Map<String, Object> toGrantMap(RoleResourceGrantDO grantDO) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", grantDO.getId());
        row.put("roleId", grantDO.getRoleId());
        row.put("resourceCode", grantDO.getResourceCode());
        row.put("createdAt", grantDO.getCreatedAt());
        return row;
    }

    private Map<String, Object> toBindingMap(UserRoleBindingDO bindingDO) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", bindingDO.getId());
        row.put("userId", bindingDO.getUserId());
        row.put("roleId", bindingDO.getRoleId());
        row.put("status", bindingDO.getStatus());
        row.put("createdAt", bindingDO.getCreatedAt());
        return row;
    }

    private String asText(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private Integer asInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        String text = String.valueOf(value);
        if (!StringUtils.hasText(text)) {
            return defaultValue;
        }
        return Integer.parseInt(text);
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
