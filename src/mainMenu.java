package TerminalRPG_v3.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mainMenu extends JFrame implements KeyListener {

    private Window window;
    private Image background;
    private Image cursor;

    private int windowWidth;
    private int windowHeight;

    private int selectedOption = 0;

    private int[] optionsX = {515, 485, 515};
    private int[] optionsY = {222, 335, 448};

   public mainMenu(Window window) {

      // Setting background and window up
      this.window = window;

      background = new ImageIcon("assets/img/main_menu.jpg").getImage();

      window.setContentPane(new JPanel() {
        @Override
        protected void paintComponent (Graphics g) {
            super.paintComponent(g);

            windowHeight = getHeight();
            windowWidth = getWidth();
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

            // Setting up option/choice arrow
            cursor = new ImageIcon("assets/img/arrow.png").getImage();
            g.drawImage(cursor, getRelativeWidth(optionsX[selectedOption]), getRelativeHeight(optionsY[selectedOption]), getRelativeWidth(50), getRelativeHeight(50), this);
        }
      });

      window.menuMusic();

      window.revalidate();
      window.repaint();

      window.setVisible(true);

      window.addKeyListener(this);
      window.setFocusable(true);
      window.requestFocusInWindow();

    }
            
      // Listening for up/down arrow keystrokes
      @Override
      public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          if (selectedOption + 1 <= 2) {
            window.optionSound();
          }

          selectedOption++;
    
          if (selectedOption > 2) {
            selectedOption = 2;
          }
          
          window.revalidate();
          window.repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
          if (selectedOption - 1 >= 0) {
            window.optionSound();
          }

          
          selectedOption--;

          if (selectedOption < 0) {
            selectedOption = 0;
          }

          window.revalidate();
          window.repaint();
        }

        // Listening for enter key strokes
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          if (selectedOption == 1) {
            window.confirmSound();
            window.removeKeyListener(this);
            window.showAbout();
          } else if (selectedOption == 2) {
            window.backSound();
            try {
              Thread.sleep(750);
            } catch (InterruptedException error) {
              error.printStackTrace();
            }
            System.exit(0);
          } else {
            window.confirmSound();
            window.removeKeyListener(this);
            window.showBattle();
          }
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