package com.configcenter.backend.control.rule;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.common.api.PageResponse;
import com.configcenter.backend.control.rule.dto.RuleCloneRequest;
import com.configcenter.backend.control.rule.dto.RuleDefinitionUpsertRequest;
import com.configcenter.backend.control.rule.dto.RuleDefinitionView;
import com.configcenter.backend.control.rule.dto.RulePreviewRequest;
import com.configcenter.backend.control.rule.dto.RulePreviewView;
import com.configcenter.backend.control.rule.dto.RuleShareRequest;
import com.configcenter.backend.control.rule.dto.RuleStatusUpdateRequest;
import com.configcenter.backend.control.rule.dto.RuleConditionGroupUpsertRequest;
import com.configcenter.backend.control.rule.dto.RuleConditionGroupView;
import com.configcenter.backend.control.rule.dto.RuleConditionUpsertRequest;
import com.configcenter.backend.control.rule.dto.RuleConditionView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control/rules")
public class RuleController {

    private final RuleService ruleService;
    private final RuleWorkflowService ruleWorkflowService;

    public RuleController(RuleService ruleService, RuleWorkflowService ruleWorkflowService) {
        this.ruleService = ruleService;
        this.ruleWorkflowService = ruleWorkflowService;
    }

    @GetMapping
    public ApiResponse<PageResponse<RuleDefinitionView>> listRules(
            @RequestParam(defaultValue = "1") Long pageNo,
            @RequestParam(defaultValue = "20") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ownerOrgId
    ) {
        return ApiResponse.success(ruleService.listRules(pageNo, pageSize, keyword, status, ownerOrgId));
    }

    @GetMapping("/{ruleId}")
    public ApiResponse<RuleDefinitionView> getRuleDetail(@PathVariable Long ruleId) {
        return ApiResponse.success(ruleService.getRuleDetail(ruleId));
    }

    @PostMapping
    public ApiResponse<RuleDefinitionView> createRule(@RequestBody RuleDefinitionUpsertRequest body) {
        return ApiResponse.success(ruleService.createRule(body));
    }

    @PostMapping("/{ruleId}")
    public ApiResponse<RuleDefinitionView> updateRule(
            @PathVariable Long ruleId,
            @RequestBody RuleDefinitionUpsertRequest body
    ) {
        return ApiResponse.success(ruleService.updateRule(ruleId, body));
    }

    @PostMapping("/{ruleId}/preview")
    public ApiResponse<RulePreviewView> previewRule(
            @PathVariable Long ruleId,
            @RequestBody RulePreviewRequest body
    ) {
        return ApiResponse.success(ruleService.previewRule(ruleId, body));
    }

    @GetMapping("/{ruleId}/condition-groups")
    public ApiResponse<List<RuleConditionGroupView>> listRuleConditionGroups(@PathVariable Long ruleId) {
        return ApiResponse.success(ruleWorkflowService.listRuleConditionGroups(ruleId));
    }

    @PostMapping("/{ruleId}/condition-groups")
    public ApiResponse<RuleConditionGroupView> createRuleConditionGroup(
            @PathVariable Long ruleId,
            @RequestBody RuleConditionGroupUpsertRequest body
    ) {
        return ApiResponse.success(ruleWorkflowService.createRuleConditionGroup(ruleId, body));
    }

    @PostMapping("/rule-condition-groups/{groupId}")
    public ApiResponse<RuleConditionGroupView> updateRuleConditionGroup(
            @PathVariable Long groupId,
            @RequestBody RuleConditionGroupUpsertRequest body
    ) {
        return ApiResponse.success(ruleWorkflowService.updateRuleConditionGroup(groupId, body));
    }

    @PostMapping("/rule-condition-groups/{groupId}/delete")
    public ApiResponse<Void> deleteRuleConditionGroup(@PathVariable Long groupId) {
        ruleWorkflowService.deleteRuleConditionGroup(groupId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{ruleId}/conditions")
    public ApiResponse<List<RuleConditionView>> listRuleConditions(@PathVariable Long ruleId) {
        return ApiResponse.success(ruleWorkflowService.listRuleConditions(ruleId));
    }

    @PostMapping("/rule-conditions")
    public ApiResponse<RuleConditionView> upsertRuleCondition(@RequestBody RuleConditionUpsertRequest body) {
        return ApiResponse.success(ruleWorkflowService.upsertRuleCondition(body));
    }

    @PostMapping("/rule-conditions/{conditionId}")
    public ApiResponse<Void> deleteRuleCondition(@PathVariable Long conditionId) {
        ruleWorkflowService.deleteRuleCondition(conditionId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{ruleId}/status")
    public ApiResponse<RuleDefinitionView> updateRuleStatus(
            @PathVariable Long ruleId,
            @RequestBody RuleStatusUpdateRequest body
    ) {
        return ApiResponse.success(ruleService.updateRuleStatus(ruleId, body));
    }

    @PostMapping("/{ruleId}/share")
    public ApiResponse<RuleDefinitionView> updateRuleShareConfig(
            @PathVariable Long ruleId,
            @RequestBody RuleShareRequest body
    ) {
        return ApiResponse.success(ruleService.updateRuleShareConfig(ruleId, body));
    }

    @PostMapping("/{sourceRuleId}/clone-logic")
    public ApiResponse<RuleDefinitionView> cloneRuleLogic(
            @PathVariable Long sourceRuleId,
            @RequestBody RuleCloneRequest body
    ) {
        return ApiResponse.success(ruleService.cloneRuleToOrg(sourceRuleId, body));
    }
}

