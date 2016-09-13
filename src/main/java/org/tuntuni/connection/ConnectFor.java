/*
 * Copyright 2016 Sudipto Chandra.
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

/**
 * Defined a few types of connection status client and server.
 */
public enum ConnectFor {

    INVALID(0),
    // to pass the port of main server
    PORT(1),
    // to pass the state information
    STATE(2),
    // to pass user profile information
    PROFILE(3),
    // to pass a single message
    MESSAGE(4),
    // to receive a call
    DIAL(5),
    // to pass audio 
    AUDIO(6),
    // to pass video 
    IMAGE(7),
    // to pass image port 
    IMAGE_PORT(8),
    // to pass audio port
    AUDIO_PORT(9),
    // to end an ongoing call
    END_CALL(10);

    private final int mData;

    ConnectFor(int data) {
        mData = data;
    }

    public byte data() {
        return (byte) mData;
    }

    public static ConnectFor from(byte data) {
        for (ConnectFor status : ConnectFor.values()) {
            if (status.data() == data) {
                return status;
            }
        }
        return ConnectFor.INVALID;
    }
}
