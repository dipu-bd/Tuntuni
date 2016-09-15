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
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;

/**
 * To manage connection with server sockets.
 * <p>
 * You can not create new client directly. To create a client use
 * {@linkplain Client.open()} method.</p>
 */
public abstract class TCPClient {

    public static int ALIVE_PERIOD = 5 * 60_000; // 5 min alive period

    // to connect with server     
    private InetSocketAddress mAddress;
    private final BooleanProperty mConnected;

    // hidesthe constructor and handle it with static open() method
    public TCPClient(InetSocketAddress socket) {
        mAddress = socket;
        mConnected = new SimpleBooleanProperty(false);
    }

    @Override
    public String toString() {
        return String.format("@%s:%s", getHostString(), getPort());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TCPClient) {
            TCPClient c = (TCPClient) other;
            return c.getHostString().equals(getHostString()) && c.getPort() == getPort();
        } else {
            return super.equals(other);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.mAddress);
        return hash;
    }

    /**
     * Gets the socket address associated with this client.
     *
     * @return the socket address
     */
    public InetSocketAddress getSocketAddress() {
        return mAddress;
    }

    /**
     * Gets the socket address associated with this client.
     *
     * @return the socket address
     */
    public InetAddress getAddress() {
        return mAddress.getAddress();
    }

    /**
     * Gets the IPV4 socket address associated with this client as integer.
     *
     * @return the socket address
     */
    public int getIntegerAddress() {
        return Commons.bytesToInt(mAddress.getAddress().getAddress());
    }

    /**
     * Update the current address of the client
     *
     * @param address
     */
    public void updateAddress(InetSocketAddress address) {
        mAddress = address;
        setConnected(false);
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
        return mAddress.getHostName();
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
        if (connected != mConnected.get()) {
            mConnected.set(connected);
        }
    }

    /**
     * Sends a request to the server.
     *
     * @param status ConnectFor of the request
     * @param data Any data to pass along the request
     * @return
     */
    Object request(ConnectFor status, Serializable... data) {
        // create a socket
        try (Socket socket = new Socket()) {
            // connect the socket with given address
            socket.connect(mAddress, 1000);

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
            Logs.error(getClass(), "Unrecognized object received", ex);
        } catch (IOException ex) {
            //Logs.severe(null, ex);
        }
        return null;
    }

}
