package ru.itis.entities;

import ru.itis.entities.player.AbstractPlayer;
import ru.itis.entities.player.implPlayers.Player;

import java.util.List;

public class World {
    private Map map;
    private List<AbstractPlayer> players;

    public World(Map map, List<AbstractPlayer> players) {
        this.map = map;
        this.players = players;
    }

    public Map getMap() {
        return map;
    }

    public List<AbstractPlayer> getPlayers() {
        return players;
    }
}
