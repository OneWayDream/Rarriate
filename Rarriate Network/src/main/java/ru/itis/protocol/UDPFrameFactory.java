package ru.itis.protocol;


import ru.itis.exceptions.IncorrectFCSException;
import ru.itis.exceptions.UDPFrameFactoryException;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public interface UDPFrameFactory {
    UDPFrame createUDPFrame(int messageType, Object ... messageContent);
    UDPFrame readUDPFrame (DatagramChannel datagramChannel) throws UDPFrameFactoryException, IncorrectFCSException;
    void writeUDPFrame (DatagramChannel datagramChannel, UDPFrame udpFrame, InetSocketAddress address)
            throws UDPFrameFactoryException;
}
