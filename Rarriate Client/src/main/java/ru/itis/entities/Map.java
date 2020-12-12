package ru.itis.entities;

import ru.itis.entities.blocks.Block;
import ru.itis.entities.blocks.implBlocks.BedrockBlock;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.blocks.implBlocks.GrassBlock;
import ru.itis.entities.blocks.implBlocks.StoneBlock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Map implements Serializable {
    private List<Block> blocks;

    public Map(double height) {
        blocks = new ArrayList<>();
        generateBlocks(height);
    }

    public Map(List<Block> blocks) {
        this.blocks = blocks;
    }

    public List<Block> getBlocks () {
        return blocks;
    }


    //1 - bedrock, 2 - bedrock, 3 - stone, 4 - stone, 5 - dirt, 6 - dirt, 7 - grass
    private void generateBlocks(double height) {
        double y = height - (height % Block.HEIGHT);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 40; j++) {
                //TODO bedrock
                BedrockBlock bb = new BedrockBlock();
                bb.setTranslateX(Block.WIDTH * j);
                bb.setTranslateY(y);
                blocks.add(bb);
            }
            y -= Block.HEIGHT;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 40; j++) {
                StoneBlock sb = new StoneBlock();
                sb.setTranslateX(Block.WIDTH * j);
                sb.setTranslateY(y);
                blocks.add(sb);
            }
            y -= Block.HEIGHT;
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 40; j++) {
                DirtBlock db = new DirtBlock();
                db.setTranslateX(Block.WIDTH * j);
                db.setTranslateY(y);
                blocks.add(db);
            }
            y -= Block.HEIGHT;
        }

        for (int j = 0; j < 40; j++) {
            GrassBlock gb = new GrassBlock();
            gb.setTranslateX(Block.WIDTH * j);
            gb.setTranslateY(y);
            blocks.add(gb);
        }
        y -= Block.HEIGHT;
    }
}
