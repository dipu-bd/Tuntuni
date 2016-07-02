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

import org.tuntuni.models.Status;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * To manage connection with server sockets.
 * <p>
 * You can not create new client directly. To create a client use
 * {@linkplain Client.open()} method.</p>
 */
public class Client {

    public static final int DEFAULT_TIMEOUT = 500;

    private int mTimeout;  // timeout for selector.select() 
    private final InetSocketAddress mAddress; // socket address

    // hidesthe constructor and handle it with static open() method
    private Client(InetSocketAddress socket) throws IOException {
        // default settings
        mTimeout = DEFAULT_TIMEOUT;
        // set the socket
        mAddress = socket;
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

    public Object connect(Status test, Object... data) {
        
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
        // Open a socket
        try (Socket socket = new Socket()) {
            socket.connect(mAddress, mAddress.getPort());

            try ( // get input-output 
                    InputStream in = socket.getInputStream();
                    ObjectInputStream req = new ObjectInputStream(in);
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream res = new ObjectOutputStream(out)) {

                int result = sc.nextInt();

                return result == 1;
            }
        }

        /*
         // first check if the channel is open
         if (isOpen()) {
         return true;
         }
         //System.out.println("Opening a channel");
         // open a channel
         mChannel = SocketChannel.open();
         mChannel.configureBlocking(false);
         // register the channel
         mChannel.register(mSelector, SelectionKey.OP_CONNECT);
         // connect the channel with address
         mChannel.connect(mAddress);
         //System.out.println("Connecting a channel");
         // wait for the selector
         //System.out.println("Selecting with selector");
         while (mSelector.select(mTimeout) > 0) {
         //System.out.println("Process selected keys");
         // Get selected keys
         Set keys = mSelector.selectedKeys();
         Iterator it = keys.iterator();
         // For each key...
         while (it.hasNext()) {
         // Get the selection key 
         SelectionKey key = (SelectionKey) it.next();
         // Remove it
         it.remove();
         //System.out.println("Selected key is removed");

         if (key.isConnectable()) {
         // Connection OK : Server Found 
         if (mChannel.isConnectionPending()) {
         mChannel.finishConnect();
         //System.out.println("Selected key has finished connection");
         }
         // write operation
         //System.out.println("Writing to server");
         ByteBuffer bb = ByteBuffer.allocate(4);
         bb.putInt(Status.TEST);
         bb.flip();
         mChannel.write(bb);
         mChannel.shutdownOutput();
         mChannel.register(mSelector, SelectionKey.OP_READ);
         continue;
         }
         if (key.isReadable()) {
         // read operation
         //System.out.println("Reading from server");
         ByteBuffer result = ByteBuffer.allocate(4);
         mChannel.read(result);
         result.flip();
         int i = result.getInt();
         //System.out.println("Hello result = " + i);
         return i == 1;
         }
         }
         }
         return false;
   
         */
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
