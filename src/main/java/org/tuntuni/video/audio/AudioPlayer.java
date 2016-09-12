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
package org.tuntuni.video.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.tuntuni.models.Logs;
import org.tuntuni.video.VideoFormat;

/**
 *
 * @author Sudipto Chandra
 */
public class AudioPlayer {

    private final AudioClient mClient;

    private DataLine.Info mSourceInfo;
    private SourceDataLine mSourceLine;

    public AudioPlayer(AudioClient client) {
        mClient = client;
        mClient.audioProperty().addListener((ov, oldVal, newVal) -> {
            playAudio(newVal);
        });
    }

    /**
     * Starts the player
     */
    public void start() {
        try {
            // start source line
            mSourceInfo = new DataLine.Info(
                    SourceDataLine.class, VideoFormat.getAudioFormat());
            mSourceLine = (SourceDataLine) AudioSystem.getLine(mSourceInfo);
            mSourceLine.open(VideoFormat.getAudioFormat());
            mSourceLine.start();
        } catch (LineUnavailableException ex) {
            Logs.error(getClass(), "Failed to start the audio line. ERROR: {0}", ex);
        }
    }

    /**
     * Stops the player
     */
    public void stop() {
        try {
            // close player
            mSourceLine.stop();
            mSourceLine.close();
        } catch (Exception ex) {
        }
    }

    /**
     * Checks whether player is active
     *
     * @return
     */
    public boolean isActive() {
        if (mClient == null || mSourceLine == null) {
            return false;
        }
        return mClient.isOpen() && mSourceLine.isOpen();
    }

    private void playAudio(AudioFrame frame) {
        // play new audio data
        byte[] data = frame.getBuffer();
        mSourceLine.write(data, 0, data.length);
    }
}
