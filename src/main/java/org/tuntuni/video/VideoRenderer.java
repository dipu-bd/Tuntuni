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

import java.io.InputStream;
import java.net.URL;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.Logs;
import org.tuntuni.util.FileService;

/**
 *
 * @author dipu
 */
public class VideoRenderer {

    private final StreamServer mServer;
    private final ImageView mImage;

    private DataLine.Info mSourceInfo;
    private SourceDataLine mSourceLine;
    private ChangeListener<ImageFrame> imageListener;
    private ChangeListener<AudioFrame> audioListener;

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
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to initialize image view. ERROR: {0}.", ex);
        }
    }

    public void start() {
        // set start time of capturing 
        // setup and start audio thread
        if (mSourceLine != null) {
            mSourceLine.start();
            audioListener = (ov, o, n) -> audioRunner(n);
            mServer.getAudio().addListener(audioListener);
        }
        // setup and start video thread
        if (mImage != null) {
            imageListener = (ov, o, n) -> imageRunner(n);
            mServer.getImage().addListener(imageListener); 
        }
    }

    public void stop() {
        // stop audio
        if (mSourceLine != null) {
            mServer.getAudio().removeListener(audioListener);
            mSourceLine.close();
        }
        // stop video
        if (mImage != null) {
            mServer.getImage().removeListener(imageListener);
        }
    }

    private void imageRunner(ImageFrame frame) {
        if (mImage != null && frame != null) {
            // display image
            System.out.println(">>>>>> Image recieved <<<<<<< ");
            Platform.runLater(() -> {
                mImage.setImage(frame.getImage());
            });
        }
    }

    private void audioRunner(AudioFrame frame) {
        if (mSourceLine != null && mSourceLine.isOpen() && frame != null) {
            // play audio
            byte[] data = frame.getBuffer();
            mSourceLine.write(data, 0, data.length);
        }
    }
}
