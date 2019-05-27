package com.melbert;

import java.util.ArrayList;
import java.util.List;

/**
 * Gamespace data structure class
 */
public class TetrisArray {
    private int[][] blockData;
    private int[][] pixelData;


    public List<TetrisBlock> getBlocks() {
        return blocks;
    }
    private List<TetrisBlock> blocks = new ArrayList<TetrisBlock>();

    private int score = 0;
    private int res;

    TetrisArray(int newRes) {
        res = newRes;

        //Create a new clear blockData array
        blockData = new int[res][res];
        pixelData = new int[res][res];
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                blockData[i][j] = 0;
                pixelData[i][j] = 0;
            }
        }

    }

    synchronized public int getScore() {
        return score;
    }

    void update() {
        //Update the array
        this.clear();
        this.updateBlocks();
    }

    public void setBlocks(List<TetrisBlock> blocks) {
        this.blocks = blocks;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < blockData.length; i++) {
            for (int j = 0; j < blockData[0].length; j++) {
                string.append(blockData[i][j]);
            }
        }
        return string.toString();
    }

    //Move the current block
    boolean moveBlocks(int direction) {
        TetrisBlock block = blocks.get(blocks.size() - 1); //Get the active block
        switch (direction) {
            case TetrisBlock.DOWN:
                if ((block.shape.length + block.x + 1 > this.res || block.shape[0].length + block.y > this.res) || collisionCheck(block, TetrisBlock.DOWN)) { // check if shape is out of area
                    return false; //Exit
                }
                block.x = block.x + 1; //If check cleared, increment coordinate
                break;
            case TetrisBlock.LEFT:
                if ((block.shape.length + block.x > this.res || block.y - 1 < 0) || collisionCheck(block, TetrisBlock.LEFT)) { // check if shape is out of area
                    return false;
                }
                block.y = block.y - 1;
                break;
            case TetrisBlock.RIGHT:
                if ((block.shape.length + block.x > this.res || block.shape[0].length + block.y + 1 > this.res) || collisionCheck(block, TetrisBlock.RIGHT)) { // check if shape is out of area
                    //System.out.println("shape out");
                    return false;
                }
                block.y = block.y + 1;
                break;
            case TetrisBlock.UP: //Unused
                block.x = block.x - 1;
                break;
        }
        return true;
    }

    //Check for collision between two blocks
    private boolean collisionCheck(TetrisBlock block, int direction) {
        boolean collision = false;
        switch (direction) {
            case TetrisBlock.DOWN:
                int i = block.shape.length - 1;
                for (int j = 0; j < block.shape[0].length; j++) {
                    if ((blockData[block.x + block.shape.length][j + block.y] == 1 && block.shape[i][j] == 1)) { //If there is another block right beneath any of the lowest pixels in the shape
                        collision = true;
                    }
                }
                break;
            case TetrisBlock.LEFT:
                for (int j = 0; j < block.shape[0].length; j++) {
                    try {
                        if (blockData[block.x + j][block.y - 1] == 1) {
                            collision = true;
                        }
                    } catch (Exception e) {
                        collision = true;
                    }
                }
                break;
            case TetrisBlock.RIGHT:
                for (int j = 0; j < block.shape[0].length; j++) {
                    try {
                        if (blockData[block.x + j][block.y + block.shape[0].length] == 1) {
                            collision = true;
                        }
                    } catch (Exception e) {
                        collision = true;
                    }
                }
                break;
        }


        return collision;
    }

    void insertBlock(TetrisBlock block) {
        blocks.add(block);
    }

    private void updateBlocks() { //TODO: Doc
        for (int i1 = 0; i1 < blocks.size(); i1++) {
            TetrisBlock block = blocks.get(i1);
            int sizex = block.shape.length;
            int sizey = block.shape[0].length;
            for (int i = 0; i < sizex; i++) {
                for (int j = 0; j < sizey; j++) {
                    try {
                        if (block.shape[i][j] == 1) blockData[block.x + i][block.y + j] = block.shape[i][j];
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    synchronized public int getRes() {
        return res;
    }

    //TODO: ROTATE

    void clear() {
        for (int i = 0; i < this.blockData.length; i++) {
            for (int j = 0; j < this.blockData[0].length; j++) {
                this.blockData[i][j] = 0;
            }
        }
    }

    int[][] getData() {
        //might have to sum arrays
        return blockData;
    }

    void clearBlocks() {
        /*
        for (TetrisBlock block : blocks) {
            int sizex = block.shape.length;
            int sizey = block.shape[0].length;
            for (int i = 0; i < sizex; i++) {
                for (int j = 0; j < sizey; j++) {
                    try {
                        blockData[block.x + i][block.y + j] = 0;
                    } catch (Exception e) {
                        System.out.println("error clearBlock");

                    }
                }
            }
        }
        System.out.println(toString());*/
        blocks.clear();
    }

    private void deactivateBlocks() {
        for (TetrisBlock block : blocks) {
            if (block.shape.length + block.x + 1 > this.res || block.shape[0].length + block.y > this.res) { // check if shape is out of area
                for (int i = 0; i < block.shape.length; i++) {
                    for (int j = 0; j < block.shape[0].length; j++) {
                        pixelData[block.x + i][block.y + j] = block.shape[i][j];
                    }
                }
                blocks.remove(block);
            }
        }
    }

    public void findWhole() { //find whole lines
        for (int i = 0; i < blockData.length; i++) {
            boolean lineFull = true;
            for (int j = 0; j < blockData[0].length; j++) {
                if (blockData[i][j] == 0 || blockData[i][j] == 2) {
                    lineFull = false;
                }
            }
            if (lineFull) {
                //clearLine(i);
                moveBottomDown();
                score++;
            }
        }
    }

    private void moveBottomDown() {
        for (TetrisBlock block : blocks) {
            if (block.x + block.shape[0].length >= res) {
                //if ((block.shape.length + block.x + 1 > this.res || block.shape[0].length + block.y > this.res) || collisionCheck(block, TetrisBlock.DOWN)) { // check if shape is out of area
                //    return;
                //}
                block.x = block.x + 1;
            }
        }
    }

    private void clearLine(int line) { //clear a single horizontal line
        //System.out.println("line: ");
        //System.out.println(line);
        for (int i = 0; i < this.blockData[line].length; i++) {
            this.blockData[line][i] = 0;
            //System.out.println("index: " + String.valueOf(i));
        }

    }

    public void setPixel(int x, int y, int state) {
        blockData[x][y] = state;
        //System.out.println(String.format("[%d, %d]: %d", x, y, state));
    }

    void moveDown() { //move all pixels and transparent pixels downwards
        for (int i = 0; i < res - 1; i++) {
            for (int j = 0; j < res; j++) {
                if (blockData[i][j] == 1 && !(blockData[i + 1][j] == 1)) {
                    blockData[i + 1][j] = 1;
                    blockData[i][j] = 0;
                } else if (blockData[i][j] == 2 && !(blockData[i + 1][j] == 20)) {
                    blockData[i + 1][j] = 2;
                    blockData[i][j] = 0;
                }
            }
        }


    }
}
