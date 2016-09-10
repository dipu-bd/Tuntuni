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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
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
    public void sendFrame(final DataFrame frame) {
        if (isOkay()) {
            try {
                request(frame.connectedFor(), frame);
                resetFailCounter();
            } catch (Exception ex) {
                increaseFailCounter();
            }
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
