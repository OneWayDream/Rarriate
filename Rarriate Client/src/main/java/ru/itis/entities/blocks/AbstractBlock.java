package ru.itis.entities.blocks;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;


public abstract class AbstractBlock extends Rectangle {

    public static final double HEIGHT = 50;
    public static final double WIDTH = 50;

    protected int id;
    protected boolean breakable;

    public AbstractBlock(){
        super(WIDTH, HEIGHT);
        breakable = true;
        id = -1;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public int getBlockId() {
        return id;
    }

    protected void fillRectangle(Image image) {
        setFill(new ImagePattern(image));
    }

    @Override
    public String toString() {
        return "AbstractBlock{" +
                "id=" + id +
                ", breakable=" + breakable +
                '}';
    }
}
