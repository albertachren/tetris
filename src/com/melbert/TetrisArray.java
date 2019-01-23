package com.melbert;

public class TetrisArray {

    int[][] data;
    int res;

    public TetrisArray(int newRes) {
        res = newRes;
        //new clear data array
        data = new int[res][res];
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                data[i][j] = 0;
            }
        }

    }

    void moveDown(){}

}
