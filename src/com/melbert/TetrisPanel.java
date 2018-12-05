package com.melbert;

import javax.swing.*;
import java.awt.*;

public class TetrisPanel extends JPanel {
    public TetrisPanel() {
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

    }

}
