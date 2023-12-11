package com.dcac.labyrinth.data;


import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import java.util.List;

public class GraphicEngineOfLabyrinth extends View {


    public GraphicEngineOfLabyrinth(Context context) {
        super(context);
        // BALL, BLOCKS, PAINT INITIALISATION
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // DRAW LABYRINTH AND BALL
    }

    // METHOD FOR INITIALISATION AND DRAW THE LABYRINTH FROM DATA
}
