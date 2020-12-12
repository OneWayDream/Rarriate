package ru.itis.entities.blocks.implBlocks;

import ru.itis.entities.blocks.Block;
import ru.itis.utils.TextureLoader;

public class StoneBlock extends Block {
    public StoneBlock() {
        super();
        fillRectangle(TextureLoader.getStoneTexture());
        id = 1;
    }
}
