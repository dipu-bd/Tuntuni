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

import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.tuntuni.connection.StreamClient;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;

/**
 *
 */
public final class VideoRenderer {

    private VideoFormat mFormat;
    private StreamLine<ImageFrame> mImageLine;
    private StreamLine<AudioFrame> mAudioLine;
    private StreamClient<ImageFrame> mImageClient;
    private StreamClient<AudioFrame> mAudioClient;

    private long mStartTime;
    private DataLine.Info mSourceInfo;
    private SourceDataLine mSourceLine;
    private final Consumer mImageConsumer;
    private Thread mAudioThread;
    private Thread mVideoThread;

    /**
     * Creates a new video renderer
     *
     * @param format Format of the audio and video data.
     * @param imageConsumer Where to show image.
     */
    public VideoRenderer(VideoFormat format, Consumer imageConsumer) {
        mFormat = format;
        mImageConsumer = imageConsumer;
        initialize();
    }

    public void initialize() {
        mImageLine = new StreamLine<>();
        mAudioLine = new StreamLine<>();
        mImageClient = new StreamClient(mFormat.getImageAddress(), mImageLine, ConnectFor.IMAGE);
        mAudioClient = new StreamClient(mFormat.getAudioAddress(), mAudioLine, ConnectFor.AUDIO);

        // setup audio
        try {
            mSourceInfo = new DataLine.Info(SourceDataLine.class, mFormat.getAudioFormat());
            mSourceLine = (SourceDataLine) AudioSystem.getLine(mSourceInfo);
            mSourceLine.open(mFormat.getAudioFormat());
        } catch (Exception ex) {
            Logs.error("Failed to initialize speaker", ex);
        }

        // setup video
        try {

        } catch (Exception ex) {
            Logs.error("Failed to initialize image-view", ex);
        }
    }

    public void start() {
        // set start time of capturing
        mStartTime = System.nanoTime();
        // setup and start audio thread
        if (mSourceLine != null) {
            mAudioThread = new Thread(() -> audioRunner(), "audioRunner");
            mAudioThread.setPriority(6);
            mAudioThread.setDaemon(true);
            mAudioLine.setStart(mStartTime);
            mAudioThread.start();
        }
        // setup and start video thread
        if (mImageConsumer != null) {
            mVideoThread = new Thread(() -> videoRunner(), "videoRunner");
            mVideoThread.setPriority(7);
            mVideoThread.setDaemon(true);
            mImageLine.setStart(mStartTime);
            mVideoThread.start();
        }
        // start stream server
        mImageClient.connect();
        mAudioClient.connect();
    }

    public void stop() {
        // stop clients
        mImageClient.close();
        mAudioClient.close();
        // stop audio
        if (mSourceLine != null) {
            mSourceLine.close();
            mAudioThread.interrupt();
        }
        // stop video
        if (mImageConsumer != null) {
            mVideoThread.interrupt();
            Platform.runLater(() -> mImageConsumer.accept(null));
        }
    }

    private void videoRunner() {
        while (mImageClient.isConnected()) {
            ImageFrame imgFrame = mImageLine.pop();
            Image image = imgFrame.getImage();
            Platform.runLater(() -> {
                mImageConsumer.accept(image);
            });
        }
    }

    private void audioRunner() {
        if (mSourceLine == null) {
            return;
        }
        mSourceLine.start();
        while (mSourceLine.isOpen()) {
            AudioFrame audioFrame = mAudioLine.pop();
            byte[] data = audioFrame.getBuffer();
            mSourceLine.write(data, 0, data.length);
        }
    }

}
