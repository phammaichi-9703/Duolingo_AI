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
import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        RecyclerView rvRanking = view.findViewById(R.id.rvRanking);
        rvRanking.setLayoutManager(new LinearLayoutManager(getContext()));

        List<UserRank> rankings = new ArrayList<>();
        rankings.add(new UserRank(1, "Nguyễn Văn An", 5420));
        rankings.add(new UserRank(2, "Trần Thị Bình", 4850));
        rankings.add(new UserRank(3, "Lê Hoàng Long", 4200));
        rankings.add(new UserRank(4, "Phạm Minh Đức", 3900));
        rankings.add(new UserRank(5, "Hoàng Lan Anh", 3550));
        rankings.add(new UserRank(6, "Đặng Quang Hải", 3100));
        rankings.add(new UserRank(7, "Bùi Tiến Dũng", 2800));
        rankings.add(new UserRank(8, "Vũ Thùy Linh", 2450));
        rankings.add(new UserRank(9, "Ngô Gia Bảo", 2100));
        rankings.add(new UserRank(10, "Đỗ Phương Thảo", 1850));

        RankAdapter adapter = new RankAdapter(rankings);
        rvRanking.setAdapter(adapter);

        return view;
    }
}
