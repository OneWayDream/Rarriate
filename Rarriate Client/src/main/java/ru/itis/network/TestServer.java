package ru.itis.network;

import ru.itis.exceptions.ServerException;
import ru.itis.network.protocol.RarriateTCPFrameFactory;
import ru.itis.network.protocol.RarriateUDPFrameFactory;
import ru.itis.network.server.RarriateServer;
import ru.itis.network.utils.RarriateServerKeyManager;

import java.net.InetSocketAddress;

public class TestServer {
    public static void main(String[] args) throws Exception {
        RarriateServer server = RarriateServer.init(
                new RarriateServerKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 1200, 127, 0),
                new RarriateTCPFrameFactory((byte) 0xCC, (byte) 0xDD, 1200, 127, 0),
                null
        );
        InetSocketAddress tcpAddress = new InetSocketAddress("localhost", 5467);
        InetSocketAddress udpGetAddress = new InetSocketAddress("localhost", 5468);
        Runnable runnable = () ->
        {
            try {
                server.start(tcpAddress, udpGetAddress);
            } catch (ServerException e) {
                System.out.println("Ошибка по причине ошибка");
            }
        };
        new Thread(runnable).start();
        Thread.sleep(10000);
        server.stop();
    }
}
