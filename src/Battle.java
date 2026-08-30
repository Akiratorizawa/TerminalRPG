package TerminalRPG_v3.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;
import java.lang.StackWalker.Option;

import static TerminalRPG_v3.src.Database.pokemonFetcher;
import static TerminalRPG_v3.src.Database.spriteFetcher;
import static TerminalRPG_v3.src.Database.effectiveCheck;


public class Battle extends JFrame implements KeyListener {
    // Initializing paint elements
    private Window window;
    private Image background;
    private Image bagImage;

    private Image cursor;
    private Image bagArrow;

    // Contains the player and opponent pokemon
    private ArrayList<Pokemon> pokemon;
    
    private Pokemon player;
    private Pokemon opponent;

    private Bag items;
    private Bag pokeballs;

    private Image playerSprite;
    private Image opponentSprite;

    private int playerMaxHp;
    private int opponentMaxHp;

    private Move lastMove;

    private int optionChoice;
    
    private int[] fightBagRunOptionsX = {580, 820, 1000};
    private int[] moveOptionsX = {100, 370, 640, 915};

    private Font pokemonFont;
    private Font biggerPokemon;
    private Font smallerPokemon;
    private Font pokemonMoves;

    private Timer battleTimer;

    private boolean playerTurn;
    private boolean pokemonCaught;

    private enum BattleState {
        BATTLE_INTRO,
        FIGHT_BAG_RUN,
        PLAYER_RUN,
        BAG_ITEMS,
        BAG_POKEBALLS,
        POTION_USED,
        POKEBALL_USED,
        POKEBALL_SHAKE,
        POKEBALL_CAUGHT,
        POKEBALL_BROKE,
        CHOOSE_MOVE,
        PLAYER_MOVE,
        OPPONENT_MOVE,
        NOT_VERY_EFFECTIVE,
        SUPER_EFFECTIVE,
        PLAYER_IMMUNE,
        OPPONENT_IMMUNE,
        PLAYER_FAINT,
        OPPONENT_FAINT,
        PLAYER_WIN,
        OPPONENT_WIN
    }

    private BattleState battleState;


    public Battle(Window window) {
        // Making this the window
        this.window = window;

        // Initializing the background and the cursor arrow
        background = new ImageIcon("assets/img/battle.png").getImage();
        cursor = new ImageIcon("assets/img/arrow.png").getImage();

        // Initializing the battle state
        battleState = BattleState.BATTLE_INTRO;

        // Getting the two pokemon for player and opponent
        try {
            pokemon = pokemonFetcher();
        } catch (Exception error) {
            error.printStackTrace();
        }

        player = pokemon.get(0);
        opponent = pokemon.get(1);

        playerMaxHp = player.hp();
        opponentMaxHp = opponent.hp();

        // Getting the two pokemon's sprites
        playerSprite = spriteFetcher(String.valueOf(player.id()), "player");
        opponentSprite = spriteFetcher(String.valueOf(opponent.id()), "opponent");

        // Creating bag
        Image potionImage = new ImageIcon("assets/img/super_potion.png").getImage();
        Item superPotion = new Item("Super Potion", potionImage, 3);

        Image pokeballImage = new ImageIcon("assets/img/pokeball.png").getImage();
        Item pokeball = new Item("Pokeball", pokeballImage, 1);

        ArrayList<Item> itemsTemp = new ArrayList<>();
        ArrayList<Item> pokeballsTemp = new ArrayList<>();

        itemsTemp.add(superPotion);
        pokeballsTemp.add(pokeball);

        items = new Bag(itemsTemp);
        pokeballs = new Bag(pokeballsTemp);

        // Initializing the fonts
        try {
            pokemonFont = Font.createFont(
                Font.TRUETYPE_FONT,
                new File("assets/ttf/pokemon.otf"));

            biggerPokemon = pokemonFont.deriveFont(Font.PLAIN, 48.0f);
            pokemonMoves = pokemonFont.deriveFont(Font.PLAIN, 32.0f);
            smallerPokemon = pokemonFont.deriveFont(Font.PLAIN, 26.0f);


        } catch (Exception error) {
            error.printStackTrace();
        }

        window.setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Drawing background every paint
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

                // Pokemon names, levels and HP paints
                g.setColor(Color.BLACK);

                g.setFont(smallerPokemon);
                
                g.drawString(player.name(), 750, 360);
                g.drawString(String.valueOf(player.level()), 1100, 362);

                g.drawString(opponent.name(), 120, 123);
                g.drawString(String.valueOf(opponent.level()), 480, 127);

                g.drawString(String.valueOf(playerMaxHp), 1100, 430);
                g.drawString(String.valueOf(player.hp), 1000, 430);


                // Drawing pokemon sprites IF they are still alive
                if (battleState != BattleState.PLAYER_FAINT && battleState != BattleState.OPPONENT_WIN) {
                    g.drawImage(playerSprite, 260, 250, 230, 230, this);
                }

                if (battleState != BattleState.OPPONENT_FAINT && battleState != BattleState.PLAYER_WIN && 
                    battleState != BattleState.POKEBALL_USED && battleState != BattleState.POKEBALL_CAUGHT && battleState != BattleState.POKEBALL_SHAKE) {
                    g.drawImage(opponentSprite, 800, 70, 235, 235, this);
                }


                // Player HP bar
                if ((double)player.hp / playerMaxHp <= 0.30) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GREEN);
                }


                // Green and black elements of player HP bar
                int width = (int)(((double)player.hp / playerMaxHp) * 250);

                g.fillRect(910, 376, width, 25);

                g.setColor(Color.black);

                g.fillRect(910 + width, 376, 250 - width, 25);


                // Opponent HP bar
                if ((double)opponent.hp / opponentMaxHp <= 0.30) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GREEN);
                }

                width = (int)(((double)opponent.hp / opponentMaxHp) * 250);
                g.fillRect(285, 141, width, 25);

                g.setColor(Color.black);

                g.fillRect(285 + width, 141, 250 - width, 25);

                g.setFont(biggerPokemon);
                g.setColor(Color.WHITE);

                if (battleState == BattleState.BATTLE_INTRO) {
                    g.drawString("A wild  " + opponent.name() + " appeared! Go, " + player.name() + "!",100, 600);
                } 


                else if (battleState == BattleState.FIGHT_BAG_RUN) {
                    g.drawString("What will you do?", 100, 580);
                    g.drawString("FIGHT", 650, 580);
                    g.drawString("BAG", 870, 580);
                    g.drawString("RUN", 1050, 580);

                    g.drawImage(cursor, fightBagRunOptionsX[optionChoice], 535, 40, 40, this);
                }

                else if (battleState == BattleState.BAG_ITEMS) {
                    bagImage = new ImageIcon("assets/img/bag.png").getImage();
                    bagArrow = new ImageIcon("assets/img/right_red_arrow.png").getImage();

                    g.drawImage(bagImage, 0, 0, getWidth(), getHeight(), this);

                    g.setFont(biggerPokemon);
                    g.setColor(Color.BLACK);

                    g.drawString("BAG", 295, 95);

                    g.setFont(smallerPokemon);

                    g.drawString("Items", 875, 65);
                    g.drawImage(bagArrow, 950, 29, 100, 50, this);

                    for (int i = 0; i < items.items().size(); i++) {
                        if (items.items().get(i).quantity > 0) {
                            int yCoordinate = 90;
                            Item item = items.items().get(i);

                            g.drawImage(item.image(), 800, yCoordinate, 40, 40, this);
                            g.drawString(item.quantity() + " - " + item.name(), 850, yCoordinate + 35);

                            g.drawImage(cursor, 750, yCoordinate, 40, 40, this);

                            yCoordinate += 50;

                            g.drawImage(item.image(), 52, 320, 100, 100, this);

                            g.drawString("A spray-type wound medicine.", 36, 500);
                            g.drawString("It restores the HP of one Pokemon", 36, 550);
                            g.drawString("by 50 points.", 36, 600);
                        }
                    }
                }

                else if (battleState == BattleState.BAG_POKEBALLS) {
                    bagImage = new ImageIcon("assets/img/bag.png").getImage();
                    bagArrow = new ImageIcon("assets/img/left_red_arrow.png").getImage();

                    g.drawImage(bagImage, 0, 0, getWidth(), getHeight(), this);

                    g.setFont(biggerPokemon);
                    g.setColor(Color.BLACK);

                    g.drawString("BAG", 295, 95);

                    g.setFont(smallerPokemon);

                    g.drawString("Pokeballs", 875, 65);
                    g.drawImage(bagArrow, 800, 29, 100, 50, this);

                    for (int i = 0; i < pokeballs.items().size(); i++) {
                        if (pokeballs.items().get(i).quantity > 0) {
                            int yCoordinate = 90;
                            Item item = pokeballs.items().get(i);

                            g.drawImage(item.image(), 840, yCoordinate, -40, 40, this);
                            g.drawString(item.quantity() + " - " + item.name(), 850, yCoordinate + 35);

                            g.drawImage(cursor, 750, yCoordinate, 40, 40, this);

                            yCoordinate += 50;

                            g.drawImage(item.image(), 52, 320, 100, 100, this);

                            g.drawString("A ball thrown to catch a wild Pokemon.", 36, 500);
                            g.drawString("It is designed in a capsule style.", 36, 550);
                        }
                    }
                }

                else if (battleState == BattleState.POTION_USED) {
                    g.drawString("You used a Super Potion.", 100, 600);
                }

                else if (battleState == BattleState.POKEBALL_USED) {
                    g.drawString("You used a Pokeball.", 100, 600);
                    g.drawImage(pokeballs.items().get(0).image(), 870, 205, 70, 70, this);
                }

                else if (battleState == BattleState.POKEBALL_SHAKE) {
                    Image tiltedPokeball = new ImageIcon("assets/img/pokeballtilt.png").getImage();

                    g.drawString("You used a Pokeball.", 100, 600);
                    g.drawImage(tiltedPokeball, 870, 205, 55, 55, this);
                }

                else if (battleState == BattleState.POKEBALL_CAUGHT) {
                    g.drawString("Gotcha! " + opponent.name() + " was caught!", 100, 600);
                    g.drawImage(pokeballs.items().get(0).image(), 870, 205, 70, 70, this);
                }

                else if (battleState == BattleState.POKEBALL_BROKE) {
                    g.drawString("Oh no! The Pokemon broke free!", 100, 600);
                }

                else if (battleState == BattleState.PLAYER_RUN) {
                    g.drawString("You ran away!", 100, 580);
                }

                else if (battleState == BattleState.CHOOSE_MOVE) {
                    int xCoordinate = 150;

                    g.setFont(pokemonMoves);

                    for (int i = 0; i < 4; i++) {
                        g.drawString(player.moves().get(i).move(), xCoordinate, 580);
                        xCoordinate += 270;
                    }

                    g.drawImage(cursor, moveOptionsX[optionChoice], 550, 40, 40, this);

                }

                else if (battleState == BattleState.PLAYER_MOVE) {
                    g.drawString(player.name() + " used " + lastMove.move() + "!", 100, 600);
                }

                else if (battleState == BattleState.NOT_VERY_EFFECTIVE) {
                    g.drawString("It's not very effective...", 100, 600);
                }

                else if (battleState == BattleState.SUPER_EFFECTIVE) {
                    g.drawString("It's super effective!", 100, 600);
                }

                else if (battleState == BattleState.PLAYER_IMMUNE) {
                    g.drawString("It doesn't affect " + player.name() + "...", 100, 600);
                }

                else if (battleState == BattleState.OPPONENT_IMMUNE) {
                    g.drawString("It doesn't affect " + opponent.name() + "...", 100, 600);
                }

                else if (battleState == BattleState.OPPONENT_MOVE) {
                    g.drawString(opponent.name() + " used " + lastMove.move() + "!", 100, 600);
                }

                else if (battleState == BattleState.PLAYER_FAINT) {
                    g.drawString(player.name() + " has fainted...", 100, 600);
                }

                else if (battleState == BattleState.OPPONENT_FAINT) {
                    g.drawString(opponent.name() + " has fainted...", 100, 600);
                }

                else if (battleState == BattleState.PLAYER_WIN) {
                    window.playWinMusic();
                    if (pokemonCaught == true) {
                        g.drawImage(pokeballs.items().get(0).image(), 870, 205, 70, 70, this);
                    }
                    g.drawString("You won the fight! Returning in 5 seconds...", 80, 600);
                }

                else if (battleState == BattleState.OPPONENT_WIN) {
                    g.drawString("You lost the fight. Returning in 5 seconds...", 75, 600);
                }

            }
        });

        // Initial repaint to actually change the screen
        window.revalidate();
        window.repaint();


        // Activating KeyListener
        window.addKeyListener(this);
        window.setFocusable(true);
        window.requestFocusInWindow();

        window.battleMusic();

        Timer firstDialogueTimer = new Timer(2500, er -> {
            battleState = BattleState.FIGHT_BAG_RUN;
            optionChoice = 0;
            window.repaint();
        });

        firstDialogueTimer.setRepeats(false);
        firstDialogueTimer.start();


    }

    @Override
    public void keyPressed (KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {

            if (battleState == BattleState.FIGHT_BAG_RUN || battleState == BattleState.CHOOSE_MOVE) {
                
                optionChoice--;

                if (optionChoice < 0) {
                    optionChoice = 0;
                } 

                else {
                    window.optionSound();
                }

                window.repaint();
            }

            else if (battleState == BattleState.BAG_POKEBALLS) {
                optionChoice = 0;
                battleState = BattleState.BAG_ITEMS;

                window.optionSound();
                window.repaint();
            }
        }


        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

            if (battleState == BattleState.FIGHT_BAG_RUN) {
                
                optionChoice++;

                if (optionChoice > 2) {
                    optionChoice = 2;
                }

                else {
                    window.optionSound();
                }

                window.repaint();
            }

            else if (battleState == BattleState.CHOOSE_MOVE) {
                
                optionChoice++;

                if (optionChoice > 3) {
                    optionChoice = 3;
                }

                else {
                    window.optionSound();
                }

                window.repaint();
            }

            else if (battleState == BattleState.BAG_ITEMS) {
                optionChoice = 0;
                battleState = BattleState.BAG_POKEBALLS;

                window.optionSound();
                window.repaint();
            }

        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            
            if (battleState == BattleState.FIGHT_BAG_RUN) {

                window.confirmSound();

                if (optionChoice == 2) {
                    battleState = BattleState.PLAYER_RUN;

                    window.repaint();

                    Timer mainMenuTimer = new Timer(3000, er -> {
                        window.showMainMenu();
                    });
                    
                    mainMenuTimer.setRepeats(false);
                    mainMenuTimer.start();
                    return;
                }

                else if (optionChoice == 1) {
                    optionChoice = 0;
                    battleState = BattleState.BAG_ITEMS;

                    window.repaint();
                }

                else {
                    battleState = BattleState.CHOOSE_MOVE;
                    window.repaint();
                    return;
                }
            }

            else if (battleState == BattleState.CHOOSE_MOVE) {
                window.confirmSound();

                lastMove = player.moves.get(optionChoice);

                double multiplier = effectiveCheck(lastMove, opponent);
                int damage = damageCalculation(lastMove, opponent);

                opponent.hp -= damage;

                if (opponent.hp < 0) {
                    opponent.hp = 0;
                }
                
                if (multiplier < 1 && multiplier != 0) {
                    window.notVeryEffectiveSound();
                }

                else if (multiplier > 1) {
                    window.superEffectiveSound();
                }

                else if (multiplier != 0) {
                    window.attackSound();
                }

                enterBattleState(BattleState.PLAYER_MOVE);
                return;
            }

            else if (battleState == BattleState.BAG_ITEMS) {
                if (optionChoice == 0 && items.items().get(0).quantity > 0) {
                    window.confirmSound();
                    potionUsed();
                }
            }

            else if (battleState == BattleState.BAG_POKEBALLS) {
                if (optionChoice == 0 && pokeballs.items().get(0).quantity > 0) {
                    window.confirmSound();
                    pokeballUsed();
                }
            }

            else {
                return;
            }

        } 

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (battleState == BattleState.CHOOSE_MOVE || battleState == BattleState.BAG_ITEMS || battleState == BattleState.BAG_POKEBALLS) {
                optionChoice = 0;
                battleState = BattleState.FIGHT_BAG_RUN;

                window.backSound();
                window.repaint();
            }
        }

    }


    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static int damageCalculation(Move move, Pokemon target) {
        int variance = ThreadLocalRandom.current().nextInt(-10, 11);

        int initialDamage = (move.power() / 2) + variance;

        double multiplier = effectiveCheck(move, target);

        double finalDamage = multiplier * initialDamage;

        return (int) finalDamage;
    }   

    public void enterBattleState(BattleState state) {
        battleState = state;
        window.repaint();

        if (battleTimer != null) {
            battleTimer.stop();
        }

        battleTimer = new Timer(2500, e -> {

            if (battleState == BattleState.PLAYER_MOVE) {
                double multiplier = effectiveCheck(lastMove, opponent);
                playerTurn = true;

                if (multiplier < 1 && multiplier != 0) {
                    enterBattleState(BattleState.NOT_VERY_EFFECTIVE);
                }

                else if (multiplier > 1) {
                    enterBattleState(BattleState.SUPER_EFFECTIVE);
                }

                else if (multiplier == 0) {
                    enterBattleState(BattleState.OPPONENT_IMMUNE);
                }

                else {

                    if (opponent.hp == 0) {
                        enterBattleState(BattleState.OPPONENT_FAINT);
                    }

                    else {
                        lastMove = opponent.moves.get(ThreadLocalRandom.current().nextInt(0, 4));

                        multiplier = effectiveCheck(lastMove, player);
                        int damage = damageCalculation(lastMove, player);

                        player.hp -= damage;

                        if (player.hp <  0) {
                            player.hp = 0;
                        }

                        if (multiplier < 1 && multiplier != 0) {
                            window.notVeryEffectiveSound();
                        }

                        else if (multiplier > 1) {
                            window.superEffectiveSound();
                        }

                        else if (multiplier == 1) {
                            window.attackSound();
                        }

                        enterBattleState(BattleState.OPPONENT_MOVE);
                    }
                }
            }

            else if (battleState == BattleState.OPPONENT_MOVE) {
                playerTurn = false;

                double multiplier = effectiveCheck(lastMove, player);

                if (multiplier < 1 && multiplier != 0) {
                    enterBattleState(BattleState.NOT_VERY_EFFECTIVE);
                }

                else if (multiplier > 1) {
                    enterBattleState(BattleState.SUPER_EFFECTIVE);
                }

                else if (multiplier == 0) {
                    enterBattleState(BattleState.PLAYER_IMMUNE);
                }

                else {
                    if (player.hp == 0) {
                        enterBattleState(BattleState.PLAYER_FAINT);
                    }

                    else {
                        optionChoice = 0;
                        enterBattleState(BattleState.FIGHT_BAG_RUN);
                    }
                }
            }

            else if (battleState == BattleState.NOT_VERY_EFFECTIVE || battleState == BattleState.SUPER_EFFECTIVE) {
                if (opponent.hp == 0) {
                    enterBattleState(BattleState.OPPONENT_FAINT);
                }

                else if (player.hp == 0) {
                    enterBattleState(BattleState.PLAYER_FAINT);
                }

                else if (playerTurn == true) {
                    lastMove = opponent.moves.get(ThreadLocalRandom.current().nextInt(0, 4));

                    double multiplier = effectiveCheck(lastMove, player);

                    int damage = damageCalculation(lastMove, player);

                    player.hp -= damage;

                    if (player.hp <  0) {
                        player.hp = 0;
                    }

                    if (multiplier < 1 && multiplier != 0) {
                        window.notVeryEffectiveSound();
                    }

                    else if (multiplier > 1) {
                        window.superEffectiveSound();
                    }

                    else if (multiplier == 1) {
                        window.attackSound();
                    }

                    enterBattleState(BattleState.OPPONENT_MOVE);
                }

                else {
                    optionChoice = 0;
                    enterBattleState(BattleState.FIGHT_BAG_RUN);
                }
            }

            else if (battleState == BattleState.PLAYER_IMMUNE) {
                optionChoice = 0;
                enterBattleState(BattleState.FIGHT_BAG_RUN);
            }

            else if (battleState == BattleState.OPPONENT_IMMUNE) {
                lastMove = opponent.moves.get(ThreadLocalRandom.current().nextInt(0, 4));

                double multiplier = effectiveCheck(lastMove, player);
                int damage = damageCalculation(lastMove, player);

                player.hp -= damage;

                if (player.hp <  0) {
                    player.hp = 0;
                }

                if (multiplier < 1 && multiplier != 0) {
                    window.notVeryEffectiveSound();
                }

                else if (multiplier > 1) {
                    window.superEffectiveSound();
                }

                else if (multiplier == 1) {
                    window.attackSound();
                }
                
                enterBattleState(BattleState.OPPONENT_MOVE);
            }

            else if (battleState == BattleState.PLAYER_FAINT) {
                enterBattleState(BattleState.OPPONENT_WIN);
            }

            else if (battleState == BattleState.OPPONENT_FAINT) {
                enterBattleState(BattleState.PLAYER_WIN);
            }

            else if (battleState == BattleState.PLAYER_WIN) {

                Timer toMenu = new Timer(5000, er -> {
                    window.removeKeyListener(this);
                    window.showMainMenu();
                });
                
                toMenu.setRepeats(false);
                toMenu.start();
            }

            else if (battleState == BattleState.OPPONENT_WIN) {
                
                Timer toMenu = new Timer(5000, er -> {
                    window.removeKeyListener(this);
                    window.showMainMenu();
                });
                
                toMenu.setRepeats(false);
                toMenu.start();
            }

        });

        battleTimer.setRepeats(false);
        battleTimer.start();
    }

    public void potionUsed() {
        battleState = BattleState.POTION_USED;
        items.items().get(0).quantity -= 1;

        window.repaint();

        Timer potionDialogueTimer = new Timer(1500, e -> {
            if (player.hp != playerMaxHp) {
                window.potionSound();
            }

            player.hp += 50;

            if (player.hp > playerMaxHp) {
                player.hp = playerMaxHp;
            }

            window.repaint();

            Timer toOpponentMove = new Timer (2500, er -> {
                lastMove = opponent.moves.get(ThreadLocalRandom.current().nextInt(0, 4));

                double multiplier = effectiveCheck(lastMove, player);

                int damage = damageCalculation(lastMove, player);

                player.hp -= damage;

                if (player.hp <  0) {
                    player.hp = 0;
                }

                if (multiplier < 1 && multiplier != 0) {
                    window.notVeryEffectiveSound();
                }

                else if (multiplier > 1) {
                    window.superEffectiveSound();
                }

                else if (multiplier == 1) {
                    window.attackSound();
                }

                enterBattleState(BattleState.OPPONENT_MOVE);
            });

            toOpponentMove.setRepeats(false);
            toOpponentMove.start();
        });

        potionDialogueTimer.setRepeats(false);
        potionDialogueTimer.start();
    }

    public void pokeballUsed() {
        battleState = BattleState.POKEBALL_USED;
        pokeballs.items().get(0).quantity -= 1;

        window.repaint();

        Timer firstShake = new Timer(1000, e -> {
            battleState = BattleState.POKEBALL_SHAKE;
            window.pokeballShakeSound();
            window.repaint();

            Timer neutral = new Timer(500, er -> {
                battleState = BattleState.POKEBALL_USED;
                window.repaint();

                Timer secondShake = new Timer(1000, er1 -> {
                    battleState = BattleState.POKEBALL_SHAKE;
                    window.pokeballShakeSound();
                    window.repaint();

                    double random = Math.random();

                    if (random >= 0.60) {
                        pokemonCaught = true;
                    }


                    if (pokemonCaught == true) {
                        Timer toCaught = new Timer(1000, er2 -> {
                            battleState = BattleState.POKEBALL_CAUGHT;
                            window.pokemonCaught();
                            window.stopBattleMusic();

                            window.repaint();

                            Timer caughtDialogueTimer = new Timer(4000, er3 -> {
                                battleState = BattleState.PLAYER_WIN;
                                window.repaint();

                                Timer backToMenu = new Timer(5000, er4 -> {
                                    window.removeKeyListener(this);
                                    window.showMainMenu();
                                });

                                backToMenu.setRepeats(false);
                                backToMenu.start();
                            });

                            caughtDialogueTimer.setRepeats(false);
                            caughtDialogueTimer.start();
                        });

                        toCaught.setRepeats(false);
                        toCaught.start();
                    }

                    else {
                        Timer pokeballBroke = new Timer(1000, er2 -> {
                            battleState = BattleState.POKEBALL_BROKE;
                            window.repaint();

                            Timer toOpponentMove = new Timer(2500, er3 -> {

                                lastMove = opponent.moves.get(ThreadLocalRandom.current().nextInt(0, 4));

                                double multiplier = effectiveCheck(lastMove, player);
                                int damage = damageCalculation(lastMove, player);

                                player.hp -= damage;

                                if (player.hp <  0) {
                                    player.hp = 0;
                                }

                                if (multiplier < 1 && multiplier != 0) {
                                    window.notVeryEffectiveSound();
                                }

                                else if (multiplier > 1) {
                                    window.superEffectiveSound();
                                }

                                else if (multiplier == 1) {
                                    window.attackSound();
                                }

                                enterBattleState(BattleState.OPPONENT_MOVE);

                            });

                            toOpponentMove.setRepeats(false);
                            toOpponentMove.start();
                        
                        });

                        pokeballBroke.setRepeats(false);
                        pokeballBroke.start();
                    }

                });

                secondShake.setRepeats(false);
                secondShake.start();
            });

            neutral.setRepeats(false);
            neutral.start();

        });

        firstShake.setRepeats(false);
        firstShake.start();

    }
}

