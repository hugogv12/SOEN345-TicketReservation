package com.example.ticket_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket_reservation.data.BookingService;
import com.example.ticket_reservation.data.EventRepository;
import com.example.ticket_reservation.data.SupabaseConfig;
import com.example.ticket_reservation.model.Event;
import com.google.android.material.textfield.TextInputEditText;


public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        findViewById(R.id.button_back_to_menu).setOnClickListener(v ->
                NavigationHelper.goToMainMenu(this));

        String eventId = getIntent().getStringExtra(MainActivity.EXTRA_EVENT_ID);
        if (eventId == null) {
            finish();
            return;
        }

        EventRepository repo = EventRepository.getInstance();
        Event event = repo.findById(eventId);
        if (event == null || event.isCanceled()) {
            Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView title = findViewById(R.id.detail_title);
        TextView category = findViewById(R.id.detail_category);
        TextView date = findViewById(R.id.detail_date);
        TextView location = findViewById(R.id.detail_location);
        TextView availability = findViewById(R.id.detail_availability);
        TextInputEditText qtyInput = findViewById(R.id.input_ticket_quantity);
        Button reserve = findViewById(R.id.button_reserve);

        title.setText(event.getTitle());
        category.setText(event.getCategory());
        date.setText(event.getDateTimeDisplay());
        location.setText(event.getLocation());
        refreshAvailability(availability, event);

        if (qtyInput.getText() == null || qtyInput.getText().toString().trim().isEmpty()) {
            qtyInput.setText("1");
        }

        reserve.setOnClickListener(v -> attemptReserve(event, qtyInput, availability));

        qtyInput.setOnEditorActionListener((v, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptReserve(event, qtyInput, availability);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String eventId = getIntent().getStringExtra(MainActivity.EXTRA_EVENT_ID);
        if (eventId == null) {
            return;
        }
        Event event = EventRepository.getInstance().findById(eventId);
        TextView availability = findViewById(R.id.detail_availability);
        if (event != null && availability != null) {
            refreshAvailability(availability, event);
        }
    }

    private static void refreshAvailability(TextView availability, Event event) {
        availability.setText(availability.getContext().getString(R.string.availability_format,
                event.getAvailableTickets(), event.getCapacity()));
    }

    private void attemptReserve(Event event, TextInputEditText qtyInput, TextView availabilityView) {
        if (!SessionPrefs.hasUser(this)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.register_required_title)
                    .setMessage(R.string.register_to_reserve)
                    .setPositiveButton(R.string.register, (d, w) ->
                            startActivity(new Intent(this, RegisterActivity.class)))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
            return;
        }

        String raw = qtyInput.getText() != null ? qtyInput.getText().toString().trim() : "";
        int qty;
        if (raw.isEmpty()) {
            qty = 1;
        } else {
            try {
                qty = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.invalid_quantity, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (qty <= 0) {
            Toast.makeText(this, R.string.invalid_quantity, Toast.LENGTH_SHORT).show();
            return;
        }

        Event live = EventRepository.getInstance().findById(event.getId());
        if (live == null || live.isCanceled()) {
            Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userKey = SessionPrefs.getUserKey(this);
        if (SupabaseConfig.isConfigured()) {
            new Thread(() -> {
                BookingService.BookResult result = BookingService.getInstance()
                        .book(userKey, live.getId(), qty);
                runOnUiThread(() -> {
                    if (result != BookingService.BookResult.SUCCESS) {
                        Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT).show();
                        Event refreshed = EventRepository.getInstance().findById(live.getId());
                        if (refreshed != null) {
                            refreshAvailability(availabilityView, refreshed);
                        }
                        return;
                    }
                    Intent confirm = new Intent(this, ReservationConfirmationActivity.class);
                    confirm.putExtra(ReservationConfirmationActivity.EXTRA_TITLE, live.getTitle());
                    confirm.putExtra(ReservationConfirmationActivity.EXTRA_QUANTITY, qty);
                    confirm.putExtra(ReservationConfirmationActivity.EXTRA_DATE, live.getDateTimeDisplay());
                    startActivity(confirm);
                    finish();
                });
            }).start();
            return;
        }

        BookingService.BookResult result = BookingService.getInstance().book(userKey, live.getId(), qty);
        if (result != BookingService.BookResult.SUCCESS) {
            Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT).show();
            refreshAvailability(availabilityView, live);
            return;
        }

        Intent confirm = new Intent(this, ReservationConfirmationActivity.class);
        confirm.putExtra(ReservationConfirmationActivity.EXTRA_TITLE, live.getTitle());
        confirm.putExtra(ReservationConfirmationActivity.EXTRA_QUANTITY, qty);
        confirm.putExtra(ReservationConfirmationActivity.EXTRA_DATE, live.getDateTimeDisplay());
        startActivity(confirm);
        finish();
    }
}
