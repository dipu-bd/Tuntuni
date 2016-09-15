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
import java.util.LinkedList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tuntuni.Core;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Message;
import org.tuntuni.models.UserData;

/**
 * Extended by client. It separates data part of client from its connection
 * part.
 */
public class Client extends TCPClient {

    static final int MAX_MESSAGE_QUEUE = 100;

    // local data from server 
    private int mState;
    private final IntegerProperty mUnseenCount;
    private final LinkedList<Message> mMessages;
    private final ObjectProperty<UserData> mUserData;

    // hides the constructor and handle it with static open() method
    public Client(InetSocketAddress socket) {
        super(socket);
        mUnseenCount = new SimpleIntegerProperty(0);
        // initialize properties 
        mMessages = new LinkedList<>();
        mUserData = new SimpleObjectProperty<>(this, "UserData");
        // load messages
        // TODO: save and restore messages
    }

////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets the user data associated with it.
     *
     * @return
     */
    public UserData getUserData() {
        return mUserData.get();
    }

    /**
     * Gets the user name.
     *
     * @return
     */
    public String getUserName() {
        return mUserData == null ? getHostString() : mUserData.get().getUserName();
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

////////////////////////////////////////////////////////////////////////////////  
    /**
     * Downloads the user profile
     *
     * @param state
     */
    public void downloadProfile(int state) {
        Object data = request(ConnectFor.PROFILE, state);
        if (data != null && data instanceof UserData) {
            setState(state);
            setConnected(true);
            UserData profile = (UserData) data;
            setUserData(profile);
        }
    }

    public void setState(int state) {
        mState = state;       
    }

    public int getState() {
        return mState;
    }

////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets the message list property of this client
     *
     * @return
     */
    public LinkedList<Message> messageList() {
        return mMessages;
    }

    /**
     * Add a new message to this client
     *
     * @param message
     */
    public void addMessage(Message message) {
        mMessages.add(message);
        if (mMessages.size() > MAX_MESSAGE_QUEUE) {
            mMessages.remove();
        }
        if (Core.instance() != null && Core.instance().messaging() != null) {
            Core.instance().messaging().messageAdded(message);
        }
    }

    /**
     * Send a sendMessage to this client
     *
     * @param message Message to be sent
     * @throws java.lang.Exception
     */
    public void sendMessage(Message message) throws Exception {
        Exception ex = (Exception) request(ConnectFor.MESSAGE, message);
        if (ex != null) {
            throw ex;
        }
        addMessage(message);
    }

    public void decreaseUnseen() {
        if (mUnseenCount.get() > 0) {
            mUnseenCount.set(mUnseenCount.get() - 1);
        }
    }

    public void increaseUnseen() {
        mUnseenCount.set(mUnseenCount.get() + 1);
    }

    public IntegerProperty unseenCountProperty() {
        return mUnseenCount;
    }

////////////////////////////////////////////////////////////////////////////////
    /**
     * Sends a call request and receive response
     *
     * @param req
     * @param data
     * @throws Exception
     */
    public void callRequest(ConnectFor req, Exception data) throws Exception {
        Exception res = (Exception) request(req, data);
        if (res != null) {
            throw res;
        }
    }
}
