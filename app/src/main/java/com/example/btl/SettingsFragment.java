package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class SettingsFragment extends Fragment {

    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        preferenceManager = new PreferenceManager(requireContext());

        MaterialButton btnEditProfile = view.findViewById(R.id.btnEditProfile);
        MaterialButton btnDarkMode = view.findViewById(R.id.btnDarkMode);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            });
        }

        if (btnDarkMode != null) {
            btnDarkMode.setOnClickListener(v -> {
                boolean isDark = !preferenceManager.isDarkMode();
                preferenceManager.setDarkMode(isDark);
                getActivity().recreate(); // To apply theme immediately
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                preferenceManager.logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        return view;
    }
}
