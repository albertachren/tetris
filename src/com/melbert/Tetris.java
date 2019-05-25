package com.melbert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;

/**
 * Main class with GUI code and I/O
 */
public class Tetris extends JFrame implements KeyListener {
    private static final String ADRESS = "192.168.1.162";
    private TetrisArray tetrisArray;
    static Tetris tetris;
    private int score = 0;
    private boolean connected = false;
    private boolean start = false;
    private Timer gTimer;
    private SwingWorker worker;
    private SwingWorker eWorker;
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
        //TODO: Save game button and function
        pack();
        setFocusable(true);
        setVisible(true);
        requestFocus();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        /*
        Timer mainTimer = new Timer(1000 / FRAMERATE, e -> {
            tetrisArray.update();
            tetrisPanel.setGraphics(tetrisArray); //master setGraphics
        });
        mainTimer.start();
        *//*
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
        */
        gTimer = new javax.swing.Timer(1000 / FRAMERATE, e -> {
            tetrisPanel.setGraphics(tetrisArray); //master setGraphics
        });

        worker = new SwingWorker<Void, Void>() {
            //TODO: Threads working
            @Override
            public Void doInBackground() {
                while (!getStart()) {
                } //TODO: Better waiting
                System.out.println("Array worker starting.");
                java.util.Timer mainTimer = new java.util.Timer();
                mainTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        tetrisArray.update();
                    }
                }, 0, 1000 / FRAMERATE);

                java.util.Timer secondaryTimer = new java.util.Timer();
                secondaryTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        boolean result = true;
                        try {
                            result = tetrisArray.moveBlocks(TetrisBlock.DOWN);
                        } catch (Exception ignored) {
                        }
                        if (!result) {


                            tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.getRandomShape(), 0, 4));
                        }
                        tetrisArray.findWhole();
                    }
                }, 0, 300);
                return null;
            }

            @Override
            public void done() {
            }

            protected Void process() {
                return null;
            }
        };


        eWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                BufferedReader in = null;
                PrintWriter out = null;
                Socket socket = null;

                System.out.println("Trying connection.");
                boolean connecting = true;
                while (connecting) {
                    try {
                        socket = new Socket(ADRESS, 5000);
                    } catch (IOException e) {
                        System.out.println("Retrying Connection.");
                        Thread.sleep(300);
                        continue;
                    }
                    connecting = false;
                }
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (Exception e) {
                    System.out.println("Streams failed.");
                    System.exit(1);
                }

                System.out.println("Connection successful.");
                connected = true;

                boolean online = true;
                while (online) {
                    System.out.println("-Looped.");

                    if (socket.getInputStream().available() != -1) {
                        String input = in.readLine();


                        if (input.toLowerCase().equals("Test".toLowerCase())) {
                            out.println("Client test response.");
                        }
                        if (input.toLowerCase().contains("start".toLowerCase())) {
                            setStart(true);
                            System.out.println("Game starting.");
                        }
                    }

                    out.println("s" + String.valueOf(score));
                    score++;
                    Thread.sleep(1000);
                }

                return null;
            }


        };

    }

    public synchronized boolean getStart() {
        return start;
    }

    public synchronized void setStart(boolean start) {
        this.start = start;
    }

    private final int FRAMERATE = 15;
    TetrisPanel tetrisPanel;

    synchronized public TetrisArray getTetrisArray() {
        return tetrisArray;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            tetris = new Tetris();
            tetris.tetrisArray = new TetrisArray(tetris.tetrisPanel.getRes());
            tetris.startGame();
        });

    }

    private void startGame() {
        eWorker.execute();
        gTimer.start();
        worker.execute();
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
