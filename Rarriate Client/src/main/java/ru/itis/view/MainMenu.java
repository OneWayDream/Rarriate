package ru.itis.view;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import ru.itis.RarriateApplication;
import ru.itis.entities.Inventory;
import ru.itis.entities.Map;
import ru.itis.entities.World;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.entities.player.implPlayers.Player;
import ru.itis.utils.FileLoader;
import ru.itis.utils.MediaLoader;
import ru.itis.utils.PropertiesLoader;
import ru.itis.view.components.ModernButton;
import ru.itis.view.components.ModernLabel;
import ru.itis.view.components.ModernTextField;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class MainMenu {

    private int sceneWidth, sceneHeight;

    //TODO создавать сцены и вызывать setScene();, а не удалять и создавать по новой

    private Stage mainStage;
    private VBox mainPane;
    private Scene mainScene;

    private static MediaPlayer mediaPlayer;

    private PropertiesLoader propertiesLoader;
    private ViewManager viewManager;

    private ModernTextField nameField;
    private ModernTextField ipField;
    private ModernTextField portField;

    public MainMenu(Stage stage, ViewManager viewManager){
        mainStage = stage;
        this.viewManager = viewManager;

        //Init size of scene
        propertiesLoader = PropertiesLoader.getInstance();

        if (mainStage.getScene() != null){
            sceneWidth = (int) mainStage.getScene().getWidth();
            sceneHeight = (int) mainStage.getScene().getHeight();
        } else {
            sceneWidth = Integer.parseInt(propertiesLoader.getProperty("WINDOW_WIDTH"));
            sceneHeight = Integer.parseInt(propertiesLoader.getProperty("WINDOW_HEIGHT"));
        }

        //Init layout
        mainPane = new VBox(30);
        mainPane.setAlignment(Pos.TOP_CENTER);

        mainScene = new Scene(mainPane, sceneWidth, sceneHeight);

        //Add nodes
        createBackground();
        playBackgroundMusic();

        setMainMenuScene();
    }

    public Scene getMainScene() {
        return mainScene;
    }

    private void setMainMenuScene() {
        mainPane.getChildren().clear();

        createLogo();
        createStartSinglePlayerButton();
        createStartMultiPlayerButton();
        createExitButton();
    }

    private void setSinglePlayerScene() {
        mainPane.getChildren().clear();

        createLogo();
        createNameField();
        createEnterSinglePlayerButton();
        createBackButton();
    }

    private void setChooseMultiPlayerScene() {
        mainPane.getChildren().clear();

        createLogo();

        ModernButton hostButton = new ModernButton("Host");
        hostButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setMultiPlayerHostScene();
            }
        });

        ModernButton connectButton = new ModernButton("Connect");
        connectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setMultiPlayerConnectScene();
            }
        });

        mainPane.getChildren().addAll(hostButton, connectButton);
        createBackButton();
    }

    private void setMultiPlayerHostScene() {
        mainPane.getChildren().clear();

        createLogo();
        createNameField();
        createStartMultiPlayerHostButton();
        createBackButton();
    }


    private void setMultiPlayerConnectScene() {
        mainPane.getChildren().clear();

        createLogo();
        createNameField();
        createIPField();
        createPortField();
        createEnterMultiPlayerConnectButton();
        createBackButton();
    }

    private void createLogo() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("img/Rarriate.png");
        Image logo = new Image(is);
        ImageView imageView = new ImageView(logo);
        mainPane.getChildren().add(imageView);
        VBox.setMargin(imageView, new Insets(100, 0, 50, 0));
    }

    private void createNameField(){
        ModernLabel nameLabel = new ModernLabel("Name: ");
        VBox.setMargin(nameLabel, new Insets(0, 0, -25, 0));
        nameField = new ModernTextField();
        nameField.setMaxWidth(190);
        nameField.setText(propertiesLoader.getProperty("PLAYER_NAME"));
        mainPane.getChildren().addAll(nameLabel, nameField);
    }

    private void createStartSinglePlayerButton(){
        ModernButton startButton = new ModernButton("Singleplayer");
        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setSinglePlayerScene();
            }
        });
        mainPane.getChildren().add(startButton);
    }

    private void createStartMultiPlayerButton(){
        ModernButton multiplayer = new ModernButton("Multiplayer");
        multiplayer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setChooseMultiPlayerScene();
            }
        });
        mainPane.getChildren().add(multiplayer);
    }

    private void createExitButton(){
        ModernButton exitButton = new ModernButton("Exit");
        exitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });
        mainPane.getChildren().add(exitButton);
    }

    //SinglePlayer
    private void createEnterSinglePlayerButton() {
        ModernButton enter = new ModernButton("Enter");
        enter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveName();
                stopPlayingBackgroundMusic();
                viewManager.setGameScene();
            }
        });
        mainPane.getChildren().add(enter);
    }


    //MultiPlayer

    private void createIPField() {
        ModernLabel ipLabel = new ModernLabel("IP:");
        VBox.setMargin(ipLabel, new Insets(0,0,-25,0));
        ipField = new ModernTextField();
        ipField.setMaxWidth(190);
        mainPane.getChildren().addAll(ipLabel, ipField);
    }

    private void createPortField() {
        ModernLabel portLabel = new ModernLabel("Port:");
        VBox.setMargin(portLabel, new Insets(0,0,-25,0));
        portField = new ModernTextField();
        portField.setMaxWidth(190);
        mainPane.getChildren().addAll(portLabel, portField);
    }

    private void createEnterMultiPlayerConnectButton() {
        ModernButton enter = new ModernButton("Enter");
        enter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int port = Integer.parseInt(portField.getText());
                Player player = new Player();
                World world = RarriateApplication.connectToServer(new InetSocketAddress(ipField.getText(), port), player);
                viewManager.setMultiPlayerScene(world, player, port);
            }
        });
        mainPane.getChildren().add(enter);
    }

    private void createStartMultiPlayerHostButton() {
        ModernButton startServer = new ModernButton("Run server");
        startServer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                World world = new World(new Map(mainScene.getHeight()), new ArrayList<AbstractPlayer>());
                Player player = new Player();
                int port = RarriateApplication.startServer(world, player);
                viewManager.setMultiPlayerScene(world, player, port);
            }
        });
        mainPane.getChildren().add(startServer);
    }

    private void createBackButton() {
        ModernButton back = new ModernButton("Back");
        back.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setMainMenuScene();
            }
        });
        mainPane.getChildren().add(back);
    }



    private void saveName() {
        propertiesLoader.setProperty("PLAYER_NAME", nameField.getText());
    }

    private void createBackground() {
        mainPane.setBackground(new Background(FileLoader.getMainMenuBackground()));
    }

    private void playBackgroundMusic(){
        mediaPlayer = MediaLoader.getMainMenuBackgroundMusic();
        double musicVolume = Double.parseDouble(propertiesLoader.getProperty("MUSIC_VOLUME"))/100;
        mediaPlayer.setVolume(musicVolume);
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        mediaPlayer.play();
    }

    private void stopPlayingBackgroundMusic(){
        mediaPlayer.stop();
    }
}
