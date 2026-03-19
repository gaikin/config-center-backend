package com.configcenter.backend.runtime.context;

import com.configcenter.backend.common.support.DemoDataFactory;
import com.configcenter.backend.infrastructure.db.runtime.context.PageContextMapper;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PageContextService {

    private final PageContextMapper pageContextMapper;

    public PageContextService(PageContextMapper pageContextMapper) {
        this.pageContextMapper = pageContextMapper;
    }

    public Map<String, Object> resolve(Map<String, Object> body) {
        return DemoDataFactory.resolvedPageContext(body);
    }
}


