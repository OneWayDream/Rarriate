package ru.itis.entities.blocks.implBlocks;

import ru.itis.entities.blocks.Block;
import ru.itis.utils.TextureLoader;

public class BedrockBlock extends Block {
    public BedrockBlock() {
        super();
        fillRectangle(TextureLoader.getBedrockTexture());
        id = 4;
        breakable = false;
    }
}
