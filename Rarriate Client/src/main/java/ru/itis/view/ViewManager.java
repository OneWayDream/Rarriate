package ru.itis.view;

import javafx.stage.Stage;
import ru.itis.entities.World;

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

    public void setMultiPlayerScene(World world) {
        boolean fullscreen = mainStage.isFullScreen();
        mainStage.setScene(new Game(mainStage, this, world).getGameScene());
        if (fullscreen){
            mainStage.setFullScreen(true);
        }
    }
}
