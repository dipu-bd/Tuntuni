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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;
import org.tuntuni.video.DataFrame;

/**
 * To manage connection with stream server sockets.
 * <p>
 * To connect with the server you have to call {@linkplain connect()}. </p>
 */
public class StreamClient {

    public final int MAX_TOLERANCE = 100;
    public final int MAX_CONCURRENT_CONNECTION = 15;

    private int mFailCounter;
    private final int mPort;
    private final InetAddress mAddress;

    /**
     * Creates a new Stream client.
     * @param address
     * @param port
     */
    public StreamClient(InetAddress address, int port) {
        mFailCounter = 0;
        mPort = port;
        mAddress = address;
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
            // create new instance of socket
            int port = Commons.getRandom(10_000, 65500);
            DatagramSocket socket = createSocket();
 
            // data to send
            byte[] sendData = Commons.toBytes(frame);
            // Send the broadcast package! 
            socket.send(new DatagramPacket(sendData, sendData.length, mAddress, mPort));
            // reset fail counter
            resetFailCounter();

            socket.close();

        } catch (Exception ex) {
            Logs.severe(null, ex);
            increaseFailCounter();
        }
    }

    public DatagramSocket createSocket() throws Exception {
        // create new instance of socket 
        for (int i = 0; i < 10; ++i) {
            try {
                int port = Commons.getRandom(10_000, 65500);
                return new DatagramSocket(port);
            } catch (SocketException ex) {
            }
        }
        throw new NullPointerException();
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
