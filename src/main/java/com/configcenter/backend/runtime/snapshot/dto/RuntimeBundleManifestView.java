package com.configcenter.backend.runtime.snapshot.dto;

public class RuntimeBundleManifestView {

    private Long pageId;
    private Long pageVersionId;
    private String snapshotVersion;

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

    public String getSnapshotVersion() {
        return snapshotVersion;
    }

    public void setSnapshotVersion(String snapshotVersion) {
        this.snapshotVersion = snapshotVersion;
    }
}
