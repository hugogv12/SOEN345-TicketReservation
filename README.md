# Ticket Reservation — SOEN 345 Project

**Cloud-based Ticket Reservation Application**  
SOEN 345: Software Testing, Verification and Quality Assurance — Winter 2026

## Overview

Android app (Java) for booking tickets to events: movies, concerts, travel, sports. Users can browse events, reserve tickets, and get digital confirmations. Built for **customers** and **event organizers/administrators**.

## Requirements (from project spec)

- **Customers:** register (email/phone), view events, search/filter by date/location/category, cancel reservations, confirmations via email/SMS.
- **Administrators:** add/edit/cancel events.
- **Non-functional:** concurrent users, cloud-based, high availability, simple UI.

## Tech stack

- **Language:** Java  
- **IDE:** Android Studio  
- **Testing:** JUnit 5 (unit/component), Android Instrumentation (UI)  
- **CI/CD:** GitHub Actions  
- **VCS:** GitHub  

See **[TESTING.md](TESTING.md)** for test strategy, case outline, and how to run unit vs instrumented tests. For **report copy-paste text and screenshot instructions**, see **[docs/REPORT_TESTING_AND_CI.md](docs/REPORT_TESTING_AND_CI.md)**.

**Design (use cases, architecture, target DB/UML):** **[docs/DESIGN.md](docs/DESIGN.md)** — living spec with git history.

**Persistence:** With **`supabase.url`** and **`supabase.anon.key`** in `local.properties`, the app loads events/reservations from **Supabase** (PostgREST + `book_event` / `cancel_reservation` RPCs). Without those keys, it falls back to **in-memory** sample data (used for CI/unit tests). SQL migrations live in **`supabase/migrations/`**.

## Project phases

| Phase | Focus |
|-------|--------|
| **1 (current)** | Project setup, runnable app, Git, CI skeleton |
| 2 | Requirements & design (use cases, architecture, DB) |
| 3 | Core features (registration, events, reservations) |
| 4 | Testing, report, presentation |

## Team

3 members. Progress reports every two weeks; presentation and report due **April 7th**.

## License & academic integrity

Respect [Concordia’s academic integrity and plagiarism rules](https://www.concordia.ca/conduct/academic-integrity/plagiarism.html).
