package com.example.ticket_reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Adapter to display a list of events.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    public EventListAdapter(@NonNull Context context, @NonNull List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }
        Event event = getItem(position);
        if (event != null) {
            ((TextView) convertView.findViewById(R.id.event_title)).setText(event.getTitle());
            ((TextView) convertView.findViewById(R.id.event_date)).setText(event.getDate());
            ((TextView) convertView.findViewById(R.id.event_location)).setText(event.getLocation());
        }
        return convertView;
    }
}
