## Sprint record

- **Issue ID:** AT-7  
- **Record opened (course timeline):** 2026-03-04  
- **Status:** Accepted  

---

## Summary

As a **signed-in user**, I can **reserve a ticket** for an event with available inventory and see confirmation of the booking.

## Acceptance criteria

1. **Given** I am signed in and the event has capacity **when** I reserve **then** booking succeeds (reservation recorded, inventory updated).  
2. **When** booking succeeds **then** I see a confirmation path (activity or message) consistent with the app.

## Automated verification (where available)

- `BookingServiceTest` — `bookingSuccess`, `reservationSnapshotsEventFields` (core booking rules on JVM).  
- `BookingSingletonIntegrationTest` — pipeline with shared repositories.  
- *End-to-end reserve from UI:* not present in this repo’s current `androidTest` set — **manual** or restore/add `AdminAndUserBookingInstrumentedTest`-style journey.

## Manual / evidence

- Screenshot: signed-in reserve → confirmation.
