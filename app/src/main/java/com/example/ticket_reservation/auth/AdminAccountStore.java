package com.example.ticket_reservation.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.ticket_reservation.logic.LoginIdentifier;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Offline admin operator accounts (separate from {@link LocalAccountStore} customer accounts).
 * Same hashing pattern; different prefs name and salt so admin and customer logins never mix.
 */
public final class AdminAccountStore {

    private static final String PREFS = "ticket_admin_accounts";
    private static final String KEY_PREFIX = "adm_";
    private static final String SALT = "soen345-ticket-admin-local";

    private AdminAccountStore() {
    }

    public static boolean register(Context context, String email, String username, String password) {
        String key = keyFor(email);
        SharedPreferences p = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (p.contains(key)) {
            return false;
        }
        String hash = hash(email, password);
        p.edit().putString(key, username + "\n" + hash).apply();
        return true;
    }

    public static boolean signIn(Context context, String email, String password) {
        String key = keyFor(email);
        SharedPreferences p = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String stored = p.getString(key, null);
        if (stored == null) {
            return false;
        }
        int nl = stored.indexOf('\n');
        if (nl < 0) {
            return false;
        }
        String hash = stored.substring(nl + 1);
        return hash.equals(hash(email, password));
    }

    public static String getUsername(Context context, String email) {
        String key = keyFor(email);
        SharedPreferences p = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String stored = p.getString(key, null);
        if (stored == null) {
            return "";
        }
        int nl = stored.indexOf('\n');
        return nl > 0 ? stored.substring(0, nl) : "";
    }

    static String stableLoginId(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        String normalized = LoginIdentifier.normalize(trimmed);
        if (!normalized.isEmpty()) {
            return normalized;
        }
        return trimmed.toLowerCase(Locale.US);
    }

    static String keyFor(String email) {
        String norm = stableLoginId(email);
        return KEY_PREFIX
                + Base64.encodeToString(norm.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    static String hash(String email, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String id = stableLoginId(email);
            String payload = SALT + "\n" + id + "\n" + password;
            byte[] dig = md.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(dig.length * 2);
            for (byte b : dig) {
                sb.append(String.format(Locale.US, "%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
