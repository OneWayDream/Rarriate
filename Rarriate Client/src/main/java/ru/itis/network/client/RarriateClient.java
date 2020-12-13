package ru.itis.network.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.itis.client.AbstractClient;
import ru.itis.entities.World;
import ru.itis.entities.player.AbstractPlayer;
import ru.itis.exceptions.*;
import ru.itis.network.dto.PlayerDto;
import ru.itis.network.dto.WorldDto;
import ru.itis.protocol.TCPFrame;
import ru.itis.protocol.TCPFrameFactory;
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
@SuperBuilder
public class RarriateClient extends AbstractClient {

    protected World world;
    protected AbstractPlayer player;

    public static RarriateClient init(ClientKeyManager keyManager, UDPFrameFactory udpFrameFactory,
                                      TCPFrameFactory tcpFrameFactory, AbstractPlayer player){
        return RarriateClient.builder()
                .keyManager(keyManager)
                .udpFrameFactory(udpFrameFactory)
                .tcpFrameFactory(tcpFrameFactory)
                .player(player)
                .build();
    }

    @Override
    public void connect(InetSocketAddress serverAddress, InetSocketAddress clientUDPAddress) throws ClientException {
        try{
            isWork = true;
            selector = Selector.open();

            clientSocketChannel = SocketChannel.open(serverAddress);

            System.out.println("Успешно подключился к серверу");

            UUID infoFrameId = UUID.randomUUID();
            TCPFrame clientInfoTCPFrame = tcpFrameFactory.createTCPFrame(1, infoFrameId,
                    clientUDPAddress, PlayerDto.from(player));
            tcpFrameFactory.writeTCPFrame(clientSocketChannel, clientInfoTCPFrame);

            System.out.println("Успешно отправил пакет с информацией о себе");

            TCPFrame serverResponseFrame = tcpFrameFactory.readTCPFrame(clientSocketChannel);

            System.out.println("Получил пакет с инфой о сервере");
            if ((serverResponseFrame!=null)&&(serverResponseFrame.getType() == 2)){
                clientUuid = (UUID) serverResponseFrame.getContent()[1];
                serverUDPAddress = (InetSocketAddress) serverResponseFrame.getContent()[2];
                serverUuid = (UUID) serverResponseFrame.getContent()[3];
                world = WorldDto.to((WorldDto) serverResponseFrame.getContent()[4]);

                System.out.println("Донастроился");

                clientSocketChannel.configureBlocking(false);
                clientSocketChannel.register(selector, SelectionKey.OP_READ);

                clientUDPChannel = DatagramChannel.open();
                clientUDPChannel.bind(clientUDPAddress);
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
                System.out.println("Пакет неверен");
                clientSocketChannel.close();
                if ((serverResponseFrame!=null)&&(serverResponseFrame.getType() == 3)){
                    throw new AlreadyRegisteredNameException();
                }
            }
        } catch (IOException ex) {
            throw new ClientException("Cannot connect client", ex);
        }
        catch (KeyManagerException | TCPFrameFactoryException ex){
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
                selector.close();
            } catch (IOException exx){
                //ignore
            }
            System.out.println("Сервер отключился(");
        } catch (ClosedSelectorException ex){
            System.out.println("Клиент успешно отключился");
        }
    }
}
