package com.configcenter.backend.runtime.context;

import com.configcenter.backend.runtime.context.dto.PageContextResolveRequest;
import com.configcenter.backend.runtime.context.dto.PageContextView;
import org.springframework.stereotype.Service;

@Service
public class PageContextService {

    public PageContextView resolve(PageContextResolveRequest body) {
        PageContextView view = new PageContextView();
        view.setPageId(100L);
        view.setPageVersionId(1000L);
        view.setMatchedBy(body.getMenuCode() != null && !body.getMenuCode().isBlank() ? "MENU" : "URL");
        view.setBundleVersion("1000-demo");
        view.setRegionId(body.getRegionId());
        view.setMenuCode(body.getMenuCode());
        view.setUrl(body.getUrl());
        return view;
    }
}


