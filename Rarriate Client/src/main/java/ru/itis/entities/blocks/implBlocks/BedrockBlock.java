package ru.itis.entities.blocks.implBlocks;

import ru.itis.entities.blocks.AbstractBlock;
import ru.itis.utils.TextureLoader;

public class BedrockBlock extends AbstractBlock {
    public BedrockBlock() {
        super();
        fillRectangle(TextureLoader.getBedrockTexture());
        id = 4;
        breakable = false;
    }
}
