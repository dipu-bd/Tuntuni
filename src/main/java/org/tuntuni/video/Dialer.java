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
import javafx.util.Callback;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.models.Logs;

/**
 *
 * @author dipu
 */
public class Dialer implements StreamListener {

    private ObjectProperty<DialStatus> mStatus;

    private final BooleanProperty mSlot;
    private volatile int mAcceptance = -1;

    private Client mClient;
    private VideoPlayer mPlayer;
    private VideoRecorder mRecorder;

    public Dialer() {
        mStatus = new SimpleObjectProperty<>(DialStatus.IDLE);
        mSlot = new SimpleBooleanProperty(false);
    }

    public void dialClient(Client client) throws DialerException {
        mStatus.set(DialStatus.DIALING);
        mClient = client;
        // occupy my slot
        if (!occupySlot()) {
            throw new DialerException("Slot in your pc is unavailable");
        }
        // occupy a slot in client
        if (!client.requestSlot()) {
            throw new DialerException("Slot in client pc is unavailable");
        }
        // start communication
        startComs();
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
        // start communication        
        startComs();
        mStatus.set(DialStatus.BUSY);
    }

    private void endCall(boolean recursive) {
        mStatus.set(DialStatus.IDLE);
        try {
            stopComs();
            freeSlot();
            if (recursive) {
                mClient.endCall();
            }
            mClient = null;
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to end call. Error: {0}", ex);
        }
    }

    public boolean endCall(Client client) {
        if (client != null && mClient != null
                && client.getHostString().equals(mClient.getHostString())) {

            endCall(false);
            return true;
        }
        return false;
    }

    public void terminate() {
        endCall(false);
    }

    public void acceptCallRequest() throws DialerException {
        try {
            mAcceptance = -1;
            Core.instance().videocall().acceptCallDialog(mClient, Thread.currentThread());
            Thread.currentThread().wait();
        } catch (InterruptedException ex) {
            throw new DialerException("Call request Failure. ERROR: " + ex.getMessage());
        }
        if (mAcceptance != 0) {
            throw new DialerException("The call was rejected by the user.");
        }
    }

    public void dialClientAsync(final Client client, Callback<Exception, Void> callback) {
        Thread t = new Thread(() -> {
            try {
                dialClient(client);
                callback.call(null);
            } catch (Exception ex) {
                callback.call(ex);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void receiveCallAsync(final Client client, Callback<Exception, Void> callback) {
        Thread t = new Thread(() -> {
            try {
                receiveCall(client);
                callback.call(null);
            } catch (Exception ex) {
                callback.call(ex);
            }
        });
        t.setDaemon(true);
        t.start();
    }

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    private boolean occupySlot() {
        if (mSlot.get()) {
            return false;
        }
        mSlot.set(true);
        return true;
    }

    private void freeSlot() {
        mSlot.set(false);
    }

    public void informAcceptance(boolean result) {
        mAcceptance = result ? 0 : 1;
    }

    private void startComs() {
        new Thread(() -> {
            try {
                mPlayer = new VideoPlayer(
                        Core.instance().videocall().getViewer());
                mPlayer.setListener(this);
                mPlayer.start();

                mRecorder = new VideoRecorder(
                        mClient.getAddress().getAddress(),
                        mClient.getImagePort(), mClient.getAudioPort());
                mRecorder.setListener(this);
                mRecorder.start();

            } catch (Exception ex) {
                endCall(true);
                Logs.error(getClass(), "Failed to start communication modules. Error: {0}", ex);
            }
        }).start();
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

    @Override
    public void errorOccured(Exception ex) {
        if (!mStatus.equals(DialStatus.IDLE)) {
            endCall(true);
        }
    }
}
