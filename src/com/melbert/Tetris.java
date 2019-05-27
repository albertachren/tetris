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
    private final int FRAMERATE = 15;
    private boolean start = false;
    private Timer gTimer;
    private SwingWorker worker;
    private SwingWorker eWorker;
    static final int WAITING = 0;
    static final int GROUNDED = 2;
    int currentShape;
    TetrisPanel tetrisPanel;
    private int gametick = 0;
    private int localtick = 0;
    private int oppscore = 0;
    static final int DROPPED = 3;
    static final int LOSS = 4;
    static final int WIN = 5;
    static final int RES = 20;
    static final int MIDDLE = RES / 2 - 1;
    private final int LOGIC = 3;
    int state = 0;
    private boolean online = false;
    private boolean loss = false;
    private boolean connected;

    public Tetris() {
        JButton btn1;
        JPanel game;
        this.addKeyListener(this);
        tetrisPanel = new TetrisPanel();

        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem load = new JMenuItem("Load");
        JMenu gameMenu = new JMenu("Game");
        JMenuItem start = new JMenuItem("Start");
        JMenuItem connect = new JMenuItem("Connect to server");

        save.addActionListener(ev -> {
            new TetrisDB("savegame.txt").saveGame(tetrisArray);
            JOptionPane.showMessageDialog(null,
                    "Game saved.");
        });
        load.addActionListener(ev -> {
            try {
                tetrisArray.setBlocks(new TetrisDB("savegame.txt").loadGame());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Loading failed.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Loading failed.");
            }
        });
        start.addActionListener(ev -> startGame());

        connect.addActionListener(ev -> connect());


        file.add(save);
        file.add(load);
        gameMenu.add(start);
        gameMenu.add(connect);
        mb.add(file);
        mb.add(gameMenu);

        mb.setBorderPainted(true);
        setJMenuBar(mb);


        setLayout(new GridBagLayout());
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
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        //add(btn1, c);

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
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        //add(btn2, c);

        JLabel txtSelf = new JLabel("-");

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 10, 0, 20);
        add(txtSelf, c);

        JLabel txtOpp = new JLabel("-");

        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        add(txtOpp, c);


        game = tetrisPanel;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 0, 0);

        int dim = RES * 20;
        game.setMinimumSize(new Dimension(dim, dim));
        game.setPreferredSize(new Dimension(dim, dim));
        add(game, c);

        c.gridheight = GridBagConstraints.RELATIVE; //reset

        int fakePadding = 10;
        game.setBorder(BorderFactory.createEmptyBorder(fakePadding, fakePadding, fakePadding, fakePadding));

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setTitle("Tetris");
        setResizable(false);
        pack();

        setFocusable(true);
        setAlwaysOnTop(true);
        setVisible(true);
        requestFocus();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        gTimer = new javax.swing.Timer(1000 / FRAMERATE, e -> {
            tetrisPanel.setGraphics(tetrisArray);
            txtSelf.setText(String.valueOf(tetrisArray.getScore()));
            if (online) {
                txtOpp.setText(String.valueOf(oppscore));
                save.setEnabled(false);
                load.setEnabled(false);
            }
            revalidate();
            pack();
        });

        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
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
                        if (state != LOSS && state != WIN) {
                            boolean result = true;
                            try {
                                result = tetrisArray.moveBlocks(TetrisBlock.DOWN);
                            } catch (Exception ignored) {
                            }
                            if (!result) {
                                if (tetrisArray.getBlocks().get(tetrisArray.getBlocks().size() - 1).x == 0) {
                                    state = LOSS;
                                    if (online) {
                                        JOptionPane.showMessageDialog(null, "You lost. Your score: "
                                                + String.valueOf(tetrisArray.getScore())
                                                + ", Opponent score: " + String.valueOf(oppscore));
                                    } else {
                                        JOptionPane.showMessageDialog(null, "You lost. Score: " + String.valueOf(tetrisArray.getScore()));
                                    }

                                } else if (!online) {
                                    int n = TetrisBlock.getRandomShapeNumber();
                                    tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[n], 0, MIDDLE, Color.GREEN, n));
                                } else {
                                    if (state == WAITING) {
                                        tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[currentShape], 0, MIDDLE, Color.red, currentShape));
                                        state = DROPPED;
                                    } else if (state == DROPPED) {
                                        state = GROUNDED;
                                    }

                                }
                            }
                            tetrisArray.findWhole();
                        }
                    }
                }, 0, 1000 / LOGIC);
                return null;
            }
        };


        eWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                BufferedReader in = null;
                PrintWriter out = null;
                Socket socket = null;
                save.setEnabled(false);
                load.setEnabled(false);

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
                setTitle("Tetris Online");

                //Setup
                out.println("gs");

                boolean onlineLoop = true;
                while (onlineLoop) {
                    while (in.ready()) {
                        String input = in.readLine();

                        if (input.toLowerCase().equals("Test".toLowerCase())) {
                            out.println("Client test response.");

                        } else if (input.toLowerCase().contains("start".toLowerCase())) {
                            mpStart();
                            setStart(true);
                            System.out.println("Game starting.");

                        } else if (input.substring(0, 1).equals("t")) {
                            setGametick(Integer.parseInt(input.substring(1)));

                        } else if (input.substring(0, 1).equals("h")) {
                            currentShape = Integer.parseInt(input.substring(1));

                        } else if (input.substring(0, 1).equals("o")) {
                            oppscore = Integer.parseInt(input.substring(1));

                        } else if (input.contains("ready") && state == GROUNDED) {
                            state = WAITING;

                        } else if (input.contains("victor")) {
                            JOptionPane.showMessageDialog(null, "You won. Your score: "
                                    + String.valueOf(tetrisArray.getScore())
                                    + ", Opponent score: " + String.valueOf(oppscore));
                            state = WIN;
                            onlineLoop = false;
                            break;
                        }

                    }


                    out.println("x" + String.valueOf(state));
                    out.println("s" + String.valueOf(tetrisArray.getScore()));
                    out.println("t" + String.valueOf(getLocaltick()));
                    if (state == LOSS) {
                        break;
                    }
                }
                online = false;
                save.setEnabled(true);
                load.setEnabled(true);
                return null;
            }


        };

    }

    public synchronized int getGametick() {
        return gametick;
    }

    public synchronized void setGametick(int gametick) {
        this.gametick = gametick;
    }

    public synchronized int getLocaltick() {
        return localtick;
    }

    public synchronized void setLocaltick(int localtick) {
        this.localtick = localtick;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            tetris = new Tetris();
            tetris.tetrisArray = new TetrisArray(tetris.tetrisPanel.getRes());
            tetris.gTimer.start();
        });

    }

    public synchronized boolean getStart() {
        return start;
    }

    public synchronized void setStart(boolean start) {
        this.start = start;
    }


    synchronized public TetrisArray getTetrisArray() {
        return tetrisArray;
    }

    private void mpStart() {
        SwingUtilities.invokeLater(() -> {
            tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[currentShape], 0, MIDDLE, Color.red, currentShape));
            state = DROPPED;
        });
    }

    private void startGame() {
        tetrisArray.clearBlocks();
        int n = TetrisBlock.getRandomShapeNumber();
        tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[n], 0, MIDDLE, Color.GREEN, n));
        state = DROPPED;
        worker.execute();
    }

    private void connect() {
        state = WAITING;
        online = true;
        tetrisArray = new TetrisArray(RES);
        setTitle("Connecting...");
        worker.execute();
        eWorker.execute();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!tetrisArray.getBlocks().isEmpty()) {
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
