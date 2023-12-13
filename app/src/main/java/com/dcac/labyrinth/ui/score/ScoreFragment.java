package com.dcac.labyrinth.ui.score;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.data.Score;
import com.dcac.labyrinth.databinding.FragmentGameBinding;
import com.dcac.labyrinth.databinding.FragmentScoreBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreFragment extends Fragment {


    private FragmentScoreBinding binding;
    private ScoreFragmentAdapter adapter;

    public static ScoreFragment newInstance() {
        return new ScoreFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentScoreBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView() {
        List<Score> scores = new ArrayList<>();
        scores.add(new Score("User1", 10));
        scores.add(new Score("User2", 20));


        adapter = new ScoreFragmentAdapter(scores);
        binding.scoreRecyclerView.setAdapter(adapter);
        binding.scoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.scoreRecyclerView.getContext(), LinearLayoutManager.VERTICAL);

        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider);
        dividerItemDecoration.setDrawable(dividerDrawable);

        binding.scoreRecyclerView.addItemDecoration(dividerItemDecoration);


    }
}