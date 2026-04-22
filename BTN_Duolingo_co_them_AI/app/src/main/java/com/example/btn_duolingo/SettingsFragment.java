
package com.example.btn_duolingo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private EditText etFullName, etUsername, etPassword, etDOB, etAddress, etPhone;
    private Button btnSave, btnLogout;
    private SwitchMaterial switchDarkMode, switchReminder;
    private TextView tvReminderTime;
    private LinearLayout layoutTimePicker;
    private Button btnSetReminder;
    private SharedPreferences reminderPrefs;
    
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        etFullName = view.findViewById(R.id.etEditFullName);
        etUsername = view.findViewById(R.id.etEditUsername);
        etPassword = view.findViewById(R.id.etEditPassword);
        etDOB = view.findViewById(R.id.etEditDOB);
        etAddress = view.findViewById(R.id.etEditAddress);
        etPhone = view.findViewById(R.id.etEditPhone);
        btnSave = view.findViewById(R.id.btnSaveSettings);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        switchReminder = view.findViewById(R.id.switchReminder);
        tvReminderTime = view.findViewById(R.id.tvReminderTime);
        layoutTimePicker = view.findViewById(R.id.layoutTimePicker);
        btnSetReminder = view.findViewById(R.id.btnSetReminder);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        reminderPrefs = getActivity().getSharedPreferences("DuolingoSettings", Context.MODE_PRIVATE);
        
        loadCurrentUserData();
        loadReminderSettings();

        etDOB.setOnClickListener(v -> showDatePickerDialog());

        SharedPreferences themePref = getActivity().getSharedPreferences("AppSettingPref", Context.MODE_PRIVATE);
        boolean isDarkMode = themePref.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            themePref.edit().putBoolean("DarkMode", isChecked).apply();
        });

        layoutTimePicker.setOnClickListener(v -> showTimePickerDialog());

        btnSetReminder.setOnClickListener(v -> {
            if (switchReminder.isChecked()) {
                scheduleAlarm();
            } else {
                Toast.makeText(getContext(), "Vui lòng bật nút nhắc nhở trước", Toast.LENGTH_SHORT).show();
            }
        });

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderPrefs.edit().putBoolean("reminder_on", isChecked).apply();
            if (!isChecked) {
                cancelAlarm();
            }
        });

        btnSave.setOnClickListener(v -> saveUserData());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadReminderSettings() {
        boolean isReminderOn = reminderPrefs.getBoolean("reminder_on", false);
        String savedTime = reminderPrefs.getString("reminder_time", "19:00");
        switchReminder.setChecked(isReminderOn);
        tvReminderTime.setText(savedTime);
    }

    private void showTimePickerDialog() {
        String time = tvReminderTime.getText().toString();
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minuteOfHour) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
            tvReminderTime.setText(selectedTime);
            reminderPrefs.edit().putString("reminder_time", selectedTime).apply();
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void scheduleAlarm() {
        String time = tvReminderTime.getText().toString();
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(getContext(), "Đã đặt nhắc nhở lúc " + time, Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year1);
                    etDOB.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadCurrentUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            etFullName.setText(user.getFullName());
                            etUsername.setText(user.getUsername());
                            etPassword.setText(user.getPassword());
                            etDOB.setText(user.getDob());
                            etAddress.setText(user.getAddress());
                            etPhone.setText(user.getPhone());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void saveUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        String newFullName = etFullName.getText().toString().trim();
        String newUsername = etUsername.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        String newDOB = etDOB.getText().toString().trim();
        String newAddress = etAddress.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch current data first to preserve XP and Streak
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user;
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                } else {
                    user = new User();
                    user.setXp(0);
                    user.setStreak(0);
                }
                
                if (user != null) {
                    user.setFullName(newFullName);
                    user.setUsername(newUsername);
                    user.setPassword(newPassword);
                    user.setDob(newDOB);
                    user.setAddress(newAddress);
                    user.setPhone(newPhone);
                    user.setEmail(currentUser.getEmail());

                    mDatabase.child("users").child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Profile updated on Firebase!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void logout() {
        mAuth.signOut();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        sharedPref.edit().clear().apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
