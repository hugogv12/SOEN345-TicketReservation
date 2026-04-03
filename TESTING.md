# Testing — SOEN 345 Ticket Reservation

This document summarizes the **test strategy** and how to run tests for the rubric (unit, integration-style, and system/UI).

## Strategy overview

| Level | Scope | Where | Purpose |
|--------|--------|--------|---------|
| **Unit** | Pure Java logic and services without Android framework | `app/src/test/...` | Fast feedback on filters, booking rules, and `BookingService` behaviour |
| **Integration-style** | Services + in-memory repositories together | `BookingServiceTest` (unit folder) | Validates booking and cancellation against shared singleton data shapes |
| **System / UI** | Real activities on device/emulator | `app/src/androidTest/...` | End-to-end checks for browse, search, register flow, and event detail navigation |

## Automated test cases (high level)

| ID | Requirement area | Automated check |
|----|------------------|-----------------|
| TC-U-FILTER | Search / filter | `EventFilterTest` — criteria matching on sample events |
| TC-U-RULES | Reservation validity | `ReservationRulesTest` — capacity, canceled events |
| TC-U-BOOK | Booking & cancel | `BookingServiceTest` — reserve, cancel restores inventory, wrong user cannot cancel |
| TC-I-PACKAGE | App identity | `MainFlowInstrumentedTest.packageNameMatchesManifest` |
| TC-I-MAIN | Event list UI | Main screen shows list, search field, register button |
| TC-I-REGISTER | Registration | Opens register screen; valid email returns to main |
| TC-I-SEARCH | Filter UI | Search text with no match shows empty state |
| TC-I-DETAIL | Event detail | First list row opens detail with reserve action; back returns to list |

For the formal report, add **manual test results** and **screenshots** (Espresso does not replace documented evidence).

## Commands

```bash
# Unit tests (runs on JVM — used in CI)
./gradlew test

# UI / instrumented tests — requires a running emulator or USB device
./gradlew connectedDebugAndroidTest
```

## Continuous integration

GitHub Actions runs `assembleDebug` and `test` on every push/PR. A second job runs **`connectedDebugAndroidTest`** on an API 29 emulator so UI tests execute in CI as well.

## Collaboration

- Keep **`main`** green: CI must pass before merging.
- Prefer small branches and pull requests so history shows review and integration practice.
