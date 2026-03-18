# Resource Based Permission Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current `ActionType` permission model with a resource-based permission system covering menus, pages, buttons, and business actions.

**Architecture:** Introduce explicit permission resources plus role-resource grants and user-role bindings in the mock store, then switch session aggregation and all frontend gating to `hasResource(resourcePath)`. Keep the resource model flat, using `resourcePath` conventions for grouping and bulk authorization instead of tree inheritance.

**Tech Stack:** React + TypeScript + Ant Design + React Router + local mock service/store

---

### Task 1: Replace Core Permission Types and Seeds

**Files:**
- Modify: `src/types.ts`
- Modify: `src/mock/seeds.ts`
- Modify: `src/permissionPolicy.ts`
- Verify: `npm run build`

- [ ] **Step 1: Remove `ActionType` from `src/types.ts` and add `PermissionResource`, `RoleResourceGrant`, and `UserRoleBinding` types**
- [ ] **Step 2: Remove `actions` from `RoleItem` in `src/types.ts` and keep role fields limited to identity/status metadata**
- [ ] **Step 3: Add seeded permission resources, role-resource grants, and user-role bindings in `src/mock/seeds.ts`**
- [ ] **Step 4: Replace action-based helpers in `src/permissionPolicy.ts` with resource-template helpers keyed by `roleType`**
- [ ] **Step 5: Run `npm run build` and confirm type errors are limited to downstream usage sites that still expect actions**
- [ ] **Step 6: Commit**

```bash
git add src/types.ts src/mock/seeds.ts src/permissionPolicy.ts
git commit -m "refactor: add resource based permission primitives"
```

### Task 2: Rework Mock Service APIs Around Resources

**Files:**
- Modify: `src/services/configCenterService.ts`
- Modify: `src/types.ts`
- Verify: `npm run build`

- [ ] **Step 1: Extend the mock store in `src/services/configCenterService.ts` to hold permission resources, role-resource grants, and user-role bindings**
- [ ] **Step 2: Add service APIs for listing/upserting permission resources**
- [ ] **Step 3: Add service APIs for listing/saving role-resource grants by role**
- [ ] **Step 4: Rename or refactor current role-member semantics to explicit user-role binding semantics while preserving current UI behavior**
- [ ] **Step 5: Remove service logic that validates or normalizes `RoleItem.actions`**
- [ ] **Step 6: Run `npm run build` and confirm compile success**
- [ ] **Step 7: Commit**

```bash
git add src/services/configCenterService.ts src/types.ts
git commit -m "refactor: move role authorization to resource grants"
```

### Task 3: Switch Session Aggregation to `hasResource`

**Files:**
- Modify: `src/session/mockSession.tsx`
- Modify: `src/components/AppShell.tsx`
- Modify: `src/pages/LoginTestPage/LoginTestPage.tsx`
- Verify: `npm run build`

- [ ] **Step 1: Replace persona `actions` defaults in `src/session/mockSession.tsx` with default resource paths or seeded role bindings**
- [ ] **Step 2: Aggregate effective permissions from user-role bindings plus role-resource grants**
- [ ] **Step 3: Replace `hasAction()` with `hasResource()` in the mock session contract**
- [ ] **Step 4: Update `src/components/AppShell.tsx` so navigation visibility is driven by menu resource paths instead of persona allowlists**
- [ ] **Step 5: Update `src/pages/LoginTestPage/LoginTestPage.tsx` to display resource-based access summaries instead of action labels**
- [ ] **Step 6: Run `npm run build` and confirm compile success**
- [ ] **Step 7: Commit**

```bash
git add src/session/mockSession.tsx src/components/AppShell.tsx src/pages/LoginTestPage/LoginTestPage.tsx
git commit -m "refactor: switch session and navigation to resource permissions"
```

### Task 4: Add Resource Management Page

**Files:**
- Create: `src/pages/PermissionResourcesPage/PermissionResourcesPage.tsx`
- Modify: `src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx`
- Modify: `src/App.tsx`
- Modify: `src/components/AppShell.tsx`
- Verify: `npm run build`

- [ ] **Step 1: Create `src/pages/PermissionResourcesPage/PermissionResourcesPage.tsx` with resource list, filters, and create/edit modal**
- [ ] **Step 2: Wire the page to the new permission-resource service APIs**
- [ ] **Step 3: Add the page into `src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx` as a new advanced-config tab**
- [ ] **Step 4: Register routing and compatibility entry points in `src/App.tsx` and `src/components/AppShell.tsx`**
- [ ] **Step 5: Run `npm run build` and confirm compile success**
- [ ] **Step 6: Commit**

```bash
git add src/pages/PermissionResourcesPage/PermissionResourcesPage.tsx src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx src/App.tsx src/components/AppShell.tsx
git commit -m "feat: add permission resource management page"
```

### Task 5: Rebuild Roles Page for Resource Authorization

**Files:**
- Modify: `src/pages/RolesPage/RolesPage.tsx`
- Modify: `src/permissionPolicy.ts`
- Modify: `src/services/configCenterService.ts`
- Verify: `npm run build`

- [ ] **Step 1: Remove the action selector UI from `src/pages/RolesPage/RolesPage.tsx`**
- [ ] **Step 2: Add resource search, grouped display, and prefix-based bulk selection to the role editor**
- [ ] **Step 3: Load and save role-resource grants alongside role basic info**
- [ ] **Step 4: Keep member assignment behavior, but relabel it to reflect user-role binding semantics**
- [ ] **Step 5: Replace current “按角色类型重置默认权限” behavior with recommended resource-template fill**
- [ ] **Step 6: Run `npm run build` and confirm compile success**
- [ ] **Step 7: Commit**

```bash
git add src/pages/RolesPage/RolesPage.tsx src/permissionPolicy.ts src/services/configCenterService.ts
git commit -m "feat: rebuild role management around resource grants"
```

### Task 6: Replace Publish/Effective Permission Gates

**Files:**
- Modify: `src/effectiveFlow.ts`
- Modify: `src/pages/RulesPage/RulesPage.tsx`
- Modify: `src/pages/InterfacesPage/InterfacesPage.tsx`
- Modify: `src/pages/JobScenesPage/JobScenesPage.tsx`
- Modify: `src/pages/PublishPage/PublishPage.tsx`
- Verify: `npm run build`

- [ ] **Step 1: Replace action-enum permission requirements in `src/effectiveFlow.ts` with concrete resource-path requirements**
- [ ] **Step 2: Update rules, interfaces, and job pages to call `hasResource()` instead of `hasAction()`**
- [ ] **Step 3: Update publish page gating, especially menu capability management and risk actions, to use resource paths**
- [ ] **Step 4: Run `npm run build` and confirm compile success**
- [ ] **Step 5: Commit**

```bash
git add src/effectiveFlow.ts src/pages/RulesPage/RulesPage.tsx src/pages/InterfacesPage/InterfacesPage.tsx src/pages/JobScenesPage/JobScenesPage.tsx src/pages/PublishPage/PublishPage.tsx
git commit -m "refactor: gate business actions with resource paths"
```

### Task 7: Replace Page-Level Permission Gates Across Advanced and Overview Flows

**Files:**
- Modify: `src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx`
- Modify: `src/pages/PageManagementPage/PageManagementPage.tsx`
- Modify: `src/pages/PageResourcesPage/PageResourcesPage.tsx`
- Modify: `src/pages/PageActivationPoliciesPage/PageActivationPoliciesPage.tsx`
- Modify: `src/pages/AuditMetricsPage/AuditMetricsPage.tsx`
- Verify: `npm run build`

- [ ] **Step 1: Replace advanced-config tab visibility checks with page/resource-path checks**
- [ ] **Step 2: Add page-level entry guards where the current UI assumes persona-based visibility**
- [ ] **Step 3: Update menu-management shortcuts and button states to use resource paths**
- [ ] **Step 4: Run `npm run build` and confirm compile success**
- [ ] **Step 5: Commit**

```bash
git add src/pages/AdvancedConfigPage/AdvancedConfigPage.tsx src/pages/PageManagementPage/PageManagementPage.tsx src/pages/PageResourcesPage/PageResourcesPage.tsx src/pages/PageActivationPoliciesPage/PageActivationPoliciesPage.tsx src/pages/AuditMetricsPage/AuditMetricsPage.tsx
git commit -m "refactor: apply resource gating to page level flows"
```

### Task 8: Remove Remaining Action-Enum References

**Files:**
- Modify: `src/types.ts`
- Modify: `src/mock/seeds.ts`
- Modify: `src/services/configCenterService.ts`
- Modify: `src/session/mockSession.tsx`
- Modify: any file returned by `rg -n "ActionType|hasAction\\(" src`
- Verify: `rg -n "ActionType|hasAction\\(" src`
- Verify: `npm run build`

- [ ] **Step 1: Run `rg -n "ActionType|hasAction\\(" src` and update each remaining caller to resource-based logic**
- [ ] **Step 2: Delete any now-dead compatibility labels, enums, and helper branches**
- [ ] **Step 3: Re-run `rg -n "ActionType|hasAction\\(" src` and confirm no results remain**
- [ ] **Step 4: Run final `npm run build`**
- [ ] **Step 5: Commit**

```bash
git add src
git commit -m "refactor: remove legacy action permission model"
```

### Task 9: Final Verification and Documentation Sync

**Files:**
- Modify (if needed): `docs/superpowers/specs/2026-03-18-resource-based-permission-design.md`
- Modify (if needed): `docs/superpowers/specs/2026-03-18-platform-jssdk-version-governance-design.md`
- Verify: `npm run build`
- Verify: `rg -n "ActionType|hasAction\\(" src`

- [ ] **Step 1: Sanity-check implementation against the approved permission spec**
- [ ] **Step 2: Verify the JSSDK governance pages still align with the new permission entry points**
- [ ] **Step 3: Run `rg -n "ActionType|hasAction\\(" src` and confirm zero results**
- [ ] **Step 4: Run final `npm run build` and confirm compile success**
- [ ] **Step 5: Record residual risks in the implementation handoff**
