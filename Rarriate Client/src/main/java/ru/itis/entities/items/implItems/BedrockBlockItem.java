package ru.itis.entities.items.implItems;

import ru.itis.entities.items.AbstractItem;
import ru.itis.utils.TextureLoader;

public class BedrockBlockItem extends AbstractItem {
    private final static int ID = 4;

    public BedrockBlockItem() {
        super();
        id = ID;
        setSprite(TextureLoader.getBedrockTexture());
    }
}
