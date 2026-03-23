package com.configcenter.backend.control.rule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.control.rule.dto.RuleConditionGroupUpsertRequest;
import com.configcenter.backend.control.rule.dto.RuleConditionGroupView;
import com.configcenter.backend.control.rule.dto.RuleConditionUpsertRequest;
import com.configcenter.backend.control.rule.dto.RuleConditionView;
import com.configcenter.backend.infrastructure.db.control.rule.RuleConditionGroupMapper;
import com.configcenter.backend.infrastructure.db.control.rule.RuleConditionMapper;
import com.configcenter.backend.infrastructure.db.control.rule.model.RuleConditionDO;
import com.configcenter.backend.infrastructure.db.control.rule.model.RuleConditionGroupDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RuleWorkflowService {

    private final RuleConditionGroupMapper ruleConditionGroupMapper;
    private final RuleConditionMapper ruleConditionMapper;
    private final ObjectMapper objectMapper;

    public RuleWorkflowService(
            RuleConditionGroupMapper ruleConditionGroupMapper,
            RuleConditionMapper ruleConditionMapper,
            ObjectMapper objectMapper
    ) {
        this.ruleConditionGroupMapper = ruleConditionGroupMapper;
        this.ruleConditionMapper = ruleConditionMapper;
        this.objectMapper = objectMapper;
    }

    public List<RuleConditionGroupView> listRuleConditionGroups(Long ruleId) {
        return ruleConditionGroupMapper.selectList(
                        new LambdaQueryWrapper<RuleConditionGroupDO>()
                                .eq(RuleConditionGroupDO::getRuleId, ruleId)
                                .orderByAsc(RuleConditionGroupDO::getId)
                )
                .stream()
                .map(this::toGroupView)
                .toList();
    }

    public List<RuleConditionView> listRuleConditions(Long ruleId) {
        return ruleConditionMapper.selectList(
                        new LambdaQueryWrapper<RuleConditionDO>()
                                .eq(RuleConditionDO::getRuleId, ruleId)
                                .orderByAsc(RuleConditionDO::getId)
                )
                .stream()
                .map(this::toConditionView)
                .toList();
    }

    @Transactional
    public RuleConditionGroupView createRuleConditionGroup(Long ruleId, RuleConditionGroupUpsertRequest body) {
        validateLogicType(body.logicType());
        validateParentGroup(ruleId, body.parentGroupId());
        RuleConditionGroupDO group = new RuleConditionGroupDO();
        group.setRuleId(ruleId);
        group.setLogicType(normalize(body.logicType()));
        group.setParentGroupId(body.parentGroupId());
        ruleConditionGroupMapper.insert(group);
        return toGroupView(group);
    }

    @Transactional
    public RuleConditionGroupView updateRuleConditionGroup(Long groupId, RuleConditionGroupUpsertRequest body) {
        RuleConditionGroupDO group = requireGroup(groupId);
        validateLogicType(body.logicType());
        validateParentGroup(group.getRuleId(), body.parentGroupId(), groupId);
        group.setLogicType(normalize(body.logicType()));
        group.setParentGroupId(body.parentGroupId());
        ruleConditionGroupMapper.updateById(group);
        return toGroupView(group);
    }

    @Transactional
    public void deleteRuleConditionGroup(Long groupId) {
        RuleConditionGroupDO root = requireGroup(groupId);
        Set<Long> groupIds = collectGroupTreeIds(root.getRuleId(), groupId);
        if (groupIds.isEmpty()) {
            return;
        }
        ruleConditionMapper.delete(
                new LambdaQueryWrapper<RuleConditionDO>().in(RuleConditionDO::getGroupId, groupIds)
        );
        ruleConditionGroupMapper.delete(
                new LambdaQueryWrapper<RuleConditionGroupDO>().in(RuleConditionGroupDO::getId, groupIds)
        );
    }

    @Transactional
    public RuleConditionView upsertRuleCondition(RuleConditionUpsertRequest body) {
        requireGroup(body.groupId(), body.ruleId());
        RuleConditionDO condition = Optional.ofNullable(body.id())
                .map(ruleConditionMapper::selectById)
                .orElse(null);
        if (condition == null) {
            condition = new RuleConditionDO();
            condition.setRuleId(body.ruleId());
            condition.setGroupId(body.groupId());
        } else if (!Objects.equals(condition.getRuleId(), body.ruleId()) || !Objects.equals(condition.getGroupId(), body.groupId())) {
            throw new IllegalArgumentException("条件不属于当前规则或条件组");
        }
        condition.setLeftJson(writeJson(body.left()));
        condition.setOperator(normalize(body.operator()));
        condition.setRightJson(body.right() == null ? null : writeJson(body.right()));
        if (body.id() == null) {
            ruleConditionMapper.insert(condition);
        } else {
            ruleConditionMapper.updateById(condition);
        }
        return toConditionView(condition);
    }

    @Transactional
    public void deleteRuleCondition(Long conditionId) {
        ruleConditionMapper.deleteById(conditionId);
    }

    @Transactional
    public void cloneRuleLogic(Long sourceRuleId, Long targetRuleId) {
        List<RuleConditionGroupDO> sourceGroups = ruleConditionGroupMapper.selectList(
                new LambdaQueryWrapper<RuleConditionGroupDO>()
                        .eq(RuleConditionGroupDO::getRuleId, sourceRuleId)
                        .orderByAsc(RuleConditionGroupDO::getId)
        );
        List<RuleConditionDO> sourceConditions = ruleConditionMapper.selectList(
                new LambdaQueryWrapper<RuleConditionDO>()
                        .eq(RuleConditionDO::getRuleId, sourceRuleId)
                        .orderByAsc(RuleConditionDO::getId)
        );
        if (sourceGroups.isEmpty() && sourceConditions.isEmpty()) {
            return;
        }

        Map<Long, Long> groupIdMap = new LinkedHashMap<>();
        for (RuleConditionGroupDO sourceGroup : sourceGroups) {
            RuleConditionGroupDO next = new RuleConditionGroupDO();
            next.setRuleId(targetRuleId);
            next.setLogicType(sourceGroup.getLogicType());
            next.setParentGroupId(sourceGroup.getParentGroupId() == null ? null : groupIdMap.get(sourceGroup.getParentGroupId()));
            ruleConditionGroupMapper.insert(next);
            groupIdMap.put(sourceGroup.getId(), next.getId());
        }

        for (RuleConditionDO sourceCondition : sourceConditions) {
            Long mappedGroupId = groupIdMap.get(sourceCondition.getGroupId());
            if (mappedGroupId == null) {
                continue;
            }
            RuleConditionDO next = new RuleConditionDO();
            next.setRuleId(targetRuleId);
            next.setGroupId(mappedGroupId);
            next.setLeftJson(sourceCondition.getLeftJson());
            next.setOperator(sourceCondition.getOperator());
            next.setRightJson(sourceCondition.getRightJson());
            ruleConditionMapper.insert(next);
        }
    }

    private RuleConditionGroupDO requireGroup(Long groupId) {
        RuleConditionGroupDO group = ruleConditionGroupMapper.selectById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("条件组不存在");
        }
        return group;
    }

    private void requireGroup(Long groupId, Long ruleId) {
        RuleConditionGroupDO group = requireGroup(groupId);
        if (!Objects.equals(group.getRuleId(), ruleId)) {
            throw new IllegalArgumentException("条件组不存在或不属于当前规则");
        }
    }

    private void validateParentGroup(Long ruleId, Long parentGroupId) {
        validateParentGroup(ruleId, parentGroupId, null);
    }

    private void validateParentGroup(Long ruleId, Long parentGroupId, Long currentGroupId) {
        if (parentGroupId == null) {
            return;
        }
        RuleConditionGroupDO parent = requireGroup(parentGroupId);
        if (!Objects.equals(parent.getRuleId(), ruleId)) {
            throw new IllegalArgumentException("父条件组不存在或不属于当前规则");
        }
        if (currentGroupId != null && Objects.equals(parentGroupId, currentGroupId)) {
            throw new IllegalArgumentException("条件组不能将自己设置为父级");
        }
        if (parent.getParentGroupId() != null) {
            throw new IllegalArgumentException("仅支持两层嵌套，不能继续新增子组");
        }
    }

    private Set<Long> collectGroupTreeIds(Long ruleId, Long groupId) {
        Deque<Long> queue = new ArrayDeque<>();
        queue.add(groupId);
        Set<Long> ids = new java.util.LinkedHashSet<>();
        while (!queue.isEmpty()) {
            Long current = queue.removeFirst();
            if (!ids.add(current)) {
                continue;
            }
            List<RuleConditionGroupDO> children = ruleConditionGroupMapper.selectList(
                    new LambdaQueryWrapper<RuleConditionGroupDO>()
                            .eq(RuleConditionGroupDO::getRuleId, ruleId)
                            .eq(RuleConditionGroupDO::getParentGroupId, current)
            );
            for (RuleConditionGroupDO child : children) {
                queue.addLast(child.getId());
            }
        }
        return ids;
    }

    private RuleConditionGroupView toGroupView(RuleConditionGroupDO group) {
        return new RuleConditionGroupView(
                group.getId(),
                group.getRuleId(),
                group.getLogicType(),
                group.getParentGroupId(),
                formatDateTime(group.getUpdateTime())
        );
    }

    private RuleConditionView toConditionView(RuleConditionDO condition) {
        return new RuleConditionView(
                condition.getId(),
                condition.getRuleId(),
                condition.getGroupId(),
                readJson(condition.getLeftJson()),
                condition.getOperator(),
                readJson(condition.getRightJson()),
                formatDateTime(condition.getUpdateTime())
        );
    }

    private JsonNode readJson(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(rawJson);
        } catch (JsonProcessingException error) {
            throw new IllegalArgumentException("JSON 解析失败", error);
        }
    }

    private String writeJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException error) {
            throw new IllegalArgumentException("JSON 序列化失败", error);
        }
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private void validateLogicType(String logicType) {
        String normalized = normalize(logicType);
        if (!"AND".equals(normalized) && !"OR".equals(normalized)) {
            throw new IllegalArgumentException("logicType must be AND or OR");
        }
    }

}
