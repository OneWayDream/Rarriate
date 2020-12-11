package ru.itis;

import ru.itis.client.Client;
import ru.itis.client.SocketClient;
import ru.itis.entities.World;
import ru.itis.exceptions.ClientException;
import ru.itis.exceptions.ClientWorkException;
import ru.itis.exceptions.ServerException;
import ru.itis.exceptions.ServerWorkException;
import ru.itis.protocol.RarriateTCPFrameFactory;
import ru.itis.protocol.RarriateUDPFrameFactory;
import ru.itis.server.Server;
import ru.itis.server.SocketServer;
import ru.itis.start.RarriateStart;
import ru.itis.utils.RarriateClientKeyManager;
import ru.itis.utils.RarriateServerKeyManager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class RarriateApplication {

    protected static int minPortValue = 5000;
    protected static int maxPortValue = 5100;

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
        client = SocketClient.init(new RarriateClientKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 2048, 64, 0),
                new RarriateTCPFrameFactory((byte) 0XCC, (byte) 0xDD, 2048, 64, 0)
        );
        InetSocketAddress clientUDPAddress = getUniqueAddress();
        Runnable clientThread = () -> {
            //TODO set player data
            try {
                client.connect(serverTCPAddress, clientUDPAddress, null);
            } catch (ClientException ex) {
                throw new ClientWorkException(ex.getMessage(), ex);
            }
        };
        new Thread(serverThread).start();
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