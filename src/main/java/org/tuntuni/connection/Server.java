/*
 * Copyright 2016 Sudipto Chandra.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tuntuni.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.models.Logs;
import org.tuntuni.models.Status;

/**
 * To listen and respond to clients sockets.
 */
public final class Server {

    public static final Logger logger = Logger.getLogger(Server.class.getName());

    public static final int PORTS[] = {
        24914, //PRIMARY_PORT
        42016, //BACKUP_PORT  
    };

    private Selector mSelector;
    private ServerSocketChannel mSSChannel;

    /**
     * Create a new Server.
     * <p>
     * It does not start the server automatically. Please call
     * {@linkplain start()} to start the server. Or you can initialize the
     * server by {@linkplain initialize()} first, then call {@linkplain start()}
     * to run server.</p>
     */
    public Server() {
    }

    /**
     * Initializes the server.
     * <p>
     * It opens the selector and server socket channel. And binds the server
     * channel to the first available port in the {@linkplain PORTS} list. If it
     * fails to bind to all of the port given, and IOException is thrown. </p>
     * <p>
     * Please call {@linkplain start()} to start the server after initializing
     * it.</p>
     *
     * @throws java.io.IOException Failed to open a server-socket channel
     */
    public void initialize() throws IOException {
        // Create the selector
        mSelector = Selector.open();
        // try create server channel for each of the given ports
        for (int i = 0; i < PORTS.length; ++i) {
            try {
                // Create the server socket channel
                mSSChannel = ServerSocketChannel.open();
                // nonblocking I/O
                mSSChannel.configureBlocking(false);
                // bind to port           
                mSSChannel.socket().bind(new InetSocketAddress(PORTS[i]));
                // Recording server to selector (type = all of SelectionKey)
                mSSChannel.register(mSelector, SelectionKey.OP_ACCEPT);
                // successfully created one server
                logger.log(Level.INFO, Logs.SERVER_BIND_SUCCESS, PORTS[i]);
                break;
            } catch (IOException ex) {
                logger.log(Level.WARNING, Logs.SERVER_BIND_FAILS, PORTS[i]);
                mSSChannel.close();
            }
        }
    }

    /**
     * Returns true if the server channel is open; false otherwise.
     *
     * @return True only if server channel and selector is open
     */
    public boolean isOpen() {
        return (mSSChannel != null && mSSChannel.isOpen()
                && mSSChannel.socket().isBound());
    }

    public int getPort() {
        if (isOpen()) {
            return mSSChannel.socket().getLocalPort();
        } else {
            return -1;
        }
    }

    /**
     * Execute an infinite loop in a separate thread and wait for clients to
     * connect.
     *
     * @throws IOException Server failed load in both Primary and backup ports.
     */
    public void start() throws IOException {
        // initialize the server if not already
        if (!isOpen()) {
            initialize();
        }
        // create a new instance of server executor
        ExecutorService executor;
        executor = Executors.newSingleThreadExecutor();
        // submit the server task to start it later
        executor.submit(serverTask);
    }

    /**
     * Sends the request to stop the server.
     * <p>
     * It stops all the channel connected with the selector first, regardless of
     * the channel type. After it close the selector. Note that, the close
     * operation is asynchronous. After this method is called server may not be
     * stopped immediately. It may take a while.</p>
     */
    public void stop() {
        // close all channels connected to selector
        // ServerSocketChannel also gets closed as it is connected to selector.
        try {
            Iterator it = mSelector.keys().iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                // close the channel 
                // it can be either ServerSocketChannel or normal SocketChannel.
                try {
                    key.channel().close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, Logs.SERVER_CLOSING_CHANNEL_ERROR, e);
                }
                // cancel key
                key.cancel();
            }
            // now close the selector
            logger.log(Level.INFO, Logs.SERVER_CLOSING_SELECTOR);
            mSelector.close();

        } catch (Exception ex) {
            logger.log(Level.SEVERE, Logs.SERVER_CLOSING_SELECTOR_ERROR, ex);
        }
    }

    // runnable containing the infinite server loop.
    // it is started via an executor service.
    private final Runnable serverTask = () -> {
        // Infinite server loop      
        logger.log(Level.INFO, Logs.SERVER_LISTENING, getPort());
        while (isOpen()) {
            try {
                // Waiting for events
                mSelector.select();
                // Iterate over the set of keys for which events are available
                Iterator it = mSelector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    // remove the key from selection list
                    it.remove();
                    // process current keys
                    processKeys(key);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, Logs.SERVER_SELECT_FAILED, ex);
            }
        }
        logger.log(Level.INFO, Logs.SERVER_LISTENING_STOPPED);
    };

    // process a selection key
    private void processKeys(SelectionKey key) {

        // check if the key is valid
        if (!key.isValid()) {
            return;
        }

        // check if the key is connectable 
        if (key.isConnectable()) {
            connect(key);
            return;
        }

        // check if the key is acceptable 
        if (key.isAcceptable()) {
            accept(key);
            return;
        }

        // check if the key is readable / sending data
        if (key.isReadable()) {
            read(key);
            return;
        }

        // check if the key is writable / receiving data
        if (key.isWritable()) {
            write(key);
            return;
        }
    }

    // respond to key that is connectable
    private void connect(SelectionKey key) {
        System.out.println("+Connectable key!");
    }

    // accept a socket channel connecting with servers
    private void accept(SelectionKey key) {
        try {
            // get client socket channel
            SocketChannel client = mSSChannel.accept();
            // Non Blocking I/O
            client.configureBlocking(false);
            // recording to the selector (reading)
            client.register(mSelector, SelectionKey.OP_READ);

            System.out.println("+Channel accepted");

        } catch (IOException ex) {
            logger.log(Level.SEVERE, Logs.SERVER_CHANNEL_ACCEPT_FAILED, ex);
        }
    }

    // read from a socket channel
    private void read(SelectionKey key) {
        try {
            System.out.println("+Reading from channel");

            // get the client socket channel from key
            SocketChannel client = (SocketChannel) key.channel();
            // no attachment. it must me a new connection.
            if (key.attachment() == null) {
                // cancel key
                key.cancel();
                // Read byte coming from the client
                ByteBuffer buffer = ByteBuffer.allocate(8);
                client.read(buffer);
                // Show bytes on the console
                buffer.flip();
                route(client, buffer.getInt());
            } else {
                // handle read on attachment
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Logs.SERVER_CHANNEL_READ_FAILED, ex);
        }
    }

    // write data to socket channel
    private void write(SelectionKey key) {
        try {
            System.out.println("+Writing to channel!");

            // Get the channel from key
            SocketChannel client = (SocketChannel) key.channel();
            // Get attachment bytes
            byte[] bytes = (byte[]) key.attachment();
            // Write bytes to the client
            client.write(ByteBuffer.wrap(bytes));
            // close the client
            client.close();
            // cancel the key
            key.cancel();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Logs.SERVER_CHANNEL_WRITE_FAILED, ex);
        }

    }

    private void route(SocketChannel channel, int type) throws ClosedChannelException {
        if (!channel.isOpen()) {
            return;
        }
        switch (type) {
            case Status.TEST:
                channel.register(mSelector, SelectionKey.OP_WRITE, new byte[]{1});
                break;
            case Status.META:
                break;
            case Status.USER:
                break;
        }
    }
}
