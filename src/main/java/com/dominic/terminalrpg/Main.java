package com.dominic.terminalrpg;

import de.gurkenlabs.input4j.InputDevice;
import de.gurkenlabs.input4j.InputDevices;

import java.util.List;

// from /TerminalRPG_v4/

// javac -d bin -cp "lib\sqlite-jdbc-3.50.3.0.jar" src\*.java
// java -cp "bin;lib\sqlite-jdbc-3.50.3.0.jar" TerminalRPG_v4.src.Game_4

public class Main {
    private static List<InputDevice> controllers;
    private static InputDevice controller;

    public static void init() {
        // Initialize the input system
        var deviceList = InputDevices.init();

        // Get all connected controllers
        controllers = (List<InputDevice>) deviceList.getAll();

        // Or get a specific controller by index
        if (!controllers.isEmpty()) {
            controller = controllers.getFirst();
        }

        else {
            controller = null;
        }

        System.out.println("Found " + controllers.size() + " controllers");
    }

    public static void main(String[] args) {
        init();
        Window window = new Window(controller);
    }
}
