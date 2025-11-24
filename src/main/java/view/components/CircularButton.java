// not proper folder by the way AND copied
package view.components;

import javax.swing.*;
import java.awt.*;

public class CircularButton extends JButton {
    public CircularButton(String label) {
        super(label);
        setOpaque(false); // Required for transparency
        setFocusPainted(false); // No focus border
        setBorderPainted(false); // No border
        setContentAreaFilled(false); // No default background
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.LIGHT_GRAY); // Clicked color
        } else {
            g.setColor(Color.BLUE); // Default color
        }
        g.fillOval(0, 0, getWidth() - 1, getHeight() - 1); // Fill circle
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.BLACK); // Border color
        g.drawOval(0, 0, getWidth() - 1, getHeight() - 1); // Draw circle border
    }

    @Override
    public boolean contains(int x, int y) {
        double radius = getWidth() / 2.0;
        double centerX = radius;
        double centerY = radius;
        // Check if the point is within the circle
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }

    @Override
    public Dimension getPreferredSize() {
        int size = Math.max(super.getPreferredSize().width, super.getPreferredSize().height);
        return new Dimension(size, size);
    }
}
