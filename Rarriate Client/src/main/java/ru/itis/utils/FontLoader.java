package ru.itis.utils;

import javafx.scene.text.Font;

public class FontLoader {
    public static Font getDefaultFont(int size) {
        Font font = Font.loadFont(FontLoader.class.getClassLoader().getResourceAsStream("font/kenvector_future.ttf"), size);
        if (font != null){
            return font;
        } else {
            return Font.font("Verdana", size);
        }
    }

    public static Font getTextFont(int size) {
        Font font = Font.loadFont(FontLoader.class.getClassLoader().getResourceAsStream("font/comic_sans_ms.ttf"), size);
        if (font != null){
            return font;
        } else {
            return Font.font("Verdana", size);
        }
    }
}
