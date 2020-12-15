package ru.itis.view.components;

import javafx.scene.text.Text;
import ru.itis.utils.FontLoader;

public class ModernText extends Text {
    public ModernText() {
        setTextFont();
    }

    private void setTextFont() {
        setFont(FontLoader.getTextFont(25));
    }
}
