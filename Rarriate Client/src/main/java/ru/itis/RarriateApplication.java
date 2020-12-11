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

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class RarriateApplication {

    protected static int minPortValue = 5000;
    protected static int maxPortValue = 5100;

    protected static RarriateClient client;
    protected static RarriateServer server;

    public static RarriateClient getClient() {
        return client;
    }

    public static RarriateServer getServer() {
        return server;
    }

    public static void main(String[] args) {
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

        Runnable serverThread = () -> {
            try {
                server.start(serverTCPAddress, serverUDPAddress);
            } catch (ServerException ex) {
                throw new ServerWorkException(ex.getMessage(), ex);
            }
        };
        new Thread(serverThread).start();
        client = RarriateClient.init(new RarriateClientKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 2048, 64, 0),
                new RarriateTCPFrameFactory((byte) 0XCC, (byte) 0xDD, 2048, 64, 0),
                player
        );
        InetSocketAddress clientUDPAddress = getUniqueAddress();
        Runnable clientThread = () -> {
            try {
                client.connect(serverTCPAddress, clientUDPAddress);
            } catch (ClientException ex) {
                throw new ClientWorkException(ex.getMessage(), ex);
            }
        };
        new Thread(clientThread).start();
        return serverTCPAddress.getPort();
    }

    public static void connectToServer(){

    }

    private static InetSocketAddress getUniqueAddress(){
        InetSocketAddress result = null;
        int counter = minPortValue;
        while ((result==null)&&(counter<=maxPortValue)){
            try{
                SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", counter));
                socketChannel.close();
            } catch (ConnectException ex){
                result = new InetSocketAddress("localhost", counter);
                break;
            } catch (IOException ex){
                //ignore
            }
            finally {
                counter++;
            }
        }
        return result;
    }
}