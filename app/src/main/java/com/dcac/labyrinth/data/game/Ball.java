package com.dcac.labyrinth.data.game;


import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {

    private int x; // X POSITION IN BLOCKS
    private int y; // Y POSITION IN BLOCKS
    private final int color; // BALL COLOR

    public Ball(int startX, int startY, int color) {
        this.x = startX;
        this.y = startY;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    // DRAW BALL
    public void draw(Canvas canvas, int blockSize) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(x * blockSize + blockSize / 2f, y * blockSize + blockSize / 2f, blockSize / 2f, paint);
    }

}
