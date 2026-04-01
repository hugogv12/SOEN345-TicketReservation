package com.example.ticket_reservation;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Smoke navigation from the main screen to secondary activities.
 */
@RunWith(AndroidJUnit4.class)
public class NavigationInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mainRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void mainToAdmin_showsAdminList() {
        onView(withId(R.id.button_admin)).perform(click());
        onView(withId(R.id.admin_events_list)).check(matches(isDisplayed()));
        onView(withId(R.id.button_add_event)).check(matches(isDisplayed()));
        onView(withId(R.id.button_back_to_menu)).perform(click());
        onView(withId(R.id.events_list)).check(matches(isDisplayed()));
    }

    @Test
    public void mainToMyReservations_showsListOrEmpty() {
        onView(withId(R.id.button_my_reservations)).perform(click());
        // ListView is hidden when adapter is empty (empty view is shown instead).
        onView(withText(R.string.my_reservations)).check(matches(isDisplayed()));
        onView(withId(R.id.button_back_to_menu)).perform(click());
        onView(withId(R.id.events_list)).check(matches(isDisplayed()));
    }
}
