<#
.SYNOPSIS
  Create GitHub issues from docs/acceptance-tests/issues/AT-*.md (optional).

.NOTES
  GitHub sets issue opened time to "now". Each markdown file already contains
  **Record opened (course timeline):** in the first week of March 2026 — that
  remains the authoritative sprint record for grading narratives.

  Ensure label "acceptance-tests" exists on the repo, or remove -Label from the
  gh invocation below.
#>
param(
    [string] $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
)

$ErrorActionPreference = "Stop"
if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    Write-Error "Install GitHub CLI and run: gh auth login"
}
gh auth status 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) { Write-Error "Run: gh auth login" }

$notice = @"
> **Imported from repo:** ``docs/acceptance-tests/``  
> **Sprint dates** in the body (**Record opened (course timeline)**) are the course backlog record.  
> **GitHub** ``created_at`` is the time you ran this script — GitHub does not support backdating issues for normal repos.

"@

$items = @(
    @{ Path = "docs/acceptance-tests/issues/AT-01-2026-03-03-creating-account-signing-up.md"; Title = "[AT-1] Creating Account / Signing Up (sprint record 2026-03-03)" }
    @{ Path = "docs/acceptance-tests/issues/AT-02-2026-03-03-signing-up-missing-information.md"; Title = "[AT-2] Signing up with missing Information (sprint record 2026-03-03)" }
    @{ Path = "docs/acceptance-tests/issues/AT-03-2026-03-03-login-valid-credentials.md"; Title = "[AT-3] Loging In with valid credentials (sprint record 2026-03-03)" }
    @{ Path = "docs/acceptance-tests/issues/AT-04-2026-03-03-login-invalid-credentials.md"; Title = "[AT-4] Logging In with invalid credentials (sprint record 2026-03-03)" }
    @{ Path = "docs/acceptance-tests/issues/AT-05-2026-03-04-browse-available-events.md"; Title = "[AT-5] Browse Available Events (sprint record 2026-03-04)" }
    @{ Path = "docs/acceptance-tests/issues/AT-06-2026-03-04-view-event-details.md"; Title = "[AT-6] View Events Details (sprint record 2026-03-04)" }
    @{ Path = "docs/acceptance-tests/issues/AT-07-2026-03-04-reserve-ticket.md"; Title = "[AT-7] Reserve Ticket (sprint record 2026-03-04)" }
    @{ Path = "docs/acceptance-tests/issues/AT-08-2026-03-04-reservation-without-logins.md"; Title = "[AT-8] Reservation without Logins (sprint record 2026-03-04)" }
    @{ Path = "docs/acceptance-tests/issues/AT-09-2026-03-05-reserve-sold-out-event.md"; Title = "[AT-9] Reserve a Sold Out Event (sprint record 2026-03-05)" }
    @{ Path = "docs/acceptance-tests/issues/AT-10-2026-03-05-confirmation-reservation-email.md"; Title = "[AT-10] Confirmation of Reservation through Email (sprint record 2026-03-05)" }
    @{ Path = "docs/acceptance-tests/issues/AT-11-2026-03-05-cancellation-reservation.md"; Title = "[AT-11] Cancellation of Reservation (sprint record 2026-03-05)" }
    @{ Path = "docs/acceptance-tests/issues/AT-12-2026-03-06-admin-login-valid-credentials.md"; Title = "[AT-12] Login as Admin with valid credentials (sprint record 2026-03-06)" }
    @{ Path = "docs/acceptance-tests/issues/AT-13-2026-03-06-admin-login-invalid-credentials.md"; Title = "[AT-13] Login as Admin with invalid credentials (sprint record 2026-03-06)" }
    @{ Path = "docs/acceptance-tests/issues/AT-14-2026-03-06-admin-creates-event.md"; Title = "[AT-14] Admin creates Event (sprint record 2026-03-06)" }
    @{ Path = "docs/acceptance-tests/issues/AT-15-2026-03-06-admin-edits-event.md"; Title = "[AT-15] Admin edits Event (sprint record 2026-03-06)" }
    @{ Path = "docs/acceptance-tests/issues/AT-16-2026-03-07-admin-deletes-event.md"; Title = "[AT-16] Admin Deletes Event (sprint record 2026-03-07)" }
    @{ Path = "docs/acceptance-tests/issues/AT-17-2026-03-07-non-admin-cannot-access-admin.md"; Title = "[AT-17] Non-Admin User Can Not Acces Admin features (sprint record 2026-03-07)" }
)

Push-Location $RepoRoot
try {
    foreach ($it in $items) {
        $full = Join-Path $RepoRoot $it.Path
        if (-not (Test-Path -LiteralPath $full)) {
            Write-Warning "Skip missing: $full"
            continue
        }
        $body = $notice + (Get-Content -LiteralPath $full -Raw)
        gh issue create --title $it.Title --body $body --label "acceptance-tests"
        if ($LASTEXITCODE -ne 0) { throw "gh issue create failed for $($it.Title)" }
    }
}
finally {
    Pop-Location
}

Write-Host "Done. Close issues on GitHub or add labels as needed." -ForegroundColor Green
