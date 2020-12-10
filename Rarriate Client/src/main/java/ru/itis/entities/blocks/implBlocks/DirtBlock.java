package ru.itis.entities.blocks.implBlocks;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import ru.itis.entities.blocks.Block;
import ru.itis.utils.TextureLoader;

import java.util.Objects;

public class DirtBlock extends Block {
    public DirtBlock() {
        super();
        fillRectangle(TextureLoader.getDirtTexture());
    }

}
