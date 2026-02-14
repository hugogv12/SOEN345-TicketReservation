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

## Run in Android Studio

1. Clone the repo and open the project in **Android Studio**.
2. Sync Gradle (File → Sync Project with Gradle Files).
3. Run on emulator or device: **Run → Run 'app'** (or green Play button).

## Project phases

| Phase | Focus |
|-------|--------|
| **1 (current)** | Project setup, runnable app, Git, CI skeleton |
| 2 | Requirements & design (use cases, architecture, DB) |
| 3 | Core features (registration, events, reservations) |
| 4 | Testing, report, presentation |

## Team

3 members. Progress reports every two weeks; presentation and report due **April 7th**.

## Creating the GitHub repository

1. **Create a new repo on GitHub**  
   - Go to [github.com/new](https://github.com/new).  
   - Name it e.g. `ticket-reservation` or `SOEN345-TicketReservation`.  
   - Do **not** add a README, .gitignore, or license (this project already has them).

2. **Push this project** (from project root):

   ```bash
   git init
   git add .
   git commit -m "Phase 1: Android project setup, MainActivity, JUnit 5, CI workflow"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
   git push -u origin main
   ```

3. **Add collaborators**  
   Repo → Settings → Collaborators → add your 2 teammates.

## License & academic integrity

Respect [Concordia’s academic integrity and plagiarism rules](https://www.concordia.ca/conduct/academic-integrity/plagiarism.html).
