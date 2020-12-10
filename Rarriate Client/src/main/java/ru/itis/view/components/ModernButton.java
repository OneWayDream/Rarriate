package ru.itis.view.components;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import ru.itis.utils.FontLoader;
import ru.itis.utils.MediaLoader;

public class ModernButton extends Button {

    private AudioClip click;

    public ModernButton(String text){
        click = MediaLoader.getClickSound();
        setText(text);
        setButtonFont();
        setPrefHeight(49);
        setPrefWidth(190);
        setButtonDefaultStyle();
        initButtonListeners();
    }

    private void setButtonFont() {
        setFont(FontLoader.getDefaultFont());
    }

    private void setButtonDefaultStyle(){
        setStyle("-fx-background-color: transparent; -fx-background-image: url('img/components/yellow_button.png'); -fx-background-size: 100%;");
        setPrefHeight(49);
        setLayoutY(getLayoutY()-4);
    }

    private void initButtonListeners(){

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setCursor(Cursor.HAND);
                setEffect(new DropShadow());
            }
        });

        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setCursor(Cursor.NONE);
                setEffect(null);
            }
        });


        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                click.play();
            }
        });
    }
}
