package com.example.ticket_reservation.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TicketingDataSeederTest {

    @Test
    public void seed_producesExpectedRowCounts() {
        var context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(TicketingContract.DB_NAME);

        TicketingOpenHelper helper = new TicketingOpenHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        TicketingDataSeeder.seed(db);

        assertEquals(TicketingDataSeeder.TARGET_USER_COUNT, count(db, TicketingContract.Users.TABLE));
        assertEquals(TicketingDataSeeder.TARGET_EVENT_COUNT, count(db, TicketingContract.Events.TABLE));
        assertEquals(TicketingDataSeeder.TARGET_REGISTRATION_COUNT,
                count(db, TicketingContract.Registrations.TABLE));

        assertEquals(0, count(db, TicketingContract.Registrations.TABLE + " r INNER JOIN "
                + TicketingContract.Events.TABLE + " e ON r." + TicketingContract.Registrations.EVENT_ID
                + "=e." + TicketingContract.Events.ID + " WHERE e." + TicketingContract.Events.STATUS
                + "='cancelled'"));

        helper.close();
    }

    private static int count(SQLiteDatabase db, String from) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + from, null)) {
            c.moveToFirst();
            return c.getInt(0);
        }
    }
}
