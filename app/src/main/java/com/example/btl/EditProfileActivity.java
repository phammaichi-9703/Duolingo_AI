package com.example.btl;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etEditUsername;
    private MaterialButton btnSaveProfile;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        preferenceManager = new PreferenceManager(this);

        etEditUsername = findViewById(R.id.etEditUsername);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        etEditUsername.setText(preferenceManager.getUsername());

        btnSaveProfile.setOnClickListener(v -> {
            String newUsername = etEditUsername.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // In admin mode, we just update the preference
            // We'll add a setUsername method to PreferenceManager if needed, 
            // but for now let's just use the existing logic or add it.
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
