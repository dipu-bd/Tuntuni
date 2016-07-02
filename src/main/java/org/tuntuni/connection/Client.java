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

import java.io.BufferedReader;
import org.tuntuni.models.Status;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketOptions;
import java.nio.IntBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import org.tuntuni.util.SocketUtils;

/**
 * To manage connection with server sockets.
 * <p>
 * You can not create new client directly. To create a client use
 * {@linkplain Client.open()} method.</p>
 */
public class Client {

    public static final int DEFAULT_TIMEOUT = 500;

    private Selector mSelector;
    private int mTimeout;  // timeout for selector.select() 
    private final InetSocketAddress mAddress; // socket address
    private SocketChannel mChannel; // currently opened channel

    // hidesthe constructor and handle it with static open() method
    private Client(InetSocketAddress socket) throws IOException {
        // default settings
        mTimeout = DEFAULT_TIMEOUT;
        // set the socket
        mAddress = socket;
        // Create selector
        mSelector = Selector.open();
    }

    /**
     * Opens a new channel to connect with server.
     * <p>
     * First a {@code selector} should be open and then the default
     * {@code channel}. This method does not automatically connect the
     * {@code channel} with the {@code socket}. Make sure to call
     * {@linkplain connect} method to connect with the server. </p>
     *
     * @param socket
     * @return The opened instance of Client.
     * @throws IOException
     */
    public static Client open(InetSocketAddress socket) throws IOException {
        return new Client(socket);
    }

    public boolean isOpen() {
        return mChannel != null && mChannel.isOpen();
    }

    public InetSocketAddress getAddress() {
        return mAddress;
    }

    public String getHostString() {
        return mAddress.getHostString();
    }

    public int getPort() {
        return mAddress.getPort();
    }

    /**
     * Set the timeout for an attempt to connect with server.
     * <p>
     * Default is {@value #DEFAULT_TIMEOUT}.</p>
     *
     * @param timeout in milliseconds.
     */
    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    /**
     * Test the server
     *
     * @return
     * @throws java.io.IOException
     */
    public boolean test() throws IOException {
        if (isOpen()) {
            return true;
        }

        mChannel = SocketChannel.open();
        mChannel.configureBlocking(true);
        mChannel.register(mSelector, mTimeout);
        mChannel.connect(mAddress);
        if (mSelector.select(mTimeout) <= 0) {
            return false;
        }

        // Get keys
        Set keys = mSelector.selectedKeys();
        Iterator it = keys.iterator();
        // For each key...
        while (it.hasNext()) {
            // Get the selection key 
            SelectionKey key = (SelectionKey) it.next();
            // Remove it.
            it.remove();

            // Get the socket channel held by the key
            SocketChannel channel = (SocketChannel) key.channel();
            // Attempt a connection
            if (key.isConnectable()) {
                // Connection OK : Server Found
                // Close pendent connections
                if (channel.isConnectionPending()) {
                    channel.finishConnect();
                }

                // write operation
            }
        }

        /*
         // Open a socket
         try (Socket socket = new Socket()) {
         socket.setSoTimeout(mTimeout);
         socket.connect(mAddress, mAddress.getPort());

         try (OutputStream out = socket.getOutputStream()) {
         out.write(SocketUtils.intToBytes(Status.TEST));
         out.flush();
         socket.shutdownOutput();
         }

         try (InputStream is = socket.getInputStream();
         Scanner sc = new Scanner(is)) {

         boolean result = sc.nextBoolean();
         return result;
         }
         }*/
    }

    /**
     * Starts the connection with server.
     *
     * @param load
     * @return
     * @throws IOException
     * @throws ConnectException
     */
    public Object communicate(Object load) throws ConnectException, IOException {
        // Waiting for the connection with timeeout
        if (mSelector.select(mTimeout) <= 0) {
            throw new ConnectException("Failed to connect after " + mTimeout + " milliseconds.");
        }

        // Get keys
        Set keys = mSelector.selectedKeys();
        Iterator it = keys.iterator();

        // For each key...
        while (it.hasNext()) {
            // Get the selection key 
            SelectionKey key = (SelectionKey) it.next();
            // Remove it.
            it.remove();

            // Get the socket channel held by the key
            SocketChannel channel = (SocketChannel) key.channel();
            // Attempt a connection
            if (key.isConnectable()) {

                // Connection OK : Server Found
                // Close pendent connections
                if (channel.isConnectionPending()) {
                    channel.finishConnect();
                }

                // write operation
            }
        }
        return null;
    }
}
