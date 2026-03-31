package com.example.btl;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl.model.AppDatabase;
import com.example.btl.model.User;
import com.example.btl.model.UserDao;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etRegUsername, etRegPassword;
    private MaterialButton btnRegister;
    private TextView btnBackToLogin;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = AppDatabase.getDatabase(this).userDao();

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userDao.getUserByUsername(username) != null) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(username, password);
            userDao.insert(newUser);
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBackToLogin.setOnClickListener(v -> finish());
    }
}
