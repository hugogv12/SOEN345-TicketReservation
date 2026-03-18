package com.soen345.ticketreservation.data;

import com.soen345.ticketreservation.admin.AdminEvent;
import com.soen345.ticketreservation.ui.events_page.Event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupabaseClientTest {

    private MockWebServer mockServer;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        String mockUrl = mockServer.url("/").toString();
        if (mockUrl.endsWith("/")) {
            mockUrl = mockUrl.substring(0, mockUrl.length() - 1);
        }
        SupabaseClient.BASE_URL = mockUrl + "/rest/v1";
        SupabaseClient.FUNCTIONS_URL = mockUrl + "/functions/v1";
    }

    @AfterEach
    void tearDown() throws IOException {
        try {
            mockServer.shutdown();
        } catch (RuntimeException ignored) {
        }
    }

    private static Event sampleUiEvent() {
        return new Event("1", "Concert", "Fun", "Music", "Hall A", "2026-03-10", 50, 30.0);
    }

    @Test
    void insertReservationReturnsTrueOnSuccess() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"event_id\":\"event1\",\"user_id\":\"user1\"}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":10}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":9}]"));

        assertTrue(SupabaseClient.insertReservation("event1", "user1", "fake-token"));
    }

    @Test
    void insertReservationReturnsFalseWhenReservationRowInsertFailsNon2xx() {
        mockServer.enqueue(new MockResponse().setResponseCode(409).setBody("conflict"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenReservationRowInsertReturnsEmptyArray() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenReservationInsertBodyIsNotJsonArray() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("not-json"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenTicketUpdateFailsAndRollsBack() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":10}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(400));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "fake-token"));
    }

    @Test
    void insertReservationReturnsFalseWhenAvailableTicketsWouldGoNegative() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":0}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenCurrentTicketCountCannotBeFetchedEmptyArray() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenFetchCurrentAvailableTicketsGetsNon200() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(500));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenPatchVerificationCountMismatches() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":5}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":99}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenPatchResponseBodyIsNotValidJson() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":5}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("not-json"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseWhenPatchArrayIsEmpty() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":5}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void insertReservationReturnsFalseOnNetworkException() throws IOException {
        mockServer.shutdown();
        assertFalse(SupabaseClient.insertReservation("event1", "user1", "token"));
    }

    @Test
    void deleteReservationReturnsTrueOnSuccess() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"event_id\":\"event1\",\"user_id\":\"user1\"}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":9}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"available_tickets\":10}]"));

        assertTrue(SupabaseClient.deleteReservation("event1", "user1", "fake-token"));
    }

    @Test
    void deleteReservationReturnsFalseWhenDeletedRowsIsZero() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        assertFalse(SupabaseClient.deleteReservation("event1", "user1", "token"));
    }

    @Test
    void deleteReservationReturnsFalseWhenDeleteRowResponseIsNon2xx() {
        mockServer.enqueue(new MockResponse().setResponseCode(404));

        assertFalse(SupabaseClient.deleteReservation("event1", "user1", "token"));
    }

    @Test
    void deleteReservationReturnsFalseWhenDeleteRowBodyIsNotValidJson() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("not-json"));

        assertFalse(SupabaseClient.deleteReservation("event1", "user1", "token"));
    }

    @Test
    void deleteReservationReturnsFalseWhenTicketCountFetchFailsAndRollsBack() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":1}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(500));
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("[{\"id\":1}]"));

        assertFalse(SupabaseClient.deleteReservation("event1", "user1", "fake-token"));
    }

    @Test
    void deleteReservationReturnsFalseOnNetworkException() throws IOException {
        mockServer.shutdown();
        assertFalse(SupabaseClient.deleteReservation("event1", "user1", "token"));
    }

    @Test
    void sendConfirmationEmailReturnsSuccessMessageOn200() {
        Event event = sampleUiEvent();
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"Email sent\"}"));

        SupabaseClient.EmailResult r = SupabaseClient.sendConfirmationEmail("test@test.com", "Test User", event, "token");
        assertEquals(200, r.code);
        assertTrue(r.message.toLowerCase().contains("ticket email sent"));
    }

    @Test
    void sendConfirmationEmailReturnsFailureMessageOnNon200Response() {
        Event event = sampleUiEvent();
        mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("internal error"));

        SupabaseClient.EmailResult r = SupabaseClient.sendConfirmationEmail("test@test.com", "User", event, "token");
        assertEquals(500, r.code);
        assertTrue(r.message.toLowerCase().contains("email failed"));
    }

    @Test
    void sendConfirmationEmailUsesUserAsFallbackWhenUserNameIsNull() {
        Event event = sampleUiEvent();
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));

        SupabaseClient.EmailResult r = SupabaseClient.sendConfirmationEmail("test@test.com", null, event, "token");
        assertEquals(200, r.code);
        assertTrue(r.message.toLowerCase().contains("ticket email sent"));
    }

    @Test
    void sendConfirmationEmailReturns500OnNetworkException() throws IOException {
        Event event = sampleUiEvent();
        mockServer.shutdown();

        SupabaseClient.EmailResult r = SupabaseClient.sendConfirmationEmail("test@test.com", null, event, "token");
        assertEquals(500, r.code);
    }

    @Test
    void fetchEventsReturnsListWithIsReservedByCurrentUserTrue() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"event_id\":\"42\"}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{"
                + "\"id\":\"42\",\"title\":\"Rock Night\",\"description\":\"Great show\","
                + "\"category\":\"Music\",\"location\":\"Stadium\",\"date\":\"2026-06-01\","
                + "\"available_tickets\":100,\"price\":25.0"
                + "}]"));

        FetchEventsResult result = SupabaseClient.fetchEvents("token", "user1");
        assertNull(result.getErrorMessage());
        assertTrue(result.getEvents().get(0).isReservedByCurrentUser());
    }

    @Test
    void fetchEventsReturnsListWithIsReservedByCurrentUserFalseWhenNoReservations() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{"
                + "\"id\":\"42\",\"title\":\"Rock Night\",\"description\":\"Great show\","
                + "\"category\":\"Music\",\"location\":\"Stadium\",\"date\":\"2026-06-01\","
                + "\"available_tickets\":100,\"price\":25.0"
                + "}]"));

        FetchEventsResult result = SupabaseClient.fetchEvents("token", "user1");
        assertNull(result.getErrorMessage());
        assertFalse(result.getEvents().get(0).isReservedByCurrentUser());
    }

    @Test
    void fetchEventsSkipsBlankEventIdInReservedSet() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"event_id\":\"\"}]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{"
                + "\"id\":\"1\",\"title\":\"Jazz\",\"description\":\"Cool\","
                + "\"category\":\"Music\",\"location\":\"Venue\",\"date\":\"2026-07-01\","
                + "\"available_tickets\":10,\"price\":10.0"
                + "}]"));

        FetchEventsResult result = SupabaseClient.fetchEvents("token", "user1");
        assertNotNull(result.getEvents());
        assertFalse(result.getEvents().get(0).isReservedByCurrentUser());
    }

    @Test
    void fetchEventsReturnsErrorWhenHttpResponseIsNotSuccessful() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));
        mockServer.enqueue(new MockResponse().setResponseCode(403).setBody("forbidden"));

        FetchEventsResult result = SupabaseClient.fetchEvents("token", "user1");
        assertNotNull(result.getErrorMessage());
        assertNull(result.getEvents());
    }

    @Test
    void fetchEventsReturnsErrorWhenRequiredFieldIsMissing() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{"
                + "\"id\":\"1\",\"title\":\"Concert\",\"description\":\"Fun\","
                + "\"category\":\"Music\",\"location\":\"Hall A\",\"date\":\"2026-03-10\","
                + "\"available_tickets\":50"
                + "}]"));

        FetchEventsResult result = SupabaseClient.fetchEvents("fake-token", "user1");
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().toLowerCase().contains("parse"));
    }

    @Test
    void fetchEventsHandlesReservedEventsFetchFailureGracefully() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{"
                + "\"id\":\"1\",\"title\":\"Jazz Night\",\"description\":\"Smooth\","
                + "\"category\":\"Music\",\"location\":\"Club\",\"date\":\"2026-07-01\","
                + "\"available_tickets\":30,\"price\":15.0"
                + "}]"));

        FetchEventsResult result = SupabaseClient.fetchEvents("token", "user1");
        assertNull(result.getErrorMessage());
        assertNotNull(result.getEvents());
    }

    @Test
    void fetchEventsReturnsErrorOnNetworkException() throws IOException {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));
        mockServer.shutdown();

        FetchEventsResult result = SupabaseClient.fetchEvents("fake-token", "user1");
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void fetchAdminEventsReturnsEventsOnSuccess() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"id\":\"1\",\"title\":\"Admin Event\"}]"));
        AdminEventsFetchResult r = SupabaseClient.fetchAdminEvents("token");
        assertNull(r.getError());
        assertEquals(1, r.getEvents().size());
        assertEquals("Admin Event", r.getEvents().get(0).getTitle());
    }

    @Test
    void fetchAdminEventsReturnsErrorOnNon200() {
        mockServer.enqueue(new MockResponse().setResponseCode(404));
        AdminEventsFetchResult r = SupabaseClient.fetchAdminEvents("token");
        assertNull(r.getEvents());
        assertNotNull(r.getError());
    }

    @Test
    void fetchAdminEventsReturnsErrorOnNetworkException() throws IOException {
        mockServer.shutdown();
        AdminEventsFetchResult r = SupabaseClient.fetchAdminEvents("token");
        assertNull(r.getEvents());
        assertNotNull(r.getError());
    }

    @Test
    void insertAdminEventReturnsTrueOnSuccess() {
        mockServer.enqueue(new MockResponse().setResponseCode(201));
        SupabaseClient.WriteResult r = SupabaseClient.insertAdminEvent(
                new AdminEvent("1", "Title", "Desc", "Cat", "Loc", "Date", 10, 10.0), "token");
        assertTrue(r.success);
    }

    @Test
    void insertAdminEventReturnsFalseOnNon2xxResponse() {
        mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("error"));
        SupabaseClient.WriteResult r = SupabaseClient.insertAdminEvent(
                new AdminEvent("1", "Title", "Desc", "Cat", "Loc", "Date", 10, 10.0), "token");
        assertFalse(r.success);
        assertTrue(r.message.contains("500"));
    }

    @Test
    void insertAdminEventReturnsFalseOnNetworkException() throws IOException {
        mockServer.shutdown();
        SupabaseClient.WriteResult r = SupabaseClient.insertAdminEvent(
                new AdminEvent("1", "Title", "Desc", "Cat", "Loc", "Date", 10, 10.0), "token");
        assertFalse(r.success);
    }

    @Test
    void updateAdminEventReturnsTrueOnSuccess() {
        mockServer.enqueue(new MockResponse().setResponseCode(200));
        SupabaseClient.WriteResult r = SupabaseClient.updateAdminEvent(
                new AdminEvent("1", "Title", "Desc", "Cat", "Loc", "2026-01-01", 10, 20.0), "token");
        assertTrue(r.success);
    }

    @Test
    void updateAdminEventReturnsFalseOnNon2xxResponse() {
        mockServer.enqueue(new MockResponse().setResponseCode(400).setBody("bad request"));
        SupabaseClient.WriteResult r = SupabaseClient.updateAdminEvent(
                new AdminEvent("1", "Title", "Desc", "Cat", "Loc", "2026-01-01", 10, 20.0), "token");
        assertFalse(r.success);
        assertTrue(r.message.contains("400"));
    }

    @Test
    void updateAdminEventReturnsFalseOnNetworkException() throws IOException {
        mockServer.shutdown();
        SupabaseClient.WriteResult r = SupabaseClient.updateAdminEvent(
                new AdminEvent("1", "Title", "Desc", "Cat", "Loc", "2026-01-01", 10, 20.0), "token");
        assertFalse(r.success);
    }

    @Test
    void cancelAdminEventReturnsTrueWhenPrimarySucceeds() {
        mockServer.enqueue(new MockResponse().setResponseCode(200));
        SupabaseClient.WriteResult r = SupabaseClient.cancelAdminEvent("1", "token");
        assertTrue(r.success);
    }

    @Test
    void cancelAdminEventReturnsTrueWhenFallbackSucceeds() {
        mockServer.enqueue(new MockResponse().setResponseCode(400));
        mockServer.enqueue(new MockResponse().setResponseCode(200));
        SupabaseClient.WriteResult r = SupabaseClient.cancelAdminEvent("1", "token");
        assertTrue(r.success);
        assertTrue(r.message.contains("0") || r.message.toLowerCase().contains("fallback"));
    }

    @Test
    void cancelAdminEventReturnsFalseWhenBothPrimaryAndFallbackFail() {
        mockServer.enqueue(new MockResponse().setResponseCode(400));
        mockServer.enqueue(new MockResponse().setResponseCode(400));
        SupabaseClient.WriteResult r = SupabaseClient.cancelAdminEvent("1", "token");
        assertFalse(r.success);
    }

    @Test
    void upsertUserProfileWithAllFieldsNonNull() {
        mockServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\"id\":\"u1\"}"));
        SupabaseClient.UpsertProfileResult r =
                SupabaseClient.upsertUserProfile("token", "u1", "a@b.com", "Alice", "555-1234");
        assertEquals(201, r.code);
    }

    @Test
    void upsertUserProfileWithNullFullNameAndNullPhone() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"id\":\"u1\"}"));
        SupabaseClient.UpsertProfileResult r =
                SupabaseClient.upsertUserProfile("token", "u1", "a@b.com", null, null);
        assertEquals(200, r.code);
    }

    @Test
    void upsertUserProfileWithNullFullNameOnly() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"id\":\"u1\"}"));
        SupabaseClient.UpsertProfileResult r =
                SupabaseClient.upsertUserProfile("token", "u1", "a@b.com", null, "555-0000");
        assertEquals(200, r.code);
    }

    @Test
    void upsertUserProfileWithNullPhoneOnly() {
        mockServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"id\":\"u1\"}"));
        SupabaseClient.UpsertProfileResult r =
                SupabaseClient.upsertUserProfile("token", "u1", "a@b.com", "Bob", null);
        assertEquals(200, r.code);
    }
}
