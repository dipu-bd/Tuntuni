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
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import java.awt.Dimension;
import javafx.application.Platform;
import org.tuntuni.models.Logs;
import org.tuntuni.video.VideoFormat;

/**
 *
 * @author Sudipto Chandra
 */
public class WebcamCapture extends ImageSource implements WebcamListener {

    private Webcam mWebcam;

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
        // start webcam
        mWebcam = Webcam.getDefault();
        if (mWebcam != null) {
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
    public void webcamImageObtained(WebcamEvent we) {
        try {
            send(new ImageFrame(we.getImage()));
        } catch (Exception ex) {
            if (getListener() != null) {
                Platform.runLater(() -> getListener().errorOccured(ex));
            }
        }
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
