package com.example.ticket_reservation;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SessionPrefsInstrumentedTest {

    @Before
    public void clearPrefs() {
        Context ctx = ApplicationProvider.getApplicationContext();
        ctx.getSharedPreferences(SessionPrefs.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @Test
    public void hasUser_falseUntilSet() {
        Context ctx = ApplicationProvider.getApplicationContext();
        assertFalse(SessionPrefs.hasUser(ctx));
        assertEquals("", SessionPrefs.getUserKey(ctx));
    }

    @Test
    public void setUserKey_roundTrips() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SessionPrefs.setUserKey(ctx, "student@example.com");
        assertTrue(SessionPrefs.hasUser(ctx));
        assertEquals("student@example.com", SessionPrefs.getUserKey(ctx));
    }
}
