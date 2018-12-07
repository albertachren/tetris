package com.melbert;

public class TetrisGame {

    int[] gamespace;

    public TetrisGame(int xres, int yres) {
        gamespace = new int[xres];
        for (int i = 0; i < yres; i++) {
            //gamespace[i] = new int[yres];
        }
    }
}
