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
package org.tuntuni.models;

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
    CALL_REQUEST(5),
    // accept response
    CALL_RESPONSE(6),
    // to end an ongoing call
    END_CALL(7);

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
