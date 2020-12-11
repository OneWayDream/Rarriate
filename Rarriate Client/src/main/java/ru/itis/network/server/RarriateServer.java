package ru.itis.network.server;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.itis.entities.World;
import ru.itis.protocol.TCPFrameFactory;
import ru.itis.protocol.UDPFrameFactory;
import ru.itis.server.AbstractServer;
import ru.itis.utils.ServerKeyManager;

import java.util.HashSet;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class RarriateServer extends AbstractServer {
    protected World world;

    public static RarriateServer init(ServerKeyManager keyManager, UDPFrameFactory udpFrameFactory,
                                    TCPFrameFactory tcpFrameFactory, World world){
        return RarriateServer.builder()
                .keyManager(keyManager)
                .clientSet(new HashSet<>())
                .udpFrameFactory(udpFrameFactory)
                .tcpFrameFactory(tcpFrameFactory)
                .serverUuid(UUID.randomUUID())
                .world(world)
                .build();
    }
}
