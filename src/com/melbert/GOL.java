package com.melbert;


import javax.swing.*;

public class GOL {
    private static int golUpate = 1000;
    private int[][] array;

    public GOL(TetrisArray tetrisArray) {
        this.array = tetrisArray.getData();
        Timer timer = new Timer(golUpate, e -> {
            update(this.array);
            System.out.println("update");
        });
        timer.start();

    }

    private void update(int[][] array) {
        this.array = updateArray(array);
        //Tetris.tetris.tetrisPanel.setGraphics(array);
    }

    private int[][] updateArray(int[][] array) {
        int[][] data = array;
        int[][] newArray = new int[data.length][data.length];

        for (int i = 0; i < data.length - 1; i++) {
            for (int j = 0; j < data[0].length - 1; j++) {
                newArray[i][j] = 0;
            }
        }

        for (int i = 0; i < data.length - 1; i++) {
            for (int j = 0; j < data[0].length - 1; j++) {
                try {
                    if (data[i][j] == 0 || data[i][j] == 2) {

                        int nCount = countNeighbors(i, j, data);
                        if (nCount == 3) {
                            newArray[i][j] = 1;
                        }

                    } else if (data[i][j] == 1) {
                        int nCount = countNeighbors(i, j, data);
                        if (nCount < 2 || nCount > 3) {
                            newArray[i][j] = 0;
                        } else {
                            newArray[i][j] = 1;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("error golupdate");
                    System.out.println(e.toString());
                    System.out.println(e.getMessage().toString());
                    System.out.println(e.getStackTrace()[0].toString());
                }
                ;
            }
        }
        return newArray;
    }

    private int countNeighbors(int i, int j, int[][] data) {
        int nCount = 0;
        try {

            if (data[i - 1][j - 1] == 1) {
                nCount++;
            }
            if (data[i - 1][j] == 1) {
                nCount++;
            }
            if (data[i - 1][j + 1] == 1) {
                nCount++;
            }
            if (data[i][j - 1] == 1) {
                nCount++;
            }
            if (data[i][j + 1] == 1) {
                nCount++;
            }
            if (data[i + 1][j - 1] == 1) {
                nCount++;
            }
            if (data[i + 1][j] == 1) {
                nCount++;
            }
            if (data[i + 1][j + 1] == 1) {
                nCount++;
            }
        } catch (Exception ignored) {
        }
        return nCount;
    }
}
