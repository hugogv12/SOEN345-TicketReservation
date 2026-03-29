## Sprint record

- **Issue ID:** AT-12  
- **Record opened (course timeline):** 2026-03-06  
- **Status:** Accepted  

---

## Summary

As an **admin operator**, I can **sign in with valid admin credentials** and reach the admin console.

## Acceptance criteria

1. **Given** valid admin credentials **when** I submit on `AdminAuthActivity` **then** I am signed in (`AdminSessionPrefs`) and can open `AdminActivity`.  
2. **When** already signed in **then** admin entry from main menu opens the console directly.

## Automated verification (where available)

- `NavigationInstrumentedTest` seeds `AdminSessionPrefs` and verifies `mainToAdmin_showsAdminList` (navigation + console visibility; **not** full credential form).  
- *Full admin auth form:* **manual** or add Espresso for `AdminAuthActivity`.

## Manual / evidence

- Screenshot: admin auth success → event list with Add Event.
