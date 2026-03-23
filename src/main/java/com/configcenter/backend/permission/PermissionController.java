package com.configcenter.backend.permission;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.common.context.RequestContext;
import com.configcenter.backend.common.context.RequestContextHolder;
import com.configcenter.backend.permission.dto.PermissionResourceUpsertRequest;
import com.configcenter.backend.permission.dto.PermissionResourceView;
import com.configcenter.backend.permission.dto.RoleResourceGrantView;
import com.configcenter.backend.permission.dto.RoleUpsertRequest;
import com.configcenter.backend.permission.dto.RoleView;
import com.configcenter.backend.permission.dto.RoleUpdateCountView;
import com.configcenter.backend.permission.dto.SessionMeView;
import com.configcenter.backend.permission.dto.UserRoleBindingView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionApplicationService permissionApplicationService;

    public PermissionController(PermissionApplicationService permissionApplicationService) {
        this.permissionApplicationService = permissionApplicationService;
    }

    @GetMapping("/resources")
    public ApiResponse<List<PermissionResourceView>> resources() {
        return ApiResponse.success(permissionApplicationService.listResources());
    }

    @PostMapping("/resources")
    public ApiResponse<PermissionResourceView> createResource(@RequestBody PermissionResourceUpsertRequest payload) {
        return ApiResponse.success(permissionApplicationService.upsertResource(null, payload));
    }

    @PostMapping("/resources/{id}")
    public ApiResponse<PermissionResourceView> updateResource(@PathVariable Long id, @RequestBody PermissionResourceUpsertRequest payload) {
        return ApiResponse.success(permissionApplicationService.upsertResource(id, payload));
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleView>> roles() {
        return ApiResponse.success(permissionApplicationService.listRoles());
    }

    @PostMapping("/roles")
    public ApiResponse<RoleView> createRole(@RequestBody RoleUpsertRequest payload) {
        return ApiResponse.success(permissionApplicationService.upsertRole(null, payload));
    }

    @PostMapping("/roles/{id}")
    public ApiResponse<RoleView> updateRole(@PathVariable Long id, @RequestBody RoleUpsertRequest payload) {
        return ApiResponse.success(permissionApplicationService.upsertRole(id, payload));
    }

    @PostMapping("/roles/{roleId}/clone")
    public ApiResponse<RoleView> cloneRole(@PathVariable Long roleId) {
        return ApiResponse.success(permissionApplicationService.cloneRole(roleId));
    }

    @PostMapping("/roles/{roleId}/status")
    public ApiResponse<RoleView> toggleRoleStatus(@PathVariable Long roleId) {
        return ApiResponse.success(permissionApplicationService.toggleRoleStatus(roleId));
    }

    @GetMapping("/roles/{roleId}/resource-grants")
    public ApiResponse<List<RoleResourceGrantView>> roleResourceGrants(@PathVariable Long roleId) {
        return ApiResponse.success(permissionApplicationService.listRoleResourceGrants(roleId));
    }

    @PostMapping("/roles/{roleId}/resource-grants")
    public ApiResponse<RoleUpdateCountView> replaceRoleResourceGrants(
            @PathVariable Long roleId,
            @RequestBody ReplaceRoleResourceGrantsRequest request
    ) {
        return ApiResponse.success(permissionApplicationService.replaceRoleResourceGrants(roleId, request.resourceCodes()));
    }

    @GetMapping("/roles/{roleId}/members")
    public ApiResponse<List<UserRoleBindingView>> roleMembers(@PathVariable Long roleId) {
        return ApiResponse.success(permissionApplicationService.listRoleMembers(roleId));
    }

    @PostMapping("/roles/{roleId}/members")
    public ApiResponse<RoleUpdateCountView> replaceRoleMembers(
            @PathVariable Long roleId,
            @RequestBody ReplaceRoleMembersRequest request
    ) {
        return ApiResponse.success(permissionApplicationService.replaceRoleMembers(roleId, request.userIds()));
    }

    @GetMapping("/session/me")
    public ApiResponse<SessionMeView> sessionMe(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "orgId", required = false) String orgId
    ) {
        RequestContext context = RequestContextHolder.get();
        String resolvedUserId = userId != null ? userId : RequestContextHolder.currentUserId();
        String resolvedOrgId = orgId != null ? orgId : (context == null ? "org.demo" : context.orgId());
        return ApiResponse.success(permissionApplicationService.sessionMe(resolvedUserId, resolvedOrgId));
    }

    public record ReplaceRoleResourceGrantsRequest(List<String> resourceCodes) {
    }

    public record ReplaceRoleMembersRequest(List<String> userIds) {
    }
}

