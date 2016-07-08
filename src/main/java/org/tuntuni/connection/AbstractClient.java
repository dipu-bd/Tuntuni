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
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.tuntuni.models.Logs;

/**
 * To manage connection with server sockets.
 * <p>
 * You can not create new client directly. To create a client use
 * {@linkplain Client.open()} method.</p>
 */
public abstract class AbstractClient {

    // to connect with server    
    private int mTimeout;
    private final InetSocketAddress mAddress;
    private final BooleanProperty mConnected;

    // hidesthe constructor and handle it with static open() method
    public AbstractClient(InetSocketAddress socket, int timeout) {
        mAddress = socket;
        mTimeout = timeout;
        mConnected = new SimpleBooleanProperty(false);
    }

    @Override
    public String toString() {
        return String.format("%s@%s:%s", getHostName(), getHostString(), getPort());
    }

    /**
     * Gets the socket address associated with this client.
     *
     * @return the socket address
     */
    public InetSocketAddress getAddress() {
        return mAddress;
    }

    /**
     * Gets the host address associated with this client.
     *
     * @return the host address as string
     */
    public String getHostString() {
        return mAddress.getHostString();
    }

    /**
     * Gets the host name of the client. It returns an empty string if host-name
     * is equals the host string.
     *
     * @return
     */
    public String getHostName() {
        return (mAddress.getHostName().equals(getHostString())) ? "" : mAddress.getHostName();
    }

    /**
     * Gets the port number associated with this client.
     *
     * @return the port number
     */
    public int getPort() {
        return mAddress.getPort();
    }

    /**
     * Gets the timeout for a connection
     *
     * @return Timeout for a connection
     */
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
     * Gets the connected property
     *
     * @return
     */
    public BooleanProperty connectedProperty() {
        return mConnected;
    }

    /**
     * True if the server is available at the socket address
     *
     * @return
     */
    public boolean isConnected() {
        return mConnected.get();
    }

    /**
     * Sets whether the server is available at the given address
     *
     * @param connected
     */
    public void setConnected(boolean connected) {
        mConnected.set(connected);
    }

    /**
     * Sends a request to the server.
     *
     * @param status Status of the request
     * @param data Any data to pass along the request
     * @return
     */
    Object request(Status status, Serializable... data) {
        // create a socket
        try (Socket socket = new Socket()) {
            // connect the socket with given address
            socket.connect(getAddress(), getTimeout());

            try ( // get all input streams from socket
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    InputStream in = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(in);) {

                // write status
                oos.writeByte(status.data());
                oos.flush();
                // write data length
                oos.writeInt(data.length);
                oos.flush();
                // send all data
                for (Object o : data) {
                    oos.writeObject(o);
                    oos.flush();
                }

                // return result
                return ois.readObject();
            }
        } catch (ClassNotFoundException ex) {
            Logs.severe(Logs.SOCKET_CLASS_FAILED, ex);

        } catch (IOException ex) {
            //Logs.severe(null, ex);
        }
        return null;
    }

    /**
     * Just connect with the server with the status. This connection is
     * keep-alive
     *
     * @param status
     */
    void connect(Status status) {
        try (Socket socket = new Socket()) {
            // connect the socket with given address
            socket.connect(getAddress(), getTimeout());
            socket.setKeepAlive(true);

            try ( // get all input streams from socket
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    InputStream in = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(in);) {

                oos.write(status.data());
                oos.flush();

                socketReceived(ois, oos);
            }
        } catch (Exception ex) {
            Logs.severe("Could not keep the {0} connection open.", status, ex);
        }
    }

    /**
     * To communicate between server and client. It is being called from inside
     * of {@linkplain openConnection()} method.
     *
     * @param oi Input stream
     * @param oo Output stream
     * @throws ClassNotFoundException
     */
    abstract void socketReceived(ObjectInput oi, ObjectOutput oo);

}
