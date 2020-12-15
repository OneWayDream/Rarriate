package ru.itis.entities;

import ru.itis.entities.blocks.AbstractBlock;
import ru.itis.entities.blocks.implBlocks.*;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.blocks.implBlocks.StoneBlock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Map implements Serializable {
    private List<AbstractBlock> abstractBlocks;

    public Map(double height) {
        abstractBlocks = new ArrayList<>();
        generateBlocks(height);
    }

    public Map(List<AbstractBlock> abstractBlocks) {
        this.abstractBlocks = abstractBlocks;
    }

    public List<AbstractBlock> getAbstractBlocks() {
        return abstractBlocks;
    }


    //1 - bedrock, 2 - bedrock, 3 - stone, 4 - stone, 5 - dirt, 6 - dirt, 7 - grass
    private void generateBlocks(double height) {
        double y = height - (height % AbstractBlock.HEIGHT);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 40; j++) {
                BedrockBlock bb = new BedrockBlock();
                bb.setTranslateX(AbstractBlock.WIDTH * j);
                bb.setTranslateY(y);
                abstractBlocks.add(bb);
            }
            y -= AbstractBlock.HEIGHT;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 40; j++) {
                StoneBlock sb = new StoneBlock();
                sb.setTranslateX(AbstractBlock.WIDTH * j);
                sb.setTranslateY(y);
                abstractBlocks.add(sb);
            }
            y -= AbstractBlock.HEIGHT;
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 40; j++) {
                DirtBlock db = new DirtBlock();
                db.setTranslateX(AbstractBlock.WIDTH * j);
                db.setTranslateY(y);
                abstractBlocks.add(db);
            }
            y -= AbstractBlock.HEIGHT;
        }

        for (int j = 0; j < 40; j++) {
            GrassBlock gb = new GrassBlock();
            gb.setTranslateX(AbstractBlock.WIDTH * j);
            gb.setTranslateY(y);
            abstractBlocks.add(gb);
        }
        y -= AbstractBlock.HEIGHT;
    }
}
