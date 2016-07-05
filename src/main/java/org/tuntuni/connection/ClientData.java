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

import java.net.InetSocketAddress;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.tuntuni.models.Message; 
import org.tuntuni.models.UserData;

/**
 * Extended by client. It separates data part of client from its connection
 * part.
 */
public class ClientData {

    private static final Logger logger = Logger.getGlobal();

    // to connect with server
    private int mTimeout;
    private final InetSocketAddress mAddress;
    // local data from server 
    private UserData mUser;
    private boolean mConnected;
    private final SimpleListProperty<Message> mMessages;

    // hidesthe constructor and handle it with static open() method
    public ClientData(InetSocketAddress socket) {
        mConnected = false;
        // default settings
        mTimeout = Client.DEFAULT_TIMEOUT;
        // set the socket
        mAddress = socket;
        // load messages
        // TODO: save and restore messages
        mMessages = new SimpleListProperty<>(FXCollections.observableArrayList());
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
     * Gets the user data associated with it.
     *
     * @return
     */
    public UserData getUserData() {
        return mUser;
    }

    /**
     * Sets the user data
     *
     */
    void setUserData(UserData user) {
        mUser = user;
    } 
    
    /**
     * Gets the message list property of this client
     *
     * @return
     */
    public SimpleListProperty<Message> messageProperty() {
        return mMessages;
    }

    /**
     * Add a new message to this client
     *
     * @param message
     */
    public void addMessage(Message message) {
        // to be thread safe
        Platform.runLater(() -> {
            mMessages.add(message);
        });
    }

    public boolean isConnected() {
        return mConnected;
    }

    public void setConnected(boolean connected) {
        mConnected = connected;
    }
}
