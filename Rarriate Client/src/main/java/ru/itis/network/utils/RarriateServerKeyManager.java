package ru.itis.network.utils;

import lombok.extern.slf4j.Slf4j;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.exceptions.*;
import ru.itis.network.dto.PlayerDto;
import ru.itis.network.dto.WorldDto;
import ru.itis.network.server.RarriateServer;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;
import ru.itis.server.AbstractServer;
import ru.itis.utils.ClientEntry;
import ru.itis.utils.ServerKeyManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

@Slf4j
public class RarriateServerKeyManager implements ServerKeyManager {

    @Override
    public void register(AbstractServer server) throws KeyManagerException {

        //System.out.println("Кто-то подключился, иду проверять");

        SocketChannel client = null;
        try {
            client = server.getServerTCPChannel().accept();

            log.info("Было обнаружено новое соединение от клиента " + client.getRemoteAddress() + ".");

            //System.out.println("Подключил клиента");

            TCPFrame tcpFrame = server.getTcpFrameFactory().readTCPFrame(client);

            //System.out.println("Принял пакет с информацией");
            if (tcpFrame!=null){
                if (tcpFrame.getType()==1){
                    Object[] userData = tcpFrame.getContent();
                    AbstractPlayer player = PlayerDto.to((PlayerDto) userData[2]);
                    boolean isUniqueNickname = true;
                    for (ClientEntry clientEntry: server.getClientSet()) {
                        if (((RarriateClientEntry)clientEntry).getPlayer().getName().equals(player.getName())){
                            isUniqueNickname = false;
                            break;
                        }
                    }
                    if (isUniqueNickname){
                        UUID clientUUID = null;
                        boolean isUniqueUuid = false;
                        while (!isUniqueUuid){
                            clientUUID = UUID.randomUUID();
                            for (ClientEntry clientEntry: server.getClientSet()) {
                                if (clientEntry.getUuid().equals(clientUUID)){
                                    break;
                                }
                            }
                            isUniqueUuid = true;
                        }

                        //System.out.println("Проверил, всё ок");


                        UUID responseFrameId = UUID.randomUUID();
                        TCPFrame tcpFrameResponse = server.getTcpFrameFactory().createTCPFrame(2,
                                responseFrameId, clientUUID, server.getServerUDPChannel().getLocalAddress(),
                                server.getServerUuid(), WorldDto.from(((RarriateServer) server).getWorld()));
                        // System.out.println("Отправляю пакет с настройками");
                        server.getTcpFrameFactory().writeTCPFrame(client, tcpFrameResponse);

                        //System.out.println("Отправил пакет с настройками");

                        ClientEntry clientEntry = RarriateClientEntry.builder()
                                .player(PlayerDto.to((PlayerDto) userData[2]))
                                .uuid(clientUUID)
                                .datagramAddress((InetSocketAddress) userData[1])
                                .socketChannel(client)
                                .build();
                        server.getClientSet().add(clientEntry);

                        UUID clientNotification = UUID.randomUUID();
//                        ((RarriateServer) server).getWorld().getPlayers().add(player);

                        server.sendBroadcastTCP(
                                server.getTcpFrameFactory().createTCPFrame(4,
                                        clientNotification, PlayerDto.from(player)),
                                client
                        );


                        client.configureBlocking(false);
                        client.register(server.getSelector(), SelectionKey.OP_READ);

                        //System.out.println("Поставил клиента на прослушку");
                        log.info("Игрок " + player.getName() + " был успешно настроен и подключен к серверу.");

                    } else {
                        TCPFrame existNickname = server.getTcpFrameFactory().createTCPFrame(3);
                        server.getTcpFrameFactory().writeTCPFrame(client, existNickname);
                        closeConnection(client);
                        log.info("Клиент" + client + " был отключен по причине повторного ника.");
                    }
                } else {
                    closeConnection(client);
                    log.info("Клиент" + client + "был отключен из-за неправильного типа пакета.");
                }
            } else {
                closeConnection(client);
                log.info("Клиент" + client + "был отключен из-за неправильного формата пакета.");
            }
        } catch (IOException|TCPFrameFactoryException ex) {
            closeConnection(client);
            log.info("Клиент" + client + "был отключен из-за ошибки соединения");
        }  catch (IncorrectFCSException e) {
            //TODO reaction on incorrect frame
        } catch (ServerException ex){
            log.warn("Не удалось разослать информацию о новом клиенте остальным клиентам.");
            throw new KeyManagerException("Cannot send broadcast to other users", ex);
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
    public void read(AbstractServer server, SelectionKey key) throws KeyManagerException, ClientDisconnectException {
        if (server.getServerUDPChannel().equals(key.channel())){
            try{
                UDPFrame receivedUDPFrame = server.getUdpFrameFactory().readUDPFrame((DatagramChannel) key.channel());
                if (receivedUDPFrame!=null){
                    Object[] messageContent = receivedUDPFrame.getContent();
                    UUID clientUUID = (UUID) messageContent[0];
                    ClientEntry client = null;
                    for (ClientEntry clientEntry: server.getClientSet()){
                        if (clientEntry.getUuid().equals(clientUUID)){
                            client = clientEntry;
                            break;
                        }
                    }
                    if (client!=null){
                        switch (receivedUDPFrame.getType()){
                            case 0:
                                double moveX = (double) messageContent[1];
                                double moveY = (double) messageContent[2];
                                AbstractPlayer clientPlayer = ((RarriateClientEntry) client).getPlayer();
                                clientPlayer.setTranslateX(moveX);
                                clientPlayer.setTranslateY(moveY);
                                server.sendBroadcastUDP(
                                        server.getUdpFrameFactory().createUDPFrame(
                                                1,
                                                server.getServerUuid(),
                                                ((RarriateClientEntry) client).getPlayer().getName(),
                                                moveX, moveY
                                        ),
                                        client.getDatagramAddress()
                                );
                                break;
                        }
                    }
                }
            } catch (UDPFrameFactoryException ex) {
                throw new KeyManagerException(ex.getMessage(), ex);
            } catch (IncorrectFCSException ex){
                //ignore
            } catch (ServerException ex){
                throw new KeyManagerException("Cannot send broadcast to other users", ex);
            }
        } else {
            try{
                SocketChannel client = (SocketChannel) key.channel();
                TCPFrame tcpFrame = server.getTcpFrameFactory().readTCPFrame(client);
                UUID messageUuid = UUID.randomUUID();
                if (tcpFrame!=null){
                    Object[] messageContent = tcpFrame.getContent();
                    switch (tcpFrame.getType()){
                        case 5:
//                            ((RarriateServer) server).getWorld().getMap().getAbstractBlocks()
//                                    .remove(BlockDto.to((BlockDto) messageContent[1]));
                            server.sendBroadcastTCP(
                                    server.getTcpFrameFactory().createTCPFrame(6, messageUuid, messageContent[1]),
                                    client
                            );
                            break;
                        case 7:
//                            ((RarriateServer) server).getWorld().getMap().getAbstractBlocks()
//                                    .add(BlockDto.to((BlockDto) messageContent[1]));
                            server.sendBroadcastTCP(
                                    server.getTcpFrameFactory().createTCPFrame(8, messageUuid, messageContent[1]),
                                    client
                            );
                            break;
                        case 9:
                            for (ClientEntry clientEntry : server.getClientSet()){
                                if ( clientEntry.getSocketChannel().equals(client)){
                                    server.sendBroadcastTCP(
                                            server.getTcpFrameFactory().createTCPFrame
                                                    (10, messageUuid,
                                                            ((RarriateClientEntry) clientEntry).getPlayer().getName(),
                                                            messageContent[1])
                                    );
                                    break;
                                }
                            }
                            break;
                    }
                }
            } catch (TCPFrameFactoryException ex) {
                if (ex.getCause().getMessage().contains("принудительно разорвал существующее подключение")){
                    for (ClientEntry clientEntry : server.getClientSet()){
                        if (clientEntry.getSocketChannel().equals(key.channel())){
                            try{
                                server.sendBroadcastTCP(
                                        server.getTcpFrameFactory().createTCPFrame(
                                                11, UUID.randomUUID(), ((RarriateClientEntry) clientEntry).getPlayer().getName()
                                        ), (SocketChannel) key.channel()
                                );
                            } catch (ServerException broadcastEx) {
                                throw new KeyManagerException("Cannot send broadcast to other users", broadcastEx);
                            }
                        }
                    }
                    throw new ClientDisconnectException(key);
                } else {
                    throw new KeyManagerException(ex.getMessage(), ex);
                }
            } catch (IncorrectFCSException ex) {
                //TODO reaction on incorrect frame
            } catch (IllegalBlockingModeException| BufferUnderflowException ex){
                for (ClientEntry clientEntry : server.getClientSet()){
                    if (clientEntry.getSocketChannel().equals(key.channel())){
                        try{
                            server.sendBroadcastTCP(
                                    server.getTcpFrameFactory().createTCPFrame(
                                            11, UUID.randomUUID(), ((RarriateClientEntry) clientEntry).getPlayer().getName()
                                    ), (SocketChannel) key.channel()
                            );
                        } catch (ServerException broadcastEx) {
                            throw new KeyManagerException("Cannot send broadcast to other users", broadcastEx);
                        }
                    }
                }
                throw new ClientDisconnectException(key);
            } catch (ServerException ex){
                throw new KeyManagerException("Cannot send broadcast to other users", ex);
            }
        }
    }

    @Override
    public void write(AbstractServer server) throws KeyManagerException {
        //**
    }
}