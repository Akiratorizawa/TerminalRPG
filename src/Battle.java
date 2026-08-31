package TerminalRPG_v3.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;

import static TerminalRPG_v3.src.Database.pokemonFetcher;
import static TerminalRPG_v3.src.Database.spriteFetcher;
import static TerminalRPG_v3.src.Database.effectiveCheck;


public class Battle extends JFrame implements KeyListener {
    // Initializing paint elements
    private Window window;

    private int windowWidth;
    private int windowHeight;

    private Image background;
    private Image bagImage;

    private Image cursor;
    private Image bagArrow;

    // Contains the player and opponent pokemon
    private ArrayList<Pokemon> pokemon;
    
    private Pokemon player;
    private Pokemon opponent;
    private Pokemon target;

    private Bag items;
    private Bag pokeballs;

    private Image playerSprite;
    private Image opponentSprite;

    private int playerMaxHp;
    private int opponentMaxHp;

    private Move lastMove;

    private int optionChoice;

    private int pokeballShakes;
    
    private int[] fightBagRunOptionsX = {580, 820, 1000};
    private int[] moveOptionsX = {100, 370, 640, 915};

    private Font pokemonFont;
    private Font biggerPokemon;
    private Font smallerPokemon;
    private Font pokemonMoves;

    private Timer battleTimer;

    private boolean playerTurn;
    private boolean pokemonCaught = false;

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
        Item pokeball = new Item("Pokeball", pokeballImage, 3);

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
                
                windowWidth = getWidth();
                windowHeight = getHeight();

                // Drawing background every paint
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

                // Pokemon names, levels and HP paints
                g.setColor(Color.BLACK);

                g.setFont(smallerPokemon);
                
                g.drawString(player.name(), getRelativeWidth(750), getRelativeHeight(360));

                g.drawString(String.valueOf(player.level()), getRelativeWidth(1100) + getRelativeWidth(15), getRelativeHeight(362));

                g.drawString(opponent.name(), getRelativeWidth(120), getRelativeHeight(123));
                g.drawString(String.valueOf(opponent.level()), getRelativeWidth(480) + getRelativeWidth(15), getRelativeHeight(127));

                g.drawString(String.valueOf(playerMaxHp), getRelativeWidth(1100), getRelativeHeight(430));
                g.drawString(String.valueOf(player.hp), getRelativeWidth(1000), getRelativeHeight(430));


                // Drawing pokemon sprites IF they are still alive
                if (battleState != BattleState.PLAYER_FAINT && battleState != BattleState.OPPONENT_WIN) {
                    g.drawImage(playerSprite, getRelativeWidth(260), getRelativeHeight(250), getRelativeWidth(230), getRelativeHeight(230), this);
                }

                if (battleState != BattleState.OPPONENT_FAINT && battleState != BattleState.PLAYER_WIN && 
                    battleState != BattleState.POKEBALL_USED && battleState != BattleState.POKEBALL_CAUGHT && battleState != BattleState.POKEBALL_SHAKE) {
                    g.drawImage(opponentSprite, getRelativeWidth(800), getRelativeHeight(70), getRelativeWidth(235), getRelativeHeight(235), this);
                }


                // Player HP bar
                if ((double)player.hp / playerMaxHp <= 0.30) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GREEN);
                }


                // Green and black elements of player HP bar
                int width = (int)(((double)player.hp / playerMaxHp) * 250);

                g.fillRect(getRelativeWidth(910), getRelativeHeight(376), getRelativeWidth(width), getRelativeHeight(28));

                g.setColor(Color.black);

                g.fillRect(getRelativeWidth(910 + width), getRelativeHeight(378), getRelativeWidth(250 - width), getRelativeHeight(28));


                // Opponent HP bar
                if ((double)opponent.hp / opponentMaxHp <= 0.30) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GREEN);
                }

                width = (int)(((double)opponent.hp / opponentMaxHp) * 250);
                g.fillRect(getRelativeWidth(285), getRelativeHeight(141), getRelativeWidth(width), getRelativeHeight(25));

                g.setColor(Color.black);

                g.fillRect(getRelativeWidth(285 + width), getRelativeHeight(141), getRelativeWidth(250 - width), getRelativeHeight(25));

                g.setFont(biggerPokemon);
                g.setColor(Color.WHITE);

                
                // State-based dialogue machine

                switch (battleState) {
                    case BATTLE_INTRO -> {
                        g.drawString("A wild  " + opponent.name() + " appeared! Go, " + player.name() + "!",getRelativeWidth(100), getRelativeHeight(600));
                    } 


                    case FIGHT_BAG_RUN -> {
                        g.drawString("What will you do?", getRelativeWidth(100), getRelativeHeight(580));
                        g.drawString("FIGHT", getRelativeWidth(650), getRelativeHeight(580));
                        g.drawString("BAG", getRelativeWidth(870), getRelativeHeight(580));
                        g.drawString("RUN", getRelativeWidth(1050), getRelativeHeight(580));

                        g.drawImage(cursor, getRelativeWidth(fightBagRunOptionsX[optionChoice]), getRelativeHeight(535) + getRelativeHeight(10), getRelativeWidth(40), getRelativeHeight(40), this);
                    }

                    case BAG_ITEMS -> {
                        bagImage = new ImageIcon("assets/img/bag.png").getImage();
                        bagArrow = new ImageIcon("assets/img/right_red_arrow.png").getImage();

                        g.drawImage(bagImage, 0, 0, getWidth(), getHeight(), this);

                        g.setFont(biggerPokemon);
                        g.setColor(Color.BLACK);

                        g.drawString("BAG", getRelativeWidth(295), getRelativeHeight(95));

                        g.setFont(smallerPokemon);

                        g.drawString("Items", getRelativeWidth(875), getRelativeHeight(65));
                        g.drawImage(bagArrow, getRelativeWidth(950), getRelativeHeight(29), getRelativeWidth(100), getRelativeHeight(50), this);

                        for (int i = 0; i < items.items().size(); i++) {
                            if (items.items().get(i).quantity > 0) {
                                int yCoordinate = 90;
                                Item item = items.items().get(i);

                                g.drawImage(item.image(), getRelativeWidth(800), getRelativeHeight(yCoordinate), getRelativeWidth(40), getRelativeHeight(40), this);
                                g.drawString(item.quantity() + " - " + item.name(), getRelativeWidth(850), getRelativeHeight(yCoordinate + 35));

                                g.drawImage(cursor, getRelativeWidth(750), getRelativeHeight(yCoordinate), getRelativeWidth(40), getRelativeHeight(40), this);

                                yCoordinate += 50;

                                g.drawImage(item.image(), getRelativeWidth(52), getRelativeHeight(320), getRelativeWidth(100), getRelativeHeight(100), this);

                                g.drawString("A spray-type wound medicine.", getRelativeWidth(36), getRelativeHeight(500));
                                g.drawString("It restores the HP of one Pokemon", getRelativeWidth(36), getRelativeHeight(550));
                                g.drawString("by 50 points.", getRelativeWidth(36), getRelativeHeight(600));
                            }
                        }
                    }

                    case BAG_POKEBALLS -> {
                        bagImage = new ImageIcon("assets/img/bag.png").getImage();
                        bagArrow = new ImageIcon("assets/img/left_red_arrow.png").getImage();

                        g.drawImage(bagImage, 0, 0, getWidth(), getHeight(), this);

                        g.setFont(biggerPokemon);
                        g.setColor(Color.BLACK);

                        g.drawString("BAG", getRelativeWidth(295), getRelativeHeight(95));

                        g.setFont(smallerPokemon);

                        g.drawString("Pokeballs", getRelativeWidth(875), getRelativeHeight(65));
                        g.drawImage(bagArrow, getRelativeWidth(800), getRelativeHeight(29), getRelativeWidth(100), getRelativeHeight(50), this);

                        for (int i = 0; i < pokeballs.items().size(); i++) {
                            if (pokeballs.items().get(i).quantity > 0) {
                                int yCoordinate = 90;
                                Item item = pokeballs.items().get(i);

                                g.drawImage(item.image(), getRelativeWidth(800), getRelativeHeight(yCoordinate), getRelativeWidth(40), getRelativeHeight(40), this);
                                g.drawString(item.quantity() + " - " + item.name(), getRelativeWidth(850), getRelativeHeight(yCoordinate + 35));

                                g.drawImage(cursor, getRelativeWidth(750), getRelativeHeight(yCoordinate), getRelativeWidth(40), getRelativeHeight(40), this);

                                yCoordinate += 50;

                                g.drawImage(item.image(), getRelativeWidth(52), getRelativeHeight(320), getRelativeWidth(100), getRelativeHeight(100), this);

                                g.drawString("A ball thrown to catch a wild Pokemon.", getRelativeWidth(36), getRelativeHeight(500));
                                g.drawString("It is designed in a capsule style.", getRelativeWidth(36), getRelativeHeight(550));
                            }
                        }
                    }

                    case POTION_USED -> {
                        g.drawString("You used a Super Potion.", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case POKEBALL_USED -> {
                        g.drawString("You used a Pokeball.", getRelativeWidth(100), getRelativeHeight(600));
                        g.drawImage(pokeballs.items().get(0).image(), getRelativeWidth(870), getRelativeHeight(205), getRelativeWidth(70), getRelativeHeight(70), this);
                    }

                    case POKEBALL_SHAKE -> {
                        Image tiltedPokeball = new ImageIcon("assets/img/pokeballtilt.png").getImage();

                        g.drawString("You used a Pokeball.", getRelativeHeight(100), getRelativeHeight(600));
                        g.drawImage(tiltedPokeball, getRelativeWidth(870), getRelativeHeight(205), getRelativeWidth(55), getRelativeHeight(55), this);
                    }

                    case POKEBALL_CAUGHT -> {
                        g.drawString("Gotcha! " + opponent.name() + " was caught!", getRelativeWidth(100), getRelativeHeight(600));
                        g.drawImage(pokeballs.items().get(0).image(), getRelativeWidth(870), getRelativeHeight(205), getRelativeWidth(70), getRelativeHeight(70), this);
                    }

                    case POKEBALL_BROKE -> {
                        g.drawString("Oh no! The Pokemon broke free!", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case PLAYER_RUN -> {
                        g.drawString("You ran away!", getRelativeWidth(100), getRelativeHeight(580));
                    }

                    case CHOOSE_MOVE -> {
                        int xCoordinate = 150;

                        g.setFont(pokemonMoves);

                        for (int i = 0; i < 4; i++) {
                            g.drawString(player.moves().get(i).move(), getRelativeWidth(xCoordinate), getRelativeHeight(580));
                            xCoordinate += 270;
                        }

                        g.drawImage(cursor, getRelativeWidth(moveOptionsX[optionChoice]), getRelativeHeight(550), getRelativeWidth(40), getRelativeHeight(40), this);

                    }

                    case PLAYER_MOVE -> {
                        g.drawString(player.name() + " used " + lastMove.move() + "!", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case NOT_VERY_EFFECTIVE -> {
                        g.drawString("It's not very effective...", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case SUPER_EFFECTIVE -> {
                        g.drawString("It's super effective!", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case PLAYER_IMMUNE -> {
                        g.drawString("It doesn't affect " + player.name() + "...", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case OPPONENT_IMMUNE -> {
                        g.drawString("It doesn't affect " + opponent.name() + "...", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case OPPONENT_MOVE -> {
                        g.drawString(opponent.name() + " used " + lastMove.move() + "!", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case PLAYER_FAINT -> {
                        window.faintSound();
                        g.drawString(player.name() + " has fainted...", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case OPPONENT_FAINT -> {
                        window.faintSound();
                        g.drawString(opponent.name() + " has fainted...", getRelativeWidth(100), getRelativeHeight(600));
                    }

                    case PLAYER_WIN -> {
                        window.playWinMusic();
                        if (pokemonCaught == true) {
                            g.drawImage(pokeballs.items().get(0).image(), getRelativeWidth(870), getRelativeHeight(205), getRelativeWidth(70), getRelativeHeight(70), this);
                        }
                        g.drawString("You won the fight! Returning in 5 seconds...", getRelativeWidth(80), getRelativeHeight(600));
                    }

                    case OPPONENT_WIN -> {
                        g.drawString("You lost the fight. Returning in 5 seconds...", getRelativeWidth(75), getRelativeHeight(600));
                    }
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
                target = opponent;
                moveUsed(player);
            
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
                    pokeballs.items().get(0).quantity--;
                    
                    pokeballShakes = 0;
                    window.confirmSound();

                    Timer timer = new Timer(225, er1 -> {
                        window.pokeballThrowSound();
                    });
                    
                    timer.setRepeats(false);
                    timer.start();

                    pokeballUsed(BattleState.POKEBALL_USED);
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
            switch (battleState) {
                case PLAYER_MOVE -> {
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
                            target = player;
                            moveUsed(opponent);
                        }
                    }
                }

                case OPPONENT_MOVE -> {
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

                case NOT_VERY_EFFECTIVE, SUPER_EFFECTIVE -> {
                    if (opponent.hp == 0) {
                        enterBattleState(BattleState.OPPONENT_FAINT);
                    }

                    else if (player.hp == 0) {
                        enterBattleState(BattleState.PLAYER_FAINT);
                    }

                    else if (playerTurn == true) {
                        target = player;
                        moveUsed(opponent);
                    }

                    else {
                        optionChoice = 0;
                        enterBattleState(BattleState.FIGHT_BAG_RUN);
                    }
                }

                case PLAYER_IMMUNE -> {
                    optionChoice = 0;
                    enterBattleState(BattleState.FIGHT_BAG_RUN);
                }

                case OPPONENT_IMMUNE -> {
                    target = player;
                    moveUsed(opponent);
                }

                case PLAYER_FAINT -> {
                    enterBattleState(BattleState.OPPONENT_WIN);
                }

                case OPPONENT_FAINT -> {
                    enterBattleState(BattleState.PLAYER_WIN);
                }

                case PLAYER_WIN -> {

                    Timer toMenu = new Timer(5000, er -> {
                        window.removeKeyListener(this);
                        window.showMainMenu();
                    });
                    
                    toMenu.setRepeats(false);
                    toMenu.start();
                }

                case OPPONENT_WIN -> {
                    
                    Timer toMenu = new Timer(5000, er -> {
                        window.removeKeyListener(this);
                        window.showMainMenu();
                    });
                    
                    toMenu.setRepeats(false);
                    toMenu.start();
                }
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
                target = player;
                moveUsed(opponent);
            });

            toOpponentMove.setRepeats(false);
            toOpponentMove.start();
        });

        potionDialogueTimer.setRepeats(false);
        potionDialogueTimer.start();
    }


    public void pokeballUsed(BattleState state) {
        if (battleTimer != null && battleTimer.isRunning()) {
            battleTimer.stop();
        }

        battleState = state;
        window.repaint();

        if (battleState == BattleState.POKEBALL_USED) {
            
            if (pokeballShakes <= 2) {
                battleTimer = new Timer(1000, e -> {

                    if (Math.random() >= 0.25) {
                        pokeballShakes++;

                        if (pokeballShakes <= 2) {
                            window.pokeballShakeSound();
                            pokeballUsed(BattleState.POKEBALL_SHAKE);
                        }

                        else {
                            window.pokemonCaught();
                            window.stopBattleMusic();
                            pokeballUsed(BattleState.POKEBALL_CAUGHT);
                        }
                    }

                    else {
                        pokeballUsed(BattleState.POKEBALL_BROKE);
                    }
                });

                battleTimer.setRepeats(false);
                battleTimer.start();
            }
        }

        else if (battleState == BattleState.POKEBALL_SHAKE) {
            battleTimer = new Timer (500, e -> {
                pokeballUsed(BattleState.POKEBALL_USED);
            });

            battleTimer.setRepeats(false);
            battleTimer.start();
        }

        else if (battleState == BattleState.POKEBALL_BROKE) {
            battleTimer = new Timer(2500, e -> {
                target = player;
                moveUsed(opponent);
            });

            battleTimer.setRepeats(false);
            battleTimer.start();
        }

        else if (battleState == BattleState.POKEBALL_CAUGHT) {
            battleTimer = new Timer(4000, e -> {
                enterBattleState(BattleState.PLAYER_WIN);
            });

            battleTimer.setRepeats(false);
            battleTimer.start();
        }
    }

    public void moveUsed(Pokemon user) {

        if (user == player) {
            lastMove = player.moves.get(optionChoice);
        }

        else {
            lastMove = opponent.moves.get(ThreadLocalRandom.current().nextInt(0, 4));
        }

        double multiplier = effectiveCheck(lastMove, target);

        int damage = damageCalculation(lastMove, target);

        target.hp -= damage;

        if (target.hp <  0) {
            target.hp = 0;
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

        if (user == player) {
            enterBattleState(BattleState.PLAYER_MOVE);
            return;
        }

        else {
            enterBattleState(BattleState.OPPONENT_MOVE);
            return;
        }
        
    }

    public int getRelativeWidth(int x) {
        return (int) (((double) x / 1280.0) * (double) (windowWidth + 16));
    }

    public int getRelativeHeight(int y) {
        return (int) (((double) y / 720.0) * (windowHeight + 39));
    }
}

