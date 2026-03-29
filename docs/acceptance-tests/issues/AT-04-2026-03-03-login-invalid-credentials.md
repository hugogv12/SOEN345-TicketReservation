## Sprint record

- **Issue ID:** AT-4  
- **Record opened (course timeline):** 2026-03-03  
- **Status:** Accepted  

---

## Summary

As a user, I **cannot** sign in with **wrong password** or **unknown account**, and I get appropriate feedback.

## Acceptance criteria

1. **Given** a registered user **when** I enter a wrong password **then** sign-in fails with feedback.  
2. **Given** no account for an identifier **when** I attempt sign-in **then** sign-in fails.

## Automated verification (where available)

- *No dedicated instrumented invalid-login test in this repo branch* — **manual** on `RegisterActivity` sign-in mode.

## Manual / evidence

- Screenshot or note: failed attempt message.
