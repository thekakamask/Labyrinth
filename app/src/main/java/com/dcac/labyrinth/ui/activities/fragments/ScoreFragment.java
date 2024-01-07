package com.dcac.labyrinth.ui.activities.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.data.models.Score;
import com.dcac.labyrinth.data.models.User;
import com.dcac.labyrinth.databinding.FragmentScoreBinding;
import com.dcac.labyrinth.injection.UserViewModelFactory;
import com.dcac.labyrinth.ui.views.ScoreFragmentAdapter;
import com.dcac.labyrinth.viewModels.UserViewModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class ScoreFragment extends Fragment {

    private UserViewModel userViewModel;
    private FragmentScoreBinding binding;
    private ScoreFragmentAdapter adapter;




    public static ScoreFragment newInstance() {
        return new ScoreFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModelFactory factory = UserViewModelFactory.getInstance(requireContext().getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

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
        // Configure Layout Manager et DividerItemDecoration
        binding.scoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.scoreRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider);
        dividerItemDecoration.setDrawable(dividerDrawable);
        binding.scoreRecyclerView.addItemDecoration(dividerItemDecoration);

        // Get user data
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        if (resource.data != null) {
                            List<Score> scores = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : resource.data.getDocuments()) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    scores.add(new Score(user.getUserName(), user.getScore()));
                                }
                            }
                            updateAdapter(scores);
                        }
                        break;
                    case ERROR:
                        Toast.makeText(getContext(), R.string.error_charging_scores, Toast.LENGTH_LONG).show();
                        Log.e("ScoreFragment", getString(R.string.Error_users_retrieval));
                        break;
                    case LOADING:
                        // Optional : display charging indicator
                        break;
                }
            } else {
                // Handle case if resource is null
            }
        });
    }
        /*List<Score> scores = new ArrayList<>();
        scores.add(new Score("User1", 10));
        scores.add(new Score("User2", 20));


        adapter = new ScoreFragmentAdapter(scores);
        binding.scoreRecyclerView.setAdapter(adapter);
        binding.scoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.scoreRecyclerView.getContext(), LinearLayoutManager.VERTICAL);

        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.recycler_view_divider);
        dividerItemDecoration.setDrawable(dividerDrawable);

        binding.scoreRecyclerView.addItemDecoration(dividerItemDecoration);
*/

    private void updateAdapter(List<Score> scores) {
        scores.sort((score1, score2) -> Integer.compare(score2.getScore(), score1.getScore()));

        adapter = new ScoreFragmentAdapter(scores);
        binding.scoreRecyclerView.setAdapter(adapter);
    }
}