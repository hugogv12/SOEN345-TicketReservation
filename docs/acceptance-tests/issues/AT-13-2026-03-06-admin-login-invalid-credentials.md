## Sprint record

- **Issue ID:** AT-13  
- **Record opened (course timeline):** 2026-03-06  
- **Status:** Accepted  

---

## Summary

As a user, I **cannot** access the admin session with **invalid admin credentials**; the app refuses sign-in and shows feedback.

## Acceptance criteria

1. **Given** wrong password **when** I submit admin sign-in **then** I remain unsigned in as admin.  
2. **Given** unknown admin email **when** I submit **then** sign-in fails.

## Automated verification (where available)

- *No dedicated instrumented test in this repo branch* — **manual** on `AdminAuthActivity`.

## Manual / evidence

- Screenshot or short log: failed attempt, still on auth screen.
