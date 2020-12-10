package ru.itis.utils;

import ru.itis.client.SocketClient;
import ru.itis.exceptions.ClientDisconnectException;
import ru.itis.exceptions.KeyManagerException;

import java.nio.channels.SelectionKey;

public interface ClientKeyManager {
    void read(SocketClient socketClient, SelectionKey key) throws KeyManagerException, ClientDisconnectException;
    void write(SocketClient socketClient) throws KeyManagerException;
}
