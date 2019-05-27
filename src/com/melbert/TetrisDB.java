package com.melbert;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TetrisDB {

    private static final String DELIMITER = ",";
    private File file;
    private PrintWriter pw;
    private BufferedReader br;
    private boolean hasData = false;

    public TetrisDB(String filename) {
        file = new File(filename);

    }

    public void saveGame(TetrisArray array) {

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        } catch (IOException e) {
            System.out.println("Could not write to file: " + file.getName() + ".");
        }

        for (TetrisBlock block : array.getBlocks()) {
            pw.println(String.valueOf(block.shapeN) + DELIMITER + String.valueOf(block.x) + DELIMITER + String.valueOf(block.y) + DELIMITER + String.valueOf(block.color));
        }
        pw.close();
        System.out.println("File saved.");
    }

    public List<TetrisBlock> loadGame() throws IOException {
        List<TetrisBlock> blocks = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found.");
        }

        while (br.ready()) {
            String[] dataStrings = br.readLine().split(DELIMITER);
            blocks.add(new TetrisBlock(TetrisBlock.shapes[Integer.parseInt(dataStrings[0])], Integer.parseInt(dataStrings[1]), Integer.parseInt(dataStrings[2]), Color.getColor(dataStrings[3])));
        }
        br.close();
        System.out.println("File read.");
        return blocks;
    }
}
