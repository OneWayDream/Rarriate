package ru.itis.entities.player;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import ru.itis.entities.player.IPlayer;


public abstract class AbstractPlayer extends Rectangle{

    protected String name;
    protected static final double HEIGHT = 50;
    protected static final double WIDTH = 50;
    protected boolean canJump;
    protected Point2D velocity;

    public AbstractPlayer(String name) {
        super(WIDTH, HEIGHT);
        this.name = name;
        canJump = true;
        velocity = new Point2D(0,0);
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public boolean isCanJump() {
        return canJump;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    protected void fillPlayer(Image image) {
        setFill(new ImagePattern(image));
    }
}
