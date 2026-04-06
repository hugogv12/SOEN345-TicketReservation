package com.example.ticket_reservation;

import android.app.Activity;
import android.content.Intent;


public final class NavigationHelper {

    private NavigationHelper() {
    }


    public static void goToMainMenu(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
