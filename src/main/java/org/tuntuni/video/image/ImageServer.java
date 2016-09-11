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
package org.tuntuni.video.image;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.models.Logs;

/**
 *
 * @author Sudipto Chandra
 */
public class ImageServer implements Runnable {

    private Thread mServerThread;
    private DatagramSocket mSocket;
    private final ImageCapture mCapture;

    public ImageServer(ImageCapture capture) {
        mCapture = capture;
    }

    public void open() throws SocketException {
        mSocket = new DatagramSocket();
        mServerThread = new Thread(this);
        mServerThread.setDaemon(true);
        mServerThread.start();
    }

    public void close() {
        mSocket.close();
        mServerThread.interrupt();
    }

    public int getPort() {
        return mSocket.getLocalPort();
    }

    public void connect(InetAddress address, int port) {
        mSocket.connect(address, port);
    }

    @Override
    public void run() {
        while (!mSocket.isClosed() && mSocket.isBound()) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.send(getNext());
                } catch (IOException ex) {
                    Logs.error(getClass(), "Failed to send packet. ERROR: {0}", ex);
                }
            }
        }
    }
    
    private DatagramPacket getNext() {
        
    }
}
