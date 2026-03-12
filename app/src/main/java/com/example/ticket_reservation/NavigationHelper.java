package com.example.ticket_reservation;

import android.app.Activity;
import android.content.Intent;

/**
 * Shared navigation back to the main events screen.
 */
public final class NavigationHelper {

    private NavigationHelper() {
    }

    /**
     * Opens {@link MainActivity} and finishes the current screen. Activities above Main in the
     * stack are cleared so the user lands on the event list.
     */
    public static void goToMainMenu(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
