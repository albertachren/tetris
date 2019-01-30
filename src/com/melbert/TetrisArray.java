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

    public int[][] getData() {
        return data;
    }
}
