package com.example.ticket_reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket_reservation.data.EventRepository;
import com.example.ticket_reservation.data.SupabaseConfig;
import com.example.ticket_reservation.data.SupabaseRest;
import com.example.ticket_reservation.model.Event;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AdminEventEditActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "admin_event_id";

    private EventRepository eventRepository;
    private String editingId;
    private String isoDate;
    /** HH:mm or empty */
    private String startTimeHhMm;
    private TextView dateDisplay;
    private TextView timeDisplay;
    private TextInputEditText titleInput;
    private TextInputEditText locationInput;
    private TextInputEditText capacityInput;
    private Spinner categorySpinner;
    private Button markCanceledButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_edit);
        eventRepository = EventRepository.getInstance();

        findViewById(R.id.button_back_to_menu).setOnClickListener(v ->
                NavigationHelper.goToMainMenu(this));

        editingId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        titleInput = findViewById(R.id.edit_title);
        locationInput = findViewById(R.id.edit_location);
        capacityInput = findViewById(R.id.edit_capacity);
        categorySpinner = findViewById(R.id.edit_category_spinner);
        dateDisplay = findViewById(R.id.edit_date_display);
        timeDisplay = findViewById(R.id.edit_time_display);
        Button pickDate = findViewById(R.id.button_event_pick_date);
        Button pickTime = findViewById(R.id.button_event_pick_time);
        Button clearTime = findViewById(R.id.button_event_clear_time);
        Button save = findViewById(R.id.button_save_event);
        markCanceledButton = findViewById(R.id.button_cancel_event_admin);

        List<String> categories = EventRepository.defaultCategoryLabels();
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                R.layout.item_spinner, categories);
        catAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        categorySpinner.setAdapter(catAdapter);

        if (editingId != null) {
            Event existing = eventRepository.findById(editingId);
            if (existing == null) {
                finish();
                return;
            }
            titleInput.setText(existing.getTitle());
            locationInput.setText(existing.getLocation());
            capacityInput.setText(String.valueOf(existing.getCapacity()));
            isoDate = existing.getIsoDate();
            dateDisplay.setText(existing.getDateDisplay());
            startTimeHhMm = existing.getStartTime() != null ? existing.getStartTime() : "";
            refreshTimeDisplay();
            selectCategory(existing.getCategory());
            markCanceledButton.setVisibility(View.VISIBLE);
            if (existing.isCanceled()) {
                markCanceledButton.setEnabled(false);
            }
        } else {
            Calendar today = Calendar.getInstance();
            isoDate = DateUtils.toIsoDate(today);
            dateDisplay.setText(formatDisplayFromIso(isoDate));
            startTimeHhMm = "19:00";
            refreshTimeDisplay();
            markCanceledButton.setVisibility(View.GONE);
        }

        pickDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (isoDate != null) {
                String[] p = isoDate.split("-");
                if (p.length == 3) {
                    cal.set(Calendar.YEAR, Integer.parseInt(p[0]));
                    cal.set(Calendar.MONTH, Integer.parseInt(p[1]) - 1);
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(p[2]));
                }
            }
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar chosen = Calendar.getInstance();
                chosen.set(year, month, dayOfMonth);
                isoDate = DateUtils.toIsoDate(chosen);
                dateDisplay.setText(formatDisplayFromIso(isoDate));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        pickTime.setOnClickListener(v -> {
            int h = 19;
            int m = 0;
            if (startTimeHhMm != null && startTimeHhMm.length() >= 5 && startTimeHhMm.charAt(2) == ':') {
                try {
                    h = Integer.parseInt(startTimeHhMm.substring(0, 2));
                    m = Integer.parseInt(startTimeHhMm.substring(3, 5));
                } catch (NumberFormatException ignored) {
                    // keep default
                }
            }
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                startTimeHhMm = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                refreshTimeDisplay();
            }, h, m, true).show();
        });

        clearTime.setOnClickListener(v -> {
            startTimeHhMm = "";
            refreshTimeDisplay();
        });

        save.setOnClickListener(v -> saveEvent());

        markCanceledButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.mark_event_canceled)
                        .setMessage(R.string.mark_canceled_confirm)
                        .setPositiveButton(R.string.yes, (d, w) -> markCanceled())
                        .setNegativeButton(R.string.no, null)
                        .show());
    }

    private void refreshTimeDisplay() {
        if (startTimeHhMm == null || startTimeHhMm.isEmpty()) {
            timeDisplay.setText(R.string.event_time_not_set);
        } else {
            String nice = Event.formatHhMmForDisplay(startTimeHhMm, Locale.getDefault());
            timeDisplay.setText(nice.isEmpty() ? startTimeHhMm : nice);
        }
    }

    private void selectCategory(String category) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) categorySpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && item.toString().equalsIgnoreCase(category)) {
                categorySpinner.setSelection(i);
                return;
            }
        }
    }

    private void saveEvent() {
        String title = titleInput.getText() != null ? titleInput.getText().toString().trim() : "";
        String location = locationInput.getText() != null ? locationInput.getText().toString().trim() : "";
        String capRaw = capacityInput.getText() != null ? capacityInput.getText().toString().trim() : "";
        if (title.isEmpty() || location.isEmpty() || isoDate == null || isoDate.isEmpty() || capRaw.isEmpty()) {
            Toast.makeText(this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        int capacity;
        try {
            capacity = Integer.parseInt(capRaw);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (capacity <= 0) {
            Toast.makeText(this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        Object catItem = categorySpinner.getSelectedItem();
        if (catItem == null) {
            Toast.makeText(this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        String category = catItem.toString();
        String timeStored = startTimeHhMm == null ? "" : startTimeHhMm.trim();

        if (editingId == null) {
            Event created = Event.createNew(title, isoDate, timeStored, location, category, capacity);
            if (SupabaseConfig.isConfigured()) {
                new Thread(() -> {
                    try {
                        SupabaseRest.insertEvent(created);
                        runOnUiThread(() -> {
                            eventRepository.add(created);
                            Toast.makeText(this, R.string.event_saved, Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT)
                                .show());
                    }
                }).start();
                return;
            }
            eventRepository.add(created);
        } else {
            Event existing = eventRepository.findById(editingId);
            if (existing == null) {
                finish();
                return;
            }
            if (capacity < existing.getTicketsReserved()) {
                Toast.makeText(this, R.string.event_capacity_below_reserved, Toast.LENGTH_LONG).show();
                return;
            }
            existing.setTitle(title);
            existing.setIsoDate(isoDate);
            existing.setStartTime(timeStored);
            existing.setLocation(location);
            existing.setCategory(category);
            existing.setCapacity(capacity);
            if (SupabaseConfig.isConfigured()) {
                new Thread(() -> {
                    try {
                        SupabaseRest.updateEvent(existing);
                        runOnUiThread(() -> {
                            Toast.makeText(this, R.string.event_saved, Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT)
                                .show());
                    }
                }).start();
                return;
            }
        }
        Toast.makeText(this, R.string.event_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void markCanceled() {
        Event existing = eventRepository.findById(editingId);
        if (existing == null) {
            finish();
            return;
        }
        existing.setCanceled(true);
        if (SupabaseConfig.isConfigured()) {
            new Thread(() -> {
                try {
                    SupabaseRest.updateEvent(existing);
                    runOnUiThread(() -> {
                        Toast.makeText(this, R.string.event_canceled_admin, Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } catch (IOException e) {
                    runOnUiThread(() -> Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT)
                            .show());
                }
            }).start();
            return;
        }
        Toast.makeText(this, R.string.event_canceled_admin, Toast.LENGTH_SHORT).show();
        finish();
    }

    private static String formatDisplayFromIso(String iso) {
        try {
            java.text.SimpleDateFormat isoFmt = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            isoFmt.setLenient(false);
            java.util.Date d = isoFmt.parse(iso);
            if (d == null) {
                return iso;
            }
            return new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US).format(d);
        } catch (java.text.ParseException e) {
            return iso;
        }
    }
}
