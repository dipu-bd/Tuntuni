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

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.Logs;

/**
 *
 */
public final class VideoStreamer {

    private final StreamServer mServer;

    private Thread mAudioThread;
    private Thread mVideoThread;

    private Webcam mWebcam;
    private DataLine.Info mTargetInfo;
    private TargetDataLine mTargetLine;

    /**
     * Creates a new video capturer instance
     *
     * @param server
     */
    public VideoStreamer(StreamServer server) {
        mServer = server;
    }

    public void initialize() {
        // setup audio
        try {
            mTargetInfo = new DataLine.Info(TargetDataLine.class, VideoFormat.getAudioFormat());
            mTargetLine = (TargetDataLine) AudioSystem.getLine(mTargetInfo);
            mTargetLine.open(VideoFormat.getAudioFormat());
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize microphone. ERROR: {0}", ex);
        }

        // setup video
        try {
            mWebcam = Webcam.getDefault();
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize webcam. ERROR: {0}.", ex);
        }
    }

    public void start() throws Exception {
        // setup and start audio thread
        if (mTargetLine != null) {
            mAudioThread = new Thread(() -> audioRunner(), "audioRunner");
            mAudioThread.setDaemon(true);
            mAudioThread.start();
        }
        // setup and start video thread
        if (mWebcam != null) {
            mVideoThread = new Thread(() -> imageRunner(), "videoRunner");
            mVideoThread.setDaemon(true);
            mVideoThread.start();
        }
    }

    public void stop() {
        // stop audio
        if (mTargetLine != null) {
            mTargetLine.close();
            mAudioThread.interrupt();
        }
        // stop video
        if (mWebcam != null) {
            mWebcam.close();
            mVideoThread.interrupt();
        }
    }

    private void imageRunner() {
        if (mWebcam == null) {
            return;
        }
        // start image line
        mWebcam.open();
        // run image capture loop
        while (mWebcam.isOpen() && mServer.isOpen()) {
            // capture single image
            BufferedImage image = mWebcam.getImage();
            if (image == null) {
                continue;
            }
            // send image frame  
            mServer.addImage(new ImageFrame(image)); 
        }
    }

    private void audioRunner() {
        if (mTargetLine == null) {
            return;
        }
        // start target line
        mTargetLine.start();
        int buffer = mTargetLine.getBufferSize() / 8;
        byte[] data = new byte[buffer];
        // run capture loop
        while (mTargetLine.isOpen() && mServer.isOpen()) {
            // read audio
            int len = mTargetLine.read(data, 0, buffer);
            if (len == -1) {
                break;
            }
            // send audio frame 
            mServer.addAudio(new AudioFrame(data, len));
        }
    }
}
