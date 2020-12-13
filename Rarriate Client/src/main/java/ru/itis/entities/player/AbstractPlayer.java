package ru.itis.entities.player;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;
import ru.itis.entities.Inventory;
import ru.itis.entities.items.implItems.StoneBlockItem;
import ru.itis.utils.MyPoint2D;

import java.io.Serializable;


public abstract class AbstractPlayer extends Rectangle implements Serializable {

    public final static int IDLE = 0;
    public final static int RUN_RIGHT = 1;
    public final static int RUN_LEFT = 2;
    private final static int STATE_COUNT = 3;
    protected static final double HEIGHT = 50;
    protected static final double WIDTH = 50;
    private final static int INVENTORY_COUNT = 10;

    protected String name;

    protected boolean canJump;
    protected MyPoint2D velocity;
    protected Integer state;
    protected Inventory inventory;

    public AbstractPlayer(String name) {
        super(WIDTH, HEIGHT);
        this.name = name;
        canJump = true;
        velocity = new MyPoint2D(0,0);
        state = 0;
        inventory = new Inventory();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public Point2D getVelocity() {
        return velocity.getPoint();
    }

    public void setVelocity(Point2D velocity) {
        this.velocity.setPoint(velocity);
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

    public void moveX(double value){
        setTranslateX(getTranslateX() + value);
    }

    public void moveY(double value) {
        setTranslateY(getTranslateY() + value);
    }

    public void setAnimation(int state) {
        if (state >= 0 && state < STATE_COUNT) {
            this.state = state;
        }
    }

    protected abstract void startAnimation();

    @Override
    public String toString() {
        return "AbstractPlayer{" +
                "name='" + name + '\'' +
                ", canJump=" + canJump +
                ", velocity=" + velocity +
                ", state=" + state +
                ", inventory=" + inventory +
                '}';
    }
}
