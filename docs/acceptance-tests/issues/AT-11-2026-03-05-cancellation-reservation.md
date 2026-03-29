## Sprint record

- **Issue ID:** AT-11  
- **Record opened (course timeline):** 2026-03-05  
- **Status:** Accepted  

---

## Summary

As a user, I can **cancel my reservation**; inventory is restored and the reservation no longer appears for me.

## Acceptance criteria

1. **Given** I own a reservation **when** I cancel it **then** cancellation succeeds and tickets become available again.  
2. **Given** another user’s reservation **when** I attempt to cancel it **then** cancellation is rejected.

## Automated verification (where available)

- `BookingServiceTest.cancelRestores`, `wrongUserCancel`, `cancelUnknownId`, `cancelAfterCatalogCleared`  
- `BookingSingletonIntegrationTest.cancelRestoresInventory`  
- *My Reservations UI cancel:* **manual** if no `ReservationLifecycleInstrumentedTest` in this branch.

## Manual / evidence

- Screenshot: My Reservations → cancel → list updated.
