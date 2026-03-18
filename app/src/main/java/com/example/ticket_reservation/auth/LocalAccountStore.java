package com.example.ticket_reservation.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import com.example.ticket_reservation.logic.LoginIdentifier;

/**
 * Offline demo accounts when Supabase is not configured (CI / local without keys).
 * Passwords stored as SHA-256 hex(salt + canonicalId + password).
 * {@code email} parameters are login identifiers: normalized the same way as {@link LoginIdentifier}
 * so e.g. {@code (514) 555-0100} and {@code +15145550100} refer to one account.
 */
public final class LocalAccountStore {

    private static final String PREFS = "ticket_local_accounts";
    private static final String KEY_PREFIX = "acct_";
    private static final String SALT = "soen345-ticket-local";

    private LocalAccountStore() {
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

    /** Stable prefs key + hash input: canonical phone/email per {@link LoginIdentifier}, else trimmed lower. */
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

    /** Package-private for JVM tests (prefs key stability). */
    static String keyFor(String email) {
        String norm = stableLoginId(email);
        return KEY_PREFIX
                + Base64.encodeToString(norm.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    /** Package-private for JVM tests (deterministic hash contract). */
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
