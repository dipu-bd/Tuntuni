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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import org.tuntuni.connection.RTSPClient;

/**
 *
 * @author Sudipto Chandra
 */
public abstract class ImageSource extends RTSPClient {

    /**
     * Gets the name of the image source
     *
     * @return
     */
    @Override
    public String getName() {
        return "ImageSource";
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
    
    @Override
    public String toString() {
        return getName();
    }
}
