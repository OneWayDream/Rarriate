package ru.itis.server;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.itis.exceptions.*;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.TCPFrameFactory;
import ru.itis.protocol.UDPFrame;
import ru.itis.protocol.UDPFrameFactory;
import ru.itis.utils.ClientEntry;
import ru.itis.utils.ServerKeyManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class AbstractServer implements Server {

    protected Selector selector;
    protected ServerSocketChannel serverTCPChannel;
    protected DatagramChannel serverUDPChannel;
    protected UUID serverUuid;
    protected ServerKeyManager keyManager;
    protected HashSet<ClientEntry> clientSet;
    protected UDPFrameFactory udpFrameFactory;
    protected TCPFrameFactory tcpFrameFactory;
    protected boolean isWork;

    @Override
    public void start(InetSocketAddress tcpAddress, InetSocketAddress udpGetAddress) throws ServerException {
        try {
            isWork = true;
            selector = Selector.open();

            serverTCPChannel = ServerSocketChannel.open();
            serverTCPChannel.bind(tcpAddress);
            serverTCPChannel.configureBlocking(false);
            serverTCPChannel.register(selector, SelectionKey.OP_ACCEPT);

            serverUDPChannel = DatagramChannel.open();
            serverUDPChannel.bind(udpGetAddress);
            serverUDPChannel.configureBlocking(false);
            serverUDPChannel.register(selector, SelectionKey.OP_READ);

            //System.out.println("Сервер успешно запущен, начата проверка");
        } catch (IOException ex){
            throw new ServerException("Cannot create server: connection exception.", ex);
        }
        while (isWork){
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isAcceptable()) {
                        keyManager.register(this);
                    }
                    if (key.isReadable()) {
                        keyManager.read(this, key);
                    }
                    if (key.isWritable()) {
                        keyManager.write(this);
                    }
                    iterator.remove();
                }
            } catch (IOException ex){
                throw new ServerException("Cannot create server: connection exception.", ex);
            } catch (KeyManagerException ex){
                throw new ServerException(ex.getMessage(), ex);
            } catch (ClientDisconnectException ex){
                ex.getSelectionKey().cancel();
                for (ClientEntry clientEntry: clientSet) {
                    if (clientEntry.getSocketChannel().equals(ex.getSelectionKey().channel())){
                        clientSet.remove(clientEntry);
                    }
                }
                //System.out.println("Client " + ex.getSelectionKey().channel() + " was disconnected");
            } catch (ClosedSelectorException ex){
                //System.out.println("Сервер был успешно выключен");
            }
        }
    }

    @Override
    public void sendTCPFrame(TCPFrame tcpFrame, SocketChannel destinationChannel) throws ServerException {
        try {
            tcpFrameFactory.writeTCPFrame(destinationChannel, tcpFrame);
        } catch (TCPFrameFactoryException ex) {
            throw new ServerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendUDPFrame(UDPFrame udpFrame, InetSocketAddress destinationAddress) throws ServerException {
        try {
            udpFrameFactory.writeUDPFrame(serverUDPChannel, udpFrame, destinationAddress);
        } catch (UDPFrameFactoryException ex) {
            throw new ServerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendBroadcastTCP(TCPFrame tcpFrame) throws ServerException {
        try {
            for (ClientEntry clientEntry : clientSet){
                tcpFrameFactory.writeTCPFrame(clientEntry.getSocketChannel(), tcpFrame);
            }
        } catch (TCPFrameFactoryException ex) {
            throw new ServerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendBroadcastTCP(TCPFrame tcpFrame, SocketChannel excludedChannel) throws ServerException {
        try {
            for (ClientEntry clientEntry : clientSet){
                if (!clientEntry.getSocketChannel().equals(excludedChannel)){
                    tcpFrameFactory.writeTCPFrame(clientEntry.getSocketChannel(), tcpFrame);
                }
            }
        } catch (TCPFrameFactoryException ex) {
            throw new ServerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendBroadcastUDP(UDPFrame udpFrame) throws ServerException {
        try {
            for (ClientEntry clientEntry : clientSet){
                udpFrameFactory.writeUDPFrame(serverUDPChannel, udpFrame,
                        clientEntry.getDatagramAddress());
            }
        } catch (UDPFrameFactoryException ex) {
            throw new ServerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendBroadcastUDP(UDPFrame udpFrame, InetSocketAddress excludedAddress) throws ServerException {
        try {
            for (ClientEntry clientEntry : clientSet){
                if (!clientEntry.getDatagramAddress().equals(excludedAddress)){
                    udpFrameFactory.writeUDPFrame(serverUDPChannel, udpFrame,
                            clientEntry.getDatagramAddress());
                }
            }
        } catch (UDPFrameFactoryException ex) {
            throw new ServerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
        isWork = false;
        try{
            serverTCPChannel.close();
            serverUDPChannel.close();
            selector.close();
        } catch (IOException ex){
            //ignore
        }

    }



}
