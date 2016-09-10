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
import org.tuntuni.connection.StreamServer;

/**
 *
 * @author dipu
 */
public class Dialer {

    private Client mClient;
    private BooleanProperty mSlot;
    private StreamServer mServer;
    private VideoCapturer mCapturer;

    public Dialer() {
        mSlot = new SimpleBooleanProperty(false);
        mServer = new StreamServer();
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
        startClient();
        // dispaly video
        displayVideo();
    }

    public void receiveCall(Client client) throws Exception {
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
        startClient();
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

    public void startServer() throws Exception {
        mServer.start();
    }

    public void stopServer() {
        mCapturer.stop();
    }

    public void startClient() {
        mCapturer = new VideoCapturer(mClient);
        mCapturer.initialize();
        mCapturer.start();
    }

    public void stopClient() {
        if (mCapturer != null) {
            mCapturer.stop();
        }
    }

    public int getStreamPort(Client client) throws Exception {
        // check slot avaiability
        if (!mSlot.get()) {
            throw new Exception("Not available");
        }
        // check client
        if (client.getAddress() != mClient.getAddress()) {
            throw new Exception("Client mismatch");
        }
        // wait atmost 10 sec until server is up.
        for (int i = 0; i < 100; ++i) {
            if (mServer.isRunning()) {
                return mServer.getPort();
            }
            Thread.sleep(100);
        }
        return -1;
    }

    public boolean acceptCallRequest() {
        return false;
    }

    public void displayVideo() {

    }
}
