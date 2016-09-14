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
package org.tuntuni.videocall.video;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.Logs;

/**
 *
 * @author Sudipto Chandra
 */
public abstract class ImageServer extends StreamServer {

    public Thread mServerThread;

    public ImageServer(int port) {
        super(port);
    }

    @Override
    public String getName() {
        return "ImageServer";
    }

    public void start() {
        mServerThread = new Thread(() -> run());
        mServerThread.setDaemon(true);
        mServerThread.start();
    }

    public void stop() {
        mServerThread.interrupt();
    }

    public void run() {
        while (isOpen()) {
            try {
                
                displayImage((ImageFrame) receive());
                
            } catch (SocketException ex) {
                Logs.error(getName(), "Connection failure! {0}", ex);
                break;
            } catch (EOFException ex) {
            } catch (IOException | ClassNotFoundException ex) {
                Logs.error(getName(), "Receive failure. {0}", ex);
            }
        }
    }

    public abstract void displayImage(ImageFrame frame);

}
