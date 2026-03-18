package com.example.ticket_reservation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket_reservation.data.EventRepository;
import com.example.ticket_reservation.data.SupabaseDataSync;
import com.example.ticket_reservation.logic.EventFilter;
import com.example.ticket_reservation.logic.FilterCriteria;
import com.example.ticket_reservation.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "event_id";

    private EventRepository eventRepository;
    private RecyclerView eventsRecycler;
    private TextView eventsEmpty;
    private HomeEventsAdapter eventsAdapter;
    private EditText searchInput;
    private TextView dateLabel;
    private Spinner locationSpinner;
    private Spinner categorySpinner;

    private String selectedIsoDate;
    private final List<Event> displayed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventRepository = EventRepository.getInstance();

        searchInput = findViewById(R.id.search_events);
        dateLabel = findViewById(R.id.filter_date_label);
        locationSpinner = findViewById(R.id.spinner_location);
        categorySpinner = findViewById(R.id.spinner_category);
        eventsRecycler = findViewById(R.id.events_recycler);
        eventsEmpty = findViewById(R.id.events_empty);
        dateLabel.setText(R.string.filter_date_none);

        eventsRecycler.setLayoutManager(new LinearLayoutManager(this));
        eventsRecycler.setNestedScrollingEnabled(false);
        eventsAdapter = new HomeEventsAdapter(displayed, this::openEventDetail);
        eventsRecycler.setAdapter(eventsAdapter);

        findViewById(R.id.button_pick_date).setOnClickListener(v -> showDatePicker());
        Button clearDate = findViewById(R.id.button_clear_date);
        clearDate.setOnClickListener(v -> {
            selectedIsoDate = null;
            dateLabel.setText(R.string.filter_date_none);
            applyFilters();
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        locationSpinner.setOnItemSelectedListener(new SimpleSelectionListener(() -> applyFilters()));
        categorySpinner.setOnItemSelectedListener(new SimpleSelectionListener(() -> applyFilters()));

        findViewById(R.id.button_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.button_my_reservations).setOnClickListener(v ->
                startActivity(new Intent(this, MyReservationsActivity.class)));

        findViewById(R.id.button_admin).setOnClickListener(v ->
                startActivity(new Intent(this, AdminActivity.class)));

        populateFilterSpinners(null, null);
        SupabaseDataSync.refreshEventsAsync(this, eventRepository, this::applyFilters);
    }

    private void openEventDetail(Event e) {
        Intent i = new Intent(this, EventDetailActivity.class);
        i.putExtra(EXTRA_EVENT_ID, e.getId());
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String prevLoc = readSpinnerValue(locationSpinner);
        String prevCat = readSpinnerValue(categorySpinner);
        SupabaseDataSync.refreshEventsAsync(this, eventRepository, () -> {
            populateFilterSpinners(prevLoc, prevCat);
            applyFilters();
        });
    }

    private static String readSpinnerValue(Spinner spinner) {
        Object item = spinner.getSelectedItem();
        return item != null ? item.toString() : null;
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (selectedIsoDate != null) {
            String[] p = selectedIsoDate.split("-");
            if (p.length == 3) {
                cal.set(Calendar.YEAR, Integer.parseInt(p[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(p[1]) - 1);
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(p[2]));
            }
        }
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar chosen = Calendar.getInstance();
            chosen.set(year, month, dayOfMonth);
            selectedIsoDate = DateUtils.toIsoDate(chosen);
            dateLabel.setText(selectedIsoDate);
            applyFilters();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void populateFilterSpinners(String previousLocation, String previousCategory) {
        List<String> locLabels = new ArrayList<>();
        locLabels.add(getString(R.string.spinner_all_locations));
        locLabels.addAll(eventRepository.distinctLocations());
        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(this,
                R.layout.item_spinner, locLabels);
        locAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        locationSpinner.setAdapter(locAdapter);
        selectSpinnerByValue(locationSpinner, previousLocation, getString(R.string.spinner_all_locations));

        List<String> catLabels = new ArrayList<>();
        catLabels.add(getString(R.string.spinner_all_categories));
        catLabels.addAll(eventRepository.distinctCategories());
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                R.layout.item_spinner, catLabels);
        catAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        categorySpinner.setAdapter(catAdapter);
        selectSpinnerByValue(categorySpinner, previousCategory, getString(R.string.spinner_all_categories));
    }

    private void selectSpinnerByValue(Spinner spinner, String value, String defaultLabel) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        if (adapter == null) {
            return;
        }
        String target = (value == null || value.isEmpty()) ? defaultLabel : value;
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && item.toString().equals(target)) {
                spinner.setSelection(i);
                return;
            }
        }
        spinner.setSelection(0);
    }

    private void applyFilters() {
        String search = searchInput.getText() != null ? searchInput.getText().toString() : "";
        String loc = locationSpinner.getSelectedItem() != null
                ? locationSpinner.getSelectedItem().toString()
                : null;
        if (getString(R.string.spinner_all_locations).equals(loc)) {
            loc = null;
        }
        String cat = categorySpinner.getSelectedItem() != null
                ? categorySpinner.getSelectedItem().toString()
                : null;
        if (getString(R.string.spinner_all_categories).equals(cat)) {
            cat = null;
        }
        FilterCriteria criteria = new FilterCriteria(search, selectedIsoDate, loc, cat);
        List<Event> filtered = EventFilter.apply(eventRepository.getAllEvents(), criteria, false);
        displayed.clear();
        displayed.addAll(filtered);
        eventsAdapter.notifyDataSetChanged();
        boolean empty = displayed.isEmpty();
        eventsRecycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        eventsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private static final class SimpleSelectionListener implements android.widget.AdapterView.OnItemSelectedListener {
        private final Runnable onChange;

        SimpleSelectionListener(Runnable onChange) {
            this.onChange = onChange;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            onChange.run();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            onChange.run();
        }
    }
}
