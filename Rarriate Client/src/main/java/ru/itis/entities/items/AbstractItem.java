package ru.itis.entities.items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.itis.utils.FileLoader;

import java.io.Serializable;

public abstract class AbstractItem extends ImageView implements Serializable {
    protected final static int WIDTH = 32;
    protected final static int HEIGHT = 32;

    protected int id;
    protected int count;
    protected Image sprite;

    public AbstractItem() {
        count = 0;
        setFitWidth(WIDTH);
        setFitHeight(HEIGHT);
    }

    protected void setSprite(Image image) {
        setImage(image);
    }


    public int getItemId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void add() {
        count++;
    }

    public void remove() {
        count--;
    }

}
