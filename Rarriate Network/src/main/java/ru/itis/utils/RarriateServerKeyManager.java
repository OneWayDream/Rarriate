package ru.itis.utils;

import ru.itis.exceptions.*;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;
import ru.itis.server.SocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class RarriateServerKeyManager implements ServerKeyManager {

    @Override
    public void register(SocketServer socketServer) throws KeyManagerException {

        System.out.println("Кто-то подключился, иду проверять");

        SocketChannel client = null;
        try {
            client = socketServer.getServerTCPChannel().accept();

            System.out.println("Подключил клиента");

            TCPFrame tcpFrame = socketServer.getTcpFrameFactory().readTCPFrame(client);

            System.out.println("Принял пакет с информацией");
            if (tcpFrame!=null){
                if (tcpFrame.getType()==1){
                    Object[] userData = tcpFrame.getContent();
                    String username = (String) userData[1];
                    boolean isUniqueNickname = true;
                    for (ClientEntry clientEntry: socketServer.getClientSet()) {
                        if (clientEntry.getNickname().equals(username)){
                            isUniqueNickname = false;
                            break;
                        }
                    }
                    if (isUniqueNickname){
                        UUID clientUUID = null;
                        boolean isUniqueUuid = false;
                        while (!isUniqueUuid){
                            clientUUID = UUID.randomUUID();
                            for (ClientEntry clientEntry: socketServer.getClientSet()) {
                                if (clientEntry.getUuid().equals(clientUUID)){
                                    break;
                                }
                            }
                            isUniqueUuid = true;
                        }

                        System.out.println("Проверил, всё ок");

                        UUID responseFrameId = UUID.randomUUID();
                        TCPFrame tcpFrameResponse = socketServer.getTcpFrameFactory().createTCPFrame(2,
                                responseFrameId, clientUUID, socketServer.getServerUDPChannel().getLocalAddress(),
                                socketServer.getServerUuid());
                        socketServer.getTcpFrameFactory().writeTCPFrame(client, tcpFrameResponse);

                        System.out.println("Отправил пакет с настройками");

                        ClientEntry clientEntry = RarriateClientEntry.builder()
                                .player((Player) userData[3])
                                .uuid(clientUUID)
                                .datagramAddress((InetSocketAddress) userData[2])
                                .nickname(username)
                                .socketChannel(client)
                                .build();
                        socketServer.getClientSet().add(clientEntry);


                        client.configureBlocking(false);
                        client.register(socketServer.getSelector(), SelectionKey.OP_READ);

                        System.out.println("Поставил клиента на прослушку");

                    } else {
                        TCPFrame existNickname = socketServer.getTcpFrameFactory().createTCPFrame(3);
                        socketServer.getTcpFrameFactory().writeTCPFrame(client, existNickname);
                        closeConnection(client);
                    }
                } else {
                    closeConnection(client);
                }
            }
        } catch (IOException|TCPFrameFactoryException ex) {
            closeConnection(client);
        }  catch (IncorrectFCSException e) {
            //TODO reaction on incorrect frame
        }
    }

    private void closeConnection(SocketChannel connection){
        try{
            if (connection!=null){
                connection.close();
            }
        } catch (IOException e) {
            //ignore
        }
    }

    @Override
    public void read(SocketServer socketServer, SelectionKey key) throws KeyManagerException, ClientDisconnectException {
        if (socketServer.getServerUDPChannel().equals(key.channel())){
            try{
                UDPFrame receivedUDPFrame = socketServer.getUdpFrameFactory().readUDPFrame((DatagramChannel) key.channel());
                if (receivedUDPFrame!=null){
                    Object[] messageContent = receivedUDPFrame.getContent();
                    UUID clientUUID = (UUID) messageContent[0];
                    ClientEntry client = null;
                    for (ClientEntry clientEntry: socketServer.getClientSet()){
                        if (clientEntry.getUuid().equals(clientUUID)){
                            client = clientEntry;
                        }
                    }
                    switch (receivedUDPFrame.getType()){
                        //TODO обработка UDP - пакетов
                        case 0:
                            int moveX = (int) messageContent[1];
                            int moveY = (int) messageContent[2];

                            System.out.println("Player " + client.getNickname() + " moved to (" + moveX + ", " + moveY + ").");

                            break;
                    }
                }
            } catch (UDPFrameFactoryException ex) {
                throw new KeyManagerException(ex.getMessage(), ex);
            } catch (IncorrectFCSException ex){
                //ignore
            }
        } else {
            try{
                SocketChannel client = (SocketChannel) key.channel();
                TCPFrame tcpFrame = socketServer.getTcpFrameFactory().readTCPFrame(client);
                switch (tcpFrame.getType()){
                    //TODO обработка TCP - пакетов
                }
            } catch (TCPFrameFactoryException ex) {
                throw new KeyManagerException(ex.getMessage(), ex);
            } catch (IncorrectFCSException ex) {
                //TODO reaction on incorrect frame
            } catch (IllegalBlockingModeException ex){
                throw new ClientDisconnectException(key);
            }
        }
    }

    @Override
    public void write(SocketServer socketServer) throws KeyManagerException {
        //**
    }
}
