package com.configcenter.backend.runtime.snapshot;

import com.configcenter.backend.common.support.DemoDataFactory;
import com.configcenter.backend.infrastructure.db.runtime.snapshot.RuntimeSnapshotMapper;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SnapshotService {

    private final RuntimeSnapshotMapper runtimeSnapshotMapper;

    public SnapshotService(RuntimeSnapshotMapper runtimeSnapshotMapper) {
        this.runtimeSnapshotMapper = runtimeSnapshotMapper;
    }

    public Map<String, Object> getBundle(Long pageId) {
        return DemoDataFactory.runtimeBundle(pageId);
    }
}


