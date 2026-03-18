# Platform JSSDK Version Governance Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current lane-based menu SDK policy prototype with platform-level prompt/job default versions plus menu-level prompt/job gray overrides, and surface the resulting state consistently in platform parameters, SDK version center, and menu management.

**Architecture:** Keep `SdkArtifactVersion` as the selectable version source, but remove `SdkReleaseLane` and the menu-level stable/gray lane model from runtime governance. Introduce a single `PlatformRuntimeConfig` baseline plus capability-aware `MenuSdkPolicy` gray fields, then centralize validation and runtime-hit resolution in a shared helper module so service validation, SDK version center, menu management, and publish views all read the same rules.

**Tech Stack:** React + TypeScript + Ant Design + Vite + local mock service/store

---

### Task 1: Rebuild the Governance Domain Model Around Platform Defaults

**Files:**
- Modify: `package.json`
- Modify: `src/types.ts`
- Modify: `src/mock/seeds.ts`
- Modify: `src/services/configCenterService.ts`
- Create: `src/sdkGovernance.ts`
- Create: `src/sdkGovernance.test.ts`
- Verify: `npx vitest run src/sdkGovernance.test.ts`
- Verify: `npm run build`

- [ ] **Step 1: Add `vitest` and a runnable test script in `package.json` so the governance rules can be checked with fast unit tests**
- [ ] **Step 2: Add `PlatformRuntimeConfig` to `src/types.ts` and refactor `MenuSdkPolicy` to capability-aware gray fields (`promptGrayEnabled`, `promptGrayVersion`, `promptGrayOrgIds`, `jobGrayEnabled`, `jobGrayVersion`, `jobGrayOrgIds`)**
- [ ] **Step 3: Remove `SdkReleaseLane` usage from `src/types.ts`, and keep `SdkArtifactVersion` as the only version catalog exposed to governance pages**
- [ ] **Step 4: Replace seeded lane data in `src/mock/seeds.ts` with one `PlatformRuntimeConfig` seed plus capability-split menu gray strategy seeds that match the approved spec**
- [ ] **Step 5: Create `src/sdkGovernance.ts` with shared pure helpers for save validation, gray-hit matching, effective-version resolution, and menu result summaries**
- [ ] **Step 6: Cover the spec’s core cases in `src/sdkGovernance.test.ts`: prompt gray hit, prompt fallback, job gray hit, job fallback, invalid gray save, invalid time range, and gray-equals-stable rejection**
- [ ] **Step 7: Add service APIs in `src/services/configCenterService.ts` for loading/saving platform runtime config and for loading/saving the new menu gray model**
- [ ] **Step 8: Update pending-item validation in `src/services/configCenterService.ts` so `MENU_SDK_POLICY` checks the new prompt/job gray requirements instead of lane presence**
- [ ] **Step 9: Run `npx vitest run src/sdkGovernance.test.ts` and confirm all governance-rule cases pass**
- [ ] **Step 10: Run `npm run build` and confirm compile success after the domain model migration**
- [ ] **Step 11: Commit**

```bash
git add package.json src/types.ts src/mock/seeds.ts src/services/configCenterService.ts src/sdkGovernance.ts src/sdkGovernance.test.ts
git commit -m "refactor: remodel jssdk governance domain"
```

### Task 2: Implement the Platform Parameters Runtime Baseline UI

**Files:**
- Modify: `src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx`
- Verify: `npm run build`

- [ ] **Step 1: Replace the current placeholder “平台参数” card in `src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx` with a real editable form backed by `PlatformRuntimeConfig`**
- [ ] **Step 2: Render two grouped sections for “智能提示” and “智能作业”, each with “正式版本” and “默认灰度版本” selectors sourced from `SdkArtifactVersion`**
- [ ] **Step 3: Enforce required validation for `promptStableVersion` and `jobStableVersion`, while allowing default gray versions to stay empty**
- [ ] **Step 4: Save through the new runtime-config service API and show success/error feedback consistent with the rest of the prototype**
- [ ] **Step 5: Keep the advanced-config tab query behavior unchanged so `/advanced?tab=platform` continues to deep-link correctly**
- [ ] **Step 6: Run `npm run build` and confirm compile success**
- [ ] **Step 7: Commit**

```bash
git add src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx
git commit -m "feat: implement platform runtime config form"
```

### Task 3: Rebuild SDK Version Center Around Capability Gray Policies

**Files:**
- Modify: `src/pages/SdkVersionCenterPage/SdkVersionCenterPage.tsx`
- Modify: `src/App.tsx`
- Modify: `src/components/AppShell.tsx`
- Verify: `npx vitest run src/sdkGovernance.test.ts`
- Verify: `npm run build`

- [ ] **Step 1: Update `src/App.tsx` so `/sdk-version-center` loads `SdkVersionCenterPage` instead of redirecting away from it**
- [ ] **Step 2: Keep `/sdk-version-center` highlighted under the existing “高级配置” navigation bucket in `src/components/AppShell.tsx`**
- [ ] **Step 3: Replace lane-oriented loading state in `src/pages/SdkVersionCenterPage/SdkVersionCenterPage.tsx` with artifact versions, platform runtime config, and capability-aware menu gray policies**
- [ ] **Step 4: Add a read-only platform baseline summary card showing the four platform defaults from `PlatformRuntimeConfig`**
- [ ] **Step 5: Replace the current table columns with prompt gray version/orgs and job gray version/orgs, plus effective time and lifecycle status**
- [ ] **Step 6: Rebuild the create/edit modal so prompt gray and job gray each have an enable switch, version selector, org selector, and shared time window**
- [ ] **Step 7: When a gray switch is enabled, default its version from `promptGrayDefaultVersion` or `jobGrayDefaultVersion`; when disabled, clear that capability’s version and org fields**
- [ ] **Step 8: Block save when a menu capability is not enabled for that menu, when gray is enabled without version/orgs, when gray equals platform stable, or when the time window is invalid**
- [ ] **Step 9: Run `npx vitest run src/sdkGovernance.test.ts` and confirm helper rules still pass after page integration**
- [ ] **Step 10: Run `npm run build` and confirm compile success**
- [ ] **Step 11: Commit**

```bash
git add src/pages/SdkVersionCenterPage/SdkVersionCenterPage.tsx src/App.tsx src/components/AppShell.tsx
git commit -m "feat: rebuild sdk version center for platform governance"
```

### Task 4: Surface Runtime Result State in Menu Management

**Files:**
- Modify: `src/pages/PageManagementPage/PageManagementPage.tsx`
- Verify: `npm run build`

- [ ] **Step 1: Load platform runtime config and menu gray policies in `src/pages/PageManagementPage/PageManagementPage.tsx` alongside existing menu capability data**
- [ ] **Step 2: Use the shared `src/sdkGovernance.ts` helpers to compute each selected menu’s prompt effective version, job effective version, prompt-gray-present flag, and job-gray-present flag**
- [ ] **Step 3: Add result-state tags/cards in the menu detail area so users can see current prompt/job status without editing policy from this page**
- [ ] **Step 4: Add a “去 SDK版本中心配置” shortcut that navigates to `/sdk-version-center` with the current menu preselected through query params**
- [ ] **Step 5: Keep menu management read-only for governance edits: show state and jump only, without exposing save actions here**
- [ ] **Step 6: Run `npm run build` and confirm compile success**
- [ ] **Step 7: Commit**

```bash
git add src/pages/PageManagementPage/PageManagementPage.tsx
git commit -m "feat: show sdk governance result state in menu management"
```

### Task 5: Align Publish and Validation Flows With the New Model

**Files:**
- Modify: `src/pages/PublishPage/PublishPage.tsx`
- Modify: `src/enumLabels.ts`
- Modify: `src/services/configCenterService.ts`
- Verify: `rg -n "stableLaneId|grayLaneId|grayOrgIds|SdkReleaseLane" src`
- Verify: `npm run build`

- [ ] **Step 1: Replace the publish-page lane summaries and table columns in `src/pages/PublishPage/PublishPage.tsx` with platform stable versions and prompt/job gray summaries**
- [ ] **Step 2: Remove any remaining assumptions that “no gray orgs means all orgs use stable lane”; after migration the fallback source is always the platform stable version**
- [ ] **Step 3: Update `src/enumLabels.ts` text so `MENU_SDK_POLICY` reflects the new “菜单能力灰度策略” meaning**
- [ ] **Step 4: Recheck `src/services/configCenterService.ts` audit, rollback, and pending-item naming so it still produces understandable labels after the model rename**
- [ ] **Step 5: Run `rg -n "stableLaneId|grayLaneId|grayOrgIds|SdkReleaseLane" src` and confirm lane-era field references are removed from the frontend**
- [ ] **Step 6: Run `npm run build` and confirm compile success**
- [ ] **Step 7: Commit**

```bash
git add src/pages/PublishPage/PublishPage.tsx src/enumLabels.ts src/services/configCenterService.ts
git commit -m "refactor: align publish flow with capability gray model"
```

### Task 6: Final Verification and Documentation Sync

**Files:**
- Modify (if needed): `docs/superpowers/specs/2026-03-18-platform-jssdk-version-governance-design.md`
- Verify: `npx vitest run src/sdkGovernance.test.ts`
- Verify: `npm run build`
- Verify: `rg -n "stableLaneId|grayLaneId|grayOrgIds|SdkReleaseLane" src`

- [ ] **Step 1: Sanity-check the implementation against the approved governance spec, especially the four runtime-hit cases and six save-blocking cases**
- [ ] **Step 2: Run `npx vitest run src/sdkGovernance.test.ts` and confirm the shared rules still pass in the final state**
- [ ] **Step 3: Run `rg -n "stableLaneId|grayLaneId|grayOrgIds|SdkReleaseLane" src` and confirm no obsolete lane-era field references remain**
- [ ] **Step 4: Run final `npm run build` and confirm compile success**
- [ ] **Step 5: Record any residual risks in the handoff, especially around missing backend persistence and the fact that current verification is helper-level rather than end-to-end UI automation**
