## Sprint record

- **Issue ID:** AT-14  
- **Record opened (course timeline):** 2026-03-06  
- **Status:** Accepted  

---

## Summary

As an **admin**, I can **create a new event** so it appears in the catalog (subject to persistence / sync rules).

## Acceptance criteria

1. **Given** I am in the admin console **when** I add an event with valid fields **then** the event is stored and listed.  
2. **When** I return to the customer browse experience **then** the new event can appear after refresh/sync as designed.

## Automated verification (where available)

- `com.soen345.ticketreservation.admin.AdminEventManagerTest` — add/update paths (JVM admin domain).  
- *UI create flow (`AdminEventEditActivity`):* **manual** or add Espresso.

## Manual / evidence

- Screenshot: admin add form → new row in admin list → optional customer list.
