package com.dcac.labyrinth.data;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

public class Ball {


    //RADIUS OF THE BALL
    public static final int RADIUS = 10;

    //COLOR OF THE BALL
    private int mCouleur = Color.GREEN;
    public int getCouleur() {
        return mCouleur;
    }

    // MAX SPEED
    private static final float MAX_SPEED = 20.0f;

    // BALL WILL ACCELERATE LESS FAST
    private static final float COMPENSATEUR = 8.0f;

    //USE FOR COMPENSATE THE REBOUND
    private static final float REBOUND = 1.75f;

    //DEPART POSITION OF THE BALL
    private RectF mInitialRectangle = null;

    //WITH THE INITIAL RECTANGLE, WE DETERMINE THE INITAL POSITION OF THE BALL
    public void setInitialRectangle(RectF pInitialRectangle) {
        this.mInitialRectangle = pInitialRectangle;
        this.mX = pInitialRectangle.left + RADIUS;
        this.mY = pInitialRectangle.top + RADIUS;
    }

    //RECTANGLE OF COLLISION
    private RectF mRectangle = null;

    // X COORDINATES
    private float mX;
    public float getX() {
        return mX;
    }

    public void setPosX(float pPosX) {
        mX = pPosX;

        // IF THE BALL IS OU THE FRAME, DO REBOUND
        if(mX < RADIUS) {
            mX = RADIUS;
            // REBOUND MEAN CHANGE DIRECTION OF THE BALL
            mSpeedY = -mSpeedY / RADIUS;
        } else if(mX > mWidth - RADIUS) {
            mX = mWidth - RADIUS;
            mSpeedY = -mSpeedY / RADIUS;
        }
    }

    // Y COORDINATES
    private float mY;
    public float getY(){
        return mY;
    }

    public void setPosY(float pPosY) {
        mY= pPosY;
        if (mY <RADIUS) {
            mY=RADIUS;
            mSpeedX = -mSpeedX / REBOUND;
        } else if (mY > mHeight - REBOUND) {
            mY = mHeight - REBOUND;
            mSpeedX = -mSpeedX / REBOUND;
        }
    }

    // X AXIS SPEED
    private float mSpeedX=0;
    // USE WHEN REBOUND ON THE X AXIS WALL
    public void changeXspeed() {
        mSpeedX = -mSpeedX;
    }

    // Y AXIS SPEED
    private float mSpeedY=0;
    // USE WHEN REBOUND ON THE Y AXIS WALL
    public void changeYspeed() {
        mSpeedY = -mSpeedY;
    }

    // SCREEN SIZE HEIGHT
    private int mHeight = -1;
    public void setHeight(int pHeight) {
        this.mHeight=pHeight;
    }

    // SCREEN SIZE WIDTH
    private int mWidth=-1;
    public void setWidth(int pWith) {
        this.mWidth= pWith;
    }

    public Ball() {
        mRectangle= new RectF();
    }

    //UPDATE BALL COORDINATES
    public RectF putXAndY (float pX, float pY) {
        mSpeedX += pX/COMPENSATEUR;

        if (mSpeedX>MAX_SPEED)
            mSpeedX= MAX_SPEED;
        if(mSpeedX<-MAX_SPEED)
            mSpeedX= -MAX_SPEED;

        mSpeedY += pY / COMPENSATEUR;
        if (mSpeedY > MAX_SPEED)
            mSpeedY= MAX_SPEED;
        if (mSpeedY<-MAX_SPEED)
            mSpeedY=-MAX_SPEED;

        setPosX(mX + mSpeedY);
        setPosY(mY + mSpeedX);

        //UPDATE COLLISION COORDINATES
        mRectangle.set(mX-RADIUS, mY -RADIUS, mX+RADIUS, mY+RADIUS);
        return mRectangle;
    }

    //PUT THE BALL IN BEGINNING POSITION
    public void reset(){
        mSpeedX=0;
        mSpeedY=0;
        this.mX = mInitialRectangle.left + RADIUS;
        this.mY = mInitialRectangle.top+RADIUS;
    }

    // Méthode pour recalculer la position de la balle
    public void recalculatePosition(int screenWidth, int screenHeight) {
        // Adaptez cette logique en fonction de la façon dont vous voulez positionner la balle
        // Par exemple, vous pouvez la centrer ou la positionner à un endroit spécifique
        float newX = screenWidth / 2f;
        float newY = screenHeight / 2f;

        // Mettre à jour la position et le rectangle de collision de la balle
        setPosX(newX);
        setPosY(newY);
        mRectangle.set(newX - RADIUS, newY - RADIUS, newX + RADIUS, newY + RADIUS);

        // Adaptez également les dimensions de la balle si nécessaire
        mWidth = screenWidth;
        mHeight = screenHeight;
    }
}
