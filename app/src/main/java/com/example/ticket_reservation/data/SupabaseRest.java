package com.example.ticket_reservation.data;

import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** PostgREST client for Supabase (events, reservations, RPCs). */
public final class SupabaseRest {

    private SupabaseRest() {
    }

    public static List<Event> fetchEvents() throws IOException, JSONException {
        String u = SupabaseConfig.baseUrl() + "/rest/v1/events?select=*&order=iso_date.asc";
        String body = http("GET", u, null);
        JSONArray arr = new JSONArray(body);
        List<Event> out = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            out.add(parseEvent(arr.getJSONObject(i)));
        }
        return out;
    }

    public static List<Reservation> fetchReservationsForUser(String userKey) throws IOException, JSONException {
        String enc = URLEncoder.encode(userKey, StandardCharsets.UTF_8.name());
        String u = SupabaseConfig.baseUrl() + "/rest/v1/reservations?user_key=eq." + enc + "&select=*";
        String body = http("GET", u, null);
        JSONArray arr = new JSONArray(body);
        List<Reservation> out = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            out.add(parseReservation(arr.getJSONObject(i)));
        }
        return out;
    }

    public static JSONObject bookEvent(String eventId, String userKey, int quantity,
                                       String title, String isoDate, String startTime, String location)
            throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("p_event_id", eventId);
        body.put("p_user_key", userKey);
        body.put("p_qty", quantity);
        body.put("p_title", title);
        body.put("p_iso", isoDate);
        body.put("p_start_time", startTime == null || startTime.isEmpty() ? JSONObject.NULL : startTime);
        body.put("p_loc", location);
        String url = SupabaseConfig.baseUrl() + "/rest/v1/rpc/book_event";
        return new JSONObject(http("POST", url, body.toString()));
    }

    public static JSONObject cancelReservation(String reservationId, String userKey)
            throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("p_reservation_id", reservationId);
        body.put("p_user_key", userKey);
        String url = SupabaseConfig.baseUrl() + "/rest/v1/rpc/cancel_reservation";
        return new JSONObject(http("POST", url, body.toString()));
    }

    public static void insertEvent(Event event) throws IOException {
        try {
            JSONObject o = new JSONObject();
            o.put("id", event.getId());
            o.put("title", event.getTitle());
            o.put("iso_date", event.getIsoDate());
            if (event.getStartTime() == null || event.getStartTime().isEmpty()) {
                o.put("start_time", JSONObject.NULL);
            } else {
                o.put("start_time", event.getStartTime());
            }
            o.put("location", event.getLocation());
            o.put("category", event.getCategory());
            o.put("canceled", event.isCanceled());
            o.put("capacity", event.getCapacity());
            o.put("tickets_reserved", event.getTicketsReserved());
            String url = SupabaseConfig.baseUrl() + "/rest/v1/events";
            http("POST", url, o.toString());
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public static void updateEvent(Event event) throws IOException {
        try {
            JSONObject o = new JSONObject();
            o.put("title", event.getTitle());
            o.put("iso_date", event.getIsoDate());
            if (event.getStartTime() == null || event.getStartTime().isEmpty()) {
                o.put("start_time", JSONObject.NULL);
            } else {
                o.put("start_time", event.getStartTime());
            }
            o.put("location", event.getLocation());
            o.put("category", event.getCategory());
            o.put("canceled", event.isCanceled());
            o.put("capacity", event.getCapacity());
            o.put("tickets_reserved", event.getTicketsReserved());
            String url = SupabaseConfig.baseUrl() + "/rest/v1/events?id=eq." + event.getId();
            http("PATCH", url, o.toString());
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /** Package-private for JVM tests: PostgREST {@code events} row → {@link Event}. */
    static Event parseEvent(JSONObject o) throws JSONException {
        return new Event(
                o.getString("id"),
                o.getString("title"),
                normalizeIsoDate(o.getString("iso_date")),
                normalizeTimeFromApi(o.opt("start_time")),
                o.getString("location"),
                o.getString("category"),
                o.getBoolean("canceled"),
                o.getInt("capacity"),
                o.getInt("tickets_reserved")
        );
    }

    /** Package-private for JVM tests. */
    static String normalizeTimeFromApi(Object raw) {
        if (raw == null || raw == JSONObject.NULL) {
            return "";
        }
        String s = String.valueOf(raw).trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) {
            return "";
        }
        if (s.length() >= 5 && s.charAt(2) == ':') {
            return s.substring(0, 5);
        }
        return s;
    }

    /** Package-private for JVM tests: PostgREST {@code reservations} row → {@link Reservation}. */
    static Reservation parseReservation(JSONObject o) throws JSONException {
        return Reservation.createWithExistingId(
                o.getString("id"),
                o.getString("event_id"),
                o.getString("user_key"),
                o.getInt("quantity"),
                o.getString("event_title_snapshot"),
                normalizeIsoDate(o.getString("event_iso_date_snapshot")),
                normalizeTimeFromApi(o.opt("event_start_time_snapshot")),
                o.getString("event_location_snapshot")
        );
    }

    /** Package-private for JVM tests. */
    static String normalizeIsoDate(String raw) {
        if (raw != null && raw.length() >= 10) {
            return raw.substring(0, 10);
        }
        return raw;
    }

    private static String http(String method, String url, String jsonBody) throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        c.setConnectTimeout(20_000);
        c.setReadTimeout(30_000);
        c.setRequestMethod(method);
        c.setRequestProperty("apikey", SupabaseConfig.anonKey());
        c.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.anonKey());
        c.setRequestProperty("Accept", "application/json");
        if ("POST".equals(method) || "PATCH".equals(method)) {
            c.setRequestProperty("Content-Type", "application/json");
            if ("POST".equals(method) && url.contains("/rest/v1/events")) {
                c.setRequestProperty("Prefer", "return=minimal");
            }
        }
        if (jsonBody != null && !jsonBody.isEmpty()) {
            c.setDoOutput(true);
            try (OutputStream os = c.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
        }
        int code = c.getResponseCode();
        InputStream stream = code >= 400 ? c.getErrorStream() : c.getInputStream();
        String resp = stream != null ? readFully(stream) : "";
        if (code < 200 || code >= 300) {
            throw new IOException("HTTP " + code + " " + resp);
        }
        return resp;
    }

    private static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
