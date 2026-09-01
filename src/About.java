package TerminalRPG_v4.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class About extends JFrame implements KeyListener {
    private Window window;
    private Image background;
    private Image cursor;

    private int windowWidth;
    private int windowHeight;

    public About(Window window) {
        background = new ImageIcon("assets/img/about.jpg").getImage();
        
        this.window = window;

        window.setContentPane(new JPanel() {
           @Override
           protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            windowWidth = getWidth();
            windowHeight = getHeight();

            // About page background
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

           } 
        });

        window.revalidate();
        window.repaint();

        window.addKeyListener(this);
        window.setFocusable(true);
        window.requestFocusInWindow();

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Listening for enter key strokes
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            window.backSound();
            window.removeKeyListener(this);
            window.showMainMenu();
        }
      }

  @Override
      public void keyReleased(KeyEvent e) {
      }

  @Override
      public void keyTyped(KeyEvent e) {
      }

    public int getRelativeWidth(int x) {
        return (int) (((double) x / 1280.0) * (double) (windowWidth + 16));
    }

    public int getRelativeHeight(int y) {
        return (int) (((double) y / 720.0) * (windowHeight + 39));
    }
}
