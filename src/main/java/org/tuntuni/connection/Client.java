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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import org.tuntuni.models.Message;
import org.tuntuni.models.UserData;

/**
 * Extended by client. It separates data part of client from its connection
 * part.
 */
public class Client extends TCPClient {

    // local data from server 
    private final ObjectProperty<UserData> mUserData;
    private final SimpleListProperty<Message> mMessages;

    // hides the constructor and handle it with static open() method
    public Client(InetSocketAddress socket) {
        super(socket);
        // initialize properties 
        mUserData = new SimpleObjectProperty<>(this, "UserData");
        mMessages = new SimpleListProperty<>(FXCollections.observableArrayList());
        // load messages
        // TODO: save and restore messages
    }

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    /**
     * Test the server is available at the address.
     *
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean checkServer() {
        if (!isConnected()) {
            return getProfile();
        }
        // check if server is alive
        Object ob = request(ConnectFor.STATE);
        if (ob == null || !(ob instanceof String)) {
            setConnected(false);
            return false;
        }
        // now check if states are the same
        String result = (String) ob;
        if (!result.equals(getState())) {
            return getProfile();
        }
        return true;
    }

    /**
     * Gets the user profile from the server
     *
     * @return
     */
    public boolean getProfile() {
        // get getProfile data
        try {
            UserData profile = (UserData) request(ConnectFor.PROFILE);
            setUserData(profile);
            if (profile != null) {
                setConnected(true);
                return true;
            }
        } catch (NullPointerException ex) {
            //logger.log(Level.SEVERE, Logs.CLIENT_TEST_FAILED, ex);
        }
        return false;
    }

    /**
     * Send a sendMessage to this client
     *
     * @param toSent Message to be sent
     * @return True if success, false otherwise.
     */
    public Exception sendMessage(Message toSent) {
        return (Exception) request(ConnectFor.MESSAGE, toSent);
    }

    //////////////////////////////////////////////////////////////////////////// 
    //////////////////////////////////////////////////////////////////////////// 
    /**
     * Gets the state of the user data
     *
     * @return
     */
    public String getState() {
        return mUserData.get().getState();
    }

    /**
     * Gets the user data associated with it.
     *
     * @return
     */
    public UserData getUserData() {
        return mUserData.get();
    }

    /**
     * Sets the user data
     *
     */
    void setUserData(UserData user) {
        mUserData.set(user);
    }

    public ObjectProperty<UserData> userdataProperty() {
        return mUserData;
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
        mMessages.add(message);
    }

    ////////////////////////////////////////////////////////////////////////////
    // methods used in dialer 
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Request for a call slot
     *
     * @return
     */
    public Object requestSlot() {
        return request(ConnectFor.DIAL);
    }

    /**
     * Request client to end an ongoing call
     */
    public void endCall() {
        request(ConnectFor.END_CALL);
    }

}
