package ru.itis.entities.items.implItems;

import ru.itis.entities.items.AbstractItem;
import ru.itis.utils.TextureLoader;

public class DirtBlockItem extends AbstractItem {
    private final static int ID = 2;

    public DirtBlockItem() {
        super();
        id = ID;
        setSprite(TextureLoader.getDirtTexture());
    }
}
