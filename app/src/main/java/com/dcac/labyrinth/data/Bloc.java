package com.dcac.labyrinth.data;

import android.graphics.RectF;

public class Bloc {
    enum Type { HOLE, BEGIN, END};



    private static float SIZE;

    private Type mType= null;
    private RectF mRectangle=null;

    // Attributs pour les coordonnées originales
    private int originalX;
    private int originalY;

    public Type getType(){
        return mType;
    }

    public RectF getRectangle() {
        return mRectangle;
    }

    public static void setSize(float size) {
        SIZE = size;
    }

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

    public void recalculatePosition(int pX, int pY){
        // Recalculer la position et la taille du rectangle du bloc
        mRectangle = new RectF(originalX * SIZE, originalY * SIZE, (originalX + 1) * SIZE, (originalY + 1) * SIZE);
    }

    public Bloc(Type pType, int pX, int pY) {
        this.mType = pType;
        this.originalX = pX;
        this.originalY = pY;
        this.mRectangle = new RectF(pX * SIZE, pY * SIZE, (pX + 1) * SIZE, (pY + 1) * SIZE);
    }
}
