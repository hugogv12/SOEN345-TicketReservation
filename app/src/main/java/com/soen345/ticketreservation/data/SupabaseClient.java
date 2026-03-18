package com.soen345.ticketreservation.data;

import com.soen345.ticketreservation.admin.AdminEvent;
import com.soen345.ticketreservation.ui.events_page.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Test-oriented Supabase-style HTTP client (PostgREST-shaped paths). Tests override {@link #BASE_URL}
 * and {@link #FUNCTIONS_URL} against a {@link okhttp3.mockwebserver.MockWebServer}.
 */
public final class SupabaseClient {

    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient HTTP = new OkHttpClient();

    /** When null, callers must set before use (tests always set). */
    public static volatile String BASE_URL;
    public static volatile String FUNCTIONS_URL;

    private SupabaseClient() {
    }

    public static boolean insertReservation(String eventId, String userId, String token) {
        try {
            JSONObject row = new JSONObject();
            row.put("event_id", eventId);
            row.put("user_id", userId);
            HttpResult ins = http("POST", join(BASE_URL, "/reservations"), row.toString(), token);
            int insertedRows = countInsertedRows(ins);
            boolean reservationInserted = ins.code >= 200 && ins.code < 300 && insertedRows > 0;
            if (!reservationInserted) {
                return false;
            }
            String reservationId = parseReservationId(ins.body);

            Integer current = fetchCurrentAvailableTickets(eventId, token);
            boolean ticketUpdateSucceeded = updateEventTicketCount(eventId, current, -1, token);
            if (!ticketUpdateSucceeded) {
                deleteReservationRowOnly(reservationId, eventId, userId, token);
                return false;
            }
            return true;
        } catch (IOException | JSONException e) {
            return false;
        }
    }

    public static boolean deleteReservation(String eventId, String userId, String token) {
        try {
            String delUrl =
                    join(BASE_URL, "/reservations?event_id=eq." + enc(eventId) + "&user_id=eq." + enc(userId));
            HttpResult del = http("DELETE", delUrl, null, token);
            int deletedRows = countDeletedRows(del);
            boolean reservationDeleted = del.code >= 200 && del.code < 300 && deletedRows > 0;
            if (!reservationDeleted) {
                return false;
            }
            JSONObject deletedRow = firstObjectOrNull(del.body);

            Integer current = fetchCurrentAvailableTickets(eventId, token);
            boolean ticketUpdateSucceeded = updateEventTicketCount(eventId, current, +1, token);
            if (!ticketUpdateSucceeded) {
                rollbackReservationInsert(deletedRow, token);
                return false;
            }
            return true;
        } catch (IOException | JSONException e) {
            return false;
        }
    }

    public static EmailResult sendConfirmationEmail(
            String email, String userName, Event event, String token) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("user_name", userName == null ? "User" : userName);
            body.put("event_id", event.getId());
            body.put("event_title", event.getTitle());
            body.put("event_date", event.getDate());
            body.put("location", event.getLocation());
            String url = join(FUNCTIONS_URL, "/send-confirmation-email");
            HttpResult r = http("POST", url, body.toString(), token);
            if (r.code >= 200 && r.code < 300) {
                return new EmailResult(r.code, "Ticket email sent");
            }
            return new EmailResult(r.code, "Email failed: " + r.body);
        } catch (IOException | JSONException e) {
            return new EmailResult(500, "Email failed: " + e.getMessage());
        }
    }

    public static FetchEventsResult fetchEvents(String token, String userId) {
        try {
            Set<String> reserved = new HashSet<>();
            String resUrl = join(BASE_URL, "/reservations?user_id=eq." + enc(userId) + "&select=event_id");
            HttpResult resResp = http("GET", resUrl, null, token);
            if (resResp.code >= 200 && resResp.code < 300) {
                JSONArray arr = new JSONArray(resResp.body);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    String eid = o.optString("event_id", "");
                    if (!eid.isBlank()) {
                        reserved.add(eid);
                    }
                }
            }

            String eventsUrl = join(BASE_URL, "/events?select=*");
            HttpResult evResp = http("GET", eventsUrl, null, token);
            if (!isSuccessful(evResp)) {
                return FetchEventsResult.error("events fetch failed: HTTP " + evResp.code);
            }
            JSONArray eventsArr = new JSONArray(evResp.body);
            List<Event> out = new ArrayList<>();
            for (int i = 0; i < eventsArr.length(); i++) {
                JSONObject o = eventsArr.getJSONObject(i);
                String err = validateEventFields(o);
                if (err != null) {
                    return FetchEventsResult.error(err);
                }
                Event ev = parseListEvent(o);
                ev.setReservedByCurrentUser(reserved.contains(ev.getId()));
                out.add(ev);
            }
            return FetchEventsResult.ok(out);
        } catch (IOException | JSONException e) {
            return FetchEventsResult.error("network or parse error: " + e.getMessage());
        }
    }

    public static AdminEventsFetchResult fetchAdminEvents(String token) {
        try {
            HttpResult r = http("GET", join(BASE_URL, "/events"), null, token);
            if (!isSuccessful(r)) {
                return AdminEventsFetchResult.error("HTTP " + r.code);
            }
            JSONArray arr = new JSONArray(r.body);
            List<AdminEvent> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                list.add(parseAdminEventLoose(arr.getJSONObject(i)));
            }
            return AdminEventsFetchResult.ok(list);
        } catch (IOException | JSONException e) {
            return AdminEventsFetchResult.error(e.getMessage());
        }
    }

    public static WriteResult insertAdminEvent(AdminEvent event, String token) {
        return performWriteRequest(event, token, false);
    }

    public static WriteResult updateAdminEvent(AdminEvent event, String token) {
        return performWriteRequest(event, token, true);
    }

    public static WriteResult cancelAdminEvent(String eventId, String token) {
        try {
            String url = join(BASE_URL, "/events?id=eq." + enc(eventId));
            JSONObject primary = new JSONObject();
            primary.put("canceled", true);
            HttpResult p = http("PATCH", url, primary.toString(), token);
            if (isSuccessful(p)) {
                return new WriteResult(true, "Event canceled");
            }
            JSONObject fallback = new JSONObject();
            fallback.put("available_tickets", 0);
            HttpResult f = http("PATCH", url, fallback.toString(), token);
            if (isSuccessful(f)) {
                return new WriteResult(true, "Fallback: available tickets set to 0");
            }
            return new WriteResult(false, "cancel failed");
        } catch (IOException | JSONException e) {
            return new WriteResult(false, e.getMessage());
        }
    }

    public static UpsertProfileResult upsertUserProfile(
            String token, String userId, String email, String fullName, String phone) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", userId);
            body.put("email", email);
            body.put("full_name", fullName == null ? JSONObject.NULL : fullName);
            body.put("phone", phone == null ? JSONObject.NULL : phone);
            HttpResult r = http("POST", join(BASE_URL, "/user_profiles"), body.toString(), token);
            return new UpsertProfileResult(r.code, r.body);
        } catch (IOException | JSONException e) {
            return new UpsertProfileResult(0, e.getMessage());
        }
    }

    // --- helpers ---

    private static WriteResult performWriteRequest(AdminEvent event, String token, boolean includeId) {
        try {
            JSONObject o = adminEventToJson(event, includeId);
            String url = includeId ? join(BASE_URL, "/events?id=eq." + enc(event.getId())) : join(BASE_URL, "/events");
            String method = includeId ? "PATCH" : "POST";
            HttpResult r = http(method, url, o.toString(), token);
            if (isSuccessful(r)) {
                return new WriteResult(true, "ok");
            }
            return new WriteResult(false, "HTTP " + r.code + " " + r.body);
        } catch (IOException | JSONException e) {
            return new WriteResult(false, e.getMessage());
        }
    }

    private static JSONObject adminEventToJson(AdminEvent event, boolean update) throws JSONException {
        JSONObject o = new JSONObject();
        if (!update) {
            o.put("id", event.getId());
        }
        o.put("title", event.getTitle());
        o.put("description", event.getDescription());
        o.put("category", event.getCategoryId());
        o.put("location", event.getLocation());
        o.put("date", event.getDate());
        o.put("available_tickets", event.getAvailableTickets());
        o.put("price", event.getPrice());
        o.put("canceled", event.isCancelled());
        return o;
    }

    private static AdminEvent parseAdminEventLoose(JSONObject o) throws JSONException {
        String id = o.optString("id", "");
        String title = o.optString("title", "");
        String desc = o.optString("description", "");
        String cat = o.optString("category", o.optString("category_id", ""));
        String loc = o.optString("location", "");
        String date = o.optString("date", "");
        int tickets = o.optInt("available_tickets", 0);
        double price = o.optDouble("price", 0.0);
        boolean canceled = o.optBoolean("canceled", false);
        return new AdminEvent(id, title, desc, cat, loc, date, tickets, price, canceled);
    }

    private static String validateEventFields(JSONObject o) {
        String[] keys = {"id", "title", "description", "category", "location", "date", "available_tickets", "price"};
        for (String k : keys) {
            if (!o.has(k)) {
                return "parse error: missing field " + k;
            }
        }
        return null;
    }

    private static Event parseListEvent(JSONObject o) throws JSONException {
        return new Event(
                o.getString("id"),
                o.getString("title"),
                o.getString("description"),
                o.getString("category"),
                o.getString("location"),
                o.getString("date"),
                o.getInt("available_tickets"),
                o.getDouble("price"));
    }

    private static int countInsertedRows(HttpResult ins) {
        try {
            JSONArray arr = new JSONArray(ins.body);
            return arr.length();
        } catch (JSONException e) {
            return 0;
        }
    }

    private static int countDeletedRows(HttpResult del) {
        return countInsertedRows(del);
    }

    private static String parseReservationId(String body) {
        try {
            JSONArray arr = new JSONArray(body);
            if (arr.length() == 0) {
                return null;
            }
            JSONObject o = arr.getJSONObject(0);
            if (o.has("id")) {
                return String.valueOf(o.get("id"));
            }
        } catch (JSONException ignored) {
        }
        return null;
    }

    private static JSONObject firstObjectOrNull(String body) {
        try {
            JSONArray arr = new JSONArray(body);
            if (arr.length() == 0) {
                return null;
            }
            return arr.getJSONObject(0);
        } catch (JSONException e) {
            return null;
        }
    }

    private static Integer fetchCurrentAvailableTickets(String eventId, String token)
            throws IOException, JSONException {
        String url = join(BASE_URL, "/events?id=eq." + enc(eventId) + "&select=available_tickets");
        HttpResult r = http("GET", url, null, token);
        if (!isSuccessful(r)) {
            return null;
        }
        JSONArray arr;
        try {
            arr = new JSONArray(r.body);
        } catch (JSONException e) {
            return null;
        }
        if (arr.length() == 0) {
            return null;
        }
        return arr.getJSONObject(0).getInt("available_tickets");
    }

    /**
     * @param delta +1 for delete flow (restore ticket), -1 for insert flow
     */
    private static boolean updateEventTicketCount(String eventId, Integer currentNullable, int delta, String token)
            throws IOException, JSONException {
        if (currentNullable == null) {
            return false;
        }
        int current = currentNullable;
        int newCount = current + delta;
        if (newCount < 0) {
            return false;
        }
        String patchUrl = join(BASE_URL, "/events?id=eq." + enc(eventId));
        JSONObject patch = new JSONObject();
        patch.put("available_tickets", newCount);
        HttpResult patchResp = http("PATCH", patchUrl, patch.toString(), token);
        if (!isSuccessful(patchResp)) {
            return false;
        }
        int returnedCount;
        try {
            JSONArray arr = new JSONArray(patchResp.body);
            if (arr.length() == 0) {
                returnedCount = Integer.MIN_VALUE;
            } else {
                returnedCount = arr.getJSONObject(0).optInt("available_tickets", Integer.MIN_VALUE);
            }
        } catch (JSONException e) {
            returnedCount = Integer.MIN_VALUE;
        }
        return returnedCount == newCount;
    }

    private static void deleteReservationRowOnly(
            String reservationId, String eventId, String userId, String token) throws IOException {
        String url;
        if (reservationId != null && !reservationId.isEmpty()) {
            url = join(BASE_URL, "/reservations?id=eq." + enc(reservationId));
        } else {
            url = join(BASE_URL, "/reservations?event_id=eq." + enc(eventId) + "&user_id=eq." + enc(userId));
        }
        http("DELETE", url, null, token);
    }

    private static void rollbackReservationInsert(JSONObject deletedRow, String token)
            throws IOException, JSONException {
        if (deletedRow == null) {
            return;
        }
        http("POST", join(BASE_URL, "/reservations"), deletedRow.toString(), token);
    }

    private static boolean isSuccessful(HttpResult r) {
        return r.code >= 200 && r.code < 300;
    }

    private static String join(String base, String path) {
        if (base == null) {
            throw new IllegalStateException("BASE_URL / FUNCTIONS_URL not set");
        }
        String b = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String p = path.startsWith("/") ? path : "/" + path;
        return b + p;
    }

    private static String enc(String s) throws IOException {
        return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
    }

    private static HttpResult http(String method, String url, String jsonBody, String bearerToken)
            throws IOException {
        Request.Builder b = new Request.Builder().url(url).header("Accept", "application/json");
        if (bearerToken != null && !bearerToken.isEmpty()) {
            b.header("apikey", bearerToken);
            b.header("Authorization", "Bearer " + bearerToken);
        }
        RequestBody body =
                jsonBody == null || jsonBody.isEmpty()
                        ? null
                        : RequestBody.create(jsonBody, JSON_MEDIA);
        switch (method) {
            case "GET":
                b.get();
                break;
            case "DELETE":
                b.delete();
                break;
            case "POST":
                b.post(body != null ? body : RequestBody.create("{}", JSON_MEDIA));
                break;
            case "PATCH":
                b.patch(body != null ? body : RequestBody.create("{}", JSON_MEDIA));
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
        try (Response resp = HTTP.newCall(b.build()).execute()) {
            String respBody = resp.body() != null ? resp.body().string() : "";
            return new HttpResult(resp.code(), respBody);
        }
    }

    private static final class HttpResult {
        final int code;
        final String body;

        HttpResult(int code, String body) {
            this.code = code;
            this.body = body == null ? "" : body;
        }
    }

    public static final class EmailResult {
        public final int code;
        public final String message;

        public EmailResult(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public static final class WriteResult {
        public final boolean success;
        public final String message;

        public WriteResult(boolean success, String message) {
            this.success = success;
            this.message = message == null ? "" : message;
        }
    }

    public static final class UpsertProfileResult {
        public final int code;
        public final String body;

        public UpsertProfileResult(int code, String body) {
            this.code = code;
            this.body = body == null ? "" : body;
        }
    }
}
