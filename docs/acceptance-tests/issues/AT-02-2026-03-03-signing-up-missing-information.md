## Sprint record

- **Issue ID:** AT-2  
- **Record opened (course timeline):** 2026-03-03  
- **Status:** Accepted  

---

## Summary

As a user attempting to sign up, I **cannot** complete registration when **required fields are missing** or invalid, and I see clear feedback.

## Acceptance criteria

1. **Given** I leave required fields empty **when** I submit **then** registration does not succeed and I see an error (toast or inline).  
2. **When** passwords do not match **then** registration is blocked with feedback.

## Automated verification (where available)

- *No `RegisterValidationInstrumentedTest` in this repo branch* — **manual** or add Espresso for `RegisterActivity` validation paths.

## Manual / evidence

- Screenshot: empty submit + mismatch password cases.
