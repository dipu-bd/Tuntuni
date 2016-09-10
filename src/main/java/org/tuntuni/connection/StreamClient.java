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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.video.DataFrame;

/**
 * To manage connection with stream server sockets.
 * <p>
 * To connect with the server you have to call {@linkplain connect()}. </p>
 */
public class StreamClient extends TCPClient {

    public final int MAX_TOLERANCE = 40;
    public final int MAX_CONCURRENT_CONNECTION = 15;

    private int mFailCounter;

    /**
     * Creates a new Stream client.
     *
     * @param address
     * @param port
     */
    public StreamClient(InetAddress address, int port) {
        super(new InetSocketAddress(address, port));
        mFailCounter = 0;
    }

    /**
     * Sends a datagram packet with given DataFrame in separate thread
     *
     * @param frame
     */
    public void sendPacket(final DataFrame frame) {
        if (!isOkay()) {
            return;
        }
        try {
            if (frame.connectedFor() == ConnectFor.IMAGE) {
                request(frame.connectedFor(), frame);
            }
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to send packet! {0}", ex);
            increaseFailCounter();
        }
    }

    public void resetFailCounter() {
        mFailCounter = 0;
    }

    public void increaseFailCounter() {
        mFailCounter++;
    }

    public boolean isOkay() {
        return mFailCounter <= MAX_TOLERANCE;
    }
}
