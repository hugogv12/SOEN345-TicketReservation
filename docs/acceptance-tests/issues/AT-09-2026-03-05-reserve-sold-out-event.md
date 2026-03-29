## Sprint record

- **Issue ID:** AT-9  
- **Record opened (course timeline):** 2026-03-05  
- **Status:** Accepted  

---

## Summary

As a user, I **cannot** reserve tickets when **inventory is exhausted** (sold out); the system rejects the booking safely.

## Acceptance criteria

1. **Given** no remaining tickets **when** I attempt to book **then** booking fails with a clear outcome (`NOT_AVAILABLE` or equivalent UX).  
2. **Given** partial inventory **when** two users exhaust capacity **then** a third booking cannot oversell.

## Automated verification (where available)

- `BookingServiceTest.notEnoughTickets`, `BookingServiceTest.twoUsersExhaustCapacity`  
- `ReservationRulesTest` / `EventInventoryTest` as applicable for canceled/zero-capacity edge cases  
- `com.soen345.ticketreservation.admin.AdminEventManagerTest` — sold-out style scenarios in admin/event domain tests

## Manual / evidence

- Screenshot or note: sold-out UX on device (if distinct from JVM-only checks).
