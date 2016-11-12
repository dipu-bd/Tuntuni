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

import com.github.sarxos.webcam.Webcam;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.tuntuni.connection.StreamClient;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;
import org.tuntuni.videocall.VideoFormat;

/**
 *
 * @author Sudipto Chandra
 */
public class ImageSource extends StreamClient {

    private long lastImageTime;
    private Webcam mWebcam;
    private boolean mScreen;
    private final ScheduledExecutorService mExecutor;

    public ImageSource() {
        super(5);
        mScreen = false;
        mExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Checks if the source is open
     *
     * @return
     */
    public boolean isOpen() {
        if (mScreen) {
            return !mExecutor.isShutdown();
        } else {
            return mWebcam == null ? false : mWebcam.isOpen();
        }
    }

    /**
     * Open the image source to capture
     */
    public void start() {
        if (!mScreen) {
            openWebcam();
        }
        mExecutor.scheduleAtFixedRate(threadRunner, 0, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Close the image source
     */
    public void stop() {
        mExecutor.shutdown();
        closeWebcam();
    }

    public void setScreen(boolean screen) {
        if (mScreen == screen) {
            return;
        }
        if (screen) {
            if (mWebcam != null) {
                mWebcam.close();
            }
        } else {
            openWebcam();
        }
        mScreen = screen;
    }

    public boolean isScreen() {
        return mScreen;
    }

    /**
     * Gets the view size or dimension of captured images
     *
     * @return
     */
    public Dimension getSize() {
        if (mScreen) {
            return VideoFormat.getViewSize();
        } else {
            return mWebcam == null ? null : mWebcam.getViewSize();
        }
    }

    /**
     * Sets the view size or dimension to capture
     *
     * @param size
     */
    public void setSize(Dimension size) {
        if (mWebcam != null) {
            mWebcam.setViewSize(size);
        }
    }

    private void openWebcam() {
        // start webcam 
        mWebcam = Webcam.getDefault();
        if (mWebcam != null) {
            mWebcam.open(true);
        } else {
            Logs.warning(getClass(), "Webcam not found");
        }
    }

    private void closeWebcam() {
        if (mWebcam != null) {
            mWebcam.close();
            mWebcam = null;
        }
    }

    private final Runnable threadRunner = new Runnable() {
        @Override
        public void run() {
            try {
                if (mScreen) {
                    sendScreen();
                } else {
                    sendWebcam();
                }
            } catch (Exception ex) {
                Logs.error(getName(), "ERROR: {0}", ex);
            }
        }
    };

    private void sendScreen() throws AWTException {
        // get desktop
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenRectangle);

        image = Commons.resizeImage(image, VideoFormat.WIDTH, VideoFormat.HEIGHT);
        sendImage(image);
    }

    private void sendWebcam() {
        if (mWebcam != null) {
            BufferedImage image = mWebcam.getImage();
            image = Commons.resizeImage(image, VideoFormat.WIDTH, VideoFormat.HEIGHT);
            sendImage(image);
        }
    }

    /**
     * To send an obtained image to stream client
     *
     * @param image
     */
    private void sendImage(BufferedImage image) {
        long time = System.currentTimeMillis();
        if (checkFrameRate(time)) {
            send(new ImageFrame(image));
            lastImageTime = time;
        }
    }

    // to check if frame rate is maintaining
    private boolean checkFrameRate(long time) {
        time -= lastImageTime;
        time *= VideoFormat.FRAME_RATE;
        return time + 50 > 1000;
    }

    @Override
    public String getName() {
        return mScreen ? "ScreenSource" : "WebcamSource";
    }
}
