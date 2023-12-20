package com.dcac.labyrinth.data.utils;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.dcac.labyrinth.data.models.Ball;
import com.dcac.labyrinth.data.models.Block;

public class GraphicEngineOfLabyrinth extends View {


    private PhysicEngineOfLabyrinth physicEngineOfLabyrinth;
    private int blockSize;
    private Paint paint; // ADD PAINT OBJECT FOR DRAW BLOCKS

    public GraphicEngineOfLabyrinth(Context context, PhysicEngineOfLabyrinth physicsEngine, int blockSize) {
        super(context);
        this.physicEngineOfLabyrinth = physicsEngine;
        this.blockSize = blockSize;
        this.paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        Log.d("GraphicEngineOfLabyrinth", "Context: " + context);
        Log.d("GraphicEngineOfLabyrinth", "PhysicsEngine: " + physicsEngine);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // DRAW ALL BLOCKS
        for (Block block : physicEngineOfLabyrinth.getBlocks()) {
            paint.setColor(block.getColor()); // DEFINE COLOR OF EACH BLOCKS
            block.draw(canvas, blockSize);


        }

        // DRAW BALL
        Ball ball = physicEngineOfLabyrinth.getBall();
        paint.setColor(ball.getColor());
        ball.draw(canvas, blockSize);

        Log.d("GraphicEngineOfLabyrinth", "onDraw called");
    }

    public void updateBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
}
