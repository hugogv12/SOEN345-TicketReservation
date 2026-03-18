package com.example.ticket_reservation;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticket_reservation.model.Event;

import java.util.List;

/**
 * Home screen event rows (same row layout as {@link EventListAdapter}, tap opens detail).
 */
public final class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.EventRowHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private final List<Event> events;
    private final OnEventClickListener listener;

    public HomeEventsAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventRowHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull EventRowHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static final class EventRowHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView category;
        private final TextView metaLine;
        private final TextView availability;

        EventRowHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            category = itemView.findViewById(R.id.event_category);
            metaLine = itemView.findViewById(R.id.event_meta_line);
            availability = itemView.findViewById(R.id.event_availability);
        }

        void bind(Event event, OnEventClickListener listener) {
            title.setText(event.getTitle());
            title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            category.setText(event.getCategory());
            metaLine.setText(event.getDateTimeDisplay() + " · " + event.getLocation());
            availability.setText(
                    itemView.getContext().getString(R.string.availability_format,
                            event.getAvailableTickets(), event.getCapacity()));
            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}
