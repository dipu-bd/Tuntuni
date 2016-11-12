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
package org.tuntuni.videocall;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.videocall.video.ImageSource;

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

    public void setShareScreen(boolean val) {
        mRecorder.setScreen(val);
    }

    public Exception dial(Client client) {
        try {
            // check result
            if (client == null) {
                throw new Exception("Invalid user");
            }
            // occupy my slot
            if (getStatus() != DialStatus.IDLE) {
                throw new Exception("You are already in a call");
            }
            // occupy call slot
            synchronized (mStatus) {
                mStatus.set(DialStatus.DIALING);
                mClient = client;
                mStatus.notify();
            }
            // send dial request
            mClient.callRequest(ConnectFor.CALL_REQUEST, null);
            // immediately start
            start();

        } catch (Exception ex) {
            stop();
            return ex;
        }
        return null;
    }

    public Exception receiveResponse(Client client, Exception err) {
        try {
            // check result
            if (err != null) {
                throw err;
            }
            // check client
            if (client != mClient) {
                throw new Exception("User mismatch");
            }
            // change the status
            synchronized (mStatus) {
                mStatus.set(DialStatus.BUSY);
                mStatus.notify();
            }
        } catch (Exception ex) {
            stop();
            Logs.error(getClass(), ex.getMessage());
            return ex;
        }
        return null;
    }

    public Exception receive(Client client) {
        try {
            // check client
            if (client == null) {
                throw new Exception("Invalid user");
            }
            // check if user is available
            if (getStatus() != DialStatus.IDLE) {
                throw new Exception("User is busy");
            }
            // change status 
            synchronized (mStatus) {
                mStatus.set(DialStatus.DIALING);
                mClient = client;
                mStatus.notify();
            }
            // request user to accept the call 
            Platform.runLater(() -> {
                Core.instance().videocall().acceptCallDialog(mClient);
            });
        } catch (Exception ex) {
            stop();
            return ex;
        }
        return null;
    }

    public void acceptResponse(Client client, Exception err) {
        try {
            // check client
            if (client != mClient) {
                throw new Exception("User mismatch");
            }
            // start communication   
            if (err == null) {
                start();
            }
            // send accept notification
            mClient.callRequest(ConnectFor.CALL_RESPONSE, err);
            if (err != null) {
                throw err;
            }
            // client should be busy
            synchronized (mStatus) {
                mStatus.set(DialStatus.BUSY);
                mStatus.notify();
            }
        } catch (Exception ex) {
            Logs.error(getClass(), ex.getMessage());
            stop();
        }
    }

    public void endCall() {
        if (getStatus() != DialStatus.IDLE) {
            // stop remote
            if (mClient != null) {
                new Thread(() -> {
                    try {
                        mClient.callRequest(ConnectFor.END_CALL, null);
                    } catch (Exception ex) {
                    }
                }).start();
            }
            // stop local
            stop();
        }
    }

    public void endCall(Client client) {
        if (getStatus() != DialStatus.IDLE && client == mClient) {
            endCall();
        }
    }

//                                                                            //
////////////////////////////////////////////////////////////////////////////////
//                                                                            // 
    private void start() throws Exception {
        try {
            mPlayer = new VideoPlayer(Core.instance().videocall().getViewer());
            mPlayer.start();

            mRecorder = new VideoRecorder(mClient.getAddress());
            mRecorder.start();

        } catch (Exception ex) {
            Logs.error(getClass(), ex.getMessage());
            throw new Exception("Failed to start channel. ERROR: " + ex.getMessage());
        }
    }

    public void stop() {
        synchronized (mStatus) {
            mStatus.set(DialStatus.IDLE);
            if (mRecorder != null) {
                mRecorder.stop();
            }
            if (mPlayer != null) {
                mPlayer.stop();
            }
            mClient = null;
            mStatus.notify();
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
        synchronized (mStatus) {
            return mStatus.get();
        }
    }

}
