package ru.itis.network.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entities.blocks.Block;
import ru.itis.entities.blocks.implBlocks.BedrockBlock;
import ru.itis.entities.blocks.implBlocks.DirtBlock;
import ru.itis.entities.blocks.implBlocks.GrassBlock;
import ru.itis.entities.blocks.implBlocks.StoneBlock;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.entities.player.implPlayers.Player;

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

    public static BlockDto from(Block block){
        int blockType = - 1;
        if (block instanceof StoneBlock){
            blockType = 1;
        } else if (block instanceof DirtBlock){
            blockType = 2;
        } else if (block instanceof GrassBlock){
            blockType = 3;
        } else if (block instanceof BedrockBlock){
            blockType = 4;
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

    public static Block to (BlockDto blockDto){
        Block result;
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

    public static List<Block> to (List<BlockDto> blockDtos){
        return blockDtos.stream()
                .map(BlockDto::to)
                .collect(Collectors.toList());
    }

}
