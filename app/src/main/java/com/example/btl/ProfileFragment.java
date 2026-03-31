package com.example.btl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 102;
    private PreferenceManager preferenceManager;
    private TextView tvUsername, tvUserHandle, tvTotalXp, tvLessonsDone, tvStreak, tvUserLevel;
    private ImageView ivAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        preferenceManager = new PreferenceManager(requireContext());

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserHandle = view.findViewById(R.id.tvUserHandle);
        tvTotalXp = view.findViewById(R.id.tvTotalXpProfile);
        tvLessonsDone = view.findViewById(R.id.tvLessonsDone);
        tvStreak = view.findViewById(R.id.tvStreakProfile);
        tvUserLevel = view.findViewById(R.id.tvUserLevel);

        ivAvatar.setOnClickListener(v -> pickImage());

        updateUI();

        return view;
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            requireContext().getContentResolver().takePersistableUriPermission(imageUri, 
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            preferenceManager.setAvatarUri(imageUri.toString());
            ivAvatar.setImageURI(imageUri);
            Toast.makeText(getContext(), "Avatar Updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        String username = preferenceManager.getUsername();
        tvUsername.setText(username);
        tvUserHandle.setText("@" + username.toLowerCase().replace(" ", ""));
        
        tvTotalXp.setText(String.valueOf(preferenceManager.getTotalXp()));
        tvLessonsDone.setText(String.valueOf(preferenceManager.getLessonsDone()));
        tvStreak.setText(String.valueOf(preferenceManager.getStreak()));
        
        int level = (preferenceManager.getTotalXp() / 100) + 1;
        tvUserLevel.setText("Level " + level);

        String avatarUri = preferenceManager.getAvatarUri();
        if (avatarUri != null) {
            ivAvatar.setImageURI(Uri.parse(avatarUri));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
