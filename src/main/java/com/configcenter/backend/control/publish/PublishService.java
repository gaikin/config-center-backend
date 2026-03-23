package com.configcenter.backend.control.publish;

import com.configcenter.backend.control.publish.dto.PublishAuditLogRecordRequest;
import com.configcenter.backend.control.publish.dto.PublishAuditLogView;
import com.configcenter.backend.control.publish.dto.PublishValidationRequest;
import com.configcenter.backend.control.publish.dto.PublishValidationView;
import com.configcenter.backend.control.publish.dto.PublishValidationItemView;
import com.configcenter.backend.infrastructure.db.control.publish.PublishOperationLogMapper;
import com.configcenter.backend.infrastructure.db.control.publish.model.PublishOperationLogDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PublishService {

    private final PublishOperationLogMapper publishOperationLogMapper;
    private final ObjectMapper objectMapper;

    public PublishService(PublishOperationLogMapper publishOperationLogMapper, ObjectMapper objectMapper) {
        this.publishOperationLogMapper = publishOperationLogMapper;
        this.objectMapper = objectMapper;
    }

    public PublishValidationView validate(PublishValidationRequest body) {
        List<PublishValidationItemView> items = new ArrayList<>();
        if (body == null || body.resourceId() == null) {
            items.add(new PublishValidationItemView("RESOURCE_ID_MISSING", "resourceId", "resourceId is required"));
        }
        return new PublishValidationView(items.isEmpty(), items);
    }

    public List<PublishAuditLogView> listLogs() {
        List<PublishOperationLogDO> rows = publishOperationLogMapper.selectList(null);
        List<PublishAuditLogView> views = new ArrayList<>(rows.size());
        for (PublishOperationLogDO row : rows) {
            views.add(toView(row));
        }
        views.sort(Comparator.comparing(PublishAuditLogView::createdAt).reversed());
        return views;
    }

    public PublishAuditLogView recordLog(PublishAuditLogRecordRequest request) {
        PublishOperationLogDO row = new PublishOperationLogDO();
        row.setId(nextLogId());
        row.setAction(request.action());
        row.setResourceType(request.resourceType());
        row.setResourceId(request.resourceId());
        row.setResourceName(request.resourceName());
        row.setOperator(request.operator());
        row.setEffectiveScopeType(request.effectiveScopeType());
        row.setEffectiveOrgIdsJson(writeJson(request.effectiveOrgIds()));
        row.setEffectiveScopeSummary(request.effectiveScopeSummary());
        row.setEffectiveStartAt(request.effectiveStartAt());
        row.setEffectiveEndAt(request.effectiveEndAt());
        row.setApprovalTicketId(request.approvalTicketId());
        row.setApprovalSource(request.approvalSource());
        row.setApprovalStatus(request.approvalStatus());
        row.setCreateTime(LocalDateTime.now());
        row.setCreatedBy(request.operator());
        row.setUpdateTime(LocalDateTime.now());
        row.setUpdatedBy(request.operator());
        publishOperationLogMapper.insert(row);
        return toView(row);
    }

    private PublishAuditLogView toView(PublishOperationLogDO row) {
        return new PublishAuditLogView(
                row.getId(),
                row.getAction(),
                row.getResourceType(),
                row.getResourceId(),
                row.getResourceName(),
                row.getOperator(),
                row.getEffectiveScopeType(),
                readJsonList(row.getEffectiveOrgIdsJson()),
                row.getEffectiveScopeSummary(),
                row.getEffectiveStartAt(),
                row.getEffectiveEndAt(),
                row.getApprovalTicketId(),
                row.getApprovalSource(),
                row.getApprovalStatus(),
                row.getCreateTime() == null ? null : row.getCreateTime().toString()
        );
    }

    private String writeJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values == null ? List.of() : values);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize publish log org ids", exception);
        }
    }

    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException exception) {
            return List.of();
        }
    }

    private Long nextLogId() {
        return publishOperationLogMapper.selectList(null)
                .stream()
                .map(PublishOperationLogDO::getId)
                .filter(id -> id != null)
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}

