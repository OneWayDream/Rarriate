package ru.itis.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entities.World;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldDto {

    protected List<PlayerDto> players;
    protected List<BlockDto> blocks;

    public static WorldDto from (World world){
        return WorldDto.builder()
                .players(PlayerDto.from(world.getPlayers()))
                .blocks(BlockDto.from(world.getMap().getBlocks()))
                .build();
    }

}
