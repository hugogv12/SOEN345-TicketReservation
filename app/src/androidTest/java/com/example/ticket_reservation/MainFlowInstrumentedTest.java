package com.example.ticket_reservation;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Focused main-screen flows that are not already covered by {@link AdminAndUserBookingInstrumentedTest}
 * (search/clear, sign-in mode, guest reserve gate).
 */
@RunWith(AndroidJUnit4.class)
public class MainFlowInstrumentedTest {

    private static final String SEEDED_TECH_CONFERENCE = "Tech Conference";

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private CountingIdlingResource authIdle;

    @Before
    public void clearSessionAndAuthIdle() {
        Context ctx = ApplicationProvider.getApplicationContext();
        ctx.getSharedPreferences(SessionPrefs.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();

        authIdle = new CountingIdlingResource("auth");
        AuthAsyncIdling.setGate(new AuthAsyncIdling.Gate() {
            @Override
            public void enter() {
                authIdle.increment();
            }

            @Override
            public void exit() {
                authIdle.decrement();
            }
        });
        IdlingRegistry.getInstance().register(authIdle);
    }

    @After
    public void tearDownAuthIdle() {
        AuthAsyncIdling.setGate(null);
        if (authIdle != null) {
            IdlingRegistry.getInstance().unregister(authIdle);
        }
    }

    @Test
    public void user_togglesAccountToSignIn_seesFewerFields() {
        // Footer actions are pinned; scrollTo() only works inside scrollable ancestors.
        onView(withId(R.id.button_register)).perform(click());
        onView(withId(R.id.mode_sign_in)).perform(click());
        onView(withId(R.id.layout_username)).check(matches(not(isDisplayed())));
        onView(withId(R.id.layout_confirm_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.register_email)).check(matches(isDisplayed()));
        onView(withId(R.id.register_password)).check(matches(isDisplayed()));
        onView(withId(R.id.button_back_to_menu)).perform(click());
        onView(withId(R.id.events_recycler)).check(matches(isDisplayed()));
    }

    @Test
    public void user_filtersEventsBySearch_thenClearsSearch_seesListAgain() {
        onView(withId(R.id.search_events)).perform(
                replaceText("no_match_for_sure_xyz"),
                closeSoftKeyboard());
        onView(withId(R.id.events_empty)).check(matches(isDisplayed()));
        onView(withId(R.id.search_events)).perform(
                replaceText(""),
                closeSoftKeyboard());
        onView(withId(R.id.events_recycler)).check(matches(isDisplayed()));
    }

    @Test
    public void guest_findsSeededConference_tapsReserve_seesSignInRequiredDialog() {
        onView(withId(R.id.search_events)).perform(
                replaceText("Tech"),
                closeSoftKeyboard());
        onView(withId(R.id.events_recycler))
                .perform(actionOnItem(hasDescendant(withText(SEEDED_TECH_CONFERENCE)), click()));
        onView(withId(R.id.detail_title)).check(matches(withText(SEEDED_TECH_CONFERENCE)));
        onView(withId(R.id.button_reserve)).perform(scrollTo(), click());
        onView(withText(R.string.register_required_title)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(android.R.string.cancel)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.button_back_to_menu)).perform(click());
        onView(withId(R.id.events_recycler)).check(matches(isDisplayed()));
    }
}
