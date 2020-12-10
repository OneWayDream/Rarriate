package ru.itis.utils;

import ru.itis.client.SocketClient;
import ru.itis.exceptions.*;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.UDPFrame;

import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;

public class RarriateClientKeyManager implements ClientKeyManager {

    @Override
    public void read(SocketClient socketClient, SelectionKey key) throws KeyManagerException, ClientDisconnectException {
        if (socketClient.getClientUDPChannel().equals(key.channel())){
            try{
                UDPFrame receivedUDPFrame = socketClient.getUdpFrameFactory().readUDPFrame((DatagramChannel) key.channel());
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
        } else {
            try{
                TCPFrame tcpFrame = socketClient.getTcpFrameFactory().readTCPFrame(socketClient.getClientSocketChannel());
                switch (tcpFrame.getType()){
                    //TODO обработка TCP - пакетов
                }
            } catch (TCPFrameFactoryException ex) {
                throw new KeyManagerException(ex.getMessage(), ex);
            } catch (IncorrectFCSException ex) {
                //TODO reaction on incorrect frame
            }catch (IllegalBlockingModeException ex){
                throw new ClientDisconnectException(key);
            }
        }
    }

    @Override
    public void write(SocketClient socketClient) throws KeyManagerException {
        //**
    }
}
