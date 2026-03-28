package com.example.ticket_reservation;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ticket_reservation.model.Event;

import java.util.List;


public class EventListAdapter extends ArrayAdapter<Event> {

    private final boolean adminStyle;

    public EventListAdapter(@NonNull Context context, @NonNull List<Event> events, boolean adminStyle) {
        super(context, 0, events);
        this.adminStyle = adminStyle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }
        Event event = getItem(position);
        if (event != null) {
            TextView title = convertView.findViewById(R.id.event_title);
            TextView category = convertView.findViewById(R.id.event_category);
            TextView metaLine = convertView.findViewById(R.id.event_meta_line);
            TextView availability = convertView.findViewById(R.id.event_availability);

            String titleText = event.getTitle();
            if (adminStyle && event.isCanceled()) {
                titleText = titleText + " — " + getContext().getString(R.string.canceled_badge);
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            title.setText(titleText);
            category.setText(event.getCategory());
            metaLine.setText(event.getDateTimeDisplay() + " · " + event.getLocation());
            availability.setText(getContext().getString(R.string.availability_format,
                    event.getAvailableTickets(), event.getCapacity()));
        }
        return convertView;
    }
}
