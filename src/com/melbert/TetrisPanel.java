package com.melbert;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

/**
 * Gameplay panel class
 */
public class TetrisPanel extends JPanel {
    private ArrayList<JPanel> pixels;
    private int res = Tetris.RES;
    private JPanel[][] pixelsArr = new JPanel[res][res];

    TetrisPanel() {
        this.setBackground(Color.WHITE);
        setLayout(new GridLayout(res, res));
        this.pixels = new ArrayList<>();

        //Create the "pixels", JPanels
        for (int i = 0; i < res * res; i++) {
            Border border = BorderFactory.createLineBorder(Color.black, 1);
            JPanel panel = new JPanel();
            panel.setBorder(border); //Give them a border for differentiating
            this.pixels.add(panel);
            try {
                pixelsArr[i / res][i % res] = panel;
            } catch (Exception e) {
                System.out.println("error");
            }
        }
        //Add the to the panel
        for (JPanel panel : pixels) {
            add(panel);
        }
    }

    int getRes() {
        return res;
    }

    //Set the pixels to the right state according to the TetrisArray
    void setGraphics(TetrisArray array) {
        int[][] data = array.getData();
        for (int i = 0; i < res; i++) { //loop trough graphics array
            for (int j = 0; j < res; j++) {
                pixelsArr[i][j].setBackground(new Color(255, 255, 255));
                if (data[i][j] == 1) {
                    pixelsArr[i][j].setBackground(Color.red); //if pixeldata is lit, light JPanel pixel
                } else if (data[i][j] == 2) {
                    pixelsArr[i][j].setBackground(Color.blue); //if pixeldata is lit, light JPanel pixel
                }
            }
        }
    }

    //Unused
    void setGraphics(int[][] data) {
        for (int i = 0; i < res; i++) { //loop trough graphics array
            for (int j = 0; j < res; j++) {
                pixelsArr[i][j].setBackground(new Color(255, 255, 255));
                if (data[i][j] == 1) {
                    pixelsArr[i][j].setBackground(Color.red); //if pixeldata is lit, light JPanel pixel
                } else if (data[i][j] == 2) {
                    pixelsArr[i][j].setBackground(Color.gray); //if pixeldata is lit, light JPanel pixel
                }
            }
        }
    }

    //Unused
    void clearGraphics() {
        for (int i = 0; i < res; i++) { //loop trough graphics array
            for (int j = 0; j < res; j++) {
                //pixelsArr[i][j].setBackground(new Color(255,255,255));
                pixelsArr[i][j].setBackground(new Color(255, 255, 255));
            }
        }
    }

}
