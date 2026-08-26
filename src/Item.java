package TerminalRPG_v3.src;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Item {
    private String name;
    private Image image;
    public int quantity;

    public Item(String name, Image image, int quantity) {
        this.name = name;
        this.image = image;
        this.quantity = quantity;
    }

    public String name() {
        return this.name;
    }

    public Image image() {
        return this.image;
    }

    public int quantity() {
        return this.quantity;
    }


}   
