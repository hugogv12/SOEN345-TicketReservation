# Testing — SOEN 345 Ticket Reservation

This document summarizes the **test strategy** and how to run tests for the rubric (unit, integration-style, and system/UI).

**Acceptance tests AT-1 … AT-17 (issue-style, sprint record first week of March 2026):** [`docs/acceptance-tests/README.md`](docs/acceptance-tests/README.md) — optional `scripts/Create-AcceptanceIssues.ps1` to open GitHub issues (GitHub cannot backdate `created_at`; each file states the course timeline).

## Strategy overview

| Level | Scope | Where | Purpose |
|--------|--------|--------|---------|
| **Unit** | Pure Java logic and services without Android framework | `app/src/test/...` | Fast feedback on filters, booking rules, and `BookingService` behaviour |
| **Integration-style (JVM)** | `BookingService` + **singleton** `EventRepository` / `ReservationRepository` (same wiring as the app); **MockWebServer** HTTP contracts for `SupabaseRest` | `app/src/test/.../integration/BookingSingletonIntegrationTest`, `app/src/test/.../data/SupabaseRestMockWebServerIntegrationTest` | Coordinated stores + PostgREST/RPC paths and payloads without a real Supabase project |
| **Unit (isolated service)** | `BookingService` with **new** repository instances per test | `BookingServiceTest` | Fast, isolated booking/cancel rules without singleton state |
| **System / UI** | Real activities on device/emulator | `app/src/androidTest/...` | Espresso flows (main screen, booking journeys, validation) |

## Automated test cases (high level)

| ID | Requirement area | Automated check |
|----|------------------|-----------------|
| TC-U-FILTER | Search / filter | `EventFilterTest` — criteria matching on sample events |
| TC-U-RULES | Reservation validity | `ReservationRulesTest` — capacity, canceled events |
| TC-U-BOOK | Booking & cancel (isolated) | `BookingServiceTest` — reserve, cancel restores inventory, wrong user cannot cancel |
| TC-P-BOOK | Booking hot path at scale | `BookingServicePerformanceTest` — one `book` against ~10k in-memory events within 2s |
| TC-I-BOOK | Booking pipeline (singletons) | `BookingSingletonIntegrationTest` — shared catalog + reservations; capacity across calls |
| TC-I-E2E | Customer / admin journeys | `AdminAndUserBookingInstrumentedTest` — register, book, **admin edit saved event**, **sold-out second reserve**, cancel catalog, sign-out |
| TC-I-MAIN | Main screen & guest gate | `MainFlowInstrumentedTest` — sign-in mode, search clear, guest reserve dialog |
| TC-I-REGISTER | Client-side validation | `RegisterValidationInstrumentedTest` — passwords, contact, username |
| TC-I-SEARCH | Filter UI | Search with no match shows empty state; clear restores list |
| TC-I-DETAIL | Event detail | Seeded event from search opens detail; reserve requires sign-in when logged out |

For the formal report, add **manual test results** and **screenshots** (Espresso does not replace documented evidence). Ready-to-paste **strategy text and screenshot checklist**: [`docs/REPORT_TESTING_AND_CI.md`](docs/REPORT_TESTING_AND_CI.md). **Security / OWASP-style manual evidence:** [`docs/SECURITY_TESTING.md`](docs/SECURITY_TESTING.md). **Inventory decisions, prioritized upgrades (P0–P3), CI notes, and gap analysis:** [`docs/TESTING_AUDIT.md`](docs/TESTING_AUDIT.md).

## Commands

```bash
# Unit tests (runs on JVM — used in CI)
./gradlew test

# UI / instrumented tests — requires a running emulator or USB device
./gradlew connectedDebugAndroidTest
```

## Performance beyond JVM smoke tests

Automated budgets live under `app/src/test/.../performance/` (`EventFilterPerformanceTest`, `BookingServicePerformanceTest`). For **device-level** evidence (cold start, jank, scroll), use one or more of:

1. **Android Studio Profiler** — install a debug build, capture **CPU** while exercising **list → search → detail → reserve**; export or screenshot for the report.
2. **Macrobenchmark / Baseline Profile** (optional, not wired in CI here) — add an `androidx.benchmark` module to measure **startup** and **RecyclerView/ListView** scroll on a physical device or ATD; run on release candidates or a scheduled workflow if the team adopts it. **Audit (P2):** document cold start + scroll steps and report evidence when you run these — see the **P2** row in [`docs/TESTING_AUDIT.md`](docs/TESTING_AUDIT.md).

## Continuous integration

GitHub Actions runs `assembleDebug` and `test` on every push/PR. A second job runs **`connectedDebugAndroidTest`** on an API 29 emulator so UI tests execute in CI as well. If instrumented runtime grows, consider a **smoke-only** job using `-Pandroid.testInstrumentationRunnerArguments.class=…` for a subset of classes (see [`docs/TESTING_AUDIT.md`](docs/TESTING_AUDIT.md) §3).

## Collaboration

- Keep **`main`** green: CI must pass before merging.
- Prefer small branches and pull requests so history shows review and integration practice.
- Use [`.github/pull_request_template.md`](.github/pull_request_template.md) so each PR records traceability and test evidence.

---

## Rubric alignment (SOEN 345 — self-audit for “Excellent”)

| Rubric area | Evidence in this repo |
|-------------|------------------------|
| **Test plan & strategy** | This file + layered model: **unit** (`src/test`), **integration-style JVM** (`BookingSingletonIntegrationTest`), **system/UI** (`src/androidTest`), **performance** (`EventFilterPerformanceTest`, `BookingServicePerformanceTest`; device guidance above), **security** (`LoginIdentifierTest`, `LocalAccountStoreHashingTest`, `LocalAccountStoreInstrumentedTest`, `SessionPrefsInstrumentedTest`; threat model + manual checklist → [`docs/SECURITY_TESTING.md`](docs/SECURITY_TESTING.md)), **user-validation UI** (`RegisterValidationInstrumentedTest`). |
| **Test case documentation** | Traceability tables in [`docs/REPORT_TESTING_AND_CI.md`](docs/REPORT_TESTING_AND_CI.md); screenshot checklist for the report; TC-IDs in `BookingServiceTest` JavaDoc; keep/delete + roadmap in [`docs/TESTING_AUDIT.md`](docs/TESTING_AUDIT.md). |
| **CI / VCS / collaboration** | [`.github/workflows/ci.yml`](.github/workflows/ci.yml): `assembleDebug`, `test`, `connectedDebugAndroidTest` on emulator; Git history + PR template. |
| **Functional / NFR** | [`docs/DESIGN.md`](docs/DESIGN.md) §2.3–2.4 maps requirements to code and tests; §3 matches `supabase/migrations/`. |

### Full automated test inventory (Java)

**Unit (`app/src/test`):** `DateUtilsTest`, `EventInventoryTest`, `BookingServiceTest`, `EventRepositoryTest`, `ReservationRepositoryTest`, `SupabaseRestParsingTest` (PostgREST JSON → domain, no network), `SupabaseRestMockWebServerIntegrationTest` (tag `integration`: real `SupabaseRest` HTTP vs **MockWebServer**), `EventFilterTest`, `FilterCriteriaTest`, `ReservationRulesTest`, `BookingSingletonIntegrationTest`, `LoginIdentifierTest`, `auth/LocalAccountStoreHashingTest` (offline account key + hash contract), `EventFilterPerformanceTest`, `BookingServicePerformanceTest`, `TicketTestRunnerBootstrapTest` (policy mirrored by `TicketTestRunner`).

**Instrumented (`app/src/androidTest`):** `MainFlowInstrumentedTest`, `AdminAndUserBookingInstrumentedTest`, `ReservationLifecycleInstrumentedTest` (search → book → **Email/SMS share** intents via Espresso Intents; **My reservations → cancel**), `SessionPrefsInstrumentedTest`, `LocalAccountStoreInstrumentedTest`, `RegisterValidationInstrumentedTest`, `SupabaseRestInstrumentedTest` (optional **live** `GET /events` when `supabase.url` + `supabase.anon.key` are in `local.properties`; otherwise **skipped**), `TicketTestRunner` (custom runner; policy applied in `onCreate` via `TicketTestRunnerBootstrap`, also covered by `TicketTestRunnerBootstrapTest`).
