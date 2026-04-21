package com.example.btn_duolingo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView tvFullName, tvUsername, tvXP, tvStreak, tvDOB, tvAddress, tvPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private ValueEventListener userListener;

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

        mAuth = FirebaseAuth.getInstance();
        loadUserData();

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded() || getContext() == null) return;
                    
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            tvFullName.setText(user.getFullName());
                            tvUsername.setText("@" + user.getUsername());
                            tvDOB.setText(user.getDob());
                            tvAddress.setText(user.getAddress());
                            tvPhone.setText(user.getPhone());
                            tvXP.setText(String.valueOf(user.getXp()));
                            tvStreak.setText(String.valueOf(user.getStreak()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            userRef.addValueEventListener(userListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }
}
