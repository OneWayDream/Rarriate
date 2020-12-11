package ru.itis.network.utils;

import ru.itis.utils.ClientEntry;

import lombok.*;
import ru.itis.entities.player.AbstractPlayer;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class RarriateClientEntry implements ClientEntry {

    protected UUID uuid;
    protected SocketChannel socketChannel;
    protected InetSocketAddress datagramAddress;
    protected AbstractPlayer player;

}