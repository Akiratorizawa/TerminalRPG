package TerminalRPG_v3.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class About extends JFrame implements KeyListener {
    private Window window;
    private Image background;
    private Image cursor;

    public About(Window window) {
        background = new ImageIcon("assets/img/about.jpg").getImage();
        
        this.window = window;

        window.setContentPane(new JPanel() {
           @Override
           protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // About page background
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

            // Cursor for going back to the main menu
            cursor = new ImageIcon("assets/img/arrow.png").getImage();

            g.drawImage(cursor, 675, 482, 50, 50, this);

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
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
}
