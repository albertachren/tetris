package com.melbert;

import java.util.ArrayList;
import java.util.List;

/**
 * Gamespace class, containing game logic data and methods
 */
public class TetrisArray {

    private int[][] data;
    private int res;
    private List<TetrisBlock> blocks = new ArrayList<TetrisBlock>();


    TetrisArray(int newRes) {
        res = newRes;
        //new clear data array
        data = new int[res][res];
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                data[i][j] = 0;
                //Random random = new Random(); //temporary random fill
                //data[i][j] = random.nextInt(2);
            }
        }

    }


    void moveDown() { //move all pixels and transparent pixels downwards
        for (int i = 0; i < res - 1; i++) {
            for (int j = 0; j < res; j++) {
                if (data[i][j] == 1 && !(data[i + 1][j] == 1)) {
                    data[i + 1][j] = 1;
                    data[i][j] = 0;
                } else if (data[i][j] == 2 && !(data[i + 1][j] == 2)) {
                    data[i + 1][j] = 2;
                    data[i][j] = 0;
                }
            }
        }


    }

    private void clear() {
        for (int i = 0; i < this.data.length; i++) {
            for (int j = 0; j < this.data[0].length; j++) {
                this.data[i][j] = 0;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                string.append(data[i][j]);
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
        System.out.println(block.x);
        System.out.println(block.y);

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
                        data[block.x + i][block.y + j] = block.shape[i][j];
                    } catch (Exception e) {
                    }
                }
            }
        }

    }


    int[][] getData() {
        //update array
        this.clear();
        this.updateBlocks();
        return data;
    }
}
