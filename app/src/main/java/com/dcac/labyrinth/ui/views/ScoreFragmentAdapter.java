package com.dcac.labyrinth.ui.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.data.models.Score;

import java.util.List;

public class ScoreFragmentAdapter extends RecyclerView.Adapter<ScoreFragmentAdapter.ViewHolder> {

    private List<Score> mScores;

    public ScoreFragmentAdapter (List<Score> scores) {
        mScores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_score_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score score = mScores.get(position);
        holder.scoreNameView.setText(score.getUsername());
        holder.scoreView.setText(String.valueOf(score.getScore()));
    }


    @Override
    public int getItemCount() {
        return mScores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView scoreIconView;
        public TextView scoreNameView;
        public TextView scoreView;
        public ViewHolder(@NonNull View view) {
            super(view);
            scoreIconView = view.findViewById(R.id.score_icon);
            scoreNameView = view.findViewById(R.id.scoreName);
            scoreView = view.findViewById(R.id.score);
        }
    }

}
