package ru.itis.utils;

import javafx.scene.text.Font;

public class FontLoader {
    public static Font getDefaultFont() {
        Font font = Font.loadFont(FontLoader.class.getClassLoader().getResourceAsStream("font/kenvector_future.ttf"), 17);
        if (font != null){
            return font;
        } else {
            return Font.font("Verdana", 17);
        }
    }
}
