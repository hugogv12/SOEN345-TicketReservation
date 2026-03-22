package com.example.ticket_reservation.data;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Supabase Auth REST API (sign up / password sign-in).
 */
public final class SupabaseAuth {

    private SupabaseAuth() {
    }

    public static class AuthResult {
        public final boolean success;
        public final String errorMessage;
        public final String accessToken;
        public final String email;
        public final String username;

        AuthResult(boolean success, String errorMessage, String accessToken, String email, String username) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.accessToken = accessToken;
            this.email = email;
            this.username = username;
        }

        static AuthResult ok(String token, String email, String username) {
            return new AuthResult(true, null, token, email, username);
        }

        /**
         * Sign-up succeeded but no session yet (email confirmation required). Caller may store email/username locally.
         */
        static AuthResult okPendingEmail(String email, String username) {
            return new AuthResult(true, null, null, email, username);
        }

        static AuthResult fail(String msg) {
            return new AuthResult(false, msg, null, null, null);
        }
    }

    public static AuthResult signUp(String email, String password, String username) {
        if (!SupabaseConfig.isConfigured()) {
            return AuthResult.fail("Supabase not configured");
        }
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            JSONObject options = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("username", username);
            options.put("data", data);
            body.put("options", options);

            String url = SupabaseConfig.baseUrl() + "/auth/v1/signup";
            String resp = httpJsonPost(url, body.toString());
            JSONObject json = new JSONObject(resp);

            if (json.has("access_token")) {
                return sessionFromJson(json, email, username);
            }
            JSONObject session = json.optJSONObject("session");
            if (session != null && session.has("access_token")) {
                return sessionFromJson(session, email, username);
            }

            JSONObject userNested = json.optJSONObject("user");
            if (userNested != null) {
                String em = userNested.optString("email", email);
                String un = readUsername(userNested, username);
                return AuthResult.okPendingEmail(em, un);
            }

            // Email confirmation on: GoTrue often returns the user object at the root (no nested "user").
            if (json.has("id") && json.has("email")) {
                String em = json.optString("email", email);
                String un = readUsername(json, username);
                return AuthResult.okPendingEmail(em, un);
            }

            return AuthResult.fail(firstApiMessage(json, "Sign up failed"));
        } catch (Exception e) {
            return AuthResult.fail(formatThrownAuthError(e));
        }
    }

    public static AuthResult signIn(String email, String password) {
        if (!SupabaseConfig.isConfigured()) {
            return AuthResult.fail("Supabase not configured");
        }
        try {
            // GoTrue (Supabase Auth) expects JSON + grant_type in the query string, same as @supabase/auth-js.
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            String url = SupabaseConfig.baseUrl() + "/auth/v1/token?grant_type=password";
            String resp = httpJsonPost(url, body.toString());
            JSONObject json = new JSONObject(resp);
            if (json.has("access_token")) {
                return sessionFromJson(json, email, "");
            }
            JSONObject session = json.optJSONObject("session");
            if (session != null && session.has("access_token")) {
                return sessionFromJson(session, email, "");
            }
            return AuthResult.fail(firstApiMessage(json, "Sign in failed"));
        } catch (Exception e) {
            return AuthResult.fail(formatThrownAuthError(e));
        }
    }

    private static AuthResult sessionFromJson(JSONObject sessionOrRoot, String fallbackEmail, String fallbackUsername) {
        String token = sessionOrRoot.optString("access_token", "");
        if (token.isEmpty()) {
            return AuthResult.fail("Invalid session response");
        }
        JSONObject user = sessionOrRoot.optJSONObject("user");
        String em = user != null ? user.optString("email", fallbackEmail) : fallbackEmail;
        String un = readUsername(user, fallbackUsername);
        return AuthResult.ok(token, em, un);
    }

    private static String readUsername(JSONObject user, String fallback) {
        if (user == null) {
            return fallback.isEmpty() ? "" : fallback;
        }
        JSONObject meta = user.optJSONObject("user_metadata");
        if (meta != null) {
            String u = meta.optString("username", "");
            if (!u.isEmpty()) {
                return u;
            }
        }
        return fallback.isEmpty() ? user.optString("email", "") : fallback;
    }

    private static String firstApiMessage(JSONObject json, String defaultMsg) {
        String m = json.optString("error_description", "");
        if (!m.isEmpty()) {
            return m;
        }
        m = json.optString("message", "");
        if (!m.isEmpty()) {
            return m;
        }
        m = json.optString("msg", "");
        if (!m.isEmpty()) {
            return m;
        }
        m = json.optString("error", "");
        if (!m.isEmpty()) {
            return m;
        }
        return defaultMsg;
    }

    private static String formatThrownAuthError(Exception e) {
        String raw = e.getMessage();
        if (raw == null || raw.isEmpty()) {
            return "Request failed";
        }
        if (raw.startsWith("{")) {
            try {
                return firstApiMessage(new JSONObject(raw), raw);
            } catch (Exception ignored) {
                return raw;
            }
        }
        int brace = raw.indexOf('{');
        if (brace >= 0) {
            try {
                return firstApiMessage(new JSONObject(raw.substring(brace)), raw);
            } catch (Exception ignored) {
                return raw;
            }
        }
        return raw;
    }

    private static String httpJsonPost(String urlStr, String json) throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(urlStr).openConnection();
        c.setConnectTimeout(20_000);
        c.setReadTimeout(30_000);
        c.setRequestMethod("POST");
        c.setRequestProperty("apikey", SupabaseConfig.anonKey());
        c.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.anonKey());
        c.setRequestProperty("X-Supabase-Api-Version", "2024-01-01");
        c.setRequestProperty("Content-Type", "application/json");
        c.setDoOutput(true);
        try (OutputStream os = c.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(c);
    }

    private static String readResponse(HttpURLConnection c) throws IOException {
        int code = c.getResponseCode();
        java.io.InputStream stream = code >= 400 ? c.getErrorStream() : c.getInputStream();
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while (stream != null && (n = stream.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }
        String resp = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        if (code < 200 || code >= 300) {
            throw new IOException(resp);
        }
        return resp;
    }
}
