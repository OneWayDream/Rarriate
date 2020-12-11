package ru.itis.utils;

import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import ru.itis.exceptions.ImageFileNotFoundException;
import ru.itis.start.RarriateStart;

import java.io.InputStream;


public class FileLoader {

    public static Image getIcon() {
        try {
            return new Image(FileLoader
                    .class
                    .getClassLoader()
                    .getResource("img/Rarriate-icon.png")
                    .toString());
        } catch (NullPointerException e){
            RarriateStart.showError(new ImageFileNotFoundException("Can't find Rarriate-icon.png file", e));
        }
        return null;
    }

    public static BackgroundImage getMainMenuBackground() {
        try {
            InputStream is = FileLoader.class.getClassLoader().getResourceAsStream("img/menu_background.png");
            Image backgroundImage = new Image(is);
            return new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null);
        } catch (NullPointerException e){
            RarriateStart.showError(new ImageFileNotFoundException("Can't find menu_background.png file", e));
        }
        return null;
    }

    public static BackgroundImage getGameBackground() {
        try {
            InputStream is = FileLoader.class.getClassLoader().getResourceAsStream("img/game_background.png");
            Image backgroundImage = new Image(is);
            return new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null);
        } catch (NullPointerException e){
            RarriateStart.showError(new ImageFileNotFoundException("Can't find game_background.png file", e));
        }
        return null;
    }
    public static BackgroundImage getChatBackground() {
        try {
            InputStream is = FileLoader.class.getClassLoader().getResourceAsStream("img/components/transparent_text_area.png");
            Image backgroundImage = new Image(is);
            return new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null);
        } catch (NullPointerException e){
            RarriateStart.showError(new ImageFileNotFoundException("Can't find chat_background.png file", e));
        }
        return null;
    }
}
