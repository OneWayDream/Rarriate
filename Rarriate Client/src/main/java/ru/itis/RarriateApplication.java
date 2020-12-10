package ru.itis;

import ru.itis.client.Client;
import ru.itis.client.SocketClient;
import ru.itis.entities.World;
import ru.itis.protocol.RarriateTCPFrameFactory;
import ru.itis.protocol.RarriateUDPFrameFactory;
import ru.itis.server.Server;
import ru.itis.server.SocketServer;
import ru.itis.start.RarriateStart;
import ru.itis.utils.RarriateClientKeyManager;
import ru.itis.utils.RarriateServerKeyManager;

public class RarriateApplication {
    protected static Client client;
    protected static Server server;

    public static Client getClient() {
        return client;
    }

    public static Server getServer() {
        return server;
    }

    public static void main(String[] args) {
        RarriateStart.main(args);
    }

    public static void startServer(World world){
        server = SocketServer.init(new RarriateServerKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 2048, 64, 0),
                new RarriateTCPFrameFactory((byte) 0XCC, (byte) 0xDD, 2048, 64, 0)
        );
        Runnable serverThread = () -> {
            //server.start();
        };
        new Thread(serverThread).start();
        client = SocketClient.init(new RarriateClientKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 2048, 64, 0),
                new RarriateTCPFrameFactory((byte) 0XCC, (byte) 0xDD, 2048, 64, 0)
        );
        Runnable clientThread = () -> {
//            client.connect();
        };
        new Thread(serverThread).start();
    }

    public static void connectToServer(){

    }
}