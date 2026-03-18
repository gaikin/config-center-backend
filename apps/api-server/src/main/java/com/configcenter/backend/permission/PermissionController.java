package com.configcenter.backend.permission;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.common.context.RequestContext;
import com.configcenter.backend.common.context.RequestContextHolder;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ApiResponse<List<Map<String, Object>>> resources() {
        return ApiResponse.success(permissionApplicationService.listResources());
    }

    @PostMapping("/resources")
    public ApiResponse<Map<String, Object>> createResource(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success(permissionApplicationService.upsertResource(null, payload));
    }

    @PutMapping("/resources/{id}")
    public ApiResponse<Map<String, Object>> updateResource(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.success(permissionApplicationService.upsertResource(id, payload));
    }

    @GetMapping("/roles")
    public ApiResponse<List<Map<String, Object>>> roles() {
        return ApiResponse.success(permissionApplicationService.listRoles());
    }

    @PostMapping("/roles")
    public ApiResponse<Map<String, Object>> createRole(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success(permissionApplicationService.upsertRole(null, payload));
    }

    @PutMapping("/roles/{id}")
    public ApiResponse<Map<String, Object>> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.success(permissionApplicationService.upsertRole(id, payload));
    }

    @GetMapping("/roles/{roleId}/resource-grants")
    public ApiResponse<List<Map<String, Object>>> roleResourceGrants(@PathVariable Long roleId) {
        return ApiResponse.success(permissionApplicationService.listRoleResourceGrants(roleId));
    }

    @PutMapping("/roles/{roleId}/resource-grants")
    public ApiResponse<Map<String, Object>> replaceRoleResourceGrants(
            @PathVariable Long roleId,
            @RequestBody ReplaceRoleResourceGrantsRequest request
    ) {
        return ApiResponse.success(permissionApplicationService.replaceRoleResourceGrants(roleId, request.resourceCodes()));
    }

    @GetMapping("/roles/{roleId}/members")
    public ApiResponse<List<Map<String, Object>>> roleMembers(@PathVariable Long roleId) {
        return ApiResponse.success(permissionApplicationService.listRoleMembers(roleId));
    }

    @PutMapping("/roles/{roleId}/members")
    public ApiResponse<Map<String, Object>> replaceRoleMembers(
            @PathVariable Long roleId,
            @RequestBody ReplaceRoleMembersRequest request
    ) {
        return ApiResponse.success(permissionApplicationService.replaceRoleMembers(roleId, request.userIds()));
    }

    @GetMapping("/session/me")
    public ApiResponse<Map<String, Object>> sessionMe(
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
