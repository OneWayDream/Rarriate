package ru.itis.entities.items.implItems;

import ru.itis.entities.items.AbstractItem;
import ru.itis.utils.TextureLoader;

public class GrassBlockItem extends AbstractItem {
    private final static int ID = 3;

    public GrassBlockItem() {
        super();
        id = ID;
        setSprite(TextureLoader.getGrassTexture());
    }
}
