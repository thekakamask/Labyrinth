package com.dcac.labyrinth.ui.game;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;


import com.dcac.labyrinth.data.Ball;
import com.dcac.labyrinth.data.Block;
import com.dcac.labyrinth.data.GraphicEngineOfLabyrinth;
import com.dcac.labyrinth.data.PhysicEngineOfLabyrinth;
import com.dcac.labyrinth.databinding.FragmentGameBinding;

import java.util.ArrayList;
import java.util.List;


public class GameFragment extends Fragment {

    private FragmentGameBinding binding;
    private GraphicEngineOfLabyrinth graphicEngineOfLabyrinth;
    private PhysicEngineOfLabyrinth physicEngineOfLabyrinth;
    private Ball ball;
    private List<Block> blocks;
    private int blockSize; // SIZE OF EACH BLOCKS IN PIXELS



    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);

        // INITIALIZE GAME COMPONENTS HERE

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonTop.setEnabled(true);
        binding.buttonBottom.setEnabled(true);
        binding.buttonLeft.setEnabled(true);
        binding.buttonRight.setEnabled(true);


        final Runnable checkGameState = new Runnable() {
            @Override
            public void run() {
                if (physicEngineOfLabyrinth.isGameWon()) {
                    Toast.makeText(getContext(), "You won", Toast.LENGTH_SHORT).show();
                    physicEngineOfLabyrinth.resetGame(); // RE INITIALIZE THE GAME AFTER THE WIN
                } else if (physicEngineOfLabyrinth.isGameLost()) {
                    Toast.makeText(getContext(), "You loose !", Toast.LENGTH_SHORT).show();
                    physicEngineOfLabyrinth.resetGame(); // RE INITIALIZE THE GAME AFTER THE LOOSE
                }
            }
        };

        binding.buttonTop.setOnClickListener(v -> {
            if (physicEngineOfLabyrinth.moveBall(0, -1)) {
                graphicEngineOfLabyrinth.invalidate();
                binding.getRoot().post(checkGameState); // CHECK GAME STATE AFTER THE REDRAW
            }
        });

        binding.buttonBottom.setOnClickListener(v -> {
            if (physicEngineOfLabyrinth.moveBall(0, 1)) {
                graphicEngineOfLabyrinth.invalidate();
                binding.getRoot().post(checkGameState);
            }
        });

        binding.buttonLeft.setOnClickListener(v -> {
            if (physicEngineOfLabyrinth.moveBall(-1, 0)) {
                graphicEngineOfLabyrinth.invalidate();
                binding.getRoot().post(checkGameState);
            }
        });

        binding.buttonRight.setOnClickListener(v -> {
            if (physicEngineOfLabyrinth.moveBall(1, 0)) {
                graphicEngineOfLabyrinth.invalidate();
                binding.getRoot().post(checkGameState);
            }
        });

        initGameComponents();

    }


    private void initGameComponents() {

        // BLOCKSIZE COULD BE DETERMINED BY THE SIZE OF THE GAMECONTAINER OR A PREDEFINED VALUE
        // EXAMPLE : GAMECONTAINER HAS A WIDTH OF 1080 PIXELS AND MY LABYRINTH HAS 9 BLOCKS HORIZONTALLY
        blockSize = binding.gameContainer.getWidth() / 20; // ( OU = 1080 /9)
        // 0 = FREE PATH , 1 = HOLE, 2 = START, 3 = ARRIVAL
        int[][] labyrinthLayout = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 2, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,1, 1, 1, 1, 1, 1, 1}
        };


        // CREATE AN INSTANCE OF THE BALL AT THE STARTING POSITION WITH A GREEN COLOR
        int ballStartX = 0; // THE X POSITION WHERE THE BALL STARTS, IN BLOCKS
        int ballStartY = 0; // THE Y POSITION WHERE THE BALL STARTS, IN BLOCKS
        int ballColor = Color.GREEN; // The color of the ball
        ball = new Ball(ballStartX, ballStartY, ballColor);


        // CREATE BLOCKS BASED ON LABYRINTH LAYOUT
        blocks = new ArrayList<>();
        int blockColor;

        Block startBlock = null;
        Block endBlock = null;

        for (int y = 0; y < labyrinthLayout.length; y++) {
            for (int x = 0; x < labyrinthLayout[y].length; x++) {

                switch (labyrinthLayout[y][x]) {
                    case 1:
                        blockColor = Color.BLACK; // HOLE COLOR
                        blocks.add(new Block(x, y, blockColor));
                        break;
                    case 2:
                        // DEPART POSITION, UPDATE BALL COORDINATES
                        ball.setX(x);
                        ball.setY(y);
                        startBlock = new Block(x,y, Color.WHITE);
                        Log.d( "GameFragment begin: ",  "x :" + x + " y :" + y );
                        break;
                    case 3:
                        blockColor = Color.RED; // FINISH ZONE COLOR
                        endBlock = new Block(x, y , blockColor);
                        Log.d( "GameFragment finish: ",  "x :" + x + " y :" + y );
                        break;
                }

                /*if (labyrinthLayout[y][x] == 2) {
                    startBlock = new Block(x, y, Color.WHITE);
                } else if (labyrinthLayout[y][x] == 3) {
                    endBlock = new Block(x, y, Color.RED);
                }*/
            }
        }

        for (Block block : blocks) {
            Log.d("GameFragment", "Block: " + block + ", Color: " + block.getColor());
        }


        // ASSURE THAT THE ENDBLOCK IS DEFINE BEFORE CONTINUE
        if (startBlock == null || endBlock == null) {
            throw new IllegalStateException("BEGIN AND FINISH BLOCKS NEED TO BE DEFINE IN THE LAYOUT");
        }

        Log.d("GameFragment", "Ball: " + ball);
        Log.d("GameFragment", "Blocks: " + blocks);
        Log.d("GameFragment", "StartBlock: " + startBlock);
        Log.d("GameFragment", "EndBlock: " + endBlock);

        // INTIALIZE PHYSICS AND GRAPHICS ENGINES
        physicEngineOfLabyrinth = new PhysicEngineOfLabyrinth(ball, blocks, startBlock, endBlock, labyrinthLayout[0].length, labyrinthLayout.length, getContext(), blockSize);
        graphicEngineOfLabyrinth = new GraphicEngineOfLabyrinth(getContext(), physicEngineOfLabyrinth, blockSize);
        binding.gameContainer.addView(graphicEngineOfLabyrinth);
        Log.d("GameFragment", "gameContainer: " + binding.gameContainer);


        // CALCULATE BLOCKSIZE ASYNCHRONOUSLY AFTER THE VIEW IS CREATED
        binding.gameContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                blockSize = binding.gameContainer.getWidth() / 20; // ( OU = 1080 /9)
                Log.d("GameFragmentTreeObserver", "BlockSize: " + blockSize);

                // REMOVE THE LISTENER TO PREVENT MULTIPLE CALLS
                binding.gameContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // UPDATE SIZE BLOC IN GRAPHICENGINEOFLABYRINTH
                graphicEngineOfLabyrinth.updateBlockSize(blockSize);
            }
        });

        graphicEngineOfLabyrinth.updateBlockSize(blockSize);
    }

}