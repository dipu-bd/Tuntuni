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

import org.tuntuni.image.ImageFrame;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import java.awt.Dimension;
import javafx.scene.image.Image;
import org.tuntuni.image.FrameListener;
import org.tuntuni.image.FrameBuilder;
import org.tuntuni.models.Logs;
import org.tuntuni.video.VideoFormat;

/**
 *
 * @author Sudipto Chandra
 */
public class WebcamCapture extends ImageSource implements WebcamListener, FrameListener {

    private Webcam mWebcam;
    private FrameBuilder mFraming;

    public WebcamCapture() {
    }

    @Override
    public String getName() {
        String name = "WebCamCapture";
        if (mWebcam != null) {
            name += mWebcam.getDevice().getName();
        }
        return name;
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
    public void start() {
        mFraming = new FrameBuilder();
        mWebcam = Webcam.getDefault();
        if (mWebcam != null) {
            mFraming.addListener(this);
            this.setMinQueueSize(mFraming.minFrameRate());
            this.setMinQueueSize(mFraming.maxFrameRate());
            
            mWebcam.setViewSize(VideoFormat.getViewSize());
            mWebcam.addWebcamListener(this);
            mWebcam.open(true);
        } else {
            Logs.warning(getClass(), "Webcam not found");
        }
    }

    @Override
    public void stop() {
        if (mWebcam != null) {
            mWebcam.removeWebcamListener(this);
            mWebcam.close();
        }
    }

    @Override
    public boolean isOpen() {
        return mWebcam == null ? false : mWebcam.isOpen();
    }

    @Override
    public void frameUpdated(ImageFrame frame) {
        send(frame);
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {
        mFraming.putImage(we.getImage());
    }

    @Override
    public void imageUpdated(Image image) {
    }

    @Override
    public void webcamOpen(WebcamEvent we) {
    }

    @Override
    public void webcamClosed(WebcamEvent we) {
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {
    }

}
