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

    private static List<Socket> clients;
    private static List<PrintWriter> outStreams;
    private static List<BufferedReader> inStreams;
    private static List<Integer> clientScores;
    private static int gametick = 1;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        clients = new ArrayList<>();
        outStreams = new ArrayList<>();
        inStreams = new ArrayList<>();
        clientScores = new ArrayList<>();
        int playerCount = 2;
        int[] scores = new int[playerCount];
        final int PORT = 5000;
        int currentShape = 0;

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

        //Work loop
        boolean online = true;
        for (PrintWriter pw : outStreams) {
            pw.println("Test");
        }

        //TODO: Setup
        writeToClients("start");


        int[] clientsSynced = {0, 0};
        while (online) {
            writeToClients("t" + String.valueOf(gametick));
            writeToClients("sh" + String.valueOf(currentShape));
            for (int i = 0; i < inStreams.size(); i++) {
                BufferedReader br = inStreams.get(i);
                try {
                    while (br.ready()) {
                        String input = "";
                        input = br.readLine();
                        //System.out.println("Client " + String.valueOf(i) + ":");
                        //System.out.println(input);
                        if (input.substring(0, 1).toLowerCase().equals("s".toLowerCase())) {
                            scores[i] = Integer.parseInt(input.substring(1));
                        } else if (input.substring(0, 1).toLowerCase().equals("t".toLowerCase())) {
                            //System.out.println("CT, GT: " + String.valueOf(input.substring(1) + ", " + String.valueOf(gametick)));
                            //System.out.println(input);
                            if (Integer.parseInt(input.substring(1)) == gametick) {
                                clientsSynced[i] = 1;
                            }
                            //System.out.println("C" + String.valueOf(i) + ": " + input.substring(1));
                        } else if (input.toLowerCase().contains("gt")) {
                            outStreams.get(i).println("t" + String.valueOf(gametick));
                        } else if (input.toLowerCase().contains("gs")) {
                            outStreams.get(i).println("sh" + String.valueOf(currentShape));
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (clientsSynced[0] + clientsSynced[1] == 2) {
                System.out.println("Clients synced.");
                gametick++;
                currentShape = new Random().nextInt(TetrisBlock.shapes.length);

                //Sync scores, very hardcoded
                for (int i = 0; i < clients.size(); i++) {
                    Socket client = clients.get(i);
                    int score;
                    if (i == 0) {
                        score = scores[1];
                    } else {
                        score = scores[0];
                    }
                    //System.out.println("o" + String.valueOf(score) );
                    outStreams.get(i).println("o" + String.valueOf(score));
                }

                System.out.print("Scores: [" + scores[0]);
                for (int i = 1; i < scores.length; i++) {
                    System.out.print(", " + scores[i]);
                }
                System.out.print("]\n");

                clientsSynced = new int[]{0, 0};
            }


            writeToClients("t" + String.valueOf(gametick));
            writeToClients("sh" + String.valueOf(currentShape));

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            for (Socket client : clients) {
                client.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToClients(String string) {
        for (PrintWriter pw : outStreams) {
            pw.println(string);
        }
    }
}


//TODO: Encoding and decoding system