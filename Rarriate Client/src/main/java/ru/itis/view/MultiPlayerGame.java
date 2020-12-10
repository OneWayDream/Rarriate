package ru.itis.view;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ru.itis.entities.World;
import ru.itis.entities.player.AbstractPlayer;

import java.util.ArrayList;
import java.util.List;

public class MultiPlayerGame extends Game{

    protected ViewManager viewManager;
    protected Stage mainStage;
    protected Pane mainPane;

    protected World world;

    public MultiPlayerGame(Stage stage, ViewManager viewManager, World world) {
        super(stage, viewManager);
        this.world = world;
        mainPane = new Pane();
        generateLevel();

    }

}
