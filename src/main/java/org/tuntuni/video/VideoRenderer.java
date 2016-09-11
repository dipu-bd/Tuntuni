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
package org.tuntuni.video;

import org.tuntuni.video.image.ImageFrame;
import org.tuntuni.video.audio.AudioFrame;
import java.net.InetAddress;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.ImageView;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.tuntuni.connection.StreamClient;
import org.tuntuni.models.Logs;

/**
 *
 * @author dipu
 */
public class VideoRenderer {

    private final int MAX_TOLERANCE = 100;

    private final StreamClient mClient;
    private final ImageView mImage;

    private Thread mAudioThread;
    private Thread mVideoThread;
    private volatile int mFailCount;

    private DataLine.Info mSourceInfo;
    private SourceDataLine mSourceLine; 

    /**
     * Creates a new video renderer instance
     *
     * @param address
     * @param port
     * @param image
     */
    public VideoRenderer(InetAddress address, int port, ImageView image) {
        mClient = new StreamClient(address, port);
        mImage = image;
    }

    public void initialize() {
        // setup video
        try {
            mImage.setSmooth(true);
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize image view. ERROR: {0}.", ex);
        }
        // setup audio
        try {
            mSourceInfo = new DataLine.Info(SourceDataLine.class, VideoFormat.getAudioFormat());
            mSourceLine = (SourceDataLine) AudioSystem.getLine(mSourceInfo);
            mSourceLine.open(VideoFormat.getAudioFormat());
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize speacker. ERROR: {0}", ex);
        }
        // other setup
        resetFailCounter();
    }

    public void start() {
        //  start video thread
        if (mImage != null) {
            mVideoThread = new Thread(() -> imageRunner(), "videoRunner");
            mVideoThread.setDaemon(true);
            mVideoThread.start();
        }
        // setup and start audio thread
        if (mSourceLine != null) {
            mAudioThread = new Thread(() -> audioRunner(), "audioRunner");
            mAudioThread.setDaemon(true);
            mAudioThread.start();
        }
    }

    public void stop() {
        // stop video
        if (mImage != null) {
            mVideoThread.interrupt();
        }
        // stop audio
        if (mSourceLine != null) {
            mAudioThread.interrupt();
            mSourceLine.close();
        }
    }

    private void imageRunner() {
        while (isOkay()) {
            // request image
            ImageFrame frame = mClient.getImage();
            
            if (frame != null) {
                resetFailCounter(); 
                
                // display image 
                Platform.runLater(() -> {
                    mImage.setImage(frame.getImage());
                });
            } else {
                increaseFailCount();
            }
        }
    }

    private void audioRunner() {
        mSourceLine.start();
        while (isOkay()) {
            // request audio
            AudioFrame frame = mClient.getAudio(); 
            
            if (mSourceLine.isOpen() && frame != null) {
                resetFailCounter();
                // play audio
                byte[] data = frame.getBuffer();
                mSourceLine.write(data, 0, data.length);
            } else {
                increaseFailCount();
            }
        }
    }

    public void resetFailCounter() {
        mFailCount = 0;
    }

    public void increaseFailCount() {
        mFailCount++;
    }

    public boolean isOkay() {
        return mFailCount <= MAX_TOLERANCE;
    }
}
