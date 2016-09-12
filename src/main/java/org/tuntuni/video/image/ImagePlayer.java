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

import java.net.SocketException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import org.tuntuni.models.Logs;

/**
 *
 * @author Sudipto Chandra
 */
public class ImagePlayer implements Runnable {

    // time to wait between to successive display operation
    public static final int WAIT_INTERVAL = 25; // milliseconds

    private final ImageClient mClient;
    private Thread mPlayerThread;
    private ObjectProperty<Image> mImage;

    public ImagePlayer(ImageClient client) {
        mClient = client;
        mImage = new SimpleObjectProperty<>(null);
    }

    /**
     * Starts the player
     *
     * @throws SocketException
     */
    public void start() throws SocketException { 
        mPlayerThread = new Thread(this);
        mPlayerThread.setDaemon(true);
        mPlayerThread.start();
    }

    /**
     * Stops the player
     */
    public void stop() { 
        mPlayerThread.interrupt();
    }
    
    /**
     * Checks whether player is active
     * @return 
     */
    public boolean isActive() {
        return mClient.isOpen();
    }

    @Override
    public void run() {
        while (isActive()) {
            try {
                display();
                Thread.sleep(WAIT_INTERVAL);
            } catch (InterruptedException ex) {
                Logs.error(getClass(), "Thread sleep was interrupted. ERROR: {0}", ex);
            }
        }
    }

    private void display() {
        if (!mClient.isImageNew()) {
            return;
        }
        // gets the image frame
        ImageFrame frame = mClient.getFrame();
        if (frame == null) {
            return;
        }
        // update image
        updateImage(frame.getImage());
    }

    private void updateImage(Image image) {
        mImage.set(image);
    }

    /**
     * Gets the image
     *
     * @return
     */
    public Image getImage() {
        return mImage.get();
    }

    /**
     * Gets the image property
     *
     * @return
     */
    public ObjectProperty<Image> imageProperty() {
        return mImage;
    }
}
