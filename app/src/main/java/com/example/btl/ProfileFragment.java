package com.example.btl;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUsername, tvUserHandle;
    private SharedPreferences profilePrefs;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    ivAvatar.setImageURI(uri);
                    // In a real app, you'd save this URI to SharedPreferences or a database
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePrefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserHandle = view.findViewById(R.id.tvUserHandle);
        
        ivAvatar.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        String name = profilePrefs.getString("display_name", "Main H.");
        String handle = profilePrefs.getString("username", "mainh2002");
        
        tvUsername.setText(name);
        tvUserHandle.setText("@" + handle);
    }
}
