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
package org.tuntuni.video;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.tuntuni.connection.Client;

/**
 *
 * @author dipu
 */
public class Dialer {

    public Client mClient;
    public BooleanProperty mSlot;

    public Dialer() {
        mSlot = new SimpleBooleanProperty(false);
    }

    public void dialClient(Client client) throws Exception {
        mClient = client;
        // occupy my slot
        if (!occupySlot()) {
            throw new Exception("Slot in your pc is unavailable");
        }
        // occupy a slot in client
        if (!client.requestSlot()) {
            throw new Exception("Slot in client pc is unavailable");
        }
        // start audio and video server
        startServer();
        // get stream server address of client
        startClient(client.requestRTSPAddress());
        // dispaly video
        displayVideo();
    }

    public void receiveClient(Client client) throws Exception {
        mClient = client;
        // check if my slot is available
        if (mSlot.get()) {
            throw new Exception("Slot is already occupied");
        }
        // request user to accept the call
        if (!acceptCallRequest()) {
            throw new Exception("Call was rejected by you");
        }
        // occupy my slot
        if (!occupySlot()) {
            throw new Exception("Slot in your pc is unavailable");
        }
        // start audio and video server
        startServer();
        // get stream server address of client
        startClient(client.requestRTSPAddress());
        // display video
        displayVideo();
    }

    public void endCall() {
        stopClient();
        stopServer();
        mClient = null;
        mSlot.set(false);
    }

    public boolean occupySlot() {
        if (mSlot.get()) {
            return false;
        }
        mSlot.set(true);
        return true;
    }

    public boolean acceptCallRequest() {
        return false;
    }

    public void displayVideo() {

    }

    public void startServer() {

    }

    public void stopServer() {

    }

    public String getRTPAddress() {
        return "";
    }

    public void startClient(String address) {

    }

    public void stopClient() {

    }

}
