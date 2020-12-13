package ru.itis.network.utils;

import ru.itis.client.AbstractClient;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.exceptions.*;
import ru.itis.network.client.RarriateClient;
import ru.itis.network.dto.BlockDto;
import ru.itis.network.dto.PlayerDto;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;
import ru.itis.utils.ClientKeyManager;

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
                        ((RarriateClient) client).getWorld().getPlayers().add(PlayerDto.to((PlayerDto) tcpFrame.getContent()[1]));
                        break;
                    case 6:
                        ((RarriateClient) client).getWorld().getMap().getBlocks().remove(BlockDto.to((BlockDto) messageContent[1]));
                        break;
                    case 8:
                        ((RarriateClient) client).getWorld().getMap().getBlocks().add(BlockDto.to((BlockDto) messageContent[1]));
                        break;
                    case 10:
                        //TODO write message in the chat
                        break;
                }
            } catch (TCPFrameFactoryException ex) {
                throw new KeyManagerException(ex.getMessage(), ex);
            } catch (IncorrectFCSException ex) {
                //TODO reaction on incorrect frame
            } catch (IllegalBlockingModeException ex){
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
                                        abstractPlayer.setTranslateX((int) messageContent[2]);
                                        abstractPlayer.setTranslateY((int) messageContent[3]);
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
