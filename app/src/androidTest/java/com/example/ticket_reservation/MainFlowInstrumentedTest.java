package com.example.ticket_reservation;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

/**
 * System-level UI tests (Espresso) for browse, search, register navigation, and event detail.
 */
@RunWith(AndroidJUnit4.class)
public class MainFlowInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void clearSession() {
        Context ctx = ApplicationProvider.getApplicationContext();
        ctx.getSharedPreferences(SessionPrefs.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @Test
    public void packageNameMatchesManifest() {
        Context app = ApplicationProvider.getApplicationContext();
        assertEquals("com.example.ticket_reservation", app.getPackageName());
    }

    @Test
    public void mainScreen_showsEventListSearchAndRegister() {
        onView(withId(R.id.events_list)).check(matches(isDisplayed()));
        onView(withId(R.id.search_events)).check(matches(isDisplayed()));
        onView(withId(R.id.button_register)).check(matches(isDisplayed()));
    }

    @Test
    public void registerButton_opensRegisterForm_andBackReturns() {
        onView(withId(R.id.button_register)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isDisplayed()));
        onView(withId(R.id.register_submit)).check(matches(isDisplayed()));
        onView(withId(R.id.button_back_to_menu)).perform(click());
        onView(withId(R.id.events_list)).check(matches(isDisplayed()));
    }

    @Test
    public void registerValidEmail_returnsToMain() {
        onView(withId(R.id.button_register)).perform(click());
        onView(withId(R.id.register_email)).perform(
                replaceText("espresso.test@example.com"),
                closeSoftKeyboard());
        onView(withId(R.id.register_submit)).perform(click());
        onView(withId(R.id.events_list)).check(matches(isDisplayed()));
    }

    @Test
    public void searchWithNoMatch_showsEmptyState() {
        onView(withId(R.id.search_events)).perform(
                replaceText("no_match_for_sure_xyz"),
                closeSoftKeyboard());
        onView(withId(R.id.events_empty)).check(matches(isDisplayed()));
    }

    @Test
    public void clickFirstListItem_opensEventDetail() {
        onData(anything())
                .inAdapterView(withId(R.id.events_list))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.detail_title)).check(matches(isDisplayed()));
        onView(withId(R.id.button_reserve)).check(matches(isDisplayed()));
        onView(withId(R.id.button_back_to_menu)).perform(click());
        onView(withId(R.id.events_list)).check(matches(isDisplayed()));
    }
}
