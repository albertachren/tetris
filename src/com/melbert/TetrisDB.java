package com.melbert;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom database class, for saving and loading games
 */
public class TetrisDB {

    private static final String DELIMITER = ",";

    private File file;
    private PrintWriter pw;
    private BufferedReader br;

    private boolean hasData = false;

    public TetrisDB(String filename) {
        file = new File(filename);

    }

    //Save a tetris game
    public void saveGame(TetrisArray array) {

        //Create a writer to the file
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        } catch (IOException e) {
            System.out.println("Could not write to file: " + file.getName() + ".");
        }

        //Print a line in the file for every block
        for (TetrisBlock block : array.getBlocks()) {
            //Shape, coordinates and color saved
            pw.println(String.valueOf(block.shapeN) + DELIMITER + String.valueOf(block.x) + DELIMITER + String.valueOf(block.y) + DELIMITER + String.valueOf(block.color));
        }
        pw.close();
        System.out.println("File saved.");
    }

    //Load a tetris game
    public List<TetrisBlock> loadGame() throws IOException {

        //Create a list to hold the blocks
        List<TetrisBlock> blocks = new ArrayList<>();

        //Create a reader to the file
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found.");
        }

        //Read all lines
        while (br.ready()) {
            String[] dataStrings = br.readLine().split(DELIMITER);
            //New blocks generated with read data
            blocks.add(new TetrisBlock(TetrisBlock.shapes[Integer.parseInt(dataStrings[0])], Integer.parseInt(dataStrings[1]), Integer.parseInt(dataStrings[2]), Color.getColor(dataStrings[3])));
        }
        br.close();
        System.out.println("File read.");
        //List of blocks returned
        return blocks;
    }
}
