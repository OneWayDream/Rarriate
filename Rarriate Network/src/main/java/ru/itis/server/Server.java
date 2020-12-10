package ru.itis.server;

import ru.itis.exceptions.ServerException;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public interface Server {
    public void start(InetSocketAddress tcpAddress, InetSocketAddress udpAddress) throws ServerException;
    void sendTCPFrame(TCPFrame tcpFrame, SocketChannel destinationChannel) throws ServerException;
    void sendUDPFrame(UDPFrame udpFrame, InetSocketAddress destinationAddress) throws ServerException;
    void sendBroadcastTCP(TCPFrame tcpFrame) throws ServerException;
    void sendBroadcastTCP (TCPFrame tcpFrame, SocketChannel excludedChannel) throws ServerException;
    void sendBroadcastUDP (UDPFrame udpFrame) throws ServerException;
    void sendBroadcastUDP (UDPFrame udpFrame, InetSocketAddress excludedAddress) throws ServerException;
    void stop();
}
