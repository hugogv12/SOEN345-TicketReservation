package com.example.ticket_reservation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Simple confirmation screen after a successful reservation.
 */
public class ReservationConfirmationActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_QUANTITY = "extra_quantity";
    public static final String EXTRA_DATE = "extra_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_confirmation);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        int qty = getIntent().getIntExtra(EXTRA_QUANTITY, 0);
        String date = getIntent().getStringExtra(EXTRA_DATE);

        TextView message = findViewById(R.id.confirm_message);
        message.setText(getString(R.string.confirmation_message_format, title, qty, date));

        Button done = findViewById(R.id.button_done);
        done.setOnClickListener(v -> NavigationHelper.goToMainMenu(this));
    }
}
