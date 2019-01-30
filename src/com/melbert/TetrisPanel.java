package com.melbert;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class TetrisPanel extends JPanel {
    ArrayList<JPanel> pixels;
    int res = 12;
    int[][] testgame = new int[res][res];
    JPanel[][] pixelsArr = new JPanel[res][res];

    public TetrisPanel() {
        this.setBackground(Color.WHITE);
        setLayout(new GridLayout(res, res));
        GridBagConstraints c = new GridBagConstraints();
        this.pixels = new ArrayList<>();
        for (int i = 0; i < res * res; i++) {

            Border border = BorderFactory.createLineBorder(Color.black, 1);
            JPanel panel = new JPanel();
            panel.setBorder(border);
            this.pixels.add(panel);
            try {
                pixelsArr[i / res][i % res] = panel;
                /*
                System.out.println("");
                System.out.println(i/res);
                System.out.println("dvi");
                System.out.println(i);
                System.out.println(i % res);
                System.out.println("");*/
            } catch (Exception e) {
                //pixelsArr[i % res][0] = panel;
                System.out.println("error");
            }
        }
        for (JPanel panel : pixels) {
            add(panel);
        }
        pixels.get(1).setBackground(Color.BLUE);
        //System.out.println(pixelsArr[0][1].toString());
    }

    public int getRes() {
        return res;
    }

    void setGraphics(TetrisArray array) {
        int[][] data = array.getData();
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

    void clearGraphics() {
        for (int i = 0; i < res; i++) { //loop trough graphics array
            for (int j = 0; j < res; j++) {
                //pixelsArr[i][j].setBackground(new Color(255,255,255));
                pixelsArr[i][j].setBackground(new Color(255, 255, 255));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        //g2d.setColor(Color.BLUE);
        //g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

    }

}
