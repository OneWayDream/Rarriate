package ru.itis.view;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import ru.itis.RarriateApplication;
import ru.itis.entities.Map;
import ru.itis.entities.World;
import ru.itis.entities.blocks.Block;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.entities.player.implPlayers.Player;
import ru.itis.utils.FileLoader;
import ru.itis.utils.MediaLoader;
import ru.itis.utils.PropertiesLoader;
import ru.itis.view.components.ModernButton;

import java.awt.event.KeyListener;
import java.util.List;

public class Game {

    protected Stage mainStage;
    protected Scene mainScene;
    protected Pane mainPane;

    protected AbstractPlayer player;

    protected List<Block> blocks;

    protected ViewManager viewManager;

    protected MediaPlayer mediaPlayer;

    protected World world;

    protected int speed = 10;
    protected int fallingSpeed = speed/2;

    protected boolean up;
    protected boolean down;
    protected boolean left;
    protected boolean right;

    public Game(Stage stage, ViewManager viewManager){
        createGUI(stage, viewManager);
    }

    public Game(Stage stage, ViewManager viewManager, World world) {
        this.world = world;
        createGUI(stage, viewManager);
    }

    protected void createGUI(Stage stage, ViewManager viewManager) {
        mainStage = stage;
        this.viewManager = viewManager;
            Pane pane = new Pane();
            Scene scene = new Scene(pane, mainStage.getWidth(), mainStage.getHeight());

            ModernButton exit = new ModernButton("EXIT");
        exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    exitToMainMenu();
                }
        });
        pane.getChildren().add(exit);

        player = new Player();
        player.setTranslateX((scene.getWidth() - player.getWidth())/2);
        player.setTranslateY((scene.getHeight() - player.getHeight())/2);
        pane.getChildren().add(player);

        scene.setOnKeyPressed(e -> processKey(e.getCode(), true));
        scene.setOnKeyReleased(e -> processKey(e.getCode(), false));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        mainScene = scene;

        mainPane = pane;
        setBackground();
        generateLevel();

        timer.start();
        playGameBackgroundMusic();
    }

    protected void update() {
        if (up && player.getTranslateY() >= speed) {
            jumpPlayer(player);
        }

        if (left && player.getTranslateX() > 0) {
            movePlayerX(-speed, player);
        }

        if (right && player.getTranslateX() + player.getWidth() <= mainScene.getWidth()) {
            movePlayerX(speed, player);
        }

        if (player.getVelocity().getY() < 10) {
            player.setVelocity(player.getVelocity().add(0, 1));
        }

        movePlayerY((int)player.getVelocity().getY(), player);
    }

    protected void update(String name) {

    }

    protected void jumpPlayer(AbstractPlayer player) {
        if (player.isCanJump()) {
            player.setVelocity(player.getVelocity().add(0, -30));
            player.setCanJump(false);
        }
    }

    protected void movePlayerX(int value, AbstractPlayer player) {
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Block block : blocks) {
                if (player.getBoundsInParent().intersects(block.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + player.getWidth() == block.getTranslateX()) {
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateX() == block.getTranslateX() + block.getWidth()) {
                            return;
                        }
                    }
                }
            }
            player.moveX(movingRight ? 1 : -1);
//            RarriateApplication.getClient().sendUDPFrame(null);
        }
    }

    protected void movePlayerY(int value, AbstractPlayer player) {
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Block block : blocks) {
                if (player.getBoundsInParent().intersects(block.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + player.getHeight() == block.getTranslateY()) {
                            player.moveY(- 1);
                            player.setCanJump(true);
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateY() == block.getTranslateY() + block.getHeight()) {
                            return;
                        }
                    }
                }
            }
            player.moveY(movingDown ? 1 : -1);
        }
    }

    protected void processKey(KeyCode code, boolean on) {
        switch (code) {
            case A:
                left = on ;
                break ;
            case D:
                right = on ;
                break ;
            case W:
                up = on ;
                break ;
            case S:
                down = on ;
                break ;
            default:
                break ;
        }
    }

    public Scene getGameScene() {
        return mainScene;
    }

    protected void generateLevel() {
        if (world == null) {
            world = new World(new Map(), null);
        }
        blocks = world.getMap().getBlocks();

        for (Block block: blocks) {
            setBlock(block);
            block.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(Math.abs(player.getTranslateX() - block.getTranslateX()) <= 150 &&
                        Math.abs(player.getTranslateY() - block.getTranslateY()) <= 150){
                        mainPane.getChildren().remove(block);
                        blocks.remove(block);
                    }
                }
            });
        }
    }

    protected void setBlock(Block block) {
        mainPane.getChildren().add(block);
    }

    protected void setBackground(){
        mainPane.setBackground(new Background(FileLoader.getGameBackground()));
    }

    protected void exitToMainMenu() {
        stopPlayingBackgroundMusic();
        viewManager.setMainMenuScene();
    }

    protected void playGameBackgroundMusic() {
        mediaPlayer = MediaLoader.getGameBackgroundMusic();
        mediaPlayer.setVolume(Integer.parseInt(PropertiesLoader.getInstance().getProperty("MUSIC_VOLUME")));
        mediaPlayer.play();
    }

    protected void stopPlayingBackgroundMusic() {
        mediaPlayer.stop();
    }
}
