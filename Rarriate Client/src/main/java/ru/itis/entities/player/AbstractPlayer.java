package ru.itis.entities.player;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import ru.itis.entities.player.IPlayer;


public abstract class AbstractPlayer extends Rectangle implements IPlayer {

    protected String name;
    protected static final double HEIGHT = 100;
    protected static final double WIDTH = 500;


    public AbstractPlayer(String name) {
        super(WIDTH, HEIGHT);
        this.name = name;
    }

    @Override
    public void moveX(int x) {
        setX(getX() + x);
    }

    @Override
    public void moveY(int y) {
        setY(getY() + y);
    }


    protected void fillPlayer(Image image) {
        setFill(new ImagePattern(image));
    }
}
