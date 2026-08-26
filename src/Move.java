package TerminalRPG_v3.src;

public class Move {
    private String move;
    private int power;
    public int pp;
    private double accuracy;
    private Type type;

    public Move(String name, int power, int pp, double accuracy, Type type) {
        this.move = name;
        this.power = power;
        this.pp = pp;
        this.accuracy = accuracy;
        this.type = type;
    }

    public String move() {
        return this.move;
    }

    public int power() {
        return this.power;
    }

    public int pp() {
        return this.pp;
    }

    public double accuracy() {
        return this.accuracy;
    }

    public Type type() {
        return this.type;
    }
}
