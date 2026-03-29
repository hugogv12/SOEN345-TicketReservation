## Sprint record

- **Issue ID:** AT-17  
- **Record opened (course timeline):** 2026-03-07  
- **Status:** Accepted  

---

## Summary

A **non-admin user** (or guest) **cannot** use admin features without authenticating as an admin operator; unauthenticated admin entry goes to **admin sign-in**, not straight to the console.

## Acceptance criteria

1. **Given** no admin session **when** I tap Admin from the main menu **then** I am taken to `AdminAuthActivity` (not `AdminActivity`).  
2. **Given** a normal customer session only **when** I attempt admin **then** I still must complete admin auth.

## Automated verification (where available)

- Code path: `MainActivity` admin button → `AdminAuthActivity` if `!AdminSessionPrefs.hasAdminSession` (review).  
- *Espresso:* **manual** or add test that clears `AdminSessionPrefs` and asserts navigation to auth.

## Manual / evidence

- Screenshot: main → admin → auth screen as guest.
