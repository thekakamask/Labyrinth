package com.dcac.labyrinth.data.models;


import android.graphics.Canvas;
import android.graphics.Paint;

public class Block {

    private final int x; // X POSITION IN BLOCKS
    private final int y; // Y POSITION IN BLOCKS
    private final int color; // BLOCK COLOR

    public Block(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    // DRAW BLOCKS
    public void draw(Canvas canvas, int blockSize) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(x * blockSize, y * blockSize, (x + 1) * blockSize, (y + 1) * blockSize, paint);
    }


}
