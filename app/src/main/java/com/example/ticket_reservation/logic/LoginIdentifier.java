package com.example.ticket_reservation.logic;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Normalizes and validates login contacts (email or phone) for registration and local account keys.
 */
public final class LoginIdentifier {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    /** E.164-style: leading + then 8–15 digits (ITU-T E.164 max 15). */
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{6,14}$");

    private LoginIdentifier() {
    }

    /**
     * Returns canonical email (lower-cased trim) or E.164 phone ({@code +} and digits), or {@code ""}
     * if the input cannot be interpreted as either.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return "";
        }
        if (s.contains("@")) {
            return s.toLowerCase(Locale.ROOT);
        }
        return normalizePhoneToE164(s);
    }

    private static String normalizePhoneToE164(String raw) {
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return "";
        }
        if (digits.length() == 10) {
            return "+1" + digits;
        }
        if (digits.length() == 11 && digits.charAt(0) == '1') {
            return "+" + digits;
        }
        if (digits.length() >= 8 && digits.length() <= 15) {
            return "+" + digits;
        }
        return "";
    }

    public static boolean isValidNormalized(String canonical) {
        if (canonical == null || canonical.isEmpty()) {
            return false;
        }
        if (canonical.contains("@")) {
            return EMAIL.matcher(canonical).matches();
        }
        return E164.matcher(canonical).matches();
    }

    public static boolean isPhoneNormalized(String canonical) {
        return canonical != null && !canonical.contains("@") && E164.matcher(canonical).matches();
    }

    /**
     * Supabase email-auth expects an email string; map a normalized E.164 phone to a deterministic synthetic address.
     */
    public static String supabaseSyntheticEmail(String normalizedPhone) {
        if (!isPhoneNormalized(normalizedPhone)) {
            throw new IllegalArgumentException("not a normalized phone: " + normalizedPhone);
        }
        return normalizedPhone.substring(1) + "@phone.soen345.local";
    }
}
