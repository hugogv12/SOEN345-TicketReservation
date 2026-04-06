package com.example.ticket_reservation;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


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

        Button emailBtn = findViewById(R.id.button_email_ticket);
        emailBtn.setOnClickListener(v -> openEmailComposer(title, date, qty));

        Button done = findViewById(R.id.button_done);
        done.setOnClickListener(v -> NavigationHelper.goToMainMenu(this));
    }

    /**
     * Opens the device email app with a pre-filled message. The app does not run an SMTP server
     * or call a transactional email API — the user completes send in Gmail / Outlook / etc.
     */
    private void openEmailComposer(String title, String date, int qty) {
        String userEmail = SessionPrefs.getEmail(this);
        String subject = getString(R.string.email_ticket_subject, title);
        String body = getString(R.string.email_ticket_body, title, date, qty);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        if (!userEmail.isEmpty()) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail});
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.email_ticket)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.email_ticket_no_app, Toast.LENGTH_LONG).show();
        }
    }
}
