package com.melbert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TetrisServer {

    final static int PORT = 5000;
    public static TetrisServer server;
    ServerSocket serverSocket = null;
    int playerCount = 2;
    int[] scores = new int[playerCount];
    int currentShape = 0;
    private List<Socket> clients;
    private List<PrintWriter> outStreams;
    private List<BufferedReader> inStreams;
    private List<Integer> clientScores;
    private int gametick = 1;

    public TetrisServer() {
        clients = new ArrayList<>();
        outStreams = new ArrayList<>();
        inStreams = new ArrayList<>();
        clientScores = new ArrayList<>();

        //Create new ServerSocket
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + String.valueOf(PORT) + ".");
            System.exit(1);
        }

        //Accept new clients
        System.out.println("Waiting for clients.");
        try {
            for (int i = 0; i < playerCount; i++) {
                Socket client = serverSocket.accept();
                clients.add(client);
            }
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        //Collect I/O streams
        try {
            for (Socket client : clients) {
                outStreams.add(new PrintWriter(client.getOutputStream(), true));
                inStreams.add(new BufferedReader(new InputStreamReader(client.getInputStream())));
            }
        } catch (IOException e) {
            System.out.println("Stream failed.");
            System.exit(1);
        }

        //Diagnostic logs
        System.out.println("Accepted " + clients.size() + " clients:");
        for (Socket client : clients) System.out.println(client.toString());
    }

    public static void main(String[] args) {
        server = new TetrisServer();
        while (true) {
            server.runGame();
        }
    }

    private void runGame() {
        //Work loop
        boolean online = true;

        //TODO: Setup
        currentShape = new Random().nextInt(TetrisBlock.shapes.length);
        writeToClients("h" + String.valueOf(currentShape));
        writeToClients("start");

        int[] clientsSynced = {0, 0};
        int[] clientsState = {0, 0};
        while (online) {
            writeToClients("h" + String.valueOf(currentShape));
            for (int i = 0; i < inStreams.size(); i++) {
                BufferedReader br = inStreams.get(i);
                try {
                    while (br.ready()) {
                        String input = "";
                        input = br.readLine();
                        if (input.substring(0, 1).toLowerCase().equals("s".toLowerCase())) {
                            scores[i] = Integer.parseInt(input.substring(1));

                        } else if (input.substring(0, 1).toLowerCase().equals("t".toLowerCase())) {
                            if (Integer.parseInt(input.substring(1)) == gametick) {
                                clientsSynced[i] = 1;
                            }

                        } else if (input.toLowerCase().contains("gt")) {
                            outStreams.get(i).println("t" + String.valueOf(gametick));

                        } else if (input.toLowerCase().contains("gs")) {
                            outStreams.get(i).println("h" + String.valueOf(currentShape));

                        } else if (input.toLowerCase().contains("x")) {
                            clientsState[i] = Integer.parseInt(input.substring(1));

                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (clientsState[0] == Tetris.GROUNDED && clientsState[1] == Tetris.GROUNDED) {
                syncScores(scores);
                currentShape = new Random().nextInt(TetrisBlock.shapes.length);
                writeToClients("h" + String.valueOf(currentShape));
                writeToClients("ready");
            }

            if (clientsState[0] == Tetris.LOSS) {
                syncScores(scores);
                outStreams.get(1).println("victor");
                online = false;
            } else if (clientsState[1] == Tetris.LOSS) {
                syncScores(scores);
                outStreams.get(0).println("victor");
                online = false;
            }


            if (clientsSynced[0] + clientsSynced[1] == 2) {
                System.out.println("Clients synced.");
                gametick++;


                System.out.print("Scores: [" + scores[0]);
                for (int i = 1; i < scores.length; i++) {
                    System.out.print(", " + scores[i]);
                }
                System.out.print("]\n");

                clientsSynced = new int[]{0, 0};
            }
        }
    }

    private void syncScores(int[] scores) {
        for (int i = 0; i < clients.size(); i++) {
            Socket client = clients.get(i);
            int score;
            if (i == 0) {
                score = scores[1];
            } else {
                score = scores[0];
            }
            outStreams.get(i).println("o" + String.valueOf(score));
        }
    }

    private void writeToClients(String string) {
        for (PrintWriter pw : outStreams) {
            pw.println(string);
        }
    }
}


//TODO: Encoding and decoding system