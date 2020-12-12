package ru.itis.network.utils;

import ru.itis.client.AbstractClient;
import ru.itis.exceptions.*;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;
import ru.itis.utils.ClientKeyManager;

import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;

public class RarriateClientKeyManager implements ClientKeyManager {

    @Override
    public void read(AbstractClient client, SelectionKey key) throws KeyManagerException, ClientDisconnectException {
        if (client.getClientSocketChannel().equals(key.channel())){
            try{
                TCPFrame tcpFrame = client.getTcpFrameFactory().readTCPFrame(client.getClientSocketChannel());

                System.out.println("Получил пакет, а вообще не должен -_-");

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
        } else {
            try{
                UDPFrame receivedUDPFrame = client.getUdpFrameFactory().readUDPFrame((DatagramChannel) key.channel());
                if (receivedUDPFrame!=null){
                    switch (receivedUDPFrame.getType()){
                        //TODO обработка UDP - пакетов
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
