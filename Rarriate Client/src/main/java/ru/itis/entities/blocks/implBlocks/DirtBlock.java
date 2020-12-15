package ru.itis.entities.blocks.implBlocks;

import ru.itis.entities.blocks.AbstractBlock;
import ru.itis.utils.TextureLoader;

public class DirtBlock extends AbstractBlock {
    public DirtBlock() {
        super();
        fillRectangle(TextureLoader.getDirtTexture());
        id = 2;
    }

}
