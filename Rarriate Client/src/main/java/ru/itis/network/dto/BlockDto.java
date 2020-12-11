package ru.itis.network.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entities.blocks.Block;
import ru.itis.entities.blocks.implBlocks.DirtBlock;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockDto {

    protected double coordX;
    protected double coordY;
    // 1- DirtBlock
    protected int type;

    public static BlockDto from(Block block){
        int blockType = - 1;
        if (block instanceof DirtBlock){
            blockType = 0;
        }
        return BlockDto.builder()
                .coordX(block.getTranslateX())
                .coordY(block.getTranslateY())
                .type(blockType)
                .build();
    }

    public static List<BlockDto> from(List<Block> blocks){
        return blocks.stream()
                .map(BlockDto::from)
                .collect(Collectors.toList());
    }

}
