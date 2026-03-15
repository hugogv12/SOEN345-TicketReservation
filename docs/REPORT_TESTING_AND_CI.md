# Report material: testing strategy, CI, and screenshots

Copy the sections below into your SOEN 345 report (Word/Google Docs/LaTeX). Replace each **Figure** placeholder with your own screenshot after you run the steps.

---

## 1. Test strategy (short discussion)

We use a **layered test strategy** aligned with common industry practice:

1. **Unit tests** (JVM, `src/test/`) exercise isolated logic without the Android framework: filtering (`EventFilterTest`), reservation rules (`ReservationRulesTest`), and `BookingService` with **fresh, injected** repository instances (`BookingServiceTest`). These tests run quickly and give precise feedback on business rules.

2. **Integration-style tests** (JVM, `src/test/.../integration/`) verify the **booking pipeline** when `BookingService` uses the same **singleton** `EventRepository` and `ReservationRepository` as the running app (`BookingSingletonIntegrationTest`, tagged `integration`). This confirms that coordinated updates to catalog inventory and reservation storage work together, not only in isolation.

3. **System / UI tests** (instrumented, `src/androidTest/`) run on a device or emulator with **Espresso**: main screen, register flow, search empty state, navigation to event detail, and an **instrumented** singleton booking round-trip (`BookingServiceSingletonInstrumentedTest`, `MainFlowInstrumentedTest`).

4. **Continuous integration**: **GitHub Actions** builds the debug APK, runs unit tests on every push/PR, and runs **connected** (instrumented) tests on an Android emulator so UI and on-device integration checks are repeatable outside a single laptop.

This mix addresses **component correctness** (unit), **wiring between subsystems** (integration), and **user-visible behaviour** (UI/system), which maps well to rubric expectations for test planning and tool use.

---

## 2. Traceability (examples — extend in your report)

| Requirement area | Automated tests |
|------------------|-----------------|
| Search / filter events | `EventFilterTest`, Espresso search empty state |
| Reservation rules (capacity, canceled) | `ReservationRulesTest` |
| Book / cancel / inventory | `BookingServiceTest`, `BookingSingletonIntegrationTest`, `BookingServiceSingletonInstrumentedTest` |
| Browse / register / detail UI | `MainFlowInstrumentedTest` |

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

## 4. One paragraph for “limitations / future work” (optional)

Automated tests use **in-memory** stores; they do not replace load, security, or end-to-end tests against a real cloud backend. Email/SMS confirmations are not exercised by automation in this phase; manual or future integration tests could target those once an API exists.

---

## File map (for your appendix)

| Path | Role |
|------|------|
| `app/src/test/java/.../logic/` | Unit: filters, rules |
| `app/src/test/java/.../data/BookingServiceTest.java` | Unit: booking with isolated repos |
| `app/src/test/java/.../integration/` | Integration-style: singleton pipeline |
| `app/src/androidTest/.../MainFlowInstrumentedTest.java` | UI / system flows |
| `app/src/androidTest/.../BookingServiceSingletonInstrumentedTest.java` | Instrumented integration |
| `.github/workflows/ci.yml` | CI definition |
| `TESTING.md` | Repo-level testing overview |
