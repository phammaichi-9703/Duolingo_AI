package com.example.btl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl.model.AppDatabase;
import com.example.btl.model.User;
import com.example.btl.model.UserDao;
import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends Fragment {

    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        userDao = AppDatabase.getDatabase(requireContext()).userDao();
        RecyclerView rvRanking = view.findViewById(R.id.rvRanking);
        rvRanking.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRanking(rvRanking);

        return view;
    }

    private void loadRanking(RecyclerView rv) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<User> users = userDao.getAllUsersByXp();
            List<RankAdapter.RankItem> rankList = new ArrayList<>();
            for (User u : users) {
                rankList.add(new RankAdapter.RankItem(u.username, u.totalXp));
            }
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> rv.setAdapter(new RankAdapter(rankList)));
            }
        });
    }
}
