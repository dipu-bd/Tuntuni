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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.models.Logs;

/**
 * To listen and respond to clients sockets.
 */
public final class Server {

    public static final int PRIMARY_PORT = 24914;
    public static final int BACKUP_PORT = 42016;
    public static final int MAX_QUEUE_SIZE = 100;

    public static final Logger logger = Logger.getLogger(Server.class.getName());

    private ServerSocket mServer; // holds main server socket

    /**
     * Create a new Server. It does not start the server automatically. Please
     * call {@linkplain start()} to start the server.
     */
    public Server() {

    }

    /**
     * Returns true if the server is up and running; false otherwise.
     */
    public boolean isActive() {
        return (mServer != null && !mServer.isClosed() && mServer.isBound());
    }

    /**
     * Creates a server and start listening.
     *
     * @throws IOException Server failed load in both Primary and backup ports.
     */
    public void start() throws IOException {
        // check if already running
        if (isActive()) {
            return;
        }

        // first try to create server on primary port
        try {
            mServer = new ServerSocket(PRIMARY_PORT, MAX_QUEUE_SIZE);
        } catch (IOException ex) {
            logger.log(Level.INFO, Logs.SERVER_PRIMARY_PORT_FAILS);

            // primary port fails. start in backup port
            try {
                mServer = new ServerSocket(BACKUP_PORT, MAX_QUEUE_SIZE);
            } catch (IOException ix) {
                logger.log(Level.SEVERE, Logs.SERVER_BACKUP_PORT_FAILS, ix);
                // backup failed. throw error
                throw ix;
            }
        }

        // now the server should be created. just listen and wait for clients. 
        listen();
    }

    /**
     * Sends the request to stop the server. Note that, after this method is
     * called server may not be stopped immediately. It may take a while.
     */
    public void stop() {
        try {
            // check if server is up and running
            if (isActive()) {
                mServer.close(); // close the server
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Logs.SERVER_FAILED_CLOSING, ex);
        }
    }

    /**
     * Start listening for the clients to connect.
     */
    public void listen() {

        try {
            // socket never time out while listening
            mServer.setSoTimeout(0);

            // start to listen on separate thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    catchClient();
                }
            });
            thread.setDaemon(true); // dies along with main thread  
            thread.start();

        } catch (SocketException | NullPointerException ex) {
            logger.log(Level.SEVERE, Logs.SERVER_SOCKET_EXCEPTION, ex);
        }
    }

    @Override
    public String toString() {
        if (mServer == null) {
            return "SERVER:NULL";
        } else if (mServer.isBound()) {
            return "SERVER:BOUND=" + mServer.getLocalPort();
        } else if (mServer.isClosed()) {
            return "SERVER:CLOSED";
        } else {
            return "SERVER:UNKNOWN";
        }
    }

    // to catch clients
    private void catchClient() {
        try {
            while (mServer.isBound()) {
                logger.log(Level.INFO, Logs.SERVER_LISTENING);
                Socket socket = mServer.accept();

                // now do something to communicate with client
                processClient(socket);
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, Logs.SERVER_FAILS_ACCEPTING_CLIENT, ex);
        }
    }

    // send and receive information to client
    private void processClient(Socket socket) {
        logger.log(Level.SEVERE, "Found a client", socket);
    }

}
