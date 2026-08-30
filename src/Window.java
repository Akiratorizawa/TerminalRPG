package TerminalRPG_v3.src;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;

public class Window extends JFrame {

    private Clip menuMusic;
    private Clip battleMusic;
    private Clip winMusic;
    private Clip hpSound;

    public Window() {

      setTitle("Valorant");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setSize(1280, 720);
      setLocationRelativeTo(null);

      setVisible(true);

      showMainMenu();

    }

    public void showMainMenu() {
        mainMenu menu = new mainMenu(this);
    }

    public void showAbout() {
        About about = new About(this);
    }

    public void showBattle() {
        Battle battle = new Battle(this);
    }
    public void menuMusic() {
        if (menuMusic != null && menuMusic.isRunning()) {
            return;
        }

        if (battleMusic != null && battleMusic.isRunning()) {
            battleMusic.stop();
        }

        if (winMusic != null && winMusic.isRunning()) {
            winMusic.stop();
        }

        try {
            File audiofile = new File("assets/wav/mainmenu.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            menuMusic = AudioSystem.getClip();
            menuMusic.open(audioStream);
            FloatControl volume = (FloatControl) menuMusic.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
            menuMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void battleMusic() {
        menuMusic.stop();
        try {
            File audiofile = new File("assets/wav/battle.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            battleMusic = AudioSystem.getClip();
            battleMusic.open(audioStream);
            FloatControl volume = (FloatControl) battleMusic.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            battleMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void optionSound() {
        try {
            File audiofile = new File("assets/wav/cursor.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void confirmSound() {
        try {
            File audiofile = new File("assets/wav/confirm.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backSound() {
        try {
            File audiofile = new File("assets/wav/back.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void playWinMusic() {
        if (battleMusic != null && battleMusic.isRunning()) {
            battleMusic.stop();
        }

        if (winMusic != null && winMusic.isRunning()) {
            return;
        }

        try {
            File audiofile = new File("assets/wav/win.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            winMusic = AudioSystem.getClip();
            winMusic.open(audioStream);
            FloatControl volume = (FloatControl) winMusic.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            winMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void attackSound() {
        try {
            Clip attackSound;
            File audiofile = new File("assets/wav/attack.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            attackSound = AudioSystem.getClip();
            attackSound.open(audioStream);
            attackSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void notVeryEffectiveSound() {
        try {
            Clip attackSound;
            File audiofile = new File("assets/wav/notveryeffective.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            attackSound = AudioSystem.getClip();
            attackSound.open(audioStream);
            attackSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void superEffectiveSound() {
        try {
            Clip attackSound;
            File audiofile = new File("assets/wav/supereffective.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            attackSound = AudioSystem.getClip();
            attackSound.open(audioStream);
            attackSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void lowHp() {
        try {
            File audiofile = new File("assets/wav/lowhp.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            hpSound = AudioSystem.getClip();
            hpSound.open(audioStream);
            hpSound.start();
            hpSound.loop(1);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void stopHpSound() {
        if (hpSound != null && hpSound.isRunning()) {
            hpSound.stop();
        }
    }

    public void potionSound() {
        try {
            File audiofile = new File("assets/wav/potion.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            hpSound = AudioSystem.getClip();
            hpSound.open(audioStream);
            FloatControl volume = (FloatControl) hpSound.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            hpSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void pokeballShakeSound() {
        try {
            File audiofile = new File("assets/wav/pokeballshake.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            hpSound = AudioSystem.getClip();
            hpSound.open(audioStream);
            FloatControl volume = (FloatControl) hpSound.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            hpSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void pokemonCaught() {
        try {
            File audiofile = new File("assets/wav/pokemonCaught.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            hpSound = AudioSystem.getClip();
            hpSound.open(audioStream);
            FloatControl volume = (FloatControl) hpSound.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            hpSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void stopBattleMusic() {
        if (battleMusic != null && battleMusic.isRunning()) {
            battleMusic.stop();
        }
    }
}
