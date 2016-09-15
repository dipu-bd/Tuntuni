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
import java.net.SocketException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tuntuni.Core;
import org.tuntuni.models.Logs;
import org.tuntuni.videocall.DialStatus;

/**
 * Sends data to the server
 *
 * @author dipu
 */
public abstract class StreamClient {

    private Socket mClient;
    private ObjectOutputStream mOutput;
    private InetSocketAddress mAddress;
    private final LinkedList<Object> mSendQueue;
    private int maxQueueSize;    
    private ExecutorService mExecutor;

    public StreamClient(int maxQueue) {
        maxQueueSize = maxQueue;
        mSendQueue = new LinkedList<>();
        mExecutor = Executors.newWorkStealingPool();
    }

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }

    public void connect(InetAddress address, int port) throws IOException {
        mAddress = new InetSocketAddress(address, port);
        mExecutor.submit(() -> run());        

    }

    public void close() {
        try {
            mExecutor.shutdownNow();
            if (mClient != null) {
                mClient.close();
            }
        } catch (Exception ex) {
            Logs.warning(getName(), "Failed to close. {0}", ex);
        } finally {
            mClient = null;
            mOutput = null;
            mSendQueue.clear();
        }
    }

    private void connect() throws IOException {
        if (isConnected()) {
            return;
        }
        mClient = new Socket();
        mClient.connect(mAddress, 5000);
        mOutput = new ObjectOutputStream(mClient.getOutputStream());
        Logs.info(getName(), "Connected to {0}", mAddress);
    }

    private void run() {
        while (!Thread.interrupted()) {
            try {
                connect();
                break;
            } catch (Exception ex) {
                if (Core.instance().dialer().getStatus() != DialStatus.DIALING) {
                    Logs.error(getName(), "Failed to connect. {0}", ex);
                    return;
                }
            }
        }
        // Run consecutive IO operations
        while (!Thread.interrupted()) {
            // Wait for data to become available
            Object data = getNext();
            // Check validity
            if (data == null) {
                continue;
            }
            // Output to stream
            if (!isConnected()) {
                continue;
            }
            try {
                mOutput.writeObject(data);
                mOutput.flush();
            } catch (SocketException ex) {
                if (Core.instance().dialer().getStatus() != DialStatus.DIALING) {
                    Logs.error(getName(), "Socket failure! {0}", ex);
                    break;
                }
            } catch (IOException ex) {
                Logs.error(getName(), "Write failure! {0}", ex);
            }
        }
    }

    private Object getNext() {
        synchronized (mSendQueue) {
            if (mSendQueue.isEmpty()) {
                try {
                    mSendQueue.wait(1000);
                } catch (InterruptedException ex) {
                    Logs.error(getName(), "Waiting interrupted. {0}", ex);
                    return null;
                }
            }
            return mSendQueue.poll();
        }
    }

    public void send(Object frame) {
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
        return mClient != null
                && mOutput != null
                && !mClient.isClosed()
                && mClient.isConnected();
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
