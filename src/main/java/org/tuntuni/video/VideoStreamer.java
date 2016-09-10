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
import com.sun.imageio.plugins.common.I18N;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import org.tuntuni.connection.StreamClient;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;

/**
 *
 */
public final class VideoStreamer {

    private final StreamClient mClient;

    private Thread mAudioThread;
    private Thread mVideoThread;

    private Webcam mWebcam;
    private DataLine.Info mTargetInfo;
    private TargetDataLine mTargetLine;

    /**
     * Creates a new video capturer instance
     *
     * @param address
     * @param port
     */
    public VideoStreamer(InetAddress address, int port) {
        mClient = new StreamClient(address, port);
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
            mWebcam.setViewSize(VideoFormat.getViewSize());
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize webcam. ERROR: {0}.", ex);
        }
    }

    public void start() throws Exception {
        // start client
        mClient.open();
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
        // close clientt
        mClient.close();
    }

    private void imageRunner() {
        if (mWebcam == null) {
            return;
        }
        // start image line
        mWebcam.open();
        // run image capture loop
        while (mWebcam.isOpen()) {
            // capture single image
            BufferedImage image = mWebcam.getImage();
            if (image == null) {
                continue;
            }
            // send image frame
            Thread t = new Thread(() -> { 
                mClient.sendFrame(new ImageFrame(image));
            });
            t.setDaemon(true);
            t.start();
            // check client is up
            if (!mClient.isOkay()) {
                break;
            }
        }
    }

    private void audioRunner() {
        if (mTargetLine == null) {
            return;
        }
        // start target line
        mTargetLine.start();
        int buffer = mTargetLine.getBufferSize();
        byte[] data = new byte[buffer];
        // run capture loop
        while (mTargetLine.isOpen() && mClient.isOkay()) {
            // read audio
            int len = mTargetLine.read(data, 0, buffer);
            if (len == -1) {
                break;
            }
            // send audio frame
            Thread t = new Thread(() -> {
                mClient.sendFrame(new AudioFrame(data, len));
            });
            t.setDaemon(true);
            t.start();
        }
    }
}
