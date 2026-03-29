package com.example.ticket_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket_reservation.data.EventRepository;
import com.example.ticket_reservation.data.SupabaseDataSync;
import com.example.ticket_reservation.model.Event;

import java.util.ArrayList;
import java.util.List;


public class AdminActivity extends AppCompatActivity {

    private EventRepository eventRepository;
    private ListView listView;
    private EventListAdapter adapter;
    private final List<Event> rows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        eventRepository = EventRepository.getInstance();

        findViewById(R.id.button_back_to_menu).setOnClickListener(v ->
                NavigationHelper.goToMainMenu(this));

        findViewById(R.id.button_admin_sign_out).setOnClickListener(v -> {
            AdminSessionPrefs.clear(this);
            NavigationHelper.goToMainMenu(this);
        });

        listView = findViewById(R.id.admin_events_list);
        adapter = new EventListAdapter(this, rows, true);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event e = rows.get(position);
            Intent i = new Intent(this, AdminEventEditActivity.class);
            i.putExtra(AdminEventEditActivity.EXTRA_EVENT_ID, e.getId());
            startActivity(i);
        });

        Button add = findViewById(R.id.button_add_event);
        add.setOnClickListener(v ->
                startActivity(new Intent(this, AdminEventEditActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupabaseDataSync.refreshEventsAsync(this, eventRepository, () -> {
            rows.clear();
            rows.addAll(eventRepository.getAllEvents());
            adapter.notifyDataSetChanged();
        });
    }
}
