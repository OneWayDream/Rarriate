package ru.itis.entities.player.implPlayers;

import ru.itis.entities.player.AbstractPlayer;
import ru.itis.utils.PropertiesLoader;
import ru.itis.utils.TextureLoader;

public class Player extends AbstractPlayer {
    public Player() {
        super(PropertiesLoader.getInstance().getProperty("PLAYER_NAME"));
        fillPlayer(TextureLoader.getPlayer1Texture());
    }
}
