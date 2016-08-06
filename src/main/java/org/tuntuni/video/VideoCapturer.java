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
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;

/**
 *
 */
public final class VideoCapturer {

    private VideoFormat mFormat;
    private StreamLine<ImageFrame> mImageLine;
    private StreamLine<AudioFrame> mAudioLine;
    private StreamServer<ImageFrame> mImageServer;
    private StreamServer<AudioFrame> mAudioServer;

    private Webcam mWebcam;
    private long mStartTime;
    private Thread mAudioThread;
    private Thread mVideoThread;
    private DataLine.Info mTargetInfo;
    private TargetDataLine mTargetLine;

    public VideoCapturer() {
        this(new VideoFormat());
    }

    public VideoCapturer(VideoFormat format) {
        mFormat = format;
    }

    public void initialize() {

        // initialize stream lines
        mImageLine = new StreamLine<>();
        mAudioLine = new StreamLine<>();

        // setup audio
        try {
            mTargetInfo = new DataLine.Info(TargetDataLine.class, mFormat.getAudioFormat());
            mTargetLine = (TargetDataLine) AudioSystem.getLine(mTargetInfo);
            mTargetLine.open(mFormat.getAudioFormat());
        } catch (Exception ex) {
            Logs.error("Failed to initialize microphone");
        }

        // setup video
        try {
            mWebcam = Webcam.getDefault();
            mWebcam.setViewSize(mFormat.getViewSize());
        } catch (Exception ex) {
            Logs.error("Failed to initialize webcam");
        }

        // initialize servers
        mImageServer = new StreamServer("Image Server", mImageLine, ConnectFor.IMAGE);
        mAudioServer = new StreamServer("Audio Server", mAudioLine, ConnectFor.AUDIO);

    }

    public void start() {
        // set start time of capturing
        mStartTime = System.nanoTime();
        // setup and start audio thread
        if (mTargetLine != null) {
            mAudioThread = new Thread(() -> audioRunner(), "audioRunner");
            mAudioThread.setPriority(6);
            mAudioThread.setDaemon(true);
            mAudioLine.setStart(mStartTime);
            mAudioThread.start();
        }
        // setup and start video thread
        if (mWebcam != null) {
            mVideoThread = new Thread(() -> videoRunner(), "videoRunner");
            mVideoThread.setPriority(7);
            mVideoThread.setDaemon(true);
            mImageLine.setStart(mStartTime);
            mVideoThread.start();
        }
        // start stream server
        if (mImageServer != null) {
            mImageServer.start();
        }
        if (mAudioServer != null) {
            mAudioServer.start();
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
        // close server
        if (mImageServer != null) {
            mImageServer.stop();
        }
        if (mAudioServer != null) {
            mAudioServer.stop();
        }
    }

    private void videoRunner() {
        if (mWebcam == null) {
            return;
        }
        mWebcam.open();
        while (mWebcam.isOpen()) {
            long time = System.nanoTime();
            ByteBuffer bb = mWebcam.getImageBytes();
            if (bb == null) {
                continue;
            }
            mImageLine.push(time, new ImageFrame(bb));
        }
    }

    private void audioRunner() {
        if (mTargetLine == null) {
            return;
        }
        mTargetLine.start();
        int buffer = mTargetLine.getBufferSize() / 10;
        byte[] data = new byte[buffer];
        while (mTargetLine.isOpen()) {
            long time = System.nanoTime();
            int len = mTargetLine.read(data, 0, buffer);
            if (len == -1) {
                break;
            }
            mAudioLine.push(time, new AudioFrame(data, len));
        }
    }

    public int getAudioPort() {
        return mAudioServer.getPort();
    }

    public int getImagePort() {
        return mImageServer.getPort();
    }
}
