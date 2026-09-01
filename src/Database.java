package TerminalRPG_v4.src;

import java.awt.Image;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;

public class Database {
    public static ArrayList<Pokemon> pokemonFetcher() throws Exception {
        // Force the driver to register
        
        Connection conn = DriverManager.getConnection("jdbc:sqlite:assets/db/pokemon.db");
        String sql = "SELECT * FROM pokemon ORDER BY RANDOM() LIMIT 2";

        String name = "";
        int level = 0;
        int hp = 0;
        int id = 0;
        String type1 = "";
        String type2 = "";

        ArrayList<Pokemon> pokemon = new ArrayList<>();

        try (Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery(sql)) {
                while (rs.next()) {

                    name = rs.getString("name");
                    level = rs.getInt("level");
                    hp = rs.getInt("hp");
                    id = rs.getInt("id");
                    type1 = rs.getString("type1");
                    type2 = rs.getString("type2");
                    
                    if (type2 == null) {
                        type2 = "NULL";
                    }

                    Type type = new Type(type1, type2);

                    ArrayList<String> moveNames = new ArrayList<>(); 
                    ArrayList<Move> moves = new ArrayList<>();

                    
                    for (int i = 1; i <= 4; i++) {
                        moveNames.add((rs.getString("move" + i)));
                    }

                    for (int i = 0; i < 4; i++) {
                        String moveName = moveNames.get(i);
                        Move move = moveFetcher(moveName);
                        moves.add(move);
                    }

                    

                    Pokemon pkmn = new Pokemon(name, level, hp, moves, type, id);
                    pokemon.add(pkmn);
                }
            }

        conn.close();
        return pokemon;
    }

    public static Move moveFetcher(String moveName) {
        Connection conn = null;

        try {
            Connection temp = DriverManager.getConnection("jdbc:sqlite:assets/db/pokemon.db");
            conn = temp;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "SELECT power, pp, accuracy, type FROM moves WHERE name = \"" + moveName + "\"" ;

        int power = 0;
        int pp = 0;
        double accuracy = 0;
        String type1 = "";
        Type type = new Type(type1, "NULL");
        try (Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery(sql)) {
                while (rs.next()) {
                    power = rs.getInt("power");
                    pp = rs.getInt("pp");
                    accuracy = rs.getDouble("accuracy");
                    type1 = rs.getString("type");
                    type.type1 = type1;
                }
            }
        catch (SQLException e) {
            throw new IllegalArgumentException("Database error.");
        }

        Move move = new Move(moveName, power, pp, accuracy, type);
        return move;

    }

    public static Image spriteFetcher(String pokemonID, String role) {
        String position = "";
        Image sprite = null;
        String spriteURL = "";
        if (role.equals("player")) {
            position = "back";
            spriteURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + position + "/"+ pokemonID + ".png";
        } else {
            position = "front";
            spriteURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonID + ".png";
        }
        
        try {
            sprite = new ImageIcon(java.net.URI.create(spriteURL).toURL()).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return sprite;
    }

    public static double effectiveCheck(Move move, Pokemon opponent) {
        Connection conn = null;

        try {
            Connection temp = DriverManager.getConnection("jdbc:sqlite:assets/db/pokemon.db");
            conn = temp;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        String type1 = opponent.type().type1();
        String type2 = opponent.type().type2();

        String sql1 = "SELECT multipler FROM effectiveness WHERE attack = \"" + move.type().type1() + "\" AND defense = \"" + type1 + "\"";
        String sql2 = "SELECT multipler FROM effectiveness WHERE attack = \"" + move.type().type1() + "\" AND defense = \"" + type2 + "\"";

        double effective = 1;
        double multiplier = 0;

        try (Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery(sql1)) {
                while (rs.next()) {
                    multiplier = rs.getDouble("multipler");
                    if (multiplier == -1) {
                        multiplier = 0;
                    } else if (multiplier == 0) {
                            multiplier = 1;
                        }
                    effective *= multiplier;
                }
            }
        catch (SQLException e) {
            throw new IllegalArgumentException("Database error.");
        }

        if (!(type2.equals("NULL"))) {
            try (Statement query = conn.createStatement();
                ResultSet rs = query.executeQuery(sql2)) {
                    while (rs.next()) {
                        multiplier = rs.getDouble("multipler");
                        if (multiplier == -1) {
                            multiplier = 0;
                        } else if (multiplier == 0) {
                            multiplier = 1;
                        }
                        effective *= multiplier;
                    }
                }
            catch (SQLException e) {
                throw new IllegalArgumentException("Database error.");
            }
        }

        return effective;

    }
}
