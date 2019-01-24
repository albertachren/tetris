package com.melbert;

import java.util.Random;

public class TetrisArray {

    int[][] data;
    int res;

    public TetrisArray(int newRes) {
        res = newRes;
        //new clear data array
        data = new int[res][res];
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                Random random = new Random(); //temporary

                data[i][j] = random.nextInt(2);
            }
        }

    }

    void moveDown(){}

    public int[][] getData() {
        return data;
    }
}
