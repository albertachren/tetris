package com.melbert;

import java.util.Random;

/**
 * Helper class for tetris block data and instancing
 */
public class TetrisBlock {

    public static final int[][][] shapes = {
            {{1, 1}, {1, 1}},
            {{0, 1, 0}, {1, 1, 1}, {1, 0, 1}},
            {{1}, {1}, {1, 1}},
            {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}}
    };
    public static final int[][] BM_SHAPE = {{0, 1, 0}, {1, 1, 1}, {1, 0, 1}};
    public static final int BLOCK = 0;
    public static final int BM = 1;
    //public static final int SW= 2;
    public static final int L = 2;
    public static final int BIG_LINE = 3;
    static final int[][] BLOCK_SHAPE = {{1, 1}, {1, 1}};
    static final int[][] SW_SHAPE = {{1, 0, 1, 1, 1}, {1, 0, 1, 0, 0}, {1, 1, 1, 1, 1}, {0, 0, 1, 0, 1}, {1, 1, 1, 0, 1}};
    static final int[][] BIG_LINE_SHAPE = {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
    static final int[][] L_SHAPE = {{1}, {1}, {1, 1}};



    static final int RIGHT = 2;
    static final int LEFT = 1;
    static final int DOWN = 0;
    static final int UP = 3;

    int[][] shape;
    int x;
    int y;

    TetrisBlock(int[][] shape, int x, int y) {
        this.shape = shape;
        this.x = x;
        this.y = y;
    }

    public static int[][] getRandomShape() {
        Random random = new Random();
        int randomInt = random.nextInt(shapes.length);
        return shapes[randomInt];
    }
}
