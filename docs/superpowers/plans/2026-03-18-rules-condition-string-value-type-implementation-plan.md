# Rules Condition String Value Type Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove manual value-type configuration in rule conditions and use string-first behavior with numeric operators doing runtime numeric conversion.

**Architecture:** Keep the existing rule model shape for compatibility, but simplify UI to no longer edit `valueType`. Move numeric comparison semantics into `workflowService` so preview/execution traces consistently explain non-numeric comparisons as non-match.

**Tech Stack:** React + TypeScript + Ant Design + local mock workflow service

---

### Task 1: Simplify Condition Editor Interaction

**Files:**
- Modify: `src/pages/RulesPage/RulesPage.tsx`
- Verify: `npm run build`

- [ ] **Step 1: Remove value-type editor from condition panel**
- [ ] **Step 2: Keep source/value editing behavior unchanged**
- [ ] **Step 3: Ensure API output-path selection no longer rewrites operand value type**
- [ ] **Step 4: Run `npm run build` and confirm compile success**

### Task 2: Unify Runtime Comparison Semantics

**Files:**
- Modify: `src/services/workflowService.ts`
- Verify: `npm run build`

- [ ] **Step 1: Refactor comparison helper to return structured result (`passed` + `reason`)**
- [ ] **Step 2: Implement numeric-only behavior for `GT/GE/LT/LE` with conversion failure reason**
- [ ] **Step 3: Wire trace reason to show conversion-failure explanation**
- [ ] **Step 4: Run `npm run build` and confirm compile success**

### Task 3: Final Verification and Documentation Sync

**Files:**
- Modify (if needed): `docs/superpowers/specs/2026-03-18-rules-condition-string-value-type-design.md`
- Verify: `npm run build`

- [ ] **Step 1: Sanity-check implementation against approved spec**
- [ ] **Step 2: Run final `npm run build`**
- [ ] **Step 3: Record outcomes and residual risks in final handoff**

