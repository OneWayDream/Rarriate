package ru.itis.network.utils;

import ru.itis.entities.player.AbstractPlayer;
import ru.itis.exceptions.*;
import ru.itis.network.dto.PlayerDto;
import ru.itis.network.dto.WorldDto;
import ru.itis.network.server.RarriateServer;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;
import ru.itis.server.AbstractServer;
import ru.itis.utils.ClientEntry;
import ru.itis.utils.Player;
import ru.itis.utils.ServerKeyManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class RarriateServerKeyManager implements ServerKeyManager {

    @Override
    public void register(AbstractServer server) throws KeyManagerException {

        System.out.println("Кто-то подключился, иду проверять");

        SocketChannel client = null;
        try {
            client = server.getServerTCPChannel().accept();

            System.out.println("Подключил клиента");

            TCPFrame tcpFrame = server.getTcpFrameFactory().readTCPFrame(client);

            System.out.println("Принял пакет с информацией");
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

                        System.out.println("Проверил, всё ок");


                        UUID responseFrameId = UUID.randomUUID();
                        TCPFrame tcpFrameResponse = server.getTcpFrameFactory().createTCPFrame(2,
                                responseFrameId, clientUUID, server.getServerUDPChannel().getLocalAddress(),
                                server.getServerUuid(), WorldDto.from(((RarriateServer) server).getWorld()));
                        System.out.println("Отправляю пакет с настройками");
                        server.getTcpFrameFactory().writeTCPFrame(client, tcpFrameResponse);

                        System.out.println("Отправил пакет с настройками");

                        ClientEntry clientEntry = RarriateClientEntry.builder()
                                .player(PlayerDto.to((PlayerDto) userData[2]))
                                .uuid(clientUUID)
                                .datagramAddress((InetSocketAddress) userData[1])
                                .socketChannel(client)
                                .build();
                        server.getClientSet().add(clientEntry);

                        UUID clientNotification = UUID.randomUUID();
                        ((RarriateServer) server).getWorld().getPlayers().add(player);

                        server.sendBroadcastTCP(
                                server.getTcpFrameFactory().createTCPFrame(4,
                                        clientNotification, PlayerDto.from(player)),
                                client
                        );


                        client.configureBlocking(false);
                        client.register(server.getSelector(), SelectionKey.OP_READ);

                        System.out.println("Поставил клиента на прослушку");

                    } else {
                        TCPFrame existNickname = server.getTcpFrameFactory().createTCPFrame(3);
                        server.getTcpFrameFactory().writeTCPFrame(client, existNickname);
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
        } catch (ServerException ex){
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
                                int moveX = (int) messageContent[1];
                                int moveY = (int) messageContent[2];
                                server.sendBroadcastUDP(
                                        server.getUdpFrameFactory().createUDPFrame(
                                                1,
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
    public void write(AbstractServer server) throws KeyManagerException {
        //**
    }
}