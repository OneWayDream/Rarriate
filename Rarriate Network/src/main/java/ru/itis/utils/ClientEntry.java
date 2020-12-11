package ru.itis.utils;

import ru.itis.entities.player.AbstractPlayer;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public interface ClientEntry {
    UUID getUuid();
    SocketChannel getSocketChannel();
    InetSocketAddress getDatagramAddress();
    AbstractPlayer getPlayer();
}
