package com.configcenter.backend.runtime.context.dto;

public class PageContextView {

    private Long pageId;
    private Long pageVersionId;
    private String matchedBy;
    private String bundleVersion;
    private String regionId;
    private String menuCode;
    private String url;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getPageVersionId() {
        return pageVersionId;
    }

    public void setPageVersionId(Long pageVersionId) {
        this.pageVersionId = pageVersionId;
    }

    public String getMatchedBy() {
        return matchedBy;
    }

    public void setMatchedBy(String matchedBy) {
        this.matchedBy = matchedBy;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
