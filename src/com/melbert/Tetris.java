package com.melbert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;

public class Tetris extends JFrame {
    static int n = 0;
    static int[] nn = {0, 0};
    TetrisArray tetrisArray;
    TetrisPanel tetrisPanel;

    public Tetris() {
        JButton btn1;
        JPanel game;
        tetrisPanel = new TetrisPanel();
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(500, 300));
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.1;
        c.weighty = 0.1;

        btn1 = new JButton("play");
        btn1.addActionListener(e -> {
            System.out.println("dank memes");
            tetrisPanel.pixels.get(n).setBackground(Color.red);
            try {
                //tetrisPanel.pixelsArr[nn[0]][nn[1]].setBackground(Color.red);
                tetrisPanel.setGraphics(tetrisArray);
            } catch (Exception b) {
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn1, c);

        JButton btn2 = new JButton("clear");
        btn2.addActionListener(e -> {
            System.out.println("dank memes");
            tetrisPanel.pixels.get(n).setBackground(Color.red);
            try {
                tetrisPanel.clearGraphics();
                tetrisArray = new TetrisArray(tetrisPanel.getRes());
            } catch (Exception b) {
            }
        });
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn2, c);

        JButton btn3 = new JButton("jujjujj");
        btn3.addActionListener(e -> {
            System.out.println("dank memes");
            tetrisArray.moveDown();
            tetrisPanel.setGraphics(tetrisArray);
        });
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn3, c);

        game = tetrisPanel;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        add(game, c);

        c.gridheight = GridBagConstraints.RELATIVE; //reset

        //game.setBackground(Color.BLUE);
        game.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        pack();
        setFocusable(true);
        setVisible(true);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println(e.getKeyCode());
                if (e.getKeyCode() == 78) {

                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.repaint();
    }

    public static void main(String[] args) {
        Tetris tetris = new Tetris();
        Scanner sc = new Scanner(System.in);
        tetris.tetrisArray = new TetrisArray(tetris.tetrisPanel.getRes());
        while (true) {
            String[] jujj = sc.nextLine().split(" ");
            try {
                nn[0] = Integer.parseInt(jujj[0]);
                nn[1] = Integer.parseInt(jujj[1]);

            } catch (Exception a) {
            }
        }

    }

}
