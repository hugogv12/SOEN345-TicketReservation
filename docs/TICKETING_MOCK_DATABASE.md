# Ticketing mock database (SQLite)

Separate from the in-memory `EventRepository` used by the current UI. This schema supports **realistic filtering and booking tests** with **120 events**, **50 users**, and **250 registrations**.

## Files

| Path | Purpose |
|------|---------|
| `app/src/main/assets/database/ticketing_schema.sql` | Reference `CREATE TABLE` + indexes (SQLite) |
| `app/src/main/assets/database/sample_inserts.sql` | Tiny example of `INSERT` shape |
| `database/TicketingContract.java` | Table/column names |
| `database/TicketingOpenHelper.java` | `SQLiteOpenHelper` — creates schema |
| `database/TicketingDataSeeder.java` | Deterministic seed (`Random` seed `42L`) |
| `database/model/Event.java`, `User.java`, `Registration.java` | Row POJOs |

## Categories (strict)

`Concert`, `Sports`, `Conference`, `Travel`, `Movie` only.

## Seed from Android

```java
// Once (e.g. debug menu, Application#onCreate in debug build):
TicketingDataSeeder.seedIfEmpty(context);

// Or replace all data:
TicketingOpenHelper helper = new TicketingOpenHelper(context);
SQLiteDatabase db = helper.getWritableDatabase();
TicketingDataSeeder.seed(db);
helper.close();
```

## Verify counts (Android Studio Database Inspector or `adb`)

```sql
SELECT COUNT(*) FROM users;        -- 50
SELECT COUNT(*) FROM events;       -- 120
SELECT COUNT(*) FROM registrations; -- 250
```

## Integration note

Wire `MainActivity` / repositories to this DB in a follow-up (Room or raw `SQLiteDatabase` + DAOs). Until then the app keeps using the existing singleton repositories.
