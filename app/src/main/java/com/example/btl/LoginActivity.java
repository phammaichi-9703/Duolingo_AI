package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView btnGoToRegister;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceManager = new PreferenceManager(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.equals("admin") && password.equals("123")) {
                preferenceManager.setLoggedIn(true);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Admin login: admin/123", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoToRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Use admin/123 to login", Toast.LENGTH_SHORT).show();
        });
    }
}
