package com.melbert;

public class TetrisBlock {

    public static final int[][] BLOCK = {{1, 1}, {1, 1}};
    public static final int[][] BM = {{0, 1, 0}, {1, 1, 1}, {1, 0, 1}};
    public static final int[][] SW = {{1, 0, 1, 1, 1}, {1, 0, 1, 0, 0}, {1, 1, 1, 1, 1}, {0, 0, 1, 0, 1}, {1, 1, 1, 0, 1}};

    public static final int RIGHT = 2;
    public static final int LEFT = 1;
    public static final int DOWN = 0;
    public static final int UP = 3;

    int[][] shape;
    int x;
    int y;

    public TetrisBlock(int[][] shape, int x, int y) {
        this.shape = shape;
        this.x = x;
        this.y = y;
    }
}
