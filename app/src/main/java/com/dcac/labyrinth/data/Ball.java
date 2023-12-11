package com.dcac.labyrinth.data;


import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {

    private float x; // X POSITION OF THE BALL
    private float y; // Y POSITION OF THE BALL
    private float radius; // RADIUS OF THE BALL

    public Ball(float x, float y, float radius) {
        this.x=x;
        this.y=y;
        this.radius=radius;
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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    // METHODE FOR MOOVE THE BALL
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    // DRAW THE BALL IN THE CANVAS
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, radius, paint);
    }

}
