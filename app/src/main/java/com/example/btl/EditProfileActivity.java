package com.example.btl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etUsername, etEmail;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        Button btnSave = findViewById(R.id.btnSave);

        // Load existing data
        etName.setText(sharedPreferences.getString("display_name", "Main H."));
        etUsername.setText(sharedPreferences.getString("username", "mainh2002"));
        etEmail.setText(sharedPreferences.getString("email", "mainh@example.com"));

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newUsername = etUsername.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            if (newName.isEmpty() || newUsername.isEmpty()) {
                Toast.makeText(this, "Name and Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("display_name", newName);
            editor.putString("username", newUsername);
            editor.putString("email", newEmail);
            editor.apply();

            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
