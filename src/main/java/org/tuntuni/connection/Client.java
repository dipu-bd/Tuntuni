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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * To manage connection with server sockets.
 * <p>
 * You can not create new client directly. To create a client use
 * {@linkplain Client.open()} method.</p>
 */
public class Client {

    public static final int DEFAULT_TIMEOUT = 500;

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private int mTimeout;
    private final InetSocketAddress mAddress;

    // hidesthe constructor and handle it with static open() method
    public Client(InetSocketAddress socket) throws IOException {
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

    public int getTimeout() {
        return mTimeout;
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
     * Sends a request to the server.
     *
     * @param status Status of the request
     * @param data Any data to pass along the request
     * @return
     */
    public Object request(Status status, Object... data) {
        // create a socket
        try (Socket socket = new Socket()) {
            // set the socket timeout
            socket.setSoTimeout(getTimeout());
            // connect the socket with given address
            socket.connect(getAddress(), getPort());

            try ( // get input-output 
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream req = new ObjectOutputStream(out);
                    InputStream in = socket.getInputStream();
                    ObjectInputStream res = new ObjectInputStream(in);) {

                // send params
                req.writeObject(status);
                req.writeObject(data);
                req.flush();

                // return result
                return res.readObject();

            } catch (IOException | ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Test the server is available at the address.
     *
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean test() {
        Object data = request(Status.TEST);
        return (data != null && Server.SECRET_CODE.equals(data));
    }
}
