package ru.itis.utils;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import ru.itis.exceptions.SoundFileNotFoundException;
import ru.itis.start.RarriateStart;

public class MediaLoader {
    public static AudioClip getClickSound(){
        try {
            return new AudioClip(MediaLoader
                    .class
                    .getClassLoader()
                    .getResource("sound/click.mp3")
                    .toString());
        } catch (NullPointerException e){
            RarriateStart.showError(new SoundFileNotFoundException("Can't find click.mp3 file", e));
        }
        return null;
    }

    public static MediaPlayer getMainMenuBackgroundMusic() {
        try {
            return new MediaPlayer(new Media(MediaLoader
                    .class
                    .getClassLoader()
                    .getResource("sound/menuBackgroundMusic.mp3")
                    .toString()));
        } catch (NullPointerException e){
            RarriateStart.showError(new SoundFileNotFoundException("Can't find menuBackgroundMusic.mp3 file", e));
        }
        return null;
    }

    public static MediaPlayer getGameBackgroundMusic() {
        try {
            return new MediaPlayer(new Media(MediaLoader
                    .class
                    .getClassLoader()
                    .getResource("sound/gameBackgroundMusic.mp3")
                    .toString()));
        } catch (NullPointerException e){
            RarriateStart.showError(new SoundFileNotFoundException("Can't find gameBackgroundMusic.mp3 file", e));
        }
        return null;
    }
}
