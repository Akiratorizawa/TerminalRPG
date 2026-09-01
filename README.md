# TerminalRPG

## A Pokemon battle simulator in Java, utilizing JFrame, JPanel, and an state-driven display system.

This originally started as a passion project, but since I needed a self-made project to pass to my CC102.1 class, I'll just pass this instead.

## History
About 2 weeks of self-studying Java for the first time after my first day of College, I got tired of constant exercises that the University of Helsinki's moocfi offered. I told myself,

    "Why don't I just make something random? I'll learn along the way right?"

As simple as it sounds, that is the origin of this program/game/adaptation/whatever you may call it.

This repo also contains version 3 of TerminalRPG, as 

1. The first version was terminal-based
2. The second version had a working GUI, but without super effective and not very effective dialogue, code was also a mess
3. The third version included super effective and not very effective dialogue, a working bag with potions and Pokeballs, and a massive refactoring as to the state tracking and display system.
4. The fourth version is just the third version, but with an exe file.

## Usage
Decompress TerminalRPG_v4-application.zip and run the exe.

If you want to make your own changes, to compile, from the folder that the source code is present in,

    javac -Xlint:deprecation  -d build\classes -cp "lib\sqlite-jdbc-3.50.3.0.jar" src\*.java
    java -cp "build\classes;lib\sqlite-jdbc-3.50.3.0.jar" TerminalRPG_v4.src.Game_4

To make your own .jar file named Game.jar for the entire project after compiling,

    jar cvfm Game.jar manifest.txt -C .\build\classes TerminalRPG_v4

Yes, src should NOT be in the package name for the system.

Do I know how to fix it? No.

Did I TRY to fix it? Of course, I did.

## Personal Context
Anyways, this is just a personal passion project I made for fun trying to learn Java and OOP along the way. 

This is also my first project from scratch that incorporates a GUI, regardless of programming language.

It just so happened that my professor for CC102.1 requested us to make a program of our choosing, and I was already in the process of making TerminalRPG.