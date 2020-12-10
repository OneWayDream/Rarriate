package ru.itis.view.components;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import ru.itis.utils.FontLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ModernLabel extends Label {
    public ModernLabel(String text) {
        setText(text);
        setTextFont();
    }

    private void setTextFont() {
        setFont(FontLoader.getDefaultFont());
    }
}
