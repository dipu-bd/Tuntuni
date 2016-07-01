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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * To manage connection with server sockets.
 */
public class Client {

    public Client() {

    }

    public void connect(int id) throws IOException {

        // Create client SocketChannel
        SocketChannel client = SocketChannel.open();
        // nonblocking I/O
        client.configureBlocking(false);
        // Connection to host
        InetAddress host = InetAddress.getByName("localhost");
        client.connect(new java.net.InetSocketAddress(host, Server.PORTS[0]));

        System.out.println("Socket channel connected");

        // Create selector
        Selector selector = Selector.open();
        // Record to selector (OP_CONNECT type)
        SelectionKey clientKey = client.register(selector, SelectionKey.OP_CONNECT);

        System.out.println("Selector registered");

        // Waiting for the connection with 500ms timeeout
        int sel = selector.select(500);
        if (sel <= 0) {
            System.out.println("Failed to connect after 500 milliseconds");
            return;
        }

        // Get keys
        Set keys = selector.selectedKeys();
        Iterator it = keys.iterator();

        // For each key...
        while (it.hasNext()) {
            SelectionKey key = (SelectionKey) it.next();

            // Remove the current key
            it.remove();

            // Get the socket channel held by the key
            SocketChannel channel = (SocketChannel) key.channel();

            // Attempt a connection
            if (key.isConnectable()) {

                // Connection OK
                System.out.println("Server Found");

                // Close pendent connections
                if (channel.isConnectionPending()) {
                    channel.finishConnect();
                }

                // Write continuously on the buffer  
                ByteBuffer buffer = ByteBuffer.wrap((" Client " + id + " ").getBytes());
                channel.write(buffer);
                buffer.clear();

            }
        }
    }

    public boolean test(InetSocketAddress socket, int timeout) throws IOException {
        try (Socket soc = new Socket()) {
            soc.connect(socket, timeout);
        }
        return true;
    }
}
