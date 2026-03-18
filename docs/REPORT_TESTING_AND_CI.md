# Report material: testing strategy, CI, and screenshots

Copy the sections below into your SOEN 345 report (Word/Google Docs/LaTeX). Replace each **Figure** placeholder with your own screenshot after you run the steps.

---

## 1. Test strategy (short discussion)

We use a **layered test strategy** aligned with common industry practice:

1. **Unit tests** (JVM, `src/test/`) exercise isolated logic without the Android framework: filtering (`EventFilterTest`), reservation rules (`ReservationRulesTest`), and `BookingService` with **fresh, injected** repository instances (`BookingServiceTest`). These tests run quickly and give precise feedback on business rules.

2. **Integration-style tests** (JVM, `src/test/.../integration/`) verify the **booking pipeline** when `BookingService` uses the same **singleton** `EventRepository` and `ReservationRepository` as the running app (`BookingSingletonIntegrationTest`, tagged `integration`). This confirms that coordinated updates to catalog inventory and reservation storage work together, not only in isolation.

3. **System / UI tests** (instrumented, `src/androidTest/`) run on a device or emulator with **Espresso**: main screen flows (`MainFlowInstrumentedTest`), end-to-end customer/admin journeys (`AdminAndUserBookingInstrumentedTest`), register validation, session and local-account checks, and optional live Supabase smoke when keys are present (`SupabaseRestInstrumentedTest`).

4. **Continuous integration**: **GitHub Actions** builds the debug APK, runs unit tests on every push/PR, and runs **connected** (instrumented) tests on an Android emulator so UI and on-device integration checks are repeatable outside a single laptop.

5. **Manual user acceptance** (§4): numbered scenarios with **P/F per milestone** for first-time and return users, admin paths, and error recovery — complements Espresso for **SUS**, **task-time**, **TalkBack / font scale**, and **real** email/SMS handoff.

This mix addresses **component correctness** (unit), **wiring between subsystems** (integration), and **user-visible behaviour** (UI/system), which maps well to rubric expectations for test planning and tool use.

---

## 2. Traceability (extend / paste into report)

| Requirement / UC | Automated tests |
|--------------------|-----------------|
| Register **email or phone** (UC-01) | `LoginIdentifierTest`, `AdminAndUserBookingInstrumentedTest` (register + book), `LocalAccountStoreInstrumentedTest`, `RegisterValidationInstrumentedTest` |
| Search / filter (UC-02) | `EventFilterTest`, Espresso search empty state (`MainFlowInstrumentedTest`) |
| Reservation rules (UC-04) | `ReservationRulesTest`, `EventInventoryTest` |
| Book / cancel / inventory (UC-04, UC-06) | `BookingServiceTest`, `BookingSingletonIntegrationTest` |
| Browse / detail UI (UC-02, UC-03) | `MainFlowInstrumentedTest`, `AdminAndUserBookingInstrumentedTest` |
| Session / sign-out | `SessionPrefsInstrumentedTest` |
| Filter / booking performance (NFR) | `EventFilterPerformanceTest`, `BookingServicePerformanceTest` |
| Local auth security (NFR) | `LoginIdentifierTest`, `LocalAccountStoreHashingTest`, `LocalAccountStoreInstrumentedTest`; manual checklist [`SECURITY_TESTING.md`](SECURITY_TESTING.md) |
| Usability / exploratory / a11y / real mail–SMS (NFR) | Espresso approximates tasks; **manual** milestone scenarios **§4** (P/F columns) + optional SUS / TalkBack / font scale in Notes |

**Design cross-reference:** full FR/NFR tables with evidence → [`DESIGN.md`](DESIGN.md) §2.3–2.4.

---

## 3. Screenshot checklist (paste into report as figures)

### Figure A — Unit tests passing (IDE)

1. Open the project in **Android Studio**.
2. Right-click `app/src/test/java` → **Run ‘All Tests’** (or run the `test` package).
3. Capture the **Run** window showing **green** results.

**Caption suggestion:** *Figure A: JUnit 5 unit test run in Android Studio (logic and BookingService with isolated repositories).*

---

### Figure B — Unit tests passing (command line)

1. In the project root, run: `./gradlew test` (macOS/Linux) or `gradlew.bat test` (Windows).
2. Screenshot the terminal when **BUILD SUCCESSFUL** appears, or the HTML report under `app/build/reports/tests/testDebugUnitTest/index.html` opened in a browser.

**Caption suggestion:** *Figure B: Gradle `test` task completing successfully (JVM unit tests).*

---

### Figure C — GitHub Actions (CI overview)

1. On GitHub, open the repo → **Actions**.
2. Open a **successful** workflow run (both **build-and-unit-test** and **instrumented-ui** green if applicable).
3. Screenshot the run summary (checkmarks and job list).

**Caption suggestion:** *Figure C: Continuous integration — GitHub Actions workflow success for build, unit tests, and instrumented UI tests.*

---

### Figure D — Instrumented / UI tests (optional second CI screenshot)

1. Inside the successful run, click the **instrumented-ui** job.
2. Screenshot the log tail showing `connectedDebugAndroidTest` / **BUILD SUCCESSFUL**.

**Caption suggestion:** *Figure D: Emulator job running Espresso and instrumented integration tests in CI.*

---

### Figure E — Local instrumented run (optional)

1. Start an emulator or connect a device with USB debugging.
2. Run: `./gradlew connectedDebugAndroidTest` (or Android Studio → right-click `androidTest` → Run).
3. Screenshot green results.

**Caption suggestion:** *Figure E: Local instrumented test run (device/emulator).*

---

### Figure F — Confirmation: email / SMS drafts (manual)

1. Complete a reservation until **Reservation confirmed** appears.
2. Tap **Email ticket** and screenshot the chooser or Gmail draft (optional).
3. Tap **Text ticket** and screenshot the SMS composer with prefilled body (optional).

**Caption suggestion:** *Figure F: Post-booking confirmation — user-triggered email and SMS drafts (no server-side messaging).*

---

## 4. Manual user-testing scenario checklist (human runs per milestone)

Espresso flows approximate **task success** (search, book, share intent, cancel, register validation). They do **not** replace **subjective usability** (e.g. SUS), **task-time baselines**, **accessibility** (TalkBack, large font / display size), or **real device handoff** to mail/SMS clients — those stay **manual**. Use the table below for **repeatable human runs** at each project milestone: copy into the report, add **date / tester initials**, and mark **P** (pass) or **F** (fail) in the milestone columns. Expand “Notes” with SUS scores, timings, or a11y findings as your rubric requires.

| # | Persona | Scenario (short) | Acceptance (tester checks) | M1 | M2 | Final | Notes |
|---|---------|------------------|----------------------------|----|----|-------|-------|
| 1 | First-time user | Cold start → register (email *or* phone) → land on catalog → open an event → book within rules → **Reservation confirmed** | Account created; no crash; confirmation shows event + qty; optional: **Email ticket** / **Text ticket** opens system chooser or draft with plausible body | | | | Real device recommended for SMS/mail |
| 2 | First-time user | **Validation & recovery** on Register | Invalid/empty fields show clear errors; duplicate or weak paths behave as designed; user can correct input and complete registration **or** see honest duplicate message | | | | Align with `RegisterValidationInstrumentedTest`; add edge cases not automated |
| 3 | Return user | Sign out (if offered) or reinstall → **sign in** → **My reservations** → open a booking → **cancel** | Reservations list matches expectations after login; cancel succeeds; list and/or inventory updates without stale UI | | | | Pair with cloud or local account per your demo config |
| 4 | Return user | **Search / filter** → open different event → book again | Filtered list matches criteria; second booking completes; confirmation distinct from prior booking | | | | Record rough **task-time** (optional) |
| 5 | Admin | Open **Admin** → add **or** edit an event → save → return to main list as customer | Changes visible on browse list/detail; no silent failure; sold-out / date fields if applicable still sensible | | | | Use admin-capable account from `DESIGN.md` / seed |
| 6 | Admin + customer | **Conflict path**: e.g. reduce capacity / sold-out / over-quantity reserve | User sees readable denial; no ghost reservation; app remains stable | | | | Mirrors instrumented sold-out paths; validate copy on device |
| 7 | Any | **Error recovery**: bad password → success; or airplane mode / backend error → retry | After mistake, user can recover without reinstall; failed network shows understandable feedback; **no corrupted** local session after recovery | | | | Optional: logcat only for dev, not end user |
| 8 | Any | **Session hygiene**: sign out → confirm guest/unauth behaviour; relaunch app | No access to private screens without auth; session prefs cleared per product rules (`SessionPrefsInstrumentedTest` manual echo) | | | | Optional: **TalkBack** + **font scale 200%** on scenarios 1 or 3 — document P/F in Notes |

**How to use:** at **M1 / M2 / Final** (or your named milestones), run scenarios **1–8** once per build you ship to markers; mark **P/F**; attach screenshots only for failures or rubric-required figures. For **SUS**, add a separate questionnaire run (same sprint) and reference totals in Notes. **Accessibility** spot-check: enable TalkBack, complete scenario 1 or 3; bump **Display size** and **Font size** — confirm no clipped CTAs.

---

## 5. Limitations / future work (honest paragraph for markers)

Most automated tests run against **in-memory** repositories on the JVM (fast, deterministic). **Cloud-backed** flows are covered by the same business logic plus **instrumented** tests and manual checks with `local.properties` keys. **Subjective usability, SUS, strict task-time, and accessibility** are not automated — use the **milestone P/F table** in §4. **Load / stress** testing is not automated beyond JVM smoke tests (`EventFilterPerformanceTest`, `BookingServicePerformanceTest`); device-level timing is documented in [`TESTING.md`](../TESTING.md) (Profiler / Macrobenchmark). **Penetration / OWASP** testing is out of course scope; the repo includes targeted **security** tests for offline accounts and session clearing.

**Confirmations:** the app opens the user’s **email** and **SMS** apps with prefilled drafts (`ReservationConfirmationActivity`); there is no server-side transactional mail/SMS pipeline to automate — capture **manual screenshots** of those intents for the report.

---

## 6. File map (for your appendix)

| Path | Role |
|------|------|
| `app/src/test/java/.../logic/` | Unit: filters, rules |
| `app/src/test/java/.../data/BookingServiceTest.java` | Unit: booking with isolated repos |
| `app/src/test/java/.../integration/` | Integration-style: singleton pipeline |
| `app/src/androidTest/.../MainFlowInstrumentedTest.java` | UI / system flows |
| `app/src/androidTest/.../AdminAndUserBookingInstrumentedTest.java` | End-to-end UI (book, admin add/edit, sold-out reserve, cancel) |
| `.github/workflows/ci.yml` | CI definition |
| `TESTING.md` | Repo-level testing overview |
