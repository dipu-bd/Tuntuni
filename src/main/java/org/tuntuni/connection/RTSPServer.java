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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.tuntuni.models.Logs;

/**
 *
 * @author dipu
 */
public abstract class RTSPServer implements Runnable {

    private Thread mServerThread;
    private SocketChannel mClient;
    private ServerSocketChannel mChannel;

    public RTSPServer() {
    }

    public void start() {
        try {
            mChannel = ServerSocketChannel.open();
            mChannel.configureBlocking(false);

            mServerThread = new Thread(this);
            mServerThread.setDaemon(true);
            mServerThread.start();
        } catch (IOException ex) {
            Logs.error(getClass(), "Failed to open channel. ERROR: {0}", ex);
        }
    }

    @Override
    public void run() {
        try {
            mClient = mChannel.accept();
            mClient.register(, 0)
            
        } catch (IOException ex) {
            Logs.error(getClass(), "Failed to accept channel. ERROR: {0}", ex);
        }
    }

    /**
     * Aquire data to send
     *
     * @return
     */
    public abstract byte[] aquireData();
}
