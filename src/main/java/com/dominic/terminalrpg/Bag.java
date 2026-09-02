package com.dominic.terminalrpg;

import java.util.ArrayList;

public class Bag {
    private ArrayList<Item> items;

    public Bag (ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Item> items() {
        return this.items;
    }
}
