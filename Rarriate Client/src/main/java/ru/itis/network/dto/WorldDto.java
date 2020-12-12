package ru.itis.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entities.Map;
import ru.itis.entities.World;
import ru.itis.entities.player.AbstractPlayer;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldDto implements Serializable {

    protected List<PlayerDto> players;
    protected List<BlockDto> blocks;

    public static WorldDto from (World world){
        return WorldDto.builder()
                .players(PlayerDto.from(world.getPlayers()))
                .blocks(BlockDto.from(world.getMap().getBlocks()))
                .build();
    }

    public static List<WorldDto> from (List<World> worlds){
        return worlds.stream()
                .map(WorldDto::from)
                .collect(Collectors.toList());
    }

    public static World to (WorldDto worldDto){
        return new World(
                new Map(BlockDto.to(worldDto.getBlocks())),
                PlayerDto.to(worldDto.getPlayers())
        );
    }

    public static List<World> to (List<WorldDto> worldDtos){
        return worldDtos.stream()
                .map(WorldDto::to)
                .collect(Collectors.toList());
    }

}
