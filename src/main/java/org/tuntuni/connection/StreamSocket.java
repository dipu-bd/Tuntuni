/*
 * Copyright 2016 Tuntuni.
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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import org.tuntuni.models.Logs;

/**
 *
 * @author Sudipto Chandra
 */
public abstract class StreamSocket implements Runnable {

    private Thread mServerThread;
    private DatagramSocket mSocket;

    /**
     * Creates a new Stream Server
     */
    public StreamSocket() {
    }

    /**
     * Gets the name of the socket
     *
     * @return
     */
    public abstract String getName();

    /**
     * Open the server, and starts listening
     *
     * @throws SocketException
     */
    public void open() throws SocketException {
        // create socket
        mSocket = new DatagramSocket();
        mSocket.setBroadcast(false);
        // run server loop in separate thread
        mServerThread = new Thread(this);
        mServerThread.setDaemon(true);
        mServerThread.start();
    }

    /**
     * Closes the current server
     */
    public void close() {
        if (mSocket != null && isOpen()) {
            // close socket
            mSocket.close();
        }
        if (mServerThread != null && mServerThread.isAlive()) {
            // interrupt server thread
            mServerThread.interrupt();
        }
    }

    /**
     * Make a point to point connection with a remote host to send data.
     *
     * @param address Remote host address
     * @param port Remote host port
     */
    public void connect(InetAddress address, int port) {
        mSocket.connect(address, port);
        if (mSocket.isConnected()) {
            Logs.info(getName(), "Connected @ {0}:{1}", address, port);
        } else {
            Logs.warning(getName(), "Failed to connect! @ {0}:{1}", address, port);
        }
    }

    /**
     * Gets the underlying datagram socket. It always returned the last opened
     * socket, even after the socket is closed.
     *
     * @return
     */
    public DatagramSocket getSocket() {
        return mSocket;
    }

    /**
     * Gets the local port bound to this server. If the socket is closed, or not
     * bound, -1 is returned.
     *
     * @return
     */
    public int getPort() {
        return mSocket == null ? -1 : mSocket.getLocalPort();
    }

    /**
     * Gets the socket address this server is connected to
     *
     * @return
     */
    public SocketAddress getRemoteAddress() {
        return mSocket.getRemoteSocketAddress();
    }

    /**
     * True only if the line between client is open and bound to a port
     *
     * @return
     */
    public boolean isOpen() {
        return !mSocket.isClosed() && mSocket.isBound();
    }

    // continuously send data packet
    @Override
    public void run() {
        Logs.info(getName(), "Opened @ port {0}...", getPort());
        while (isOpen()) {
            if (mSocket.isConnected()) {
                doWork();
            }
        }
    }

    /**
     * Called from inside the server loop. Send or receive data in this method.
     */
    public abstract void doWork();

}
