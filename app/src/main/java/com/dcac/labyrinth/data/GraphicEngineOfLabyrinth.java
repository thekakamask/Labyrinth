package com.dcac.labyrinth.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.List;

public class GraphicEngineOfLabyrinth extends SurfaceView implements SurfaceHolder.Callback {

    Ball mBall;
    public Ball getBall() {
        return mBall;
    }

    public void setBall(Ball pBall) {
        this.mBall = pBall;
    }

    SurfaceHolder mSurfaceHolder;
    DrawingThread mThread;

    private List<Bloc> mBlocks = null;
    public List<Bloc> getBlocks() {
        return mBlocks;
    }

    public void setBlocks(List<Bloc> pBlocks) {
        this.mBlocks = pBlocks;
    }

    Paint mPaint;

    public GraphicEngineOfLabyrinth(Context pContext) {
        super(pContext);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mThread = new DrawingThread();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        mBall = new Ball();
    }

    // THIS METHODE WILL BE CALL FROM THE DRAW THREAD
    private void drawGame(Canvas pCanvas) {

        if (pCanvas != null) {
            // DELETE BACKGROUND
            pCanvas.drawColor(Color.CYAN);

            // DRAW BLOCKS
            if (mBlocks != null) {
                for (Bloc b : mBlocks) {
                    mPaint.setColor(getBlockColor(b.getType()));
                    pCanvas.drawRect(b.getRectangle(), mPaint);
                }
            }

            // DRAW BALL
            if (mBall != null) {
                mPaint.setColor(mBall.getCouleur());
                pCanvas.drawCircle(mBall.getX(), mBall.getY(), Ball.RADIUS, mPaint);
            }
        }
    }

    // METHOD FOR OBTAINING THE COLOR OF A BLOC WITH HIS TYPE
    private int getBlockColor(Bloc.Type type) {
        switch (type) {
            case BEGIN:
                return Color.WHITE;
            case END:
                return Color.RED;
            case HOLE:
                return Color.BLACK;
            default:
                return Color.GRAY; // Une couleur par défaut
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder pHolder, int pFormat, int pWidth, int pHeight) {
        //
    }

    @Override
    public void surfaceCreated(SurfaceHolder pHolder) {
        mThread.keepDrawing = true;
        mThread.start();
        // ADAPT SIZE OF THE BALL AND BLOCS
        adjustGameElementsToScreenSize();
    }

    private void adjustGameElementsToScreenSize(){
        int screenWidth = getWidth();
        int screenHeight= getHeight();

        // Exemple : définir la taille des blocs comme un pourcentage de la largeur de l'écran
        Bloc.setSize(screenWidth * 0.05f);

        // Recalculer les positions des blocs
        for (Bloc bloc : mBlocks) {
            // Ici, remplacez par la logique appropriée pour obtenir les coordonnées originales
            int originalX = bloc.getOriginalX(); // Exemple, assurez-vous que cette méthode existe
            int originalY = bloc.getOriginalY(); // Exemple, assurez-vous que cette méthode existe
            bloc.recalculatePosition(originalX, originalY);
        }

        if (mBall != null) {
            // Ici, vous devrez également recalculer la position de la balle si nécessaire
            mBall.recalculatePosition(screenWidth, screenHeight);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder pHolder) {
        mThread.keepDrawing = false;
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }

    }

    private class DrawingThread extends Thread {
        boolean keepDrawing = true;

        @Override
        public void run() {
            Canvas canvas;
            while (keepDrawing) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder) {
                        if (canvas != null) {
                            drawGame(canvas);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                // DRAW AT 50 FPS
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {

                }
            }
        }

    }

}
