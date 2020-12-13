package ru.itis.view;

import javafx.stage.Stage;
import ru.itis.RarriateApplication;
import ru.itis.entities.World;
import ru.itis.entities.player.implPlayers.Player;

public class ViewManager {
    private Stage mainStage;

    public ViewManager(Stage stage) {
        mainStage = stage;
    }

    public void setMainMenuScene() {
        boolean fullscreen = mainStage.isFullScreen();
        mainStage.setScene(new MainMenu(mainStage, this).getMainScene());
        if (fullscreen){
            mainStage.setFullScreen(true);
        }
    }

    public void setGameScene() {
        boolean fullscreen = mainStage.isFullScreen();
        mainStage.setScene(new Game(mainStage, this).getGameScene());
        if (fullscreen){
            mainStage.setFullScreen(true);
        }
    }

    public void setMultiPlayerScene(World world, Player player, int port) {
        boolean fullscreen = mainStage.isFullScreen();
        Game game = new Game(mainStage, this, world, player, port);
        mainStage.setScene(game.getGameScene());
        RarriateApplication.setGame(game);
        if (fullscreen){
            mainStage.setFullScreen(true);
        }
    }
}
