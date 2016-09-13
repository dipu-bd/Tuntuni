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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.models.Logs;

/**
 *
 * @author dipu
 */
public class Dialer {

    public static final int IMAGE_PORT = 44731;
    public static final int AUDIO_PORT = 44732;

    private final ObjectProperty<DialStatus> mStatus;
    private Client mClient;
    private VideoPlayer mPlayer;
    private VideoRecorder mRecorder;

    public Dialer() {
        mStatus = new SimpleObjectProperty<>(DialStatus.IDLE);
    }

    public void dial(Client client) throws DialerException {
        try {
            mClient = client;
            // occupy my slot
            if (mStatus.get() != DialStatus.IDLE) {
                throw new DialerException("Your are already in a call");
            }
            // occupy a slot in client 
            mStatus.set(DialStatus.DIALING);
            DialerException ex = (DialerException) client.requestSlot();
            if (ex != null) {
                throw ex;
            }
            // start communication
            startComs();
        } catch (Exception ex) {
            stopComs();
            throw ex;
        }
    }

    public void receive(Client client) throws DialerException {
        try {
            mClient = client;
            // check if my slot is available
            if (mStatus.get() != DialStatus.IDLE) {
                throw new DialerException("Your are already in a call");
            }
            // request user to accept the call 
            mStatus.set(DialStatus.DIALING);
            boolean res = Core.instance().videocall().acceptCallDialog(mClient);
            if (!res) {
                throw new DialerException("Call was rejected");
            }
            // start communication        
            startComs();
        } catch (Exception ex) {
            stopComs();
            throw ex;
        }
    }

    public void stop() {
        stopComs();
        mClient = null;
        mStatus.set(DialStatus.IDLE);
    }

    public void endCall() {
        if (mClient != null && mStatus.get() != DialStatus.IDLE) {
            stop();
            new Thread(() -> mClient.endCall()).start();
        }
    }

    public boolean endCall(Client client) {
        if (client != null && mClient != null
                && client.getHostString().equals(mClient.getHostString())) {
            stop();
            return true;
        }
        return false;
    }

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    private void startComs() throws DialerException {
        try {
            mPlayer = new VideoPlayer(Core.instance().videocall().getViewer());
            mPlayer.start();

            mRecorder = new VideoRecorder(mClient.getAddress().getAddress());
            mRecorder.start();
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to start coms. {0}", ex);
            throw new DialerException("Failed to start communication.");
        }
    }

    private void stopComs() {
        if (mRecorder != null) {
            mRecorder.stop();
        }
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    public VideoPlayer player() {
        return mPlayer;
    }

    public VideoRecorder recorder() {
        return mRecorder;
    }

    public ObjectProperty<DialStatus> statusProperty() {
        return mStatus;
    }

    public DialStatus getStatus() {
        return mStatus.get();
    }

}
