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

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 *
 * @author Sudipto Chandra
 */
public class WebcamImage implements ImageSource {

    private final Webcam mWebcam;

    public WebcamImage() throws WebcamException {
        // get the default webcam
        mWebcam = Webcam.getDefault();
        if (mWebcam == null) {
            throw new WebcamException("Webcam not found!");
        }
    }

    @Override
    public String getName() {
        return "Webcam : " + mWebcam.getDevice().getName();
    }

    @Override
    public BufferedImage getImage() {
        return mWebcam.getImage(); 
    }

    @Override
    public Dimension getSize() {
        return mWebcam.getViewSize();
    }

    @Override
    public void setSize(Dimension size) {
        mWebcam.setViewSize(size);
    }

    @Override
    public void open() {
        mWebcam.open();
    }

    @Override
    public void close() {
        mWebcam.close();
    }

    @Override
    public boolean isOpen() {
        return mWebcam.isOpen();
    }

    @Override
    public boolean isImageNew() {
        return mWebcam.isImageNew();
    }
}
