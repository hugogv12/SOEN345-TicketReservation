package com.example.ticket_reservation;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnitRunner;

/**
 * Delegates Supabase test policy to {@link TicketTestRunnerBootstrap} (mirrored in JVM unit tests).
 * <p>
 * On headless CI emulators the activity decor view often stays {@code hasWindowFocus() == false},
 * which makes Espresso throw {@code RootViewWithoutFocusException}. A tiny focus nudge on resume
 * (GitHub Actions or generic emulator images only) avoids that without affecting physical devices.
 */
public class TicketTestRunner extends AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        TicketTestRunnerBootstrap.applyDefaultSupabaseTestPolicy();
        super.onCreate(arguments);
        registerHeadlessEmulatorWindowFocusWorkaround();
    }

    private static void registerHeadlessEmulatorWindowFocusWorkaround() {
        if (!shouldApplyFocusWorkaround()) {
            return;
        }
        Application app = (Application) InstrumentationRegistry.getInstrumentation()
                .getTargetContext()
                .getApplicationContext();
        final long postDelayMs = "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS")) ? 50L : 0L;
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                View root = activity.getWindow().getDecorView();
                Runnable nudge = () -> {
                    root.setFocusable(true);
                    root.setFocusableInTouchMode(true);
                    if (!root.hasWindowFocus()) {
                        root.requestFocusFromTouch();
                    }
                };
                if (postDelayMs > 0) {
                    root.postDelayed(nudge, postDelayMs);
                } else {
                    root.post(nudge);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private static boolean shouldApplyFocusWorkaround() {
        if ("true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"))) {
            return true;
        }
        String fp = Build.FINGERPRINT != null ? Build.FINGERPRINT : "";
        String model = Build.MODEL != null ? Build.MODEL : "";
        String hw = Build.HARDWARE != null ? Build.HARDWARE : "";
        if (fp.contains("generic") || fp.contains("unknown")) {
            return true;
        }
        if (model.contains("google_sdk") || model.contains("Emulator") || model.contains("Android SDK")) {
            return true;
        }
        return "goldfish".equals(hw) || "ranchu".equals(hw);
    }
}
