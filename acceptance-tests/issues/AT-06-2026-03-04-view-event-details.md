## Sprint record

- **Issue ID:** AT-6  
- **Record opened (course timeline):** 2026-03-04  
- **Status:** Accepted  

---

## Summary

As a user, I can **open an event** from the list and **view event details** (title, date, location, availability, etc.).

## Acceptance criteria

1. **Given** an event visible in the list **when** I tap it **then** the detail screen opens with matching title/content.  
2. **Given** the detail screen **when** I use back **then** I return to the browse experience.

## Automated verification (where available)

- `MainFlowInstrumentedTest.guest_findsSeededConference_tapsReserve_seesSignInRequiredDialog` (opens `EventDetailActivity`, asserts `detail_title` for seeded **Tech Conference**)

## Manual / evidence

- Screenshot: list → detail for one event.
