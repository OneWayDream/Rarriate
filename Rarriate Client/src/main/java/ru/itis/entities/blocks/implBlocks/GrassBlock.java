package ru.itis.entities.blocks.implBlocks;

import ru.itis.entities.blocks.Block;
import ru.itis.utils.TextureLoader;

public class GrassBlock extends Block {
    public GrassBlock() {
        super();
        fillRectangle(TextureLoader.getGrassTexture());
        id = 3;
    }
}
