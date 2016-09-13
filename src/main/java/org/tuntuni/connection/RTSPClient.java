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
package org.tuntuni.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import javafx.application.Platform;
import org.tuntuni.models.Logs;
import org.tuntuni.video.StreamListener;

/**
 * Sends data to the server
 *
 * @author dipu
 */
public abstract class RTSPClient {

    private Socket mClient;
    private ObjectOutputStream mOutput;
    private Thread mClientThread;
    private InetSocketAddress mAddress;
    private final LinkedList<Object> mSendQueue;
    private int maxQueueSize;
    private StreamListener mListener;

    public RTSPClient() {
        maxQueueSize = 5;
        mSendQueue = new LinkedList<>();
    }

    public abstract String getName();

    public void setListener(StreamListener listener) {
        mListener = listener;
    }

    public StreamListener getListener() {
        return mListener;
    }

    public void connect(InetAddress address, int port) {
        mAddress = new InetSocketAddress(address, port);
        mClientThread = new Thread(() -> run());
        mClientThread.setName(getName());
        mClientThread.setDaemon(true);
        mClientThread.start();
    }

    public void close() {
        try {
            mClientThread.interrupt();
            if (mOutput != null) {
                mOutput.close();
            }
            if (mClient != null) {
                mClient.close();
            }
            mClientThread.interrupt();
        } catch (Exception ex) {
            Logs.error(getName(), "Failed to close. {0}", ex);
        }
    }

    private void run() {
        try {
            // Connect with the server
            if (!makeConnection()) {
                return;
            }

            Logs.info(getName(), "Connected @ {0}", mAddress);
            // Run consecutive IO operations
            while (true) {
                // Wait for data to become available
                Object data = getNext();
                // Check validity
                if (data == null) {
                    continue;
                }
                // Output to stream
                try {
                    mOutput.writeObject(data);
                    mOutput.flush();
                } catch (IOException ex) {
                    Logs.error(getName(), "Write failure! {0}", ex);
                }
            }
        } catch (Exception ex) {
            if (getListener() != null) {
                Platform.runLater(() -> getListener().errorOccured(ex));
            }
        }
    }

    private boolean makeConnection() {
        try {
            mClient = new Socket();
            mClient.connect(mAddress);
            mOutput = new ObjectOutputStream(mClient.getOutputStream());
            return true;
        } catch (IOException ex) {
            Logs.error(getName(), "Connection failure. {0}", ex);
            return false;
        }
    }

    private Object getNext() {
        synchronized (mSendQueue) {
            while (mSendQueue.isEmpty()) {
                try {
                    mSendQueue.wait();
                } catch (InterruptedException ex) {
                    Logs.error(getName(), "Waiting interrupted. {0}", ex);
                }
            }
            return mSendQueue.remove();
        }
    }

    public void send(Object frame) {
        // check if connected
        if (!isConnected()) {
            return;
        }
        // Add to queue
        synchronized (mSendQueue) {
            mSendQueue.add(frame);
            if (mSendQueue.size() > maxQueueSize) {
                mSendQueue.remove();
            }
            mSendQueue.notify();
        }
    }

    public void send(byte[] data, int size) {
        send(new DataFrame(data, size));
    }

    public boolean isConnected() {
        return mClient != null && mClient.isConnected() && mOutput != null;
    }

    public InetSocketAddress getAddress() {
        return mAddress;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int size) {
        maxQueueSize = Math.max(1, size);
    }
}
