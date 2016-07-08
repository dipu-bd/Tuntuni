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
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamStreamer;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.ConnectFor;

/**
 *
 */
public final class VideoCapturer {

    private static final Logger logger = Logger.getGlobal();

    private VideoFormat mFormat;
    private StreamLine<ImageFrame> mImageLine;
    private StreamLine<AudioFrame> mAudioLine;
    private StreamServer<ImageFrame> mImageServer;
    private StreamServer<AudioFrame> mAudioServer;

    private long mStartTime;
    private Webcam mWebcam;
    private DataLine.Info mTargetInfo;
    private TargetDataLine mTargetLine;
    private Thread mAudioThread;
    private Thread mVideoThread;

    public VideoCapturer() {
        this(new VideoFormat());
    }

    public VideoCapturer(VideoFormat format) {
        mFormat = format;
        initialize();
    }

    public void initialize() {
        mImageLine = new StreamLine<>();
        mAudioLine = new StreamLine<>();
        mImageServer = new StreamServer(mImageLine, ConnectFor.IMAGE);
        mAudioServer = new StreamServer(mAudioLine, ConnectFor.AUDIO);

        // setup audio
        try {
            mTargetInfo = new DataLine.Info(TargetDataLine.class, mFormat.getAudioFormat());
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(mTargetInfo);
            targetLine.open(mFormat.getAudioFormat());
        } catch (LineUnavailableException ex) {
            logger.log(Level.SEVERE, "Failed to initialize microphone", ex);
        }

        // setup video
        try {
            mWebcam = Webcam.getDefault();
            mWebcam.setViewSize(mFormat.getViewSize());
        } catch (WebcamException ex) {
            logger.log(Level.SEVERE, "Failed to initialize webcam", ex);
        }
    }

    public void start() {
        // setup and start audio thread
        mAudioThread = new Thread(() -> videoRunner(), "videoRunner");
        mAudioThread.setPriority(7);
        mAudioThread.setDaemon(true);
        mAudioThread.start();

        // setup and start video thread
        mVideoThread = new Thread(() -> audioRunner(), "audioRunner");
        mVideoThread.setPriority(6);
        mVideoThread.setDaemon(true);
        mVideoThread.start();

        // set start time of capturing
        mStartTime = System.nanoTime();
        // start stream server
        mImageServer.start();
        mAudioServer.start();
        // start stream line
        mImageLine.setStart(mStartTime);
        mAudioLine.setStart(mStartTime);
    }

    public void stop() {
        mTargetLine.close();
        mWebcam.close();
        mAudioThread.interrupt();
        mVideoThread.interrupt();
    }

    public void videoRunner() {
        mWebcam.open(); 
        while (mWebcam.open()) {
            mImageLine.push(mStartTime,
                    new ImageFrame(mWebcam.getImageBytes()));
        }
    }

    public void audioRunner() {
        mTargetLine.start();
        while (mTargetLine.isOpen()) {

        }
    }

}
