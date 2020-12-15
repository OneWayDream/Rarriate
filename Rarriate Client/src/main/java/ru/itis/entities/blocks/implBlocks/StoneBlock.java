package ru.itis.entities.blocks.implBlocks;

import ru.itis.entities.blocks.AbstractBlock;
import ru.itis.utils.TextureLoader;

public class StoneBlock extends AbstractBlock {
    public StoneBlock() {
        super();
        fillRectangle(TextureLoader.getStoneTexture());
        id = 1;
    }
}
