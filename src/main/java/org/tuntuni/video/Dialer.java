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
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.Logs;

/**
 *
 * @author dipu
 */
public class Dialer {

    private Client mClient;
    private VideoCapturer mCapturer;
    private StreamServer mServer;
    private final BooleanProperty mSlot;
    private VideoRenderer mRenderer;
    private volatile int mAcceptance = -1;

    public Dialer() {
        mSlot = new SimpleBooleanProperty(false);
    }

    public void dialClient(Client client) throws DialerException {
        mClient = client;
        // occupy my slot
        if (!occupySlot()) {
            throw new DialerException("Slot in your pc is unavailable");
        }
        try {
            // occupy a slot in client
            if (!client.requestSlot()) {
                throw new DialerException("Slot in client pc is unavailable");
            }
            // start audio and video server
            startServer();
            // start video capturer
            startClient();
            // start videp renderer
            displayVideo();
        } catch (DialerException ex) {
            Logs.severe(null, ex);
            // free slot
            freeSlot();
            throw ex;
        }
    }

    public void receiveCall(Client client) throws DialerException {
        mClient = client;
        // check if my slot is available
        if (mSlot.get()) {
            throw new DialerException("Slot is already occupied");
        }
        // request user to accept the call
        acceptCallRequest();
        // occupy my slot
        if (!occupySlot()) {
            throw new DialerException("Slot in your pc is unavailable");
        }
        try {
            // start audio and video server
            startServer();
            // start video capturer
            startClient();
            // start video renderer
            displayVideo();
        } catch (DialerException ex) {
            Logs.severe(null, ex);
            freeSlot();
            throw ex;
        }
    }

    public void endCall() {
        stopClient();
        stopServer();
        mClient = null;
        freeSlot();
    }

    public boolean occupySlot() {
        if (mSlot.get()) {
            return false;
        }
        mSlot.set(true);
        Core.instance().videocall().callStarting();
        return true;
    }

    public void freeSlot() {
        mSlot.set(false);
        Core.instance().videocall().callEnded();
    }

    public void startServer() throws DialerException {
        try {
            mServer = new StreamServer();
            mServer.start();
            if (mServer.getPort() == -1) {
                throw new Exception("Server start failed!");
            }
        } catch (Exception ex) {
            Logs.severe(null, ex);
            throw new DialerException("Failed to start server");
        }
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

    public int getStreamPort() {
        try {
            // wait atmost 10 sec until server is up.
            for (int i = 0; i < 100; ++i) {
                if (mServer.isRunning()) {
                    return mServer.getPort();
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            Logs.error(getClass(), "Failed to aquire stream port. ERROR: {0}", ex);
        }
        return -1;
    }

    public void acceptCallRequest() throws DialerException {
        try {
            Core.instance().videocall().acceptCallDialog(mClient);
            while (mAcceptance == -1) {
                Thread.sleep(100);
            }
            if (mAcceptance != 0) {
                throw new DialerException("The call was rejected by the user.");
            };
        } catch (Exception ex) {
            Logs.severe(null, ex);
            throw new DialerException("Call request Failure. ERROR: " + ex.getMessage());
        }
    }

    public void informAcceptance(boolean result) {
        mAcceptance = result ? 0 : 1;
    }

    public void displayVideo() throws DialerException {
        try {
            Core.instance().videocall().prepareDisplay();
            mRenderer = new VideoRenderer(mServer, Core.instance().videocall().getVideoImage());
            mRenderer.initialize();
            mRenderer.start();
        } catch (Exception ex) {
            Logs.severe(null, ex);
            throw new DialerException("Failed to display image");
        }
    }
}
