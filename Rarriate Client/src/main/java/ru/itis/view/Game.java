package ru.itis.view;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.itis.RarriateApplication;
import ru.itis.entities.Map;
import ru.itis.entities.World;
import ru.itis.entities.blocks.Block;
import ru.itis.entities.blocks.implBlocks.BedrockBlock;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.blocks.implBlocks.GrassBlock;
import ru.itis.entities.blocks.implBlocks.StoneBlock;
import ru.itis.entities.items.AbstractItem;
import ru.itis.entities.items.implItems.DirtBlockItem;
import ru.itis.entities.items.implItems.GrassBlockItem;
import ru.itis.entities.items.implItems.StoneBlockItem;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.entities.player.implPlayers.Player;
import ru.itis.exceptions.ClientException;
import ru.itis.network.dto.BlockDto;
import ru.itis.protocol.TCPFrame;
import ru.itis.utils.FileLoader;
import ru.itis.utils.MediaLoader;
import ru.itis.utils.PropertiesLoader;
import ru.itis.utils.TextureLoader;
import ru.itis.view.components.ModernButton;
import ru.itis.view.components.ModernText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Game {

    protected static final int SPEED = 10;
    protected static final int FALLING_SPEED = SPEED /2;
    protected static final int MESSAGE_COUNT = 5;

    protected Stage mainStage;
    protected Scene mainScene;
    protected Pane mainPane;
    protected ModernText chat;
    protected Pane inventoryPane;

    protected AbstractPlayer player;
    protected List<AbstractPlayer> players;
    protected ImageView inventorySprite;
    protected List<Block> blocks;

    protected ViewManager viewManager;

    protected MediaPlayer mediaPlayer;

    protected World world;

    protected String[] messages;
    protected int fillMessages;

    protected Integer port;

    protected AnimationTimer timer;

    protected boolean up;
    protected boolean down;
    protected boolean left;
    protected boolean right;

    //SinglePlayer game
    public Game(Stage stage, ViewManager viewManager){
        createGUI(stage, viewManager);
    }

    //MultiPlayer game
    public Game(Stage stage, ViewManager viewManager, World world, AbstractPlayer player, int port) {
        this.player = player;
        this.world = world;
        this.players = world.getPlayers();
        this.port = port;
        createGUI(stage, viewManager);
    }

    protected void createGUI(Stage stage, ViewManager viewManager) {
        if (players == null) {
            players = new ArrayList<>();
        }
        mainStage = stage;
        this.viewManager = viewManager;
        mainPane = new Pane();
        mainScene = new Scene(mainPane, mainStage.getWidth(), mainStage.getHeight());

        mainScene.setOnKeyPressed(e -> processKey(e.getCode(), true));
        mainScene.setOnKeyReleased(e -> processKey(e.getCode(), false));

        setBackground();
        generateLevel();
        createPlayer();
        createInventory();
        playGameBackgroundMusic();
        setChat();

        addListeners();

        addPlayers(players);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    protected void addListeners() {
        addMainSceneClickListener();
        addEscKeyListener();
//        addMovingKeyListener();
    }

//    protected void addMovingKeyListener() {
//        mainScene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                switch (event.getCode()) {
//                    case A:
//                        if (player.getTranslateX() > 0) {
//                            player.moveX(-SPEED);
//                        }
//                        break;
//                    case W:
//                        if (player.getTranslateY() >= SPEED) {
//                            jumpPlayer(player);
//                        }
//                        break;
//                    case D:
//                        if (player.getTranslateX() + player.getWidth() <= mainScene.getWidth()) {
//                            player.moveX(SPEED);
//                        }
//                        break;
//                }
//
//            }
//        });
//    }

    protected void addEscKeyListener() {
        Pane escapePane = new Pane();
        escapePane.setBackground(new Background(FileLoader.getEscapeBackground()));
        escapePane.setPrefSize(mainStage.getWidth(),mainStage.getHeight());
        ModernButton exit = createExitToMainMenuButton();
        exit.setTranslateX((mainStage.getWidth()-200)/2);
        exit.setTranslateY((mainStage.getHeight()-60)/2);
        escapePane.getChildren().add(exit);
        mainScene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    if (mainPane.getChildren().contains(escapePane)) {
                        mainPane.getChildren().remove(escapePane);
                    } else {
                        mainPane.getChildren().add(escapePane);
                    }
                }
            }
        });
    }

    protected void addPlayers(List<AbstractPlayer> players) {
        for (AbstractPlayer player: players) {
            if (!player.getName().equals(this.player.getName())) {
                mainPane.getChildren().add(player);
            }
        }
    }

    protected void addMainSceneClickListener() {
        mainScene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                boolean isBlock;
                for (Block block: blocks) {
                    if (Math.abs(player.getTranslateX() - block.getTranslateX()) <= 150 &&
                            Math.abs(player.getTranslateY() - block.getTranslateY()) <= 150) {
                        if (block.getBoundsInParent().intersects(event.getX(), event.getY(), 1, 1)) {
                            if (block.isBreakable()) {
                                removeBlockAndAddToInventory(block);
                            }
                            return;
                        }
                    }
                }
                if (Math.abs(player.getTranslateX() - event.getX()) <= 150 &&
                        Math.abs(player.getTranslateY() - event.getY()) <= 150) {
                    setBlockFromInventory(event.getX(), event.getY());
                }
            }
        });
    }

    protected void update() {
        if (up && player.getTranslateY() >= SPEED) {
            jumpPlayer(player);
        }
        if (left && player.getTranslateX() > 0) {
            movePlayerX(-SPEED, player);
        }

        if (right && player.getTranslateX() + player.getWidth() <= mainScene.getWidth()) {
            movePlayerX(SPEED, player);
        }

        if (player.getVelocity().getY() < 10) {
            player.setVelocity(player.getVelocity().add(0, 1));
        }

        movePlayerY((int)player.getVelocity().getY(), player);

    }


    //1 - addNewPlayer, 2 - movePlayer, ....
    public void updatePlayer(int type, String name, double x, double y) {
        AbstractPlayer player = null;
        switch (type) {
            case 1:
                addNewPlayer(name);
                break;
            case 2:
                for (AbstractPlayer abstractPlayer : players) {
                    if (abstractPlayer.getName().equals(name) && !abstractPlayer.getName().equals(this.player.getName())) {
                        abstractPlayer.setTranslateX(x);
                        abstractPlayer.setTranslateY(y);
                    }
                }
                break;
            default:
                break;
        }
    }

    //1 -add, 2-delete
    public void updateBlocks(int type, int id, double x, double y) {
        switch (type) {
            case 1:
                setBlock(id, x, y);
                break;
            case 2:
                removeBlock(x,y);
                break;
            default:
                break;
        }
    }

    protected void addNewPlayer(String name) {
        AbstractPlayer newPlayer = new Player(name);
        players.add(newPlayer);
        createPlayer(newPlayer);
    }

    protected void createPlayer() {
        if (player == null) {
            player = new Player();
        }
        if ((world.getPlayers() != null) && (!world.getPlayers().contains(player))) {
            world.getPlayers().add(player);
        }
        player.setTranslateX((mainScene.getWidth() - player.getWidth())/2);
        player.setTranslateY((mainScene.getHeight() - player.getHeight())/2);
        mainPane.getChildren().add(player);
    }

    protected void createPlayer(AbstractPlayer player) {
        player.setTranslateX((mainScene.getWidth() - player.getWidth())/2);
        player.setTranslateY((mainScene.getHeight() - player.getHeight())/2);
        mainPane.getChildren().add(player);
        addChatMessage(player.getName() + " has been connected");
    }

    protected void deletePlayer(String name) {
        for (AbstractPlayer player: players) {
            if (player.getName().equals(name)) {
                mainPane.getChildren().remove(player);
                players.remove(player);
                addChatMessage(player.getName() + " has been disconnected");
                return;
            }
        }
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
            if (RarriateApplication.getClient() != null) {
                try {
                    RarriateApplication.getClient().sendUDPFrame(
                            RarriateApplication.getClient().getUdpFrameFactory().createUDPFrame(0,
                                    RarriateApplication.getClient().getClientUuid(), player.getTranslateX(), player.getTranslateY())
                    );
                } catch (ClientException e) {
                    System.out.println("X");
                    System.err.println(e.getMessage());
                }
            }
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
            if (RarriateApplication.getClient() != null) {
                try {
                    RarriateApplication.getClient().sendUDPFrame(
                            RarriateApplication.getClient().getUdpFrameFactory().createUDPFrame(0,
                                    RarriateApplication.getClient().getClientUuid(), player.getTranslateX(), player.getTranslateY())
                    );
                } catch (ClientException e) {
                    System.out.println("Y");
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    protected void processKey(KeyCode code, boolean on) {
        switch (code) {
            case A:
                left = on ;
                player.setAnimation(AbstractPlayer.RUN_LEFT);
                if (!on) {
                    player.setAnimation(AbstractPlayer.IDLE);
                }
                break ;
            case D:
                right = on;
                player.setAnimation(AbstractPlayer.RUN_RIGHT);
                if (!on) {
                    player.setAnimation(AbstractPlayer.IDLE);
                }
                break;
            case W:
                up = on;
                break;
            case S:
                down = on;
                break;
        }
    }

    public Scene getGameScene() {
        return mainScene;
    }

    protected void generateLevel() {
        if (world == null) {
            world = new World(new Map(mainScene.getHeight()), null);
        }
        blocks = world.getMap().getBlocks();
        for (Block block: blocks) {
            setBlock(block);
        }
    }

    protected void removeBlock(double x, double y) {
        for (Block block: blocks) {
            if (block.getBoundsInParent().intersects(x+1,y,1,1)) {
                blocks.remove(block);
                mainPane.getChildren().remove(block);
                break;
            }
        }
    }

    protected void removeBlockAndAddToInventory(Block block) {
        player.getInventory().addItem(getItemFromBlock(block));
        mainPane.getChildren().remove(block);
        blocks.remove(block);
        updateInventory();
        if (RarriateApplication.getClient() != null) {
            try {
                RarriateApplication.getClient().sendTCPFrame(
                        RarriateApplication.getClient().getTcpFrameFactory().createTCPFrame(5,
                                UUID.randomUUID(), BlockDto.from(block))
                );
            } catch (ClientException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    protected void setBlockFromInventory(double x, double y) {
        if (player.getInventory().getItems().size() > 0) {
            Block block = getBlockFromItem(player.getInventory().getItems().get(0));
            player.getInventory().getItems().remove(0);
            block.setTranslateX(x - (x % Block.WIDTH));
            block.setTranslateY(y - (y % Block.HEIGHT));
            blocks.add(block);
            setBlock(block);
            updateInventory();
            if (RarriateApplication.getClient() != null) {
                try {
                    RarriateApplication.getClient().sendTCPFrame(
                            RarriateApplication.getClient().getTcpFrameFactory().createTCPFrame(7,
                                    UUID.randomUUID(), BlockDto.from(block))
                    );
                } catch (ClientException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    protected void createInventory() {
        inventorySprite = new ImageView(TextureLoader.getInventoryImage());
        inventorySprite.setFitHeight(100);
        inventorySprite.setFitWidth(800);
        inventorySprite.setTranslateX(10);
        inventorySprite.setTranslateY(10);
        mainPane.getChildren().add(inventorySprite);

        inventoryPane = new Pane();
        mainPane.getChildren().add(inventoryPane);
    }

    protected void updateInventory() {
        List<AbstractItem> items = player.getInventory().getItems();
        AbstractItem item;

        mainPane.getChildren().remove(inventoryPane);
        inventoryPane = new Pane();
        inventoryPane.setTranslateX(inventorySprite.getTranslateX() + (double) AbstractItem.WIDTH/2);
        inventoryPane.setTranslateY(inventorySprite.getTranslateY() + (double) AbstractItem.HEIGHT/2);

        double offsetX = 0;
        for (int i = 0; i < items.size() && i < 8; i++) {
            item = items.get(i);
            item.setTranslateX(offsetX/2);
            item.setTranslateY((inventorySprite.getTranslateY()-10)/2);
            inventoryPane.getChildren().add(item);
            offsetX += AbstractItem.WIDTH * 4;
        }
        mainPane.getChildren().add(inventoryPane);
    }

    protected AbstractItem getItemFromBlock(Block block) {
        switch (block.getBlockId()) {
            case 1:
                return new StoneBlockItem();
            case 2:
                return new DirtBlockItem();
            case 3:
                return new GrassBlockItem();
            default:
                return null;
        }
    }

    protected Block getBlockFromItem(AbstractItem item) {
        switch (item.getItemId()) {
            case 1:
                return new StoneBlock();
            case 2:
                return new DirtBlock();
            case 3:
                return new GrassBlock();
            default:
                return null;
        }
    }

    protected ModernButton createExitToMainMenuButton() {
        ModernButton exit = new ModernButton("EXIT");
        exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timer.stop();
                RarriateApplication.disconnect();
                port = null;
                exitToMainMenu();
            }
        });
        return exit;
    }

    protected void setBlock(Block block) {
        mainPane.getChildren().add(block);
    }

    protected void setBlock(int id, double x, double y) {
        Block block = null;

        switch (id) {
            case 1:
                block = new StoneBlock();
                break;
            case 2:
                block = new DirtBlock();
                break;
            case 3:
                block = new GrassBlock();
                break;
            case 4:
                block = new BedrockBlock();
                break;
            default:
                return;
        }
        block.setTranslateX(x);
        block.setTranslateY(y);
        blocks.add(block);
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

    protected void setChat() {
        messages = new String[MESSAGE_COUNT];
        chat = new ModernText();
        chat.setTranslateX(20);
        chat.setTranslateY(mainScene.getHeight() - 200);
        chat.setFill(Color.WHITE);
        mainPane.getChildren().add(chat);

            addChatMessage("Port: " + port);
        }
    }

    public void addChatMessage(String message) {
        if (fillMessages < messages.length) {
            chat.setText("");
            messages[fillMessages] = message;
            fillMessages++;
            for (int j = 0; j < fillMessages; j++) {
                chat.setText(chat.getText() + messages[j] + "\n");
            }
        } else {
            chat.setText("");
            messages = Arrays.copyOf(Arrays.copyOfRange(messages, 1, messages.length), messages.length);
            messages[messages.length-1] = message;
            for (String string : messages) {
                chat.setText(chat.getText() + string + "\n");
            }
        }
    }

    protected void stopPlayingBackgroundMusic() {
        mediaPlayer.stop();
    }
}
