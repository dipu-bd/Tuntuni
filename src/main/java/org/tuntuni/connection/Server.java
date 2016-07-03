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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.model.Status;
import org.tuntuni.util.Logs;

/**
 * To listen and respond to clients sockets.
 */
public final class Server {

    public static final int MAX_SOCKET_THREAD = 10;
    public static final String SECRET_CODE = "B33KS83JNF";
    public static final int PORTS[] = {
        24914, //PRIMARY_PORT
        42016, //BACKUP_PORT  
    };

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private ServerSocket mSSocket;
    private final ExecutorService mExecutor;

    /**
     * Creates a new Server.
     * <p>
     * It does not start the server automatically. Please call
     * {@linkplain start()} to start the server. Or you can initialize the
     * server by {@linkplain initialize()} first, then call {@linkplain start()}
     * to run server.</p>
     */
    public Server() {
        mExecutor = Executors.newFixedThreadPool(MAX_SOCKET_THREAD);
    }

    /**
     * Returns true if the server channel is open; false otherwise.
     *
     * @return True only if server channel and selector is open
     */
    public boolean isOpen() {
        return (mSSocket != null && mSSocket.isBound());
    }

    /**
     * Gets the port to which the server is bound to
     *
     * @return
     */
    public int getPort() {
        return isOpen() ? mSSocket.getLocalPort() : -1;
    }

    /**
     * Initializes the server.
     * <p>
     * It creates a server socket and try to bind it to the first available port
     * in the {@linkplain PORTS} list. If it fails to bind to all of the port
     * given, an IOException is thrown. </p>
     * <p>
     * Please call {@linkplain start()} to start the server after initializing
     * it.</p>
     *
     * @throws java.io.IOException Failed to open a server-socket
     */
    public void initialize() throws IOException {
        // try create server channel for each of the given ports
        for (int i = 0; i < PORTS.length; ++i) {
            try {
                // Create the server socket channel
                mSSocket = new ServerSocket(PORTS[i]);
                // successfully created a server
                logger.log(Level.INFO, Logs.SERVER_BIND_SUCCESS, PORTS[i]);
                break;
            } catch (IOException ex) {
                logger.log(Level.WARNING, Logs.SERVER_BIND_FAILS, PORTS[i]);
            }
        }
    }

    /**
     * Execute an infinite loop in a separate thread and wait for clients to
     * connect.
     * <p>
     * This method calls {@linkplain initialize()} if a server socket is not
     * open.</p>
     *
     * @throws IOException Server failed load in both Primary and backup ports.
     */
    public void start() throws IOException {
        // initialize the server if not already
        if (!isOpen()) {
            initialize();
        }
        // execute server in separate thread
        mExecutor.submit(() -> {
            runServer();
        });
    }

    /**
     * Sends the request to stop the server.
     * <p>
     * It may take a while to stop the server completely.</p>
     */
    public void stop() {
        try {
            if (mSSocket != null && !mSSocket.isClosed()) {
                mSSocket.close();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, Logs.SERVER_CLOSING_ERROR, ex);
        }
    }

    // runnable containing the infinite server loop.
    // it is started via an executor service.
    private void runServer() {
        if (!isOpen()) {
            return;
        }
        // Infinite server loop
        logger.log(Level.INFO, Logs.SERVER_LISTENING, getPort());
        while (isOpen()) {

            try {
                Socket socket = mSSocket.accept();
                // process the request in a separate thread
                mExecutor.submit(() -> {
                    processSocket(socket);
                });

            } catch (IOException ex) {
                logger.log(Level.SEVERE, Logs.SERVER_ACCEPT_FAILED, ex);
            }
        }
        logger.log(Level.INFO, Logs.SERVER_LISTENING_STOPPED);
    }

    // process a selection key
    private void processSocket(Socket socket) {

        try ( // get all input and output streams from socket
                InputStream in = socket.getInputStream();
                ObjectInputStream req = new ObjectInputStream(in);
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream res = new ObjectOutputStream(out);) {

            // get request type
            Status status = (Status) req.readObject();
            // get params
            Object[] data = (Object[]) req.readObject();
            // log this connection
            logger.log(Level.INFO, Logs.SERVER_RECEIVED_CLIENT,
                    new Object[]{socket.getRemoteSocketAddress(), status, data.length});
            // routing by status type
            Object result = ServerRoute.request(status, data);
            // send the result
            if (res != null) {
                res.writeObject(result);
                res.flush();
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, Logs.SERVER_IO_FAILED, ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, Logs.SOCKET_CLASS_FAILED, ex);
        }

        // close the socket
        try {
            socket.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Logs.SERVER_CLOSING_ERROR, ex);
        }
    }

}
