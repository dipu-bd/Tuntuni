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

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.Logs;

/**
 *
 * @author dipu
 */
public class VideoRenderer {

    private final StreamServer mServer;
    private final ImageView mImage;

    private Thread mAudioThread;
    private Thread mVideoThread;
    private DataLine.Info mSourceInfo;
    private SourceDataLine mSourceLine;

    /**
     * Creates a new video renderer instance
     *
     * @param server
     * @param image
     */
    public VideoRenderer(StreamServer server, ImageView image) {
        mServer = server;
        mImage = image;
    }

    public void initialize() {
        // setup audio
        try {
            mSourceInfo = new DataLine.Info(SourceDataLine.class, VideoFormat.getAudioFormat());
            mSourceLine = (SourceDataLine) AudioSystem.getLine(mSourceInfo);
            mSourceLine.open(VideoFormat.getAudioFormat());
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize speacker. ERROR: {0}", ex);
        }

        // setup video
        try {
            mImage.setSmooth(true);
            mImage.setFitWidth(VideoFormat.WIDTH);
            mImage.setFitHeight(VideoFormat.HEIGHT);
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize image view. ERROR: {0}.", ex);
        }
    }

    public void start() {
        // set start time of capturing 
        // setup and start audio thread
        if (mSourceLine != null) {
            mAudioThread = new Thread(() -> audioRunner(), "audioRunner");
            mAudioThread.setDaemon(true);
            mAudioThread.start();
        }
        // setup and start video thread
        if (mImage != null) {
            mVideoThread = new Thread(() -> imageRunner(), "videoRunner");
            mVideoThread.setDaemon(true);
            mVideoThread.start();
        }
    }

    public void stop() {
        // stop audio
        if (mSourceLine != null) {
            mSourceLine.close();
            mAudioThread.interrupt();
        }
        // stop video
        if (mImage != null) {
            mVideoThread.interrupt();
        }
    }

    private void imageRunner() {
        if (mImage == null) {
            return;
        }
        // run image capture loop
        while (mServer.isRunning()) {
            // get single image
            ImageFrame frame = mServer.getImageFrame();
            if (frame == null) {
                continue;
            }
            // display image
            Platform.runLater(() -> {
                mImage.setImage(frame.getImage());
            });
        }
    }

    private void audioRunner() {
        if (mSourceLine == null) {
            return;
        }
        // start target line
        mSourceLine.start();
        // run capture loop
        while (mSourceLine.isOpen() && mServer.isRunning()) {
            // get frame
            AudioFrame frame = mServer.getAudioFrame();
            if (frame == null) {
                continue;
            }
            // play audio
            byte[] data = frame.getBuffer();
            mSourceLine.write(data, 0, data.length);
        }
    }
}
