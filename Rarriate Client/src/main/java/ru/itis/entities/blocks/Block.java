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
    protected int id;
    protected static final double HEIGHT = 50;
    protected static final double WIDTH = 50;

    public Block(){
        super(WIDTH, HEIGHT);
        id = -1;
    }

    public int getBlockId() {
        return id;
    }

    protected void fillRectangle(Image image) {
        setFill(new ImagePattern(image));
    }
}
