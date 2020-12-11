package ru.itis.entities;

import ru.itis.entities.blocks.Block;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.blocks.implBlocks.GrassBlock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Map implements Serializable {
    private List<Block> blocks;

    public Map() {
        blocks = new ArrayList<>();
        generateBlocks();
    }

    public List<Block> getBlocks () {
        return blocks;
    }

    private void generateBlocks() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 40; j++) {
                DirtBlock dirtBlock = new DirtBlock();
                dirtBlock.setTranslateX(dirtBlock.getWidth() * j);
                dirtBlock.setTranslateY(1080 - (dirtBlock.getHeight() * i));
                blocks.add(dirtBlock);
            }
        }
        GrassBlock grassBlock = new GrassBlock();
        grassBlock.setTranslateX(50);
        grassBlock.setTranslateY(800);
        blocks.add(grassBlock);
    }
}
