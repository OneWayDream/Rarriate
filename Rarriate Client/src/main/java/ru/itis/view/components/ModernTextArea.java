package ru.itis.view.components;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import ru.itis.utils.FontLoader;

public class ModernTextArea extends TextArea {
    public ModernTextArea() {
//        super();
        setDefaultStyle();
        setTextFont();
    }

    private void setTextFont() {
        setFont(FontLoader.getTextFont(17));
    }

    private void setDefaultStyle() {
        setStyle("-fx-background-color: transparent; -fx-background-image: url('img/components/transparent_text_area.png')");
    }
}
