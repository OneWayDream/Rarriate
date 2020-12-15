package ru.itis.network.utils;

import javafx.application.Platform;
import ru.itis.RarriateApplication;
import ru.itis.client.AbstractClient;
import ru.itis.entities.blocks.Block;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.exceptions.*;
import ru.itis.network.client.RarriateClient;
import ru.itis.network.dto.BlockDto;
import ru.itis.network.dto.PlayerDto;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;
import ru.itis.utils.ClientKeyManager;

import java.nio.BufferUnderflowException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.util.UUID;

public class RarriateClientKeyManager implements ClientKeyManager {

    @Override
    public void read(AbstractClient client, SelectionKey key) throws KeyManagerException, ClientDisconnectException {
        if (client.getClientSocketChannel().equals(key.channel())){
            try{
                TCPFrame tcpFrame = client.getTcpFrameFactory().readTCPFrame(client.getClientSocketChannel());
                Object[] messageContent = tcpFrame.getContent();
                switch (tcpFrame.getType()){
                    case 4:
                        PlayerDto playerDto = (PlayerDto) tcpFrame.getContent()[1];
                        ((RarriateClient) client).getWorld().getPlayers().add(PlayerDto.to(playerDto));
                        Platform.runLater(()->
                                        RarriateApplication.getGame().updatePlayer(1, playerDto.getName(), playerDto.getCoordX(), playerDto.getCoordY())
                                );
                        //RarriateApplication.getGame().updatePlayer(1, playerDto.getName(), playerDto.getCoordX(), playerDto.getCoordY());
                        break;
                    case 6:
                        BlockDto blockDto = (BlockDto) messageContent[1];
                        ((RarriateClient) client).getWorld().getMap().getBlocks().remove(BlockDto.to(blockDto));
                        Platform.runLater(()->
                                        RarriateApplication.getGame().updateBlocks(2,blockDto.getType(), blockDto.getCoordX(), blockDto.getCoordY())
                                );
                        //RarriateApplication.getGame().updateBlocks(2,blockDto.getType(), blockDto.getCoordX(), blockDto.getCoordY());
                        break;
                    case 8:
                        BlockDto blockDto1 = (BlockDto) messageContent[1];
                        ((RarriateClient) client).getWorld().getMap().getBlocks().add(BlockDto.to(blockDto1));
                        Platform.runLater(()->
                                        RarriateApplication.getGame().updateBlocks(1,blockDto1.getType(), blockDto1.getCoordX(), blockDto1.getCoordY())
                                );
                        //RarriateApplication.getGame().updateBlocks(2,blockDto1.getType(), blockDto1.getCoordX(), blockDto1.getCoordY());
                        break;
                    case 10:
                        //TODO write message in the chat
                        break;
                }
            } catch (TCPFrameFactoryException ex) {
                throw new KeyManagerException(ex.getMessage(), ex);
            } catch (IncorrectFCSException ex) {
                //TODO reaction on incorrect frame
            } catch (IllegalBlockingModeException | BufferUnderflowException ex){
                throw new ClientDisconnectException(key);
            }
        } else {
            try{
                UDPFrame receivedUDPFrame = client.getUdpFrameFactory().readUDPFrame((DatagramChannel) key.channel());
                if (receivedUDPFrame!=null){
                    Object[] messageContent = receivedUDPFrame.getContent();
                    UUID frameUuid = (UUID) messageContent[0];
                    if (frameUuid.equals(client.getServerUuid())){
                        switch (receivedUDPFrame.getType()){
                            case 1:
                                String playerName = (String) messageContent[1];
                                for (AbstractPlayer abstractPlayer: ((RarriateClient) client).getWorld().getPlayers()){
                                    if (abstractPlayer.getName().equals(playerName)){
                                        abstractPlayer.setTranslateX((double) messageContent[2]);
                                        abstractPlayer.setTranslateY((double) messageContent[3]);
                                        //System.out.println(RarriateApplication.getGame()==null);
                                        Platform.runLater(()->
                                                RarriateApplication.getGame().updatePlayer(2, abstractPlayer.getName(), abstractPlayer.getTranslateX(), abstractPlayer.getTranslateY())
                                                );
                                        //RarriateApplication.getGame().updatePlayer(2, abstractPlayer.getName(), abstractPlayer.getTranslateX(), abstractPlayer.getTranslateY());
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }
            } catch (UDPFrameFactoryException ex) {
                throw new KeyManagerException(ex.getMessage(), ex);
            } catch (IncorrectFCSException ex){
                //ignore
            }
        }
    }

    @Override
    public void write(AbstractClient client) throws KeyManagerException {
        //**
    }
}
