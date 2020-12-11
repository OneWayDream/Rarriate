package ru.itis.network;

import ru.itis.exceptions.ClientException;
import ru.itis.network.client.RarriateClient;
import ru.itis.network.protocol.RarriateTCPFrameFactory;
import ru.itis.network.protocol.RarriateUDPFrameFactory;
import ru.itis.network.utils.RarriateClientKeyManager;

import java.net.InetSocketAddress;

public class TestClient {
    public static void main(String[] args) throws Exception {
        RarriateClient client = RarriateClient.init(
                new RarriateClientKeyManager(),
                new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 1200, 127, 0),
                new RarriateTCPFrameFactory((byte) 0xCC, (byte) 0xDD, 1200, 127, 0),
                null
        );
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 5467);
        InetSocketAddress udpGetAddress = new InetSocketAddress("localhost", 5470);
        InetSocketAddress udpSendAddress = new InetSocketAddress("localhost", 5471);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    client.connect(serverAddress, udpGetAddress);
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(3000);
        System.out.println("----------------");
        Integer x = 5;
        Integer y = 10;
        client.sendUDPFrame(client.getUdpFrameFactory().createUDPFrame(0, client.getClientUuid(),x, y));
        Thread.sleep(2000);
        client.disconnect();
    }
}
