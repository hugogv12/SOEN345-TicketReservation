package com.example.ticket_reservation;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket_reservation.data.BookingService;
import com.example.ticket_reservation.data.EventRepository;
import com.example.ticket_reservation.data.ReservationRepository;
import com.example.ticket_reservation.data.SupabaseConfig;
import com.example.ticket_reservation.data.SupabaseDataSync;
import com.example.ticket_reservation.model.Reservation;

import java.util.ArrayList;
import java.util.List;


public class MyReservationsActivity extends AppCompatActivity implements ReservationListAdapter.OnCancelClickListener {

    private ListView listView;
    private TextView emptyMessage;
    private ReservationListAdapter adapter;
    private final List<Reservation> rows = new ArrayList<>();
    private BookingService bookingService;
    private ReservationRepository reservationRepository;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        findViewById(R.id.button_back_to_menu).setOnClickListener(v ->
                NavigationHelper.goToMainMenu(this));

        bookingService = BookingService.getInstance();
        reservationRepository = ReservationRepository.getInstance();
        eventRepository = EventRepository.getInstance();

        listView = findViewById(R.id.reservations_list);
        emptyMessage = findViewById(R.id.reservations_empty_text);
        adapter = new ReservationListAdapter(this, rows, this, eventRepository);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.reservations_empty));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if (!SessionPrefs.hasUser(this)) {
            rows.clear();
            adapter.notifyDataSetChanged();
            emptyMessage.setText(R.string.register_to_reserve);
            return;
        }
        emptyMessage.setText(R.string.no_reservations);
        String userKey = SessionPrefs.getUserKey(this);
        if (SupabaseConfig.isConfigured()) {
            SupabaseDataSync.refreshReservationsAsync(this, userKey, reservationRepository, () -> {
                List<Reservation> list = reservationRepository.findByUser(userKey);
                rows.clear();
                rows.addAll(list);
                adapter.notifyDataSetChanged();
            });
            return;
        }
        List<Reservation> list = reservationRepository.findByUser(userKey);
        rows.clear();
        rows.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelReservation(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cancel_reservation)
                .setMessage(R.string.cancel_reservation_confirm)
                .setPositiveButton(R.string.yes, (d, w) -> {
                    String uk = SessionPrefs.getUserKey(this);
                    if (SupabaseConfig.isConfigured()) {
                        new Thread(() -> {
                            boolean ok = bookingService.cancelReservation(uk, reservation.getId());
                            runOnUiThread(() -> {
                                if (ok) {
                                    refresh();
                                } else {
                                    Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).start();
                    } else {
                        boolean ok = bookingService.cancelReservation(uk, reservation.getId());
                        if (ok) {
                            refresh();
                        } else {
                            Toast.makeText(this, R.string.reservation_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
