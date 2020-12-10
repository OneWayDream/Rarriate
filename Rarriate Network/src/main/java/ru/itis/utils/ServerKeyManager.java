package ru.itis.utils;

import ru.itis.exceptions.ClientDisconnectException;
import ru.itis.exceptions.KeyManagerException;
import ru.itis.server.SocketServer;

import java.nio.channels.SelectionKey;

public interface ServerKeyManager {
    void register(SocketServer socketServer) throws KeyManagerException;
    void read(SocketServer socketServer, SelectionKey key) throws KeyManagerException, ClientDisconnectException;
    void write(SocketServer socketServer) throws KeyManagerException;
}
