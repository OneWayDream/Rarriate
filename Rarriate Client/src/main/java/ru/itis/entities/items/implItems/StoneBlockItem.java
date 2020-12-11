package ru.itis.entities.items.implItems;

import ru.itis.entities.items.AbstractItem;
import ru.itis.utils.FileLoader;
import ru.itis.utils.TextureLoader;

public class StoneBlockItem extends AbstractItem {

    private final static int ID = 1;

    public StoneBlockItem() {
        super();
        id = ID;
        setSprite(TextureLoader.getDirtTexture());
    }
}
