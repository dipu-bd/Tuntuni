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

/**
 *
 * @author Sudipto Chandra
 */
public interface ImageSource {

    /**
     * Gets the name of the image source
     *
     * @return
     */
    public String getName();

    /**
     * Gets current image from the source
     *
     * @return
     */
    public BufferedImage getImage();

    /**
     * True if a new image is available that has not yet been pooled
     *
     * @return
     */
    public boolean isImageNew();

    /**
     * Gets the view size or dimension of captured images
     *
     * @return
     */
    public Dimension getSize();

    /**
     * Sets the view size or dimension to capture
     *
     * @param size
     */
    public void setSize(Dimension size);

    /**
     * Open the image source to capture
     */
    public void open();

    /**
     * Close the image source
     */
    public void close();

    /**
     * Checks if the source is open
     *
     * @return
     */
    public boolean isOpen();

    /**
     * Gets the name
     *
     * @return
     */
    @Override
    public String toString();
}
