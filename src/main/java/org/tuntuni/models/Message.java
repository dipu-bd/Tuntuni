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

import java.io.Serializable;
import java.util.Date;
import org.tuntuni.connection.Client;

/**
 * To pass message between two users
 */
public class Message implements Serializable {

    // to be serialized
    private Date mTime;
    private String mText;
    private Serializable mContent;

    // not to be serialized
    private transient boolean mReceiver;
    private transient Client mClient;

    /**
     * Creates an instance of this class
     */
    public Message() {

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
     * @return the content
     */
    public Serializable getContent() {
        return mContent;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Serializable content) {
        this.mContent = content;
    }

    /**
     * @return true if the receiver of this message is this machine.
     */
    public boolean isReceiver() {
        return mReceiver;
    }

    /**
     * @param receiver set if the receiver of this message is this machine.
     */
    public void setReceiver(boolean receiver) {
        this.mReceiver = receiver;
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
}
