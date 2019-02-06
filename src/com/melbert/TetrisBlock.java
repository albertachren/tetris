package com.melbert;

/**
 * Helper class for tetris block data and instancing
 */
public class TetrisBlock {

    static final int[][] BLOCK = {{1, 1}, {1, 1}};
    public static final int[][] BM = {{0, 1, 0}, {1, 1, 1}, {1, 0, 1}};
    static final int[][] SW = {{1, 0, 1, 1, 1}, {1, 0, 1, 0, 0}, {1, 1, 1, 1, 1}, {0, 0, 1, 0, 1}, {1, 1, 1, 0, 1}};
    static final int[][] BIG_LINE = {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

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
}
