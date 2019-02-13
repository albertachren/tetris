package com.melbert;


import javax.swing.*;

public class GOL {
    public static int golUpate;

    public GOL(TetrisArray array) {
        Timer timer = new Timer(golUpate, e -> {
            update(array);
        });


    }

    private void update(TetrisArray array) {
        Tetris.tetris.tetrisPanel.setGraphics(updateArray(array));
    }

    private int[][] updateArray(TetrisArray array) {
        int[][] newArray = {{}};
        int[][] data = array.getData();

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                newArray[i][j] = 0;
            }
        }

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                try {
                    if(data[i][j] == 0 || data[i][j] == 2){

                        int nCount = countNeighbors(i,j,data);



                    }
                    else if(data[i][j] == 1){}
                } catch (Exception ignored) {
                }
                ;
            }
        }
        return newArray;
    }

    private int countNeighbors(int i, int j, int[][] data) {
        int nCount = 0;
        if(data[i-1][j-1] == 1){nCount++;}
        if(data[i-1][j] == 1){nCount++;}
        if(data[i-1][j+1] == 1){nCount++;}
        if(data[i][j-1] == 1){nCount++;}
        if(data[i][j+1] == 1){nCount++;}
        if(data[i+1][j-1] == 1){nCount++;}
        if(data[i+1][j] == 1){nCount++;}
        if(data[i+1][j+1] == 1){nCount++;}
        return nCount;
    }
}
