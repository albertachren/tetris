package com.melbert;

import java.util.ArrayList;
import java.util.List;

/**
 * Gamespace class, containing game logic blockData and methods
 */
public class TetrisArray {

    private int[][] blockData;
    private int[][] pixelData;

    private int res;
    private List<TetrisBlock> blocks = new ArrayList<TetrisBlock>();


    TetrisArray(int newRes) {
        res = newRes;
        //new clear blockData array
        blockData = new int[res][res];
        pixelData = new int[res][res];
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                blockData[i][j] = 0;
                pixelData[i][j] = 0;
                //Random random = new Random(); //temporary random fill
                //blockData[i][j] = random.nextInt(2);
            }
        }

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

    private void clear() {
        for (int i = 0; i < this.blockData.length; i++) {
            for (int j = 0; j < this.blockData[0].length; j++) {
                this.blockData[i][j] = 0;
            }
        }
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

    void moveBlocks(int direction) {
        //for (TetrisBlock block : blocks) {}
        TetrisBlock block = blocks.get(blocks.size() - 1);
        switch (direction) {
            case TetrisBlock.DOWN:
                if (block.shape.length + block.x + 1 > this.res || block.shape[0].length + block.y > this.res) { // check if shape is out of area
                    System.out.println("shape out");
                    break;
                }
                block.x = block.x + 1;
                break;
            case TetrisBlock.LEFT:
                if (block.shape.length + block.x > this.res || block.y - 1 < 0) { // check if shape is out of area
                    System.out.println("shape out");
                    break;
                }
                block.y = block.y - 1;
                break;
            case TetrisBlock.RIGHT:
                if (block.shape.length + block.x > this.res || block.shape[0].length + block.y + 1 > this.res) { // check if shape is out of area
                    System.out.println("shape out");
                    break;
                }
                block.y = block.y + 1;
                break;
            case TetrisBlock.UP:
                block.x = block.x - 1;
                break;
        }
        System.out.println("block x coordinate: " + String.valueOf(block.x));
        System.out.println("block y coordinate: " + String.valueOf(block.y));

    }

    void insertBlock(TetrisBlock block) {
        blocks.add(block);
    }

    private void updateBlocks() {
        for (TetrisBlock block : this.blocks) {
            //TetrisBlock block = blocks.get(blocks.size() - 1);
            int sizex = block.shape.length;
            int sizey = block.shape[0].length;


            for (int i = 0; i < sizex; i++) {
                for (int j = 0; j < sizey; j++) {
                    try {
                        blockData[block.x + i][block.y + j] = block.shape[i][j];
                    } catch (Exception e) {
                    }
                }
            }
        }

    }


    int[][] getData() {
        //update array
        this.deactivateBlocks();
        this.findWhole();
        for (TetrisBlock block : blocks) {
            this.clearBlock(block);
        }
        this.updateBlocks();
        //TODO: combine block and pixelData
        return blockData;
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

    private void clearBlock(TetrisBlock block) {
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

    public void findWhole() { //find whole lines
        for (int i = 0; i < blockData.length; i++) {
            boolean lineFull = true;
            for (int j = 0; j < blockData[0].length; j++) {
                if (blockData[i][j] == 0 || blockData[i][j] == 2) {
                    lineFull = false;
                }
            }
            if (lineFull) {
                clearLine(i);
                System.out.println("lines found");
            }
        }
    }

    private void clearLine(int line) { //clear a single horizontal line
        //System.out.println("line: ");
        //System.out.println(line);
        for (int i = 0; i < this.pixelData[line].length; i++) {
            this.pixelData[line][i] = 0;
            //System.out.println("index: " + String.valueOf(i));
        }

    }

    public void setPixel(int x, int y, int state) {
        pixelData[x][y] = state;
        System.out.println(String.format("[%d, %d]: %d", x, y, state));
    }
}
