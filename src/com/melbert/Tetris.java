package com.melbert;
import javax.swing.*;
import java.awt.*;

public class Tetris extends JFrame{

    public Tetris() throws HeadlessException {
        JButton btn1, btn2;
        JPanel game;
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(500,300));
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.1;
        c.weighty = 0.1;

        btn1 = new JButton("play");
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = 0;
        add(btn1, c);

        game = new JPanel();
        c.gridx = 0;
        c.gridy = 0;

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10,10,10,10);

        add(game, c);
        game.setBackground(Color.BLUE);


        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
	Tetris tetris = new Tetris();
    }
}
