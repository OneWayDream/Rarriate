package ru.itis.utils;

import ru.itis.client.AbstractClient;
import ru.itis.exceptions.ClientDisconnectException;
import ru.itis.exceptions.KeyManagerException;

import java.nio.channels.SelectionKey;

public interface ClientKeyManager {
    void read(AbstractClient client, SelectionKey key) throws KeyManagerException, ClientDisconnectException;
    void write(AbstractClient client) throws KeyManagerException;
}
