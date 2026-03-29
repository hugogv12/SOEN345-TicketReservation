## Sprint record

- **Issue ID:** AT-5  
- **Record opened (course timeline):** 2026-03-04  
- **Status:** Accepted  

---

## Summary

As a **guest or signed-in user**, I can **browse available events** on the main screen (list, filters/search as implemented).

## Acceptance criteria

1. **Given** events are loaded **when** I open the app **then** I see a list or a clear empty state.  
2. **When** I use search **then** the list filters; **when** no event matches **then** I see the empty state.  
3. **When** I clear search **then** the list shows again.

## Automated verification (where available)

- `MainFlowInstrumentedTest.user_filtersEventsBySearch_thenClearsSearch_seesListAgain`  
- `EventFilterTest` / `FilterCriteriaTest` (JVM) for matching rules

## Manual / evidence

- Screenshot: populated list + filtered empty state + cleared search.
