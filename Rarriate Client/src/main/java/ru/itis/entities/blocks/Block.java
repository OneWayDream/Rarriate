package ru.itis.entities.blocks;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

public abstract class Block extends Rectangle {

    public static final double HEIGHT = 50;
    public static final double WIDTH = 50;

    protected int id;
    protected boolean breakable;

    public Block(){
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
}
