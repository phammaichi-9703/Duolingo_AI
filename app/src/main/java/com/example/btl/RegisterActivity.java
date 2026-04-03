package com.example.btl;

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
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase.databaseWriteExecutor.execute(() -> {
                if (userDao.getUserByUsername(username) != null) {
                    runOnUiThread(() -> Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show());
                    return;
                }

                String hashedPassword = SecurityUtils.hashPassword(password);
                User newUser = new User(username, hashedPassword);
                userDao.insert(newUser);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });

        btnBackToLogin.setOnClickListener(v -> finish());
    }
}
