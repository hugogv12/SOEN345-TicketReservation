## Sprint record

- **Issue ID:** AT-10  
- **Record opened (course timeline):** 2026-03-05  
- **Status:** Accepted  

---

## Summary

After a successful reservation, the system supports **confirmation via email** (e.g. Supabase-backed or stubbed), and failures are surfaced when the backend does not return success.

## Acceptance criteria

1. **Given** a successful booking flow that triggers email **when** the email API returns success **then** the user sees a positive confirmation message.  
2. **When** the email API fails **then** the user sees a failure-oriented message (no silent failure).

## Automated verification (where available)

- `com.soen345.ticketreservation.data.SupabaseClientTest` — `sendConfirmationEmail*` cases (MockWebServer / mocked HTTP)

## Manual / evidence

- Screenshot of in-app confirmation copy; optional: provider dashboard or log showing send (if allowed for the course).
