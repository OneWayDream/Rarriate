package ru.itis;

import ru.itis.entities.World;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.exceptions.ClientException;
import ru.itis.exceptions.ClientWorkException;
import ru.itis.exceptions.ServerException;
import ru.itis.exceptions.ServerWorkException;
import ru.itis.network.client.RarriateClient;
import ru.itis.network.protocol.RarriateTCPFrameFactory;
import ru.itis.network.protocol.RarriateUDPFrameFactory;
import ru.itis.network.server.RarriateServer;
import ru.itis.network.utils.RarriateClientKeyManager;
import ru.itis.network.utils.RarriateServerKeyManager;
import ru.itis.start.RarriateStart;
import ru.itis.view.Game;

import java.io.IOException;
import java.net.*;

public class RarriateApplication {

    protected static int minPortValue = 5000;
    protected static int maxPortValue = 5100;

    protected static RarriateClient client;
    protected static RarriateServer server;
    protected static Game game;

    public static RarriateClient getClient() {
        return client;
    }

    public static RarriateServer getServer() {
        return server;
    }

    public static Game getGame(){
        return game;
    }

    public static void setGame(Game gameG){
        game = gameG;
    }

    public static void main(String[] args) throws SocketException {
        counter = minPortValue;
        RarriateStart.main(args);
    }

    public static int startServer(World world, AbstractPlayer player){
        server = RarriateServer.init(new RarriateServerKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 2048, 64, 0),
                new RarriateTCPFrameFactory((byte) 0XCC, (byte) 0xDD, 2048, 64, 0),
                world
        );

        InetSocketAddress serverTCPAddress = getUniqueAddress();
        InetSocketAddress serverUDPAddress = getUniqueAddress();
//        System.out.println(serverTCPAddress);
//        System.out.println(serverUDPAddress);

        Runnable serverRun = () -> {
            try {
                server.start(serverTCPAddress, serverUDPAddress);
            } catch (ServerException ex) {
                throw new ServerWorkException(ex.getMessage(), ex);
            }
        };
        Thread serverThread = new Thread(serverRun);
        serverThread.setDaemon(true);
        serverThread.start();
        client = RarriateClient.init(new RarriateClientKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 2048, 64, 0),
                new RarriateTCPFrameFactory((byte) 0XCC, (byte) 0xDD, 2048, 64, 0),
                player
        );
        InetSocketAddress clientUDPAddress = getUniqueAddress();
        Runnable clientRun = () -> {
            try {
                client.connect(serverTCPAddress, clientUDPAddress);
            } catch (ClientException ex) {
                throw new ClientWorkException(ex.getMessage(), ex);
            }
        };
        Thread clientThread = new Thread(clientRun);
        clientThread.setDaemon(true);
        clientThread.start();
        try{
            Thread.sleep(3000); //Time to server settings
        } catch (InterruptedException e) {
            //ignore
        }
        return serverTCPAddress.getPort();
    }

    public static World connectToServer(InetSocketAddress serverAddress, AbstractPlayer player){

        client = RarriateClient.init(new RarriateClientKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 2048, 64, 0),
                new RarriateTCPFrameFactory((byte) 0XCC, (byte) 0xDD, 2048, 64, 0),
                player
        );

        InetSocketAddress clientUDPAddress = getUniqueAddress();
        System.out.println(clientUDPAddress);
        Runnable clientRun = () -> {
            try {
                client.connect(serverAddress, clientUDPAddress);
            } catch (ClientException ex) {
                throw new ClientWorkException(ex.getMessage(), ex);
            }
        };
        Thread clientThread = new Thread(clientRun);
        clientThread.setDaemon(true);
        try{
            clientThread.start();
        } catch (ClientWorkException ex){
            RarriateStart.main(null);
        }

        return client.getWorld();

    }

    public static void disconnect(){
        if (server!=null){
            server.stop();
        }
        if (client!=null){
            client.disconnect();
        }
        game = null;
    }

    protected static int counter;

    private static InetSocketAddress getUniqueAddress(){
        InetSocketAddress result = null;
        ServerSocket ss = null;
        DatagramSocket ds = null;
        while ((result==null)&&(counter<=maxPortValue)){
            try{
                ss = new ServerSocket(counter);
                ss.setReuseAddress(true);
                ds = new DatagramSocket(counter);
                ds.setReuseAddress(true);
                return new InetSocketAddress("localhost", counter);
            } catch (IOException e) {
            } finally {
                if (ds != null) {
                    ds.close();
                }
                if (ss != null) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }
                counter++;
            }
        }
        return result;
    }
}