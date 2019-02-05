package com.melbert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Main class with GUI code and I/O
 */
public class Tetris extends JFrame implements KeyListener {
    private TetrisArray tetrisArray;
    private TetrisPanel tetrisPanel;

    public Tetris() {
        JButton btn1;
        JPanel game;
        this.addKeyListener(this);
        tetrisPanel = new TetrisPanel();
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(500, 300));
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.1;
        c.weighty = 0.1;

        btn1 = new JButton("play");
        btn1.setFocusable(false);
        btn1.addActionListener(e -> {
            System.out.println("dank memes");

            tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.BLOCK, 2, 2));
            tetrisPanel.setGraphics(tetrisArray);

            try {
                //tetrisPanel.pixelsArr[nn[0]][nn[1]].setBackground(Color.red);
            } catch (Exception b) {
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn1, c);

        JButton btn2 = new JButton("clear");
        btn2.setFocusable(false);
        btn2.addActionListener(e -> {
            System.out.println("dank memes");
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
        btn3.setFocusable(false);
        btn3.addActionListener(e -> {
            System.out.println("dank memes");
            //tetrisArray.moveDown();
            //tetrisArray.moveBlocks(TetrisBlock.DOWN);
            tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.SW, 2, 2));
            tetrisPanel.setGraphics(tetrisArray);
            System.out.println(tetrisArray.toString());
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
        requestFocus();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.repaint();
    }

    public static void main(String[] args) {
        Tetris tetris = new Tetris();
        tetris.tetrisArray = new TetrisArray(tetris.tetrisPanel.getRes());
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        switch (e.getKeyCode()) {
            case 37:
                tetrisArray.moveBlocks(TetrisBlock.LEFT);
                tetrisPanel.setGraphics(tetrisArray);
                break;
            case 38:
                tetrisArray.moveBlocks(TetrisBlock.UP);
                tetrisPanel.setGraphics(tetrisArray);
                break;
            case 39:
                tetrisArray.moveBlocks(TetrisBlock.RIGHT);
                tetrisPanel.setGraphics(tetrisArray);
                break;
            case 40:
                tetrisArray.moveBlocks(TetrisBlock.DOWN);
                tetrisPanel.setGraphics(tetrisArray);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
