package ru.itis.entities.blocks.implBlocks;

import ru.itis.entities.blocks.AbstractBlock;
import ru.itis.utils.TextureLoader;

public class GrassBlock extends AbstractBlock {
    public GrassBlock() {
        super();
        fillRectangle(TextureLoader.getGrassTexture());
        id = 3;
    }
}
