package ru.itis.utils;

import lombok.*;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class RarriateClientEntry implements ClientEntry {

    protected String nickname;
    protected UUID uuid;
    protected SocketChannel socketChannel;
    protected InetSocketAddress datagramAddress;
    protected Player player;

}
