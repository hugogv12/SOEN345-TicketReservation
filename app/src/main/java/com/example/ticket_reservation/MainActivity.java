package com.example.ticket_reservation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Main launcher activity for the Ticket Reservation app.
 * Phase 1: Basic runnable shell. Customers and admins will be added in later phases.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
