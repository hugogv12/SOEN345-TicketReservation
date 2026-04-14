## Sprint record

- **Issue ID:** AT-8  
- **Record opened (course timeline):** 2026-03-04  
- **Status:** Accepted  

---

## Summary

As a **guest** (not signed in), I **cannot** complete a reservation without signing in; the app prompts me appropriately.

## Acceptance criteria

1. **Given** I am not signed in **when** I tap reserve on an event **then** I am blocked and asked to register/sign in (dialog or navigation).  
2. **When** I dismiss the prompt **then** I can return to browsing without a reservation being created.

## Automated verification (where available)

- `MainFlowInstrumentedTest.guest_findsSeededConference_tapsReserve_seesSignInRequiredDialog`

## Manual / evidence

- Screenshot: sign-in required dialog.
