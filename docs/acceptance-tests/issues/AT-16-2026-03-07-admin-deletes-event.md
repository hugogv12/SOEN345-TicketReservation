## Sprint record

- **Issue ID:** AT-16  
- **Record opened (course timeline):** 2026-03-07  
- **Status:** Accepted  

---

## Summary

As an **admin**, I can **remove or cancel an event** from the operational catalog so it is no longer sold (implementation may be hard delete or **cancel flag** / soft delete).

## Acceptance criteria

1. **Given** an existing event **when** I delete/cancel it as admin **then** the event is marked removed or canceled per product rules.  
2. **When** the event is canceled **then** customers cannot book it (`ReservationRulesTest` / filter rules align).

## Automated verification (where available)

- `AdminEventManagerTest` — `cancelEvent*` and related cases  
- `ReservationRulesTest` — canceled event cannot be reserved  
- *Admin UI delete/cancel button:* **manual** if not covered by Espresso.

## Manual / evidence

- Screenshot: admin action → event shows canceled/hidden in customer list.
