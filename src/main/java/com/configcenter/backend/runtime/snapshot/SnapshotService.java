package com.configcenter.backend.runtime.snapshot;

import com.configcenter.backend.runtime.snapshot.dto.RuntimeBundleManifestView;
import com.configcenter.backend.runtime.snapshot.dto.RuntimeBundleView;
import com.configcenter.backend.runtime.snapshot.dto.RuntimeInterfaceConfigView;
import com.configcenter.backend.runtime.snapshot.dto.RuntimePageConfigView;
import com.configcenter.backend.runtime.snapshot.dto.RuntimeRuleConfigView;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SnapshotService {

    public RuntimeBundleView getBundle(Long pageId) {
        RuntimeBundleManifestView manifest = new RuntimeBundleManifestView();
        manifest.setPageId(pageId);
        manifest.setPageVersionId(1000L);
        manifest.setSnapshotVersion("snapshot-1");

        RuntimePageConfigView pageConfig = new RuntimePageConfigView();
        pageConfig.setPageTitle("Loan Apply");
        pageConfig.setUrlPattern("/loan/apply");

        RuntimeRuleConfigView ruleConfig = new RuntimeRuleConfigView();
        ruleConfig.setRuleId(300L);
        ruleConfig.setRuleName("Large Amount Prompt");

        RuntimeInterfaceConfigView interfaceConfig = new RuntimeInterfaceConfigView();
        interfaceConfig.setInterfaceId(200L);
        interfaceConfig.setName("Customer Profile API");

        RuntimeBundleView bundle = new RuntimeBundleView();
        bundle.setManifest(manifest);
        bundle.setPageConfig(pageConfig);
        bundle.setRuleConfigs(List.of(ruleConfig));
        bundle.setInterfaceConfigs(List.of(interfaceConfig));
        return bundle;
    }
}


