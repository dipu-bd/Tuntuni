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
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import org.tuntuni.models.Logs;

/**
 *
 * @author Sudipto Chandra
 */
public class WebcamCapture implements ImageSource {

    private Webcam mWebcam;

    public WebcamCapture() {
    }

    @Override
    public String getName() {
        return "WebcamCapture:" + mWebcam.getDevice().getName();
    }

    @Override
    public BufferedImage getImage() {
        return mWebcam == null ? null : mWebcam.getImage();
    }

    @Override
    public Dimension getSize() {
        return mWebcam == null ? null : mWebcam.getViewSize();
    }

    @Override
    public void setSize(Dimension size) {
        if (mWebcam != null) {
            mWebcam.setViewSize(size);
        }
    }

    @Override
    public void open() {
        mWebcam = Webcam.getDefault();
        if (mWebcam != null) {
            mWebcam.open();
        } else {
            Logs.warning(getClass(), "Webcam not found");
        }
    }

    @Override
    public void close() {
        if (mWebcam != null) {
            mWebcam.close();
        }
    }

    @Override
    public boolean isOpen() {
        return mWebcam == null ? false : mWebcam.isOpen();
    }

    @Override
    public boolean isImageNew() {
        return mWebcam == null ? false : mWebcam.isImageNew();
    }
}
