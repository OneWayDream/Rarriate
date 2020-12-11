package ru.itis;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Test {
    public static void main(String[] args) throws Throwable {
        //UDP write - all good
//        UDPFrameFactory udpFrameFactory = new RarriateUDPFrameFactory((byte) 0xAA, (byte) 0xBB, 1024, 0, 64);
//        UDPFrame udpFrame = udpFrameFactory.createUDPFrame(5, "Hello");
//        udpFrameFactory.writeUDPFrame(null, udpFrame, null, 1);
//        DatagramChannel serverUDPSocket = DatagramChannel.open();
//        serverUDPSocket.bind(new InetSocketAddress("localhost", 5555));
//        System.out.println(serverUDPSocket.getLocalAddress().toString());
        InetSocketAddress address = new InetSocketAddress("localhost", 5555);
//        ServerSocketChannel serverTCPChannel = ServerSocketChannel.open();
//        serverTCPChannel.bind(address);
        SocketChannel socketChannel = SocketChannel.open(address);
    }

}
