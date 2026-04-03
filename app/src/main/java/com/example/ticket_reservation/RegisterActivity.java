package com.example.ticket_reservation;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticket_reservation.auth.LocalAccountStore;
import com.example.ticket_reservation.data.SupabaseAuth;
import com.example.ticket_reservation.data.SupabaseAuth.AuthResult;
import com.example.ticket_reservation.data.SupabaseConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private View signedInPanel;
    private View authFormPanel;
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
        setContentView(R.layout.activity_register);

        findViewById(R.id.button_back_to_menu).setOnClickListener(v ->
                NavigationHelper.goToMainMenu(this));

        signedInPanel = findViewById(R.id.signed_in_panel);
        authFormPanel = findViewById(R.id.auth_form_panel);
        signedInDetails = findViewById(R.id.signed_in_details);
        findViewById(R.id.button_sign_out).setOnClickListener(v -> {
            SessionPrefs.clear(this);
            refreshSignedInState();
            Toast.makeText(this, R.string.signed_out_toast, Toast.LENGTH_SHORT).show();
        });

        modeToggle = findViewById(R.id.auth_mode_toggle);
        layoutUsername = findViewById(R.id.layout_username);
        layoutConfirmPassword = findViewById(R.id.layout_confirm_password);
        emailInput = findViewById(R.id.register_email);
        usernameInput = findViewById(R.id.register_username);
        passwordInput = findViewById(R.id.register_password);
        confirmInput = findViewById(R.id.register_password_confirm);
        submitButton = findViewById(R.id.register_submit);

        registerMode = modeToggle.getCheckedButtonId() == R.id.mode_register;
        modeToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            registerMode = checkedId == R.id.mode_register;
            updateModeUi();
        });

        submitButton.setOnClickListener(v -> attemptSubmit());
        refreshSignedInState();
    }

    /**
     * When {@code finishing} is true we just called {@link #finish()}; defer idling so Espresso waits
     * until the previous activity (e.g. Main) is shown again.
     */
    private void signalAuthAsyncIdleDone(boolean finishing) {
        if (finishing) {
            ViewGroup root = findViewById(android.R.id.content);
            if (root != null) {
                root.post(AuthAsyncIdling::exit);
                return;
            }
        }
        AuthAsyncIdling.exit();
    }

    private void refreshSignedInState() {
        if (SessionPrefs.hasUser(this)) {
            signedInPanel.setVisibility(View.VISIBLE);
            authFormPanel.setVisibility(View.GONE);
            String u = SessionPrefs.getUsername(this);
            String e = SessionPrefs.getEmail(this);
            String who = !u.isEmpty() ? u : (e.contains("@") ? e.substring(0, e.indexOf('@')) : e);
            signedInDetails.setText(getString(R.string.signed_in_body, who, e));
        } else {
            signedInPanel.setVisibility(View.GONE);
            authFormPanel.setVisibility(View.VISIBLE);
            updateModeUi();
        }
    }

    private void updateModeUi() {
        layoutUsername.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        layoutConfirmPassword.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        submitButton.setText(registerMode ? R.string.submit_register : R.string.submit_sign_in);
    }

    private void attemptSubmit() {
        String email = text(emailInput);
        String password = text(passwordInput);
        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, R.string.auth_invalid_email, Toast.LENGTH_SHORT).show();
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
            submitButton.setEnabled(false);
            AuthAsyncIdling.enter();
            new Thread(() -> {
                if (SupabaseConfig.isConfigured()) {
                    AuthResult r = SupabaseAuth.signUp(email, password, username);
                    runOnUiThread(() -> {
                        try {
                            submitButton.setEnabled(true);
                            if (r.success) {
                                SessionPrefs.setSession(this, r.email, r.username, r.accessToken);
                                boolean hasToken = r.accessToken != null && !r.accessToken.isEmpty();
                                Toast.makeText(this,
                                        hasToken ? R.string.register_success : R.string.register_confirm_email,
                                        hasToken ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                                NavigationHelper.goToMainMenu(this);
                                signalAuthAsyncIdleDone(true);
                            } else {
                                Toast.makeText(this, r.errorMessage, Toast.LENGTH_LONG).show();
                                signalAuthAsyncIdleDone(false);
                            }
                        } catch (RuntimeException e) {
                            signalAuthAsyncIdleDone(false);
                            throw e;
                        }
                    });
                } else {
                    boolean ok = LocalAccountStore.register(this, email, username, password);
                    runOnUiThread(() -> {
                        try {
                            submitButton.setEnabled(true);
                            if (ok) {
                                SessionPrefs.setSession(this, email, username, null);
                                Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                                NavigationHelper.goToMainMenu(this);
                                signalAuthAsyncIdleDone(true);
                            } else {
                                Toast.makeText(this, R.string.auth_email_taken, Toast.LENGTH_SHORT).show();
                                signalAuthAsyncIdleDone(false);
                            }
                        } catch (RuntimeException e) {
                            signalAuthAsyncIdleDone(false);
                            throw e;
                        }
                    });
                }
            }).start();
        } else {
            submitButton.setEnabled(false);
            AuthAsyncIdling.enter();
            new Thread(() -> {
                if (SupabaseConfig.isConfigured()) {
                    AuthResult r = SupabaseAuth.signIn(email, password);
                    runOnUiThread(() -> {
                        try {
                            submitButton.setEnabled(true);
                            if (r.success) {
                                String displayName = r.username;
                                if (displayName.isEmpty() && r.email.contains("@")) {
                                    displayName = r.email.substring(0, r.email.indexOf('@'));
                                }
                                SessionPrefs.setSession(this, r.email, displayName, r.accessToken);
                                Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
                                NavigationHelper.goToMainMenu(this);
                                signalAuthAsyncIdleDone(true);
                            } else {
                                Toast.makeText(this, r.errorMessage, Toast.LENGTH_LONG).show();
                                signalAuthAsyncIdleDone(false);
                            }
                        } catch (RuntimeException e) {
                            signalAuthAsyncIdleDone(false);
                            throw e;
                        }
                    });
                } else {
                    boolean ok = LocalAccountStore.signIn(this, email, password);
                    runOnUiThread(() -> {
                        try {
                            submitButton.setEnabled(true);
                            if (ok) {
                                String un = LocalAccountStore.getUsername(this, email);
                                SessionPrefs.setSession(this, email, un, null);
                                Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
                                NavigationHelper.goToMainMenu(this);
                                signalAuthAsyncIdleDone(true);
                            } else {
                                Toast.makeText(this, R.string.auth_sign_in_failed, Toast.LENGTH_SHORT).show();
                                signalAuthAsyncIdleDone(false);
                            }
                        } catch (RuntimeException e) {
                            signalAuthAsyncIdleDone(false);
                            throw e;
                        }
                    });
                }
            }).start();
        }
    }

    private static String text(TextInputEditText e) {
        if (e.getText() == null) {
            return "";
        }
        return e.getText().toString().trim();
    }
}
