package com.configcenter.backend.control.jobscene;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.control.jobscene.dto.JobNodeUpsertRequest;
import com.configcenter.backend.control.jobscene.dto.JobNodeView;
import com.configcenter.backend.infrastructure.db.control.jobscene.JobSceneNodeMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobSceneNodeDO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobSceneWorkflowService {

    private final JobSceneNodeMapper jobSceneNodeMapper;

    public JobSceneWorkflowService(JobSceneNodeMapper jobSceneNodeMapper) {
        this.jobSceneNodeMapper = jobSceneNodeMapper;
    }

    public List<JobNodeView> listJobNodes(Long sceneId) {
        return jobSceneNodeMapper.selectList(
                        new LambdaQueryWrapper<JobSceneNodeDO>()
                                .eq(JobSceneNodeDO::getSceneId, sceneId)
                                .orderByAsc(JobSceneNodeDO::getOrderNo)
                                .orderByAsc(JobSceneNodeDO::getId)
                )
                .stream()
                .map(this::toView)
                .toList();
    }

    @Transactional
    public JobNodeView upsertJobNode(Long sceneId, JobNodeUpsertRequest body) {
        JobSceneNodeDO node = body.id() == null ? new JobSceneNodeDO() : jobSceneNodeMapper.selectById(body.id());
        if (node == null) {
            node = new JobSceneNodeDO();
            node.setSceneId(sceneId);
        }
        if (node.getSceneId() != null && !node.getSceneId().equals(sceneId)) {
            throw new IllegalArgumentException("节点不属于当前场景");
        }
        node.setSceneId(sceneId);
        node.setNodeType(normalize(body.nodeType()));
        node.setName(normalize(body.name()));
        node.setOrderNo(body.orderNo() == null ? 0 : body.orderNo());
        node.setEnabled(body.enabled() == null ? Boolean.TRUE : body.enabled());
        node.setConfigJson(body.configJson() == null ? "{}" : body.configJson().trim());
        if (body.id() == null) {
            jobSceneNodeMapper.insert(node);
        } else {
            jobSceneNodeMapper.updateById(node);
        }
        return toView(node);
    }

    @Transactional
    public void deleteJobNode(Long nodeId) {
        jobSceneNodeMapper.deleteById(nodeId);
    }

    private JobNodeView toView(JobSceneNodeDO node) {
        return new JobNodeView(
                node.getId(),
                node.getSceneId(),
                node.getNodeType(),
                node.getName(),
                node.getOrderNo(),
                Boolean.TRUE.equals(node.getEnabled()),
                node.getConfigJson(),
                formatDateTime(node.getUpdateTime())
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }
}
