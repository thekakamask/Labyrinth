package com.dcac.labyrinth.ui.game;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.labyrinth.data.Ball;
import com.dcac.labyrinth.data.Bloc;
import com.dcac.labyrinth.data.GraphicEngineOfLabyrinth;
import com.dcac.labyrinth.data.PhysicEngineOfLabyrinth;
import com.dcac.labyrinth.databinding.FragmentGameBinding;

import java.util.List;


public class GameFragment extends Fragment {

    private FragmentGameBinding binding;

    // ID VICTORY DIALOG BOX
    public static final int VICTORY_DIALOG = 0;
    // ID DEFEAT DIALOG BOX
    public static final int DEFEAT_DIALOG = 1;

    // GAME GRAPHIC ENGINE
    private GraphicEngineOfLabyrinth mView = null;

    // GAME PHYSIC ENGINE
    private PhysicEngineOfLabyrinth mEngine = null;

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);
        mView = new GraphicEngineOfLabyrinth(getContext());
        binding.gameContainer.addView(mView); // Assurez-vous d'avoir un FrameLayout ou similaire dans votre layout

        mEngine = new PhysicEngineOfLabyrinth(this);

        Ball ball = new Ball();
        mView.setBall(ball);
        mEngine.setBall(ball);

        List<Bloc> blocks = mEngine.buildLabyrinthe();
        mView.setBlocks(blocks);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mEngine.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mEngine.stop();
    }

    public void showDialog(int dialogId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch(dialogId) {
            case VICTORY_DIALOG:
                builder.setTitle("Victoire!")
                        .setMessage("Félicitations, vous avez gagné!")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Gérer le clic sur le bouton OK si nécessaire
                        });
                break;
            case DEFEAT_DIALOG:
                builder.setTitle("Défaite")
                        .setMessage("Dommage, vous avez perdu!")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Gérer le clic sur le bouton OK si nécessaire
                        });
                break;
        }
        builder.show();
    }
}