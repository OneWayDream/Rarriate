package ru.itis.utils;

import ru.itis.exceptions.ClientDisconnectException;
import ru.itis.exceptions.KeyManagerException;
import ru.itis.server.AbstractServer;

import java.nio.channels.SelectionKey;

public interface ServerKeyManager {
    void register(AbstractServer server) throws KeyManagerException;
    void read(AbstractServer server, SelectionKey key) throws KeyManagerException, ClientDisconnectException;
    void write(AbstractServer server) throws KeyManagerException;
}
