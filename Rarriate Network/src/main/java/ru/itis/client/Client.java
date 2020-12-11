package ru.itis.client;

import ru.itis.exceptions.ClientException;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;

import java.net.InetSocketAddress;

public interface Client {
    void connect(InetSocketAddress serverAddress, InetSocketAddress clientUDPAddress) throws ClientException;
    void sendTCPFrame (TCPFrame tcpFrame) throws ClientException;
    void sendUDPFrame (UDPFrame udpFrame) throws ClientException;
    void disconnect();
}
