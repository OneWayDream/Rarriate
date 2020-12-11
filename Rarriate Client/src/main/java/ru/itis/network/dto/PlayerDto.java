package ru.itis.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.entities.player.implPlayers.Player;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDto implements Serializable {

    protected String name;
    protected double coordX;
    protected double coordY;

    public static PlayerDto from (AbstractPlayer player){
        return PlayerDto.builder()
                .name(player.getName())
                .coordX(player.getTranslateX())
                .coordY(player.getTranslateY())
                .build();
    }

    public static List<PlayerDto> from (List<AbstractPlayer> players){
        return players.stream()
                .map(PlayerDto::from)
                .collect(Collectors.toList());
    }

    public static AbstractPlayer to (PlayerDto playerDto){
//        return new Player(playerDto.getName());
        return null;
    }

}
