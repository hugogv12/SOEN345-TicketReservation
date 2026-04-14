## Sprint record

- **Issue ID:** AT-1  
- **Record opened (course timeline):** 2026-03-03 (Monday, first week of March)  
- **Status:** Accepted  

---

## Summary

As a **new user**, I can **create an account** (sign up) with the required information so I can sign in later.

## Acceptance criteria

1. **Given** I am on the registration screen **when** I enter valid email (or phone), username, password, and confirmation **then** my account is created and I can proceed (e.g. return to main menu or sign in).  
2. **Given** a successful registration **when** I sign in with the same identifier **then** authentication succeeds.

## Automated verification (where available)

- *No dedicated automated UI test in this repo branch* — cover with **manual** steps and optional future Espresso on `RegisterActivity` + `LocalAccountStore`.

## Manual / evidence

- Screenshot: registration form filled, success path, then signed-in or main menu state (report pack).
