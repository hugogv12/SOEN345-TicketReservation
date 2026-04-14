# Acceptance tests (issue-style backlog)

This folder holds **acceptance test specifications** aligned with the course backlog **AT-1 … AT-17** (same themes as the reference `khujista-01/SOEN345` acceptance issues), with a **course timeline** in the **first week of March 2026**.

## GitHub `created_at` vs sprint record

Normal **`gh issue create`** and the web UI set the issue **opened** time to **now**. They cannot backdate `created_at` on github.com.

Use these files as:

1. **Source of truth** for what was accepted and when (by **sprint record date** in each file).
2. **Bodies** for optional import: run **`scripts/Create-AcceptanceIssues.ps1`** after `gh auth login` to open real issues. The description includes the sprint block so the narrative stays correct even if GitHub’s clock says today.

Create labels **`acceptance-tests`** (and optionally **`Acceptance Tests`**) in the repo before running the script if you want labeled issues; the script applies **`acceptance-tests`** by default.

## Index (first week of March 2026)

| Record opened | ID | Title | File |
|---------------|-----|--------|------|
| 2026-03-03 | AT-1 | Creating Account / Signing Up | [issues/AT-01-2026-03-03-creating-account-signing-up.md](issues/AT-01-2026-03-03-creating-account-signing-up.md) |
| 2026-03-03 | AT-2 | Signing up with missing Information | [issues/AT-02-2026-03-03-signing-up-missing-information.md](issues/AT-02-2026-03-03-signing-up-missing-information.md) |
| 2026-03-03 | AT-3 | Loging In with valid credentials | [issues/AT-03-2026-03-03-login-valid-credentials.md](issues/AT-03-2026-03-03-login-valid-credentials.md) |
| 2026-03-03 | AT-4 | Logging In with invalid credentials | [issues/AT-04-2026-03-03-login-invalid-credentials.md](issues/AT-04-2026-03-03-login-invalid-credentials.md) |
| 2026-03-04 | AT-5 | Browse Available Events | [issues/AT-05-2026-03-04-browse-available-events.md](issues/AT-05-2026-03-04-browse-available-events.md) |
| 2026-03-04 | AT-6 | View Events Details | [issues/AT-06-2026-03-04-view-event-details.md](issues/AT-06-2026-03-04-view-event-details.md) |
| 2026-03-04 | AT-7 | Reserve Ticket | [issues/AT-07-2026-03-04-reserve-ticket.md](issues/AT-07-2026-03-04-reserve-ticket.md) |
| 2026-03-04 | AT-8 | Reservation without Logins | [issues/AT-08-2026-03-04-reservation-without-logins.md](issues/AT-08-2026-03-04-reservation-without-logins.md) |
| 2026-03-05 | AT-9 | Reserve a Sold Out Event | [issues/AT-09-2026-03-05-reserve-sold-out-event.md](issues/AT-09-2026-03-05-reserve-sold-out-event.md) |
| 2026-03-05 | AT-10 | Confirmation of Reservation through Email | [issues/AT-10-2026-03-05-confirmation-reservation-email.md](issues/AT-10-2026-03-05-confirmation-reservation-email.md) |
| 2026-03-05 | AT-11 | Cancellation of Reservation | [issues/AT-11-2026-03-05-cancellation-reservation.md](issues/AT-11-2026-03-05-cancellation-reservation.md) |
| 2026-03-06 | AT-12 | Login as Admin with valid credentials | [issues/AT-12-2026-03-06-admin-login-valid-credentials.md](issues/AT-12-2026-03-06-admin-login-valid-credentials.md) |
| 2026-03-06 | AT-13 | Login as Admin with invalid credentials | [issues/AT-13-2026-03-06-admin-login-invalid-credentials.md](issues/AT-13-2026-03-06-admin-login-invalid-credentials.md) |
| 2026-03-06 | AT-14 | Admin creates Event | [issues/AT-14-2026-03-06-admin-creates-event.md](issues/AT-14-2026-03-06-admin-creates-event.md) |
| 2026-03-06 | AT-15 | Admin edits Event | [issues/AT-15-2026-03-06-admin-edits-event.md](issues/AT-15-2026-03-06-admin-edits-event.md) |
| 2026-03-07 | AT-16 | Admin Deletes Event | [issues/AT-16-2026-03-07-admin-deletes-event.md](issues/AT-16-2026-03-07-admin-deletes-event.md) |
| 2026-03-07 | AT-17 | Non-Admin User Can Not Acces Admin features | [issues/AT-17-2026-03-07-non-admin-cannot-access-admin.md](issues/AT-17-2026-03-07-non-admin-cannot-access-admin.md) |

## Optional: open GitHub issues from these files

```powershell
pwsh scripts/Create-AcceptanceIssues.ps1
```

See [../../scripts/Create-AcceptanceIssues.ps1](../../scripts/Create-AcceptanceIssues.ps1).

Related: [TESTING.md](../../TESTING.md), [REPORT_TESTING_AND_CI.md](../REPORT_TESTING_AND_CI.md).
