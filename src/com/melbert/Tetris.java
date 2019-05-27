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
 * Main class with GUI, internet I/O and gameplay logic
 */
public class Tetris extends JFrame implements KeyListener {
    public static final int WAITING = 0;
    public static final int GROUNDED = 2;
    public static final int DROPPED = 3;
    public static final int LOSS = 4;
    public static final int WIN = 5;
    public static final int RES = 20;
    public static final int MIDDLE = RES / 2 - 1;
    public static final String ADRESS = "192.168.1.162";
    private static final int FRAMERATE = 15;
    private static final int LOGIC = 3;

    static Tetris tetris;
    private TetrisArray tetrisArray;
    private Timer gTimer;
    private SwingWorker worker;
    private SwingWorker eWorker;
    private TetrisPanel tetrisPanel;

    int currentShape;
    private int gametick = 0;
    private int localtick = 0;
    private int oppscore = 0;
    private int state = 0;

    private boolean start = false;
    private boolean online = false;

    public Tetris() {
        JButton btn1;
        JPanel game;
        this.addKeyListener(this);
        tetrisPanel = new TetrisPanel();

        //Create JMenuBar
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem load = new JMenuItem("Load");
        JMenu gameMenu = new JMenu("Game");
        JMenuItem start = new JMenuItem("Start");
        JMenuItem connect = new JMenuItem("Connect to server");

        //Save the current game
        save.addActionListener(ev -> {
            new TetrisDB("savegame.txt").saveGame(tetrisArray);
            JOptionPane.showMessageDialog(null,
                    "Game saved.");
        });

        //Load game
        load.addActionListener(ev -> {
            try {
                tetrisArray.setBlocks(new TetrisDB("savegame.txt").loadGame());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Loading failed.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Loading failed.");
            }
        });

        //Start the game
        start.addActionListener(ev -> startGame());

        //Connect to the server to start an online game
        connect.addActionListener(ev -> connect());


        file.add(save);
        file.add(load);
        gameMenu.add(start);
        gameMenu.add(connect);
        mb.add(file);
        mb.add(gameMenu);

        mb.setBorderPainted(true);
        setJMenuBar(mb);

        //Create GBL and components
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.1;
        c.weighty = 0.1;

        /*
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
        //add(btn1, c);*/

        /*JButton btn2 = new JButton("CLEAR");
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
        //add(btn2, c);*/

        //Scorelabels
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

        //Gameplay panel
        game = tetrisPanel;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.NONE;

        c.insets = new Insets(0, 0, 0, 0); //Reset the insets

        //Keeping "pixel" size constant
        int dim = RES * 20;
        game.setMinimumSize(new Dimension(dim, dim));
        game.setPreferredSize(new Dimension(dim, dim));
        add(game, c);

        c.gridheight = GridBagConstraints.RELATIVE; //Reset

        //Add some "padding" to the gameplay panel with an empty border
        int fakePadding = 10;
        game.setBorder(BorderFactory.createEmptyBorder(fakePadding, fakePadding, fakePadding, fakePadding));

        //Basic JFrame operations
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

        //GUI Master timer
        gTimer = new javax.swing.Timer(1000 / FRAMERATE, e -> {
            tetrisPanel.setGraphics(tetrisArray); //Updates the gameplay panel
            txtSelf.setText(String.valueOf(tetrisArray.getScore())); //Updating the score labels
            if (online) {
                txtOpp.setText(String.valueOf(oppscore)); //Updating opponent label
                save.setEnabled(false);
                load.setEnabled(false);
            }
            //These two prevent issues with packing and runtime resizing
            revalidate();
            pack();
        });

        //Game logic SwingWorker
        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {

                System.out.println("Array worker starting.");

                //TetrisArray object update timer, should avoid EDT
                java.util.Timer mainTimer = new java.util.Timer();
                mainTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        tetrisArray.update();
                    }
                }, 0, 1000 / FRAMERATE);

                //Gameplay and gamestate logic timer
                java.util.Timer secondaryTimer = new java.util.Timer();
                secondaryTimer.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {

                        if (state != LOSS && state != WIN) { //If the program is mid-game

                            boolean result = true;

                            try {
                                result = tetrisArray.moveBlocks(TetrisBlock.DOWN); //Returns false if the block is not moved -> the block is placed
                            } catch (Exception ignored) {
                            }

                            if (!result) {

                                if (tetrisArray.getBlocks().get(tetrisArray.getBlocks().size() - 1).x == 0) { //If the placed block is at the top
                                    state = LOSS; //You lose
                                    if (online) { //Different loss messages depending on mode
                                        JOptionPane.showMessageDialog(null, "You lost. Your score: "
                                                + String.valueOf(tetrisArray.getScore())
                                                + ", Opponent score: " + String.valueOf(oppscore));
                                    } else {
                                        JOptionPane.showMessageDialog(null, "You lost. Score: " + String.valueOf(tetrisArray.getScore()));
                                    }

                                } else if (!online) { //If the placed block is not at the top and the game is offline
                                    int n = TetrisBlock.getRandomShapeNumber();
                                    tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[n], 0, MIDDLE, Color.GREEN, n)); //Spawn new block at the top
                                } else { //If the placed block is not at the top and the game is online
                                    if (state == WAITING) { //If the client is waiting to spawn new block
                                        tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[currentShape], 0, MIDDLE, Color.red, currentShape)); //Spawn block
                                        state = DROPPED; //Assign new state after "dropping" new block
                                    } else if (state == DROPPED) { //If a block was already dropped
                                        state = GROUNDED; // -> The current block is placed/grounded
                                    }

                                }
                            }
                            tetrisArray.findWhole(); //Check for complete lines
                        }
                    }
                }, 0, 1000 / LOGIC);
                return null;
            }
        };

        //Online functionality SwingWorker
        eWorker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                BufferedReader in = null;
                PrintWriter out = null;
                Socket socket = null;
                save.setEnabled(false); //Prevent saving and loading in MP
                load.setEnabled(false);

                //Connecting to server
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

                //Creating I/O streams to the server
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (Exception e) {
                    System.out.println("Streams failed.");
                    System.exit(1);
                }

                System.out.println("Connection successful.");
                setTitle("Tetris Online");

                //Setup
                out.println("gs"); //Ask the server for current tetris shape

                //Loop
                boolean onlineLoop = true;
                while (onlineLoop) {

                    while (in.ready()) { //While there is input to be processed

                        String input = in.readLine();

                        //Ping
                        if (input.toLowerCase().equals("Test".toLowerCase())) {
                            out.println("Client test response.");

                            //Game start command
                        } else if (input.toLowerCase().contains("start".toLowerCase())) {
                            mpStart();
                            setStart(true);
                            System.out.println("Game starting.");

                            //Receiving global tick, DEPRECATED
                        } else if (input.substring(0, 1).equals("t")) {
                            setGametick(Integer.parseInt(input.substring(1)));

                            //Receiving global shape
                        } else if (input.substring(0, 1).equals("h")) {
                            currentShape = Integer.parseInt(input.substring(1));

                            //Receiving ready-to-drop-new-block command
                        } else if (input.contains("ready") && state == GROUNDED) { //If the server says go and the current block is placed
                            state = WAITING; //Set the state to spawn new block when possible

                            //Receiving victory status
                        } else if (input.contains("victor")) {
                            JOptionPane.showMessageDialog(null, "You won. Your score: " //Show victory message
                                    + String.valueOf(tetrisArray.getScore())
                                    + ", Opponent score: " + String.valueOf(oppscore));
                            state = WIN; //Set the state to WIN, marking the end of the game
                            onlineLoop = false; //Stop the MP loop
                            break; //Break out of the input processing loop
                        }

                    }

                    //Send data to server
                    out.println("x" + String.valueOf(state));
                    out.println("s" + String.valueOf(tetrisArray.getScore()));
                    out.println("t" + String.valueOf(getLocaltick()));
                    if (state == LOSS) { //If the game is lost, break out of the MP loop
                        break;
                    }
                }
                online = false; //The client is going offline
                save.setEnabled(true); //Reenable save & load
                load.setEnabled(true);
                out.close();
                in.close();
                return null;
            }


        };

    }


    public synchronized void setGametick(int gametick) {
        this.gametick = gametick;
    }

    public synchronized int getLocaltick() {
        return localtick;
    }

    public static void main(String[] args) {
        //Create objects
        SwingUtilities.invokeLater(() -> {
            tetris = new Tetris();
            tetris.tetrisArray = new TetrisArray(tetris.tetrisPanel.getRes());
            tetris.gTimer.start();
        });

    }

    public synchronized void setStart(boolean start) {
        this.start = start;
    }


    synchronized public TetrisArray getTetrisArray() {
        return tetrisArray;
    }

    //MP game starter method
    private void mpStart() {
        SwingUtilities.invokeLater(() -> {
            tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[currentShape], 0, MIDDLE, Color.red, currentShape)); //Spawn the first block
            state = DROPPED; //Set the correct state
        });
    }

    //Offline game starter method
    private void startGame() {
        tetrisArray.clearBlocks(); //Clear any blocks
        int n = TetrisBlock.getRandomShapeNumber();
        tetrisArray.insertBlock(new TetrisBlock(TetrisBlock.shapes[n], 0, MIDDLE, Color.GREEN, n)); //Spawn the first block
        state = DROPPED;
        worker.execute(); //Start the worker
    }

    //Server connection method
    private void connect() {
        state = WAITING;
        online = true;
        tetrisArray = new TetrisArray(RES); //Get a new empty array
        setTitle("Connecting...");
        worker.execute(); //In case the worker is not yet started
        eWorker.execute();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //Moving the block with arrowkeys
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
