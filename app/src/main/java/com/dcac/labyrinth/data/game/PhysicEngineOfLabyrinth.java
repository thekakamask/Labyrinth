package com.dcac.labyrinth.data.game;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import java.util.List;

public class PhysicEngineOfLabyrinth {

    private Ball ball;
    private List<Block> blocks;

    private Block startBlock; // START BLOCK

    private Block endBlock; // FINISH BLOCK

    private int width; // LABYRINTH WIDTH IN BLOCKS
    private int height; // LABYRINTH HEIGHT IN BLOCKS
    private boolean gameWon = false; // FLAG TO KEEP GAME WIN STATE
    private boolean gameLost = false; // FLAG TO KEEP GAME LOST STATE

    private GraphicEngineOfLabyrinth graphicEngineOfLabyrinth;


    public PhysicEngineOfLabyrinth(Ball ball, List<Block> blocks, Block startBlock, Block endBlock, int width, int height, Context context, int blockSize) {
        this.ball = ball;
        this.blocks = blocks;
        this.startBlock = startBlock;
        this.endBlock = endBlock;
        this.width = width;
        this.height = height;
        this.graphicEngineOfLabyrinth = new GraphicEngineOfLabyrinth(context, this, blockSize);}

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Block getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(Block startBlock) {
        this.startBlock = startBlock;
    }

    public Block getEndBlock() {
        return endBlock;
    }

    public void setEndBlock(Block endBlock) {
        this.endBlock = endBlock;
    }

    public GraphicEngineOfLabyrinth getGraphicEngineOfLabyrinth() {
        return graphicEngineOfLabyrinth;
    }

    public void setGraphicEngineOfLabyrinth(GraphicEngineOfLabyrinth graphicEngineOfLabyrinth) {
        this.graphicEngineOfLabyrinth = graphicEngineOfLabyrinth;
    }

    public boolean moveBall(int dx, int dy) {

        Log.d("PhysicEngineOfLabyrinth", "Width: " + width);
        Log.d("PhysicEngineOfLabyrinth", "Height: " + height);

        // IF GAME IS OVER , DO NOTHING
        if (gameWon || gameLost) {
            return false;
        }

        int newX = ball.getX() + dx;
        int newY = ball.getY() + dy;

        // CHECK IF MOVE IS IN LIMITS OF THE LABYRINTH
        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
            // CHECK IF MOVE IS SAFE
            if (isSafe(newX, newY)) {
                // MOVE BALL
                ball.move(dx, dy);

                graphicEngineOfLabyrinth.invalidate();

                // CHKEC IF THE BALL GET TO THE END ZONE
                if (newX == endBlock.getX() && newY == endBlock.getY()) {
                    gameWon = true; // GAME WIN
                }
            } else {
                // BALL FALL IN A HOLE
                gameLost = true; // GAME LOST
                ball.setX(startBlock.getX());
                ball.setY(startBlock.getY());
                graphicEngineOfLabyrinth.invalidate();
            }
        } else {
            // MOVE OUT OF LIMIT , DO NOTHING
            return false;
        }

        // RETURN TRUE, IF THE BALL HAS MOVE
        return true;



    }


    private boolean isPath(int x, int y) {
        // CHECK IF COORDINATES DONT MATCH WITH A HOLE
        for (Block block : blocks) {
            if (block.getX() == x && block.getY() == y) {
                return false; //ITS A HOLE
            }
        }
        return true; // RIGHT PATH
    }
    private boolean isSafe(int x, int y) {
        // CHECK IF THE COORDINATES DONT MATCH WITH A HOLE
        // Vérifiez si les coordonnées ne correspondent pas à un trou
        for (Block block : blocks) {
            if (block.getX() == x && block.getY() == y) {
                // IF COLOR IS BLACK, ITS A HOLE
                return block.getColor() != Color.BLACK;
            }
        }
        // SAFE MOVE
        return true;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameLost() {
        return gameLost;
    }

    // ADD A METHOD FOR REINITIALIZE THE GAME IF NECESSARY
    public void resetGame() {
        gameWon = false;
        gameLost = false;
        ball.setX(startBlock.getX());
        ball.setY(startBlock.getY());
    }
    private boolean isArrival(int x, int y) {
        for (Block block : blocks) {
            if (block.getX() == x && block.getY() == y && block.getColor() == Color.RED) {
                return true; // BALL GET TO THE FINISH ZONE
            }
        }
        return false; // BALL DONT GET TO THE FINISH ZONE
    }

    public void loadNewLabyrinth(int[][] newLayout) {
        // Initialiser les nouveaux blocks en fonction du nouveau layout
        blocks.clear();
        for (int y = 0; y < newLayout.length; y++) {
            for (int x = 0; x < newLayout[y].length; x++) {
                int blockColor = getColorForLayoutValue(newLayout[y][x]);
                if (newLayout[y][x] == 2) {
                    startBlock = new Block(x, y, blockColor);
                    ball.setX(x);
                    ball.setY(y);
                } else if (newLayout[y][x] == 3) {
                    endBlock = new Block(x, y, blockColor);
                } else {
                    blocks.add(new Block(x, y, blockColor));
                }
            }
        }

        resetGame();

        graphicEngineOfLabyrinth.invalidate();
    }


    private int getColorForLayoutValue(int value) {
        switch (value) {
            case 0: return Color.CYAN; // GOOD PATH COLOR
            case 1: return Color.BLACK; // HOLE COLOR
            case 2: return Color.WHITE; // START BLOCK COLOR
            case 3: return Color.RED;   // FINISH BLOCK COLOR
            default: return Color.TRANSPARENT;
        }
    }

}

