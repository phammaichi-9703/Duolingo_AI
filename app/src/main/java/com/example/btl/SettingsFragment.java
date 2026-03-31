package com.example.btl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
        SwitchMaterial switchSound = view.findViewById(R.id.switchSound);
        SwitchMaterial switchNotifications = view.findViewById(R.id.switchNotifications);
        View btnEditProfile = view.findViewById(R.id.btnEditProfile);
        View btnLogout = view.findViewById(R.id.btnLogout);

        // Load saved states
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        switchSound.setChecked(sharedPreferences.getBoolean("sound", true));
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications", true));

        // Dark Mode Logic
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Sound Logic
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("sound", isChecked).apply();
        });

        // Notifications Logic
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications", isChecked).apply();
        });

        // Edit Profile Click - Launch New Activity
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Logout Click
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
