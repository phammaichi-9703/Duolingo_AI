package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl.model.AppDatabase;
import com.example.btl.model.User;
import com.example.btl.model.UserDao;
import com.example.btl.utils.SecurityUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView btnGoToRegister;
    private PreferenceManager preferenceManager;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceManager = new PreferenceManager(this);
        userDao = AppDatabase.getDatabase(this).userDao();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase.databaseWriteExecutor.execute(() -> {
                User user = userDao.getUserByUsername(username);
                String hashedInput = SecurityUtils.hashPassword(password);

                // Handle default accounts
                if (user == null && isDefaultAccount(username, password)) {
                    user = new User(username, hashedInput);
                    userDao.insert(user);
                    user = userDao.getUserByUsername(username);
                }

                User finalUser = user;
                runOnUiThread(() -> {
                    if (finalUser != null && finalUser.password.equals(hashedInput)) {
                        preferenceManager.setLoggedIn(true, finalUser.username);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private boolean isDefaultAccount(String user, String pass) {
        return (user.equals("admin") || user.equals("chi") || user.equals("bao") || user.equals("manh")) 
                && pass.equals("123");
    }
}
