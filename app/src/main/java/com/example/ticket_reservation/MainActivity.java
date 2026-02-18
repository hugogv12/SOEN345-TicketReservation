package com.example.ticket_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Main screen: list of available events and Register entry point.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Event> events = getSampleEvents();
        ListView listView = findViewById(R.id.events_list);
        listView.setAdapter(new EventListAdapter(this, events));

        Button registerButton = findViewById(R.id.button_register);
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private List<Event> getSampleEvents() {
        List<Event> list = new ArrayList<>();
        list.add(new Event("Summer Concert 2026", "Mar 15, 2026", "Bell Centre, Montreal"));
        list.add(new Event("Tech Conference", "Mar 20, 2026", "Palais des congrès"));
        list.add(new Event("Hockey Game", "Mar 25, 2026", "Bell Centre, Montreal"));
        return list;
    }
}
