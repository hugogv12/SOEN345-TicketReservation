## Sprint record

- **Issue ID:** AT-3  
- **Record opened (course timeline):** 2026-03-03  
- **Status:** Accepted  

---

## Summary

As a **registered user**, I can **sign in with valid credentials** and the app reflects my session.

## Acceptance criteria

1. **Given** an existing local account **when** I enter correct identifier and password **then** sign-in succeeds.  
2. **When** sign-in succeeds **then** session data is persisted (`SessionPrefs`) and the UI shows authenticated behaviour where applicable.

## Automated verification (where available)

- `SessionPrefsInstrumentedTest` — persistence contract after `setSession` / `setUserKey` (session layer used after successful auth).  
- *Full login UI flow:* **manual** unless instrumented register+sign-in journey is added.

## Manual / evidence

- Screenshot: main screen after successful login (if UI indicates user state).
