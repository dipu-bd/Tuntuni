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
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;

/**
 * To listen and respond to clients sockets.
 */
public abstract class TCPServer {

    public static final int MAX_EXECUTOR_THREAD = 10;

    private final String mName;
    private Exception mError;
    private ServerSocket mSSocket;
    private final ExecutorService mExecutor;
    private int[] mPorts;

    /**
     * Creates a new Server.
     * <p>
     * It does not start the server automatically. Please call
     * {@linkplain start()} to start the server. Or you can initialize the
     * server by {@linkplain initialize()} first, then call {@linkplain start()}
     * to run server.</p>
     *
     * @param name Name of this server.
     * @param ports Valid ports to use. Null or empty to use default.
     */
    public TCPServer(String name, int[] ports) {
        mName = name;
        mPorts = ports;
        if (mPorts == null || mPorts.length == 0) {
            mPorts = new int[]{0};
        }
        mExecutor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREAD);
    }

    /**
     * Gets the getName of the server
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns true if the server channel is open; false otherwise.
     *
     * @return True only if server channel and selector is open
     */
    public boolean isOpen() {
        return (mSSocket != null && !mSSocket.isClosed() && mSSocket.isBound());
    }

    /**
     * Gets any error associated with this server. If none, {@code null} value
     * is returned.
     *
     * @return
     */
    public Exception getError() {
        return mError;
    }

    /**
     * Gets the port to which the server is bound to or -1 if not
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
     * from the given list. If no port list is null or empty, it chooses any
     * random port. </p>
     * <p>
     * Please call {@linkplain start()} to start the server after initializing
     * it.</p>
     *
     * @throws java.io.IOException Failed to open a server-socket
     */
    public void initialize() throws IOException {
        // try create server channel for each of the given ports
        for (int port : mPorts) {
            try {
                // Create the server socket channel
                mSSocket = new ServerSocket(port);
                break;
            } catch (IOException ex) {
                Logs.warning(getName(), "Could not bind to {0}", port);
            }
        }
    }

    /**
     * Execute an infinite loop in a separate thread and wait for clients to
     * connect.
     * <p>
     * This method calls {@linkplain initialize()} if a server socket is not
     * open.</p>
     */
    public void start() {
        try {
            // initialize the server if not already
            if (!isOpen()) {
                initialize();
            }
            // execute server in separate thread
            mExecutor.submit(() -> runServer());
        } catch (IOException ex) {
            mError = ex;
        }
    }

    /**
     * Sends the getResponse to stop the server.
     * <p>
     * It may take a while to stop the server completely.</p>
     */
    public void stop() {
        // close server socket
        try {
            if (isOpen()) {
                mSSocket.close();
            }
        } catch (Exception ex) {
            Logs.warning(getName(), "Error stopping server. {0}", ex);
        }
        // shutdown executors
        mExecutor.shutdownNow();
    }

    // runnable containing the infinite server loop.
    // it is started via an executor service.
    private void runServer() {
        // Infinite server loop
        Logs.info(getName(), "Opened @ {0}", getPort());
        while (isOpen()) {
            try {
                Socket socket = mSSocket.accept();
                // process the getResponse in a separate thread
                mExecutor.submit(() -> {
                    processSocket(socket);
                    closeSocket(socket);
                });
            } catch (IOException ex) {
                if (isOpen()) {
                    Logs.error(getName(), "Accept failure. {0}", ex);
                }
            }
        }
        Logs.info(getName(), "Stopped listening");
    }

    // process a selection key
    void processSocket(Socket socket) {

        try ( // DON'T CHANGE THE ORDER
                InputStream in = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in);
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out);) {

            // response type
            ConnectFor status = ConnectFor.from(ois.readByte());
            // param length
            int length = ois.readInt();

            // read all params
            Object[] data = new Object[length];
            for (int i = 0; i < length; ++i) {
                data[i] = ois.readObject();
            }

            // send response  
            Object result = null;
            try {
                result = getResponse(status, socket, data);
            } catch (Exception e) {
                // failed to get response
                Logs.warning(getClass(), "{0}", e);
            }
            if (result != null) {
                oos.writeObject(result);
                oos.flush();
            }

        } catch (IOException ex) {
            Logs.warning(getClass(), ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logs.warning(this.getName(), ex.getMessage());
        }
    }

    // close the socket
    void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            Logs.error(this.getName(), "Failed to close socket. {0}", ex);
        }
    }

    /**
     * Implement this and return the processed response to the server.
     *
     * @param status
     * @param socket
     * @param data
     * @return
     */
    abstract Object getResponse(ConnectFor status, Socket socket, Object[] data);

}
