package com.dominic.terminalrpg;


import de.gurkenlabs.input4j.InputDevice;
import de.gurkenlabs.input4j.components.XInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class About extends JFrame implements KeyListener {
    private final Window window;
    private final Image background;

    private Thread controllerThread;
    private InputDevice controller;
    private boolean controllerThreadOpen = false;

    private Runnable buttonB;

    public About(Window window) {
        background = new ImageIcon("assets/img/about.jpg").getImage();
        
        this.window = window;
        controller = window.controller;


        window.setContentPane(new JPanel() {
           @Override
           protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // About page background
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

           } 
        });

        window.revalidate();
        window.repaint();

        window.addKeyListener(this);
        window.setFocusable(true);
        window.requestFocusInWindow();

        if (controller != null) {
            controllerInput();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Listening for enter               key strokes
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            window.backSound();
            window.removeKeyListener(this);
            window.showMainMenu();

            controller.removeButtonPressedListener(buttonB);

            controllerThread.interrupt();
        }
      }

  @Override
      public void keyReleased(KeyEvent e) {
      }

  @Override
      public void keyTyped(KeyEvent e) {
      }

    public void controllerInput() {

        buttonB = () -> {
            window.backSound();
            window.removeKeyListener(this);
            window.showMainMenu();

            controller.removeButtonPressedListener(buttonB);

            controllerThread.interrupt();

        };

        controller.onButtonPressed(XInput.B, buttonB);

        controllerThread = new Thread(this::pollController);
        controllerThreadOpen = true;
        controllerThread.start();

    }

    public void pollController () {
        try {
            while (controllerThreadOpen) {
                controller.poll();
                Thread.sleep(30);
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}
