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

/**
 * Instrumented checks for {@link SessionPrefs} against real {@link android.content.SharedPreferences}.
 */
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
    public void given_clearedPrefs_whenQueried_thenNoUserAndEmptyStrings() {
        Context ctx = ApplicationProvider.getApplicationContext();
        assertFalse(SessionPrefs.hasUser(ctx));
        assertEquals("", SessionPrefs.getUserKey(ctx));
        assertEquals("", SessionPrefs.getEmail(ctx));
        assertEquals("", SessionPrefs.getUsername(ctx));
        assertEquals("", SessionPrefs.getAccessToken(ctx));
    }

    @Test
    public void given_setUserKeyWithEmail_whenRead_thenHasUserAndKeyMatches() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SessionPrefs.setUserKey(ctx, "student@example.com");
        assertTrue(SessionPrefs.hasUser(ctx));
        assertEquals("student@example.com", SessionPrefs.getUserKey(ctx));
        assertEquals("student@example.com", SessionPrefs.getEmail(ctx));
        assertEquals("student", SessionPrefs.getUsername(ctx));
        assertEquals("", SessionPrefs.getAccessToken(ctx));
    }

    @Test
    public void given_setUserKeyWithWhitespace_whenRead_thenTrimmed() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SessionPrefs.setUserKey(ctx, "  a@b.co  ");
        assertTrue(SessionPrefs.hasUser(ctx));
        assertEquals("a@b.co", SessionPrefs.getUserKey(ctx));
        assertEquals("a", SessionPrefs.getUsername(ctx));
    }

    @Test
    public void given_setUserKeyBlankOrNull_whenRead_thenCleared() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SessionPrefs.setUserKey(ctx, "x@y.z");
        assertTrue(SessionPrefs.hasUser(ctx));

        SessionPrefs.setUserKey(ctx, "");
        assertFalse(SessionPrefs.hasUser(ctx));

        SessionPrefs.setUserKey(ctx, "p@q.r");
        SessionPrefs.setUserKey(ctx, "   ");
        assertFalse(SessionPrefs.hasUser(ctx));

        SessionPrefs.setUserKey(ctx, "u@v.w");
        SessionPrefs.setUserKey(ctx, null);
        assertFalse(SessionPrefs.hasUser(ctx));
    }

    @Test
    public void given_setSession_whenRead_thenEmailUsernameTokenAndUserKey() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SessionPrefs.setSession(ctx, "  pat@example.com  ", "  Pat  ", "opaque-access-token");
        assertTrue(SessionPrefs.hasUser(ctx));
        assertEquals("pat@example.com", SessionPrefs.getEmail(ctx));
        assertEquals("Pat", SessionPrefs.getUsername(ctx));
        assertEquals("pat@example.com", SessionPrefs.getUserKey(ctx));
        assertEquals("opaque-access-token", SessionPrefs.getAccessToken(ctx));
    }

    @Test
    public void given_setSessionWithNullToken_whenRead_thenEmptyToken() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SessionPrefs.setSession(ctx, "a@b.c", "A", null);
        assertEquals("", SessionPrefs.getAccessToken(ctx));
        assertTrue(SessionPrefs.hasUser(ctx));
    }

    @Test
    public void given_session_whenClear_thenNoUserNoToken() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SessionPrefs.setSession(ctx, "pat@example.com", "Pat", "opaque-access-token");
        assertEquals("opaque-access-token", SessionPrefs.getAccessToken(ctx));
        SessionPrefs.clear(ctx);
        assertEquals("", SessionPrefs.getAccessToken(ctx));
        assertFalse(SessionPrefs.hasUser(ctx));
        assertEquals("", SessionPrefs.getEmail(ctx));
        assertEquals("", SessionPrefs.getUsername(ctx));
    }
}
