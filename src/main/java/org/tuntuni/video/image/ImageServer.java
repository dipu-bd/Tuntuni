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

import java.io.EOFException;
import java.io.IOException;
import javafx.application.Platform;
import org.tuntuni.connection.RTSPServer;
import org.tuntuni.models.Logs;
import org.tuntuni.video.StreamListener;

/**
 *
 * @author Sudipto Chandra
 */
public abstract class ImageServer extends RTSPServer {

    public Thread mServerThread;

    public ImageServer() {
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
        try {
            while (isOpen()) {
                try {
                    displayImage((ImageFrame) receive());
                } catch (IOException | ClassNotFoundException ex) {
                    if (!(ex instanceof EOFException)) {
                        Logs.error(getName(), "Receive failure. {0}", ex);
                    }
                }
            }
        } catch (Exception ex) {
            if (getListener() != null) {
                Platform.runLater(() -> getListener().errorOccured(ex));
            }
        }
    }

    public abstract void displayImage(ImageFrame frame);

}
