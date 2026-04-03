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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl.model.AppDatabase;
import com.example.btl.model.User;
import com.example.btl.model.UserDao;

public class ProfileFragment extends Fragment {

    private PreferenceManager preferenceManager;
    private UserDao userDao;
    private TextView tvUsername, tvUserHandle, tvTotalXp, tvLessonsDone, tvStreak, tvUserLevel;
    private ImageView ivAvatar;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        handleImageResult(imageUri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        preferenceManager = new PreferenceManager(requireContext());
        userDao = AppDatabase.getDatabase(requireContext()).userDao();

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserHandle = view.findViewById(R.id.tvUserHandle);
        tvTotalXp = view.findViewById(R.id.tvTotalXpProfile);
        tvLessonsDone = view.findViewById(R.id.tvLessonsDone);
        tvStreak = view.findViewById(R.id.tvStreakProfile);
        tvUserLevel = view.findViewById(R.id.tvUserLevel);

        ivAvatar.setOnClickListener(v -> pickImage());

        loadUserData();

        return view;
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void handleImageResult(Uri imageUri) {
        requireContext().getContentResolver().takePersistableUriPermission(imageUri, 
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        String username = preferenceManager.getUsername();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDao.getUserByUsername(username);
            if (user != null) {
                user.avatarUri = imageUri.toString();
                userDao.update(user);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        ivAvatar.setImageURI(imageUri);
                        Toast.makeText(getContext(), "Avatar Updated!", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void loadUserData() {
        String username = preferenceManager.getUsername();
        if (username == null) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDao.getUserByUsername(username);
            if (user != null && isAdded()) {
                requireActivity().runOnUiThread(() -> updateUI(user));
            }
        });
    }

    private void updateUI(User user) {
        tvUsername.setText(user.username);
        tvUserHandle.setText("@" + user.username.toLowerCase().replace(" ", ""));
        
        tvTotalXp.setText(String.valueOf(user.totalXp));
        tvLessonsDone.setText(String.valueOf(user.lessonsDone));
        tvStreak.setText(String.valueOf(user.streak));
        
        int level = (user.totalXp / 100) + 1;
        tvUserLevel.setText("Level " + level);

        if (user.avatarUri != null) {
            ivAvatar.setImageURI(Uri.parse(user.avatarUri));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }
}
