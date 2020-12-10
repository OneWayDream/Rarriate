package ru.itis.view;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import ru.itis.entities.blocks.Block;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.blocks.implBlocks.GrassBlock;
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
//    private VBox mainPane;
    private Pane mainPane;

    private Player player;

    private List<Block> blocks;

    private ViewManager viewManager;

    private MediaPlayer mediaPlayer;

    private int speed = 10;
    private int fallingSpeed = speed/2;

    private boolean up ;
    private boolean down ;
    private boolean left ;
    private boolean right ;


    public Game(Stage stage, ViewManager viewManager){
        PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

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

        player.setX((mainStage.getWidth() - player.getWidth())/2);
        player.setY((mainStage.getHeight() - player.getHeight())/2);

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
        if (up && player.getY() >= speed) {
            player.moveY(-speed);
        }

        if (left && player.getX() > 0 && checkLeft()) {
            player.moveX(-speed);
        }

        if (right && player.getX() + player.getWidth() <= mainScene.getWidth()) {
            player.moveX(speed);
        }

        checkBottom();
    }

    private boolean checkLeft() {
        for (Block block : blocks) {
            if (player.getBoundsInParent().intersects(block.getBoundsInParent())) {
                if (player.getX() + player.getWidth() == block.getLayoutX()) {
                    System.out.println(true);
                    return false;
                }
            }
        }
        return true;
    }

    private void checkBottom() {
        for (Block block : blocks) {
            if (player.getBoundsInParent().intersects(block.getBoundsInParent())) {
                if (player.getY() + player.getHeight() == block.getLayoutY()) {
                    return;
                }
            }
        }
        player.setY(player.getY() + fallingSpeed);
    }

    private void jumpPlayer() {

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
        DirtBlock dirtBlock = new DirtBlock();
        blocks.add(dirtBlock);
        setNode(50, (int)(mainScene.getHeight()-dirtBlock.getHeight() - 50), dirtBlock);

        int i = 0;
        while (i*50 < mainScene.getWidth()) {
            DirtBlock block = new DirtBlock();
            setNode(50*i, (int)(mainScene.getHeight()-block.getHeight()), block);
            blocks.add(block);
            i++;
        }
    }

    private void setBackground(){
        mainPane.setBackground(new Background(FileLoader.getGameBackground()));
    }

    private void setNode(int x, int y, Node item) {
        item.setLayoutX(x);
        item.setLayoutY(y);
        mainPane.getChildren().add(item);
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
