## Sprint record

- **Issue ID:** AT-15  
- **Record opened (course timeline):** 2026-03-06  
- **Status:** Accepted  

---

## Summary

As an **admin**, I can **edit an existing event** and persist changes.

## Acceptance criteria

1. **Given** an existing event **when** I change fields (e.g. title, date, capacity) **then** changes are saved.  
2. **When** customers refresh **then** updated information is reflected per sync rules.

## Automated verification (where available)

- `AdminEventManagerTest` — update/edit scenarios  
- *UI edit:* **manual** or instrumented admin journey if added.

## Manual / evidence

- Screenshot: before/after edit in admin list or detail.
