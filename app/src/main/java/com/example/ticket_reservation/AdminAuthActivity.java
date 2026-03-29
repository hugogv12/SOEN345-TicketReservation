package com.example.ticket_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket_reservation.auth.AdminAccountStore;
import com.example.ticket_reservation.logic.LoginIdentifier;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Local admin operator sign-in / registration (separate from customer {@link RegisterActivity}).
 */
public class AdminAuthActivity extends AppCompatActivity {

    private View signedInPanel;
    private View formPanel;
    private TextView signedInDetails;
    private MaterialButtonToggleGroup modeToggle;
    private View layoutUsername;
    private View layoutConfirmPassword;
    private TextInputEditText emailInput;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmInput;
    private MaterialButton submitButton;

    private boolean registerMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_auth);

        findViewById(R.id.button_admin_auth_back).setOnClickListener(v ->
                NavigationHelper.goToMainMenu(this));

        signedInPanel = findViewById(R.id.admin_auth_signed_in_panel);
        formPanel = findViewById(R.id.admin_auth_form_panel);
        signedInDetails = findViewById(R.id.admin_auth_signed_in_details);
        findViewById(R.id.button_admin_auth_sign_out).setOnClickListener(v -> {
            AdminSessionPrefs.clear(this);
            refreshSignedInState();
            Toast.makeText(this, R.string.admin_signed_out_toast, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.button_admin_continue).setOnClickListener(v -> openAdminConsole());

        modeToggle = findViewById(R.id.admin_auth_mode_toggle);
        layoutUsername = findViewById(R.id.admin_auth_layout_username);
        layoutConfirmPassword = findViewById(R.id.admin_auth_layout_confirm_password);
        emailInput = findViewById(R.id.admin_auth_email);
        usernameInput = findViewById(R.id.admin_auth_username);
        passwordInput = findViewById(R.id.admin_auth_password);
        confirmInput = findViewById(R.id.admin_auth_password_confirm);
        submitButton = findViewById(R.id.admin_auth_submit);

        registerMode = modeToggle.getCheckedButtonId() == R.id.mode_admin_register;
        modeToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            registerMode = checkedId == R.id.mode_admin_register;
            updateModeUi();
        });

        submitButton.setOnClickListener(v -> attemptSubmit());
        refreshSignedInState();
    }

    private void refreshSignedInState() {
        if (AdminSessionPrefs.hasAdminSession(this)) {
            signedInPanel.setVisibility(View.VISIBLE);
            formPanel.setVisibility(View.GONE);
            String display = AdminSessionPrefs.getDisplayName(this);
            String contact = AdminSessionPrefs.getContact(this);
            String who = !display.isEmpty() ? display : displayHandleFromContact(contact);
            signedInDetails.setText(getString(R.string.signed_in_admin_body, who, contact));
        } else {
            signedInPanel.setVisibility(View.GONE);
            formPanel.setVisibility(View.VISIBLE);
            updateModeUi();
        }
    }

    private void updateModeUi() {
        layoutUsername.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        layoutConfirmPassword.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        submitButton.setText(registerMode ? R.string.admin_submit_register : R.string.admin_submit_sign_in);
    }

    private void attemptSubmit() {
        String rawContact = text(emailInput);
        String canonical = LoginIdentifier.normalize(rawContact);
        String password = text(passwordInput);
        if (!LoginIdentifier.isValidNormalized(canonical)) {
            Toast.makeText(this, R.string.auth_invalid_contact, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, R.string.auth_password_short, Toast.LENGTH_SHORT).show();
            return;
        }

        if (registerMode) {
            String username = text(usernameInput);
            if (username.length() < 2) {
                Toast.makeText(this, R.string.auth_username_short, Toast.LENGTH_SHORT).show();
                return;
            }
            String confirm = text(confirmInput);
            if (!password.equals(confirm)) {
                Toast.makeText(this, R.string.auth_password_mismatch, Toast.LENGTH_SHORT).show();
                return;
            }
            boolean ok = AdminAccountStore.register(this, canonical, username, password);
            if (ok) {
                AdminSessionPrefs.setSession(this, canonical, username);
                Toast.makeText(this, R.string.admin_register_success, Toast.LENGTH_SHORT).show();
                openAdminConsole();
            } else {
                Toast.makeText(this, R.string.admin_auth_email_taken, Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean ok = AdminAccountStore.signIn(this, canonical, password);
            if (ok) {
                String un = AdminAccountStore.getUsername(this, canonical);
                AdminSessionPrefs.setSession(this, canonical, un);
                Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
                openAdminConsole();
            } else {
                Toast.makeText(this, R.string.auth_sign_in_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openAdminConsole() {
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }

    private static String text(TextInputEditText e) {
        if (e.getText() == null) {
            return "";
        }
        return e.getText().toString().trim();
    }

    private static String displayHandleFromContact(String contact) {
        if (contact == null || contact.isEmpty()) {
            return "";
        }
        if (contact.contains("@")) {
            return contact.substring(0, contact.indexOf('@'));
        }
        return contact;
    }
}
