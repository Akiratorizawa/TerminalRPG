package com.dominic.terminalrpg;

import java.util.ArrayList;

public class Pokemon {
    private String name;
    private int level;
    public int hp;
    public ArrayList<Move> moves;
    public Type type;
    public int id;

    public Pokemon(String name, int level, int hp, ArrayList<Move> moves, Type types, int id) {
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.moves = moves;
        this.type = types;
        this.id = id;
    }

    public String name() {
        return this.name;
    }

    public int level() {
        return this.level;
    }

    public int hp() {
        return this.hp;
    }

    public ArrayList<Move> moves() {
        return this.moves;
    }

    public Type type() {
        return this.type;
    }
    
    public int id() {
        return this.id;
    }
    
} 
