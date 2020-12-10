package ru.itis.utils;

import javafx.scene.image.Image;
import ru.itis.exceptions.ImageFileNotFoundException;
import ru.itis.start.RarriateStart;

import java.util.Objects;

public class TextureLoader {
    public static Image getDirtTexture() {
        try {
            return new Image(Objects.requireNonNull(TextureLoader.class
                    .getClassLoader()
                    .getResourceAsStream("img/blocks/dirt_block.jpg")));
        } catch (NullPointerException e) {
            RarriateStart.showError(new ImageFileNotFoundException("Can't find dirt_block.jpg file", e));
        }
        return null;
    }

    public static Image getGrassTexture() {
        try {
            return new Image(Objects.requireNonNull(TextureLoader.class
                    .getClassLoader()
                    .getResourceAsStream("img/blocks/grass_block.jpg")));
        } catch (NullPointerException e) {
            RarriateStart.showError(new ImageFileNotFoundException("Can't find grass_block.jpg file", e));
        }
        return null;
    }

    public static Image getPlayer1Texture() {
        try {
            return new Image(Objects.requireNonNull(TextureLoader.class
                    .getClassLoader()
                    .getResourceAsStream("img/players/player1_stay.png")));
        } catch (NullPointerException e) {
            RarriateStart.showError(new ImageFileNotFoundException("Can't find player1_stay.png file", e));
        }
        return null;
    }
}
