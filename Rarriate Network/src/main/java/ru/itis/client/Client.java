package ru.itis.client;

import ru.itis.exceptions.ClientException;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;
import ru.itis.utils.Player;

import java.net.InetSocketAddress;

public interface Client {
    void connect(InetSocketAddress serverAddress, InetSocketAddress clientUDPSocket,
                 Player playerData, String username) throws ClientException;
    void sendTCPFrame (TCPFrame tcpFrame) throws ClientException;
    void sendUDPFrame (UDPFrame udpFrame) throws ClientException;
    void disconnect();
}
