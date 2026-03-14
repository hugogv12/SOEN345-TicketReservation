package com.example.ticket_reservation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.button_back_to_menu).setOnClickListener(v ->
                NavigationHelper.goToMainMenu(this));

        EditText emailInput = findViewById(R.id.register_email);
        EditText phoneInput = findViewById(R.id.register_phone);
        Button submitButton = findViewById(R.id.register_submit);

        submitButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            if (email.isEmpty() && phone.isEmpty()) {
                Toast.makeText(this, R.string.register_need_email_or_phone, Toast.LENGTH_SHORT).show();
                return;
            }
            String userKey = !email.isEmpty() ? email : phone;
            SessionPrefs.setUserKey(this, userKey);
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
