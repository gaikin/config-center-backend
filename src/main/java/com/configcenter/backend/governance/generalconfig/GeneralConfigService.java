package com.configcenter.backend.governance.generalconfig;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.governance.generalconfig.dto.GeneralConfigItemStatusUpdateRequest;
import com.configcenter.backend.governance.generalconfig.dto.GeneralConfigItemUpsertRequest;
import com.configcenter.backend.governance.generalconfig.dto.GeneralConfigItemView;
import com.configcenter.backend.infrastructure.db.governance.mapper.GeneralConfigItemMapper;
import com.configcenter.backend.infrastructure.db.governance.model.GeneralConfigItemDO;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GeneralConfigService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final GeneralConfigItemMapper generalConfigItemMapper;

    public GeneralConfigService(GeneralConfigItemMapper generalConfigItemMapper) {
        this.generalConfigItemMapper = generalConfigItemMapper;
    }

    public List<GeneralConfigItemView> listGeneralConfigItems(String groupKey) {
        LambdaQueryWrapper<GeneralConfigItemDO> query = new LambdaQueryWrapper<GeneralConfigItemDO>()
                .orderByAsc(GeneralConfigItemDO::getOrderNo)
                .orderByAsc(GeneralConfigItemDO::getId);
        if (StringUtils.hasText(groupKey)) {
            query.eq(GeneralConfigItemDO::getGroupKey, groupKey.trim());
        }
        return generalConfigItemMapper.selectList(query).stream().map(this::toView).toList();
    }

    public GeneralConfigItemView upsertGeneralConfigItem(GeneralConfigItemUpsertRequest request) {
        String groupKey = requireText(request.getGroupKey(), "groupKey");
        String itemKey = requireText(request.getItemKey(), "itemKey");
        GeneralConfigItemDO item = null;
        if (request.getId() != null) {
            item = generalConfigItemMapper.selectById(request.getId());
        }
        if (item == null) {
            item = generalConfigItemMapper.selectOne(new LambdaQueryWrapper<GeneralConfigItemDO>()
                    .eq(GeneralConfigItemDO::getGroupKey, groupKey)
                    .eq(GeneralConfigItemDO::getItemKey, itemKey));
        }
        if (item == null) {
            item = new GeneralConfigItemDO();
        }
        applyRequest(item, request, groupKey, itemKey);
        if (item.getId() == null) {
            generalConfigItemMapper.insert(item);
        } else if (generalConfigItemMapper.selectById(item.getId()) == null) {
            generalConfigItemMapper.insert(item);
        } else {
            generalConfigItemMapper.updateById(item);
        }
        return toView(generalConfigItemMapper.selectById(item.getId()));
    }

    public GeneralConfigItemView updateGeneralConfigItem(Long id, GeneralConfigItemUpsertRequest request) {
        GeneralConfigItemDO item = generalConfigItemMapper.selectById(id);
        if (item == null) {
            throw new IllegalArgumentException("通用配置不存在");
        }
        applyRequest(item, request, requireText(request.getGroupKey(), "groupKey"), requireText(request.getItemKey(), "itemKey"));
        generalConfigItemMapper.updateById(item);
        return toView(generalConfigItemMapper.selectById(id));
    }

    public GeneralConfigItemView updateGeneralConfigItemStatus(Long id, GeneralConfigItemStatusUpdateRequest request) {
        GeneralConfigItemDO item = generalConfigItemMapper.selectById(id);
        if (item == null) {
            throw new IllegalArgumentException("通用配置不存在");
        }
        item.setStatus(normalizeStatus(request.getStatus()));
        generalConfigItemMapper.updateById(item);
        return toView(generalConfigItemMapper.selectById(id));
    }

    private void applyRequest(
            GeneralConfigItemDO item,
            GeneralConfigItemUpsertRequest request,
            String groupKey,
            String itemKey
    ) {
        item.setGroupKey(groupKey);
        item.setItemKey(itemKey);
        item.setItemValue(requireText(request.getItemValue(), "itemValue"));
        item.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        item.setStatus(normalizeStatus(request.getStatus()));
        item.setOrderNo(request.getOrderNo() == null ? 0 : request.getOrderNo());
    }

    private GeneralConfigItemView toView(GeneralConfigItemDO item) {
        GeneralConfigItemView view = new GeneralConfigItemView();
        view.setId(item.getId());
        view.setGroupKey(item.getGroupKey());
        view.setItemKey(item.getItemKey());
        view.setItemValue(item.getItemValue());
        view.setDescription(item.getDescription());
        view.setStatus(item.getStatus());
        view.setOrderNo(item.getOrderNo());
        view.setUpdateTime(item.getUpdateTime() == null ? null : item.getUpdateTime().format(TIME_FORMATTER));
        view.setUpdatedBy(item.getUpdatedBy());
        return view;
    }

    private String requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("请填写" + fieldName);
        }
        return value.trim();
    }

    private String normalizeStatus(String status) {
        return StringUtils.hasText(status) ? status.trim().toUpperCase() : "ACTIVE";
    }
}
