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
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import org.tuntuni.models.Logs;

/**
 * Receives data from client
 *
 * @author dipu
 */
public abstract class RTSPServer {

    private ServerSocket mServer;
    private Socket mClient;
    private ObjectInputStream mInput; 

    public RTSPServer() {
    }

    public abstract String getName();
 
    public void open() throws IOException {
        mServer = new ServerSocket(0);
        Logs.info(getName(), "Opened @ {0}", getPort());
    }

    public void close() {
        try {
            if (mServer != null) {
                mServer.close();
                mServer = null;
            }
            if (mClient != null) {
                mClient.close();
                mClient = null;
            }
            if (mInput != null) {
                mInput.close();
                mInput = null;
            }
        } catch (Exception ex) {
            Logs.error(getName(), "Failed to close. {0}", ex);
        }
    }

    public int getPort() {
        return mServer == null ? -1 : mServer.getLocalPort();
    }

    public Object receive() throws IOException, ClassNotFoundException {
        if (!isConnected()) {
            accept();
        }
        return mInput.readObject();
    }

    private void accept() throws IOException {
        if (!isOpen()) {
            throw new IOException("Server not initialized");
        }
        mClient = mServer.accept();
        mInput = new ObjectInputStream(mClient.getInputStream());
        Logs.info(getName(), "Accepted client {0}", getRemoteAddress());
    }

    public SocketAddress getRemoteAddress() {
        return mClient != null ? mClient.getRemoteSocketAddress() : null;
    }

    @Override
    public String toString() {
        return String.format("%s:%d:%s", getName(), getPort(), getRemoteAddress());
    }

    public boolean isOpen() {
        return mServer != null && mServer.isBound();
    }

    public boolean isConnected() {
        return isOpen() && mClient != null && !mClient.isClosed() && mInput != null;
    }
}
