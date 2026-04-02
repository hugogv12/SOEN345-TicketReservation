package com.example.ticket_reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;
import com.example.ticket_reservation.data.EventRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Lists the current user\'s reservations with cancel actions.
 */
public class ReservationListAdapter extends ArrayAdapter<Reservation> {

    public interface OnCancelClickListener {
        void onCancelReservation(Reservation reservation);
    }

    private final OnCancelClickListener listener;
    private final EventRepository events;

    public ReservationListAdapter(Context context, List<Reservation> reservations,
                                  OnCancelClickListener listener, EventRepository events) {
        super(context, 0, reservations);
        this.listener = listener;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservation, parent, false);
        }
        Reservation r = getItem(position);
        if (r != null) {
            TextView title = convertView.findViewById(R.id.reservation_title);
            TextView meta = convertView.findViewById(R.id.reservation_meta);
            Button cancel = convertView.findViewById(R.id.button_cancel_reservation);

            Event live = events.findById(r.getEventId());
            String suffix = "";
            if (live != null && live.isCanceled()) {
                suffix = " (" + getContext().getString(R.string.canceled_badge) + ")";
            }
            title.setText(r.getEventTitleSnapshot() + suffix);
            String dateDisplay = formatDisplay(r.getEventIsoDateSnapshot());
            String quantityLine = r.getQuantity() + " ticket(s)";
            meta.setText(quantityLine + " · " + dateDisplay + " · " + r.getEventLocationSnapshot());

            cancel.setOnClickListener(v -> listener.onCancelReservation(r));
        }
        return convertView;
    }

    private static String formatDisplay(String iso) {
        try {
            SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            isoFmt.setLenient(false);
            Date d = isoFmt.parse(iso);
            if (d == null) {
                return iso;
            }
            return new SimpleDateFormat("MMM d, yyyy", Locale.US).format(d);
        } catch (ParseException e) {
            return iso;
        }
    }
}
