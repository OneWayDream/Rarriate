package ru.itis.view;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import ru.itis.entities.blocks.Block;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.entities.player.implPlayers.Player;
import ru.itis.utils.FileLoader;
import ru.itis.utils.MediaLoader;
import ru.itis.utils.PropertiesLoader;
import ru.itis.view.components.ModernButton;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Stage mainStage;
    private Scene mainScene;
    private Pane mainPane;

    private AbstractPlayer player;

    private List<Block> blocks;

    private ViewManager viewManager;

    private MediaPlayer mediaPlayer;

    private int speed = 10;
    private int fallingSpeed = speed/2;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    public Game(Stage stage, ViewManager viewManager){

        mainStage = stage;
        this.viewManager = viewManager;
        Pane pane = new Pane();

        ModernButton exit = new ModernButton("EXIT");
        exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                exitToMainMenu();
            }
        });
        pane.getChildren().add(exit);

        player = new Player();

        player.setTranslateX((mainStage.getWidth() - player.getWidth())/2);
        player.setTranslateY((mainStage.getHeight() - player.getHeight())/2);

        pane.getChildren().add(player);

        Scene scene = new Scene(pane, mainStage.getWidth(), mainStage.getHeight());

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

    private void update() {
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

    private void jumpPlayer(AbstractPlayer player) {
        if (player.isCanJump()) {
            player.setVelocity(player.getVelocity().add(0, -30));
            player.setCanJump(false);
        }
    }

    private void movePlayerX(int value, AbstractPlayer player) {
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
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
        }
    }

    private void movePlayerY(int value, AbstractPlayer player) {
        boolean movingDown = value > 0;

        for (int i = 0; i < Math.abs(value); i++) {
            for (Block block : blocks) {
                if (player.getBoundsInParent().intersects(block.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + player.getHeight() == block.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
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
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }
    }

    private void processKey(KeyCode code, boolean on) {
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

    private void generateLevel() {
        blocks = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 40; j++) {
                DirtBlock dirtBlock = new DirtBlock();
                setNode(dirtBlock.getWidth() * j, mainScene.getHeight() - (dirtBlock.getHeight() * i), dirtBlock);
                blocks.add(dirtBlock);
                dirtBlock.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(Math.abs(player.getTranslateX() - dirtBlock.getTranslateX()) <= 150 &&
                            Math.abs(player.getTranslateY() - dirtBlock.getTranslateY()) <= 150){
                            mainPane.getChildren().remove(dirtBlock);
                            blocks.remove(dirtBlock);
                        }

                    }
                });
            }
        }
    }

    private void setNode(double x, double y, Block item) {
        item.setTranslateX(x);
        item.setTranslateY(y);
        mainPane.getChildren().add(item);
    }

    private void setBackground(){
        mainPane.setBackground(new Background(FileLoader.getGameBackground()));
    }

    private void exitToMainMenu() {
        stopPlayingBackgroundMusic();
        viewManager.setMainMenuScene();
    }

    private void playGameBackgroundMusic() {
        mediaPlayer = MediaLoader.getGameBackgroundMusic();
        mediaPlayer.play();
    }

    private void stopPlayingBackgroundMusic() {
        mediaPlayer.stop();
    }
}
