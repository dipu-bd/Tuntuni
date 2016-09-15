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
package org.tuntuni.models;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.Objects;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;

/**
 * To pass message between two users
 */
public class Message implements Externalizable {

    private Date mTime;
    private String mText;
    private boolean mReceived;
    private Client mClient;
    private boolean mViewed;

    /**
     * Creates an instance with empty message body
     */
    public Message() {
        this("");
    }

    /**
     * Creates an instance of message
     *
     * @param message Message body
     */
    public Message(String message) {
        mText = message;
        mClient = null;
        mReceived = false;
        mTime = new Date();
        mViewed = false;
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeUTF(mText);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        mText = oi.readUTF();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.mText);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        return this.mText.equals(other.mText);
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return mTime;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.mTime = time;
    }

    /**
     * @return the text
     */
    public String getText() {
        return mText;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.mText = text;
    }

    /**
     * @return true if the receiver of this message is this machine.
     */
    public boolean isReceived() {
        return mReceived;
    }

    /**
     * @param received set if the receiver of this message is this machine.
     */
    public void setReceived(boolean received) {
        this.mReceived = received;
    }

    public boolean isViewed() {
        return mViewed;
    }

    public void setViewed(boolean viewed) { 
        this.mViewed = viewed;
    }

    /**
     * Gets the receiver client, if available.
     *
     * @return {@code null} if not available.
     */
    public Client getClient() {
        return mClient;
    }

    /**
     * Gets the receiver client, if available.
     *
     * @param client client to set
     */
    public void setClient(Client client) {
        this.mClient = client;
    }

    /**
     * Gets the user data of the sender of this message
     *
     * @return UserData of sender
     */
    public UserData getSender() {
        if (mReceived) { // current machine is reciever
            return mClient.getUserData();
        } else {
            return Core.instance().user().getData();
        }
    }

    /**
     * Gets the user data of the receiver of this message
     *
     * @return UserData of receiver
     */
    public UserData getReceiver() {
        if (mReceived) { // current machine is reciever
            return Core.instance().user().getData();
        } else {
            return mClient.getUserData();
        }
    }
}
