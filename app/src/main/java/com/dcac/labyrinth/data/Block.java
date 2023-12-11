package com.dcac.labyrinth.data;


import android.graphics.Canvas;
import android.graphics.Paint;

public class Block {

    private float x; // X POSITION OF THE BLOCK
    private float y; // Y POSITION OF THE BLOCK
    private float width; // BLOCK WIDTH
    private float height; // BLOCK HEIGHT


    public Block(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    // DRAW BLOCK IN THE CANVAS
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(x,y,x+width, y+height, paint);
    }


}
