package com.example.btn_duolingo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView tvFullName, tvUsername, tvXP, tvStreak, tvDOB, tvAddress, tvPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvFullName = view.findViewById(R.id.tvProfileFullName);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvXP = view.findViewById(R.id.tvProfileXP);
        tvStreak = view.findViewById(R.id.tvProfileStreak);
        tvDOB = view.findViewById(R.id.tvProfileDOB);
        tvAddress = view.findViewById(R.id.tvProfileAddress);
        tvPhone = view.findViewById(R.id.tvProfilePhone);

        loadUserData();

        return view;
    }

    private void loadUserData() {
        if (getActivity() == null) return;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPref.getString("current_username", "");

        if (!username.isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            User user = dbHelper.getUserByUsername(username);

            if (user != null) {
                tvFullName.setText(user.getFullName());
                tvUsername.setText("@" + user.getUsername());
                tvDOB.setText(user.getDob());
                tvAddress.setText(user.getAddress());
                tvPhone.setText(user.getPhone());
                
                if (tvXP != null) tvXP.setText(String.valueOf(user.getXp()));
                if (tvStreak != null) tvStreak.setText(String.valueOf(user.getStreak()));
            }
        }
    }
}