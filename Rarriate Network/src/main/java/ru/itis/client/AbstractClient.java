package ru.itis.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.itis.exceptions.*;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.TCPFrameFactory;
import ru.itis.protocol.UDPFrame;
import ru.itis.protocol.UDPFrameFactory;
import ru.itis.utils.ClientKeyManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class AbstractClient implements Client {

    protected Selector selector;
    protected SocketChannel clientSocketChannel;
    protected DatagramChannel clientUDPChannel;
    protected InetSocketAddress serverUDPAddress;
    protected ClientKeyManager keyManager;
    protected UDPFrameFactory udpFrameFactory;
    protected TCPFrameFactory tcpFrameFactory;
    protected UUID clientUuid;
    protected UUID serverUuid;
    protected boolean isWork;

    @Override
    public void sendTCPFrame(TCPFrame tcpFrame) throws ClientException {
        try {
            tcpFrameFactory.writeTCPFrame(clientSocketChannel, tcpFrame);
        } catch (TCPFrameFactoryException ex) {
            throw new ClientException(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendUDPFrame(UDPFrame udpFrame) throws ClientException {
        try {
            udpFrameFactory.writeUDPFrame(clientUDPChannel, udpFrame, serverUDPAddress);
        } catch (UDPFrameFactoryException ex) {
            throw new ClientException(ex.getMessage(), ex);
        }
    }

    @Override
    public void disconnect() {
        isWork = false;
        try{
            clientSocketChannel.close();
            clientUDPChannel.close();
            selector.close();
        } catch (IOException ex){
            //ignore
        }
    }

}
