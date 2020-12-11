package ru.itis.client;

import lombok.*;
import ru.itis.entities.World;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.exceptions.*;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.TCPFrameFactory;
import ru.itis.protocol.UDPFrame;
import ru.itis.protocol.UDPFrameFactory;
import ru.itis.utils.ClientKeyManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class SocketClient implements Client {

    protected Selector selector;
    protected SocketChannel clientSocketChannel;
    protected DatagramChannel clientUDPChannel;
    protected InetSocketAddress serverUDPAddress;
    protected ClientKeyManager keyManager;
    protected UDPFrameFactory udpFrameFactory;
    protected TCPFrameFactory tcpFrameFactory;
    protected UUID clientUuid;
    protected UUID serverUuid;
    protected World world;
    boolean isWork;

    public static SocketClient init(ClientKeyManager keyManager, UDPFrameFactory udpFrameFactory, TCPFrameFactory tcpFrameFactory){
        return SocketClient.builder()
                .keyManager(keyManager)
                .udpFrameFactory(udpFrameFactory)
                .tcpFrameFactory(tcpFrameFactory)
                .build();
    }

    @Override
    public void connect(InetSocketAddress serverAddress, InetSocketAddress clientUDPGetAddress
            , AbstractPlayer playerData) throws ClientException {
        try{
            isWork = true;
            selector = Selector.open();

            clientSocketChannel = SocketChannel.open(serverAddress);

            System.out.println("Успешно подключился к серверу");

            UUID infoFrameId = UUID.randomUUID();
            TCPFrame clientInfoTCPFrame = tcpFrameFactory.createTCPFrame(1, infoFrameId,
                    clientUDPGetAddress, playerData);
            tcpFrameFactory.writeTCPFrame(clientSocketChannel, clientInfoTCPFrame);

            System.out.println("Успешно отправил пакет с информацией о себе");

            TCPFrame serverResponseFrame = tcpFrameFactory.readTCPFrame(clientSocketChannel);

            System.out.println("Получил пакет с инфой о сервере");
            if (serverResponseFrame.getType() == 2){
                clientUuid = (UUID) serverResponseFrame.getContent()[1];
                serverUDPAddress = (InetSocketAddress) serverResponseFrame.getContent()[2];
                serverUuid = (UUID) serverResponseFrame.getContent()[3];
                world = (World) serverResponseFrame.getContent()[4];

                System.out.println("Донастроился");

                clientSocketChannel.configureBlocking(false);
                clientSocketChannel.register(selector, SelectionKey.OP_READ);

                clientUDPChannel = DatagramChannel.open();
                clientUDPChannel.bind(clientUDPGetAddress);
                clientUDPChannel.configureBlocking(false);
                clientUDPChannel.register(selector, SelectionKey.OP_READ);

                System.out.println("Все каналы прослушиваются");

                while (isWork){
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();

                        if (key.isReadable()){
                            keyManager.read(this, key);
                        }
                        if (key.isWritable()){
                            keyManager.write(this);
                        }
                        iterator.remove();
                    }
                }
            } else {
                clientSocketChannel.close();
                if (serverResponseFrame.getType() == 3){
                    throw new AlreadyRegisteredNameException();
                }
            }
        } catch (IOException ex) {
            throw new ClientException("Cannot connect client", ex);
        } catch (KeyManagerException | TCPFrameFactoryException ex){
            throw new ClientException(ex.getMessage(), ex);
        } catch (IncorrectFCSException ex) {
            //TODO reaction
        } catch (AlreadyRegisteredNameException ex) {
            throw new ClientException("User with such name is already play on this server", ex);
        } catch (ClientDisconnectException ex) {
            ex.getSelectionKey().cancel();
            try{
                clientUDPChannel.close();
                clientSocketChannel.close();
            } catch (IOException exx){
                //ignore
            }
            System.out.println("Сервер отключился(");
        } catch (ClosedSelectorException ex){
            System.out.println("Сервер успешно отключился");
        }
    }

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
