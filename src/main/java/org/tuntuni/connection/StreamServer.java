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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tuntuni.Core;
import org.tuntuni.models.Logs;
import org.tuntuni.videocall.DialStatus;

/**
 * Receives data from client
 *
 * @author dipu
 */
public abstract class StreamServer {

    private final int mPort;
    private ServerSocket mServer;
    private Socket mClient;
    private ObjectInputStream mInput; 
    private ExecutorService mExecutor;

    public StreamServer(int port) {
        mPort = port;
        mExecutor = Executors.newWorkStealingPool();
    }

    public abstract String getName();

    public void open() throws IOException {
        mServer = new ServerSocket(mPort);
        mExecutor.submit(() -> listen()); 
        Logs.info(getName(), "Opened @ {0}", getPort());
    }

    public void close() {
        try {
            mExecutor.shutdownNow();
            
            if (mServer != null) {
                mServer.close(); 
            }
            if (mClient != null) {
                mClient.close();
                mClient = null;
            } 
        } catch (Exception ex) {
            Logs.warning(getName(), "Failed to close. {0}", ex);
        }
    }

    public void listen() {
        while (isOpen()) {
            try {
                
                dataReceived(receive());
                
            } catch (EOFException ex) {
            } catch (SocketException ex) {
                if (Core.instance().dialer().getStatus() != DialStatus.DIALING) {
                    Logs.error(getName(), "Connection failure! {0}", ex);
                    break;
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logs.error(getName(), "Receive failure. {0}", ex);
            }
        }
    }

    public abstract void dataReceived(Object data);

    public Object receive() throws SocketException, IOException, ClassNotFoundException {
        if (!isConnected()) {
            accept();
        }
        return mInput.readObject();
    }

    private void accept() throws IOException {
        if (!isOpen()) {
            throw new IOException("Server was not initialized");
        }
        mClient = mServer.accept();
        mInput = new ObjectInputStream(mClient.getInputStream());
        Logs.info(getName(), "Accepted client {0}", getRemoteAddress());
    }

    public boolean isOpen() {
        return mServer != null
                && !mServer.isClosed();
    }

    public boolean isConnected() {
        return isOpen()
                && mInput != null
                && mClient != null
                && !mClient.isClosed();
    }

    public int getPort() {
        return mServer == null ? -1 : mServer.getLocalPort();
    }

    public SocketAddress getRemoteAddress() {
        return mClient != null ? mClient.getRemoteSocketAddress() : null;
    }

    @Override
    public String toString() {
        return String.format("%s:%d:%s", getName(), getPort(), getRemoteAddress());
    }

}
