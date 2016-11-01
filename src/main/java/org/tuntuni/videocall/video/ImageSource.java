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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import org.tuntuni.connection.StreamClient;
import org.tuntuni.videocall.VideoFormat;

/**
 *
 * @author Sudipto Chandra
 */
public abstract class ImageSource extends StreamClient {

    private long lastImageTime;
    
    public ImageSource() {
        super(5);
    }
 
    /**
     * Open the image source to capture
     */
    public abstract void start();

    /**
     * Close the image source
     */
    public abstract void stop();

    /**
     * Gets the view size or dimension of captured images
     *
     * @return
     */
    public abstract Dimension getSize();

    /**
     * Sets the view size or dimension to capture
     *
     * @param size
     */
    public abstract void setSize(Dimension size);

    /**
     * Checks if the source is open
     *
     * @return
     */
    public abstract boolean isOpen();
    
    /**
     * To send an obtained image to stream client
     * @param image 
     */
    public void sendImage(BufferedImage image) {
        long time = System.currentTimeMillis();
        if (checkFrameRate(time)) {
            send(new ImageFrame(image));
            lastImageTime = time;
        }
    }   
    
    private boolean checkFrameRate(long time) {
        time -= lastImageTime;
        time *= VideoFormat.FRAME_RATE;
        return time + 50 > 1000;
    }
}
