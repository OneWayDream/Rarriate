package ru.itis.network.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entities.blocks.AbstractBlock;
import ru.itis.entities.blocks.implBlocks.*;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.blocks.implBlocks.StoneBlock;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockDto implements Serializable {

    protected double coordX;
    protected double coordY;
    // 1- DirtBlock
    protected int type;

    public static BlockDto from(AbstractBlock abstractBlock){
        int blockType = - 1;
        if (abstractBlock instanceof StoneBlock){
            blockType = 1;
        } else if (abstractBlock instanceof DirtBlock){
            blockType = 2;
        } else if (abstractBlock instanceof GrassBlock){
            blockType = 3;
        } else if (abstractBlock instanceof BedrockBlock){
            blockType = 4;
        }
        return BlockDto.builder()
                .coordX(abstractBlock.getTranslateX())
                .coordY(abstractBlock.getTranslateY())
                .type(blockType)
                .build();
    }

    public static List<BlockDto> from(List<AbstractBlock> abstractBlocks){
        return abstractBlocks.stream()
                .map(BlockDto::from)
                .collect(Collectors.toList());
    }

    public static AbstractBlock to (BlockDto blockDto){
        AbstractBlock result;
        switch (blockDto.getType()){
            case 1:
                result = new StoneBlock();
                result.setTranslateX(blockDto.getCoordX());
                result.setTranslateY(blockDto.getCoordY());
                break;
            case 2:
                result = new DirtBlock();
                result.setTranslateX(blockDto.getCoordX());
                result.setTranslateY(blockDto.getCoordY());
                break;
            case 3:
                result = new GrassBlock();
                result.setTranslateX(blockDto.getCoordX());
                result.setTranslateY(blockDto.getCoordY());
                break;
            case 4:
                result = new BedrockBlock();
                result.setTranslateX(blockDto.getCoordX());
                result.setTranslateY(blockDto.getCoordY());
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

    public static List<AbstractBlock> to (List<BlockDto> blockDtos){
        return blockDtos.stream()
                .map(BlockDto::to)
                .collect(Collectors.toList());
    }

}
