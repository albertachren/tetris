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
    static Tetris tetris;
    private final int FRAMERATE = 15;
    TetrisPanel tetrisPanel;

    public Tetris() {
        JButton btn1;
        JPanel game;
        this.addKeyListener(this);
        tetrisPanel = new TetrisPanel();
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(200, 200));
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.1;
        c.weighty = 0.1;

        btn1 = new JButton("PLAY");
        btn1.setFocusable(false);
        btn1.addActionListener(e -> {
            //tetrisArray.findWhole();
            tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[TetrisBlock.BLOCK], 0, 4));
            System.out.println(tetrisArray.toString());
        });
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn1, c);

        JButton btn2 = new JButton("CLEAR");
        btn2.setFocusable(false);
        btn2.addActionListener(e -> {
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

        JButton btn3 = new JButton("F1");
        btn3.setFocusable(false);
        btn3.addActionListener(e -> {
            //tetrisArray.moveDown();
            //tetrisArray.moveBlocks(TetrisBlock.DOWN);
            tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[TetrisBlock.L], 2, 2));
            System.out.println("Array: ");
            System.out.println(tetrisArray.toString());
        });

        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn3, c);

        JButton btn4 = new JButton("F2");
        btn4.setFocusable(false);
        btn4.addActionListener(e -> {
            tetrisArray.setPixel(0, 0, 1);
            System.out.println(tetrisArray.toString());
        });

        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn4, c);

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

        Timer mainTimer = new Timer(1000 / FRAMERATE, e -> {
            tetrisArray.update();
            tetrisPanel.setGraphics(tetrisArray); //master setGraphics
        });
        mainTimer.start();
        Timer secondaryTimer = new Timer(300, e -> {
            boolean result = true;
            try {
                result = tetrisArray.moveBlocks(TetrisBlock.DOWN);
            } catch (Exception ignored) {
            }
            if (!result) {
                tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.getRandomShape(), 0, 4));
            }
            tetrisArray.findWhole();
        });
        secondaryTimer.start();

        SwingWorker worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {

                return null;
            }

            @Override
            public void done() {
            }

            protected void process() {
            }
        };
    }

    public static void main(String[] args) {
        tetris = new Tetris();
        tetris.tetrisArray = new TetrisArray(tetris.tetrisPanel.getRes());
        tetris.startGame();
    }

    private void startGame() {
        tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.getRandomShape(), 3, 2));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 37:
                tetrisArray.moveBlocks(TetrisBlock.LEFT);
                break;
            case 38:
                //tetrisArray.moveBlocks(TetrisBlock.UP);
                break;
            case 39:
                tetrisArray.moveBlocks(TetrisBlock.RIGHT);
                break;
            case 40:
                tetrisArray.moveBlocks(TetrisBlock.DOWN);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
