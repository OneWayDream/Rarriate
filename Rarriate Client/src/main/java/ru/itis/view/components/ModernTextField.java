package ru.itis.view.components;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import ru.itis.utils.FontLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ModernTextField extends TextField {
    public ModernTextField(){
        setFieldStyle();
        setFieldFont();
    }

    private void setFieldStyle() {
        setStyle("-fx-background-color: transparent; -fx-background-image: url('img/components/yellow_text_field.png'); -fx-background-size: 100%;");
        setPrefHeight(45);
    }


    private void setFieldFont() {
        setFont(FontLoader.getDefaultFont());
    }
}
