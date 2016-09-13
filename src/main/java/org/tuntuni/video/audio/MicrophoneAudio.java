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

import javafx.application.Platform;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import org.tuntuni.models.Logs;
import org.tuntuni.video.VideoFormat;

/**
 *
 * @author Sudipto Chandra
 */
public class MicrophoneAudio extends AudioSource implements Runnable {

    private Thread mAudioThread;
    private DataLine.Info mTargetInfo;
    private TargetDataLine mTargetLine;

    public MicrophoneAudio() {
        super();
    }

    @Override
    public String getName() {
        return "MicrophoneAudio";
    }

    @Override
    public void start() {
        try {
            // start target line
            mTargetInfo = new DataLine.Info(
                    TargetDataLine.class, VideoFormat.getAudioFormat());
            mTargetLine = (TargetDataLine) AudioSystem.getLine(mTargetInfo);
            mTargetLine.open(VideoFormat.getAudioFormat());
            mTargetLine.start();
            // start audio thread
            mAudioThread = new Thread(this);
            mAudioThread.setDaemon(true);
            mAudioThread.start();
        } catch (LineUnavailableException ex) {
            Logs.error(getName(), "Failed to open. {0}", ex);
        }
    }

    @Override
    public void stop() {
        try {
            mTargetLine.stop();
            mTargetLine.close();
            mAudioThread.interrupt();
        } catch (Exception ex) {
            Logs.error(getName(), "Failed to close. {0}", ex);
        }
    }

    @Override
    public AudioFormat getFormat() {
        return mTargetLine.getFormat();
    }

    @Override
    public void setFormat(AudioFormat format) {

    }

    // run audio thread task
    @Override
    public void run() {
        try {
            // available: 2 5 6 7 9 10 15 25 
            int size = mTargetLine.getBufferSize() / 5;
            byte[] buffer = new byte[size];
            Logs.info(getName(), "Line opened with buffer size = {0}\n", size);

            while (isOpen()) {
                // read audio
                int len = mTargetLine.read(buffer, 0, size);
                if (len == -1) {
                    return;
                }
                // update buffer  
                send(buffer, size);
            }
        } catch (Exception ex) {
            if (getListener() != null) {
                Platform.runLater(() -> getListener().errorOccured(ex));
            }
        }
    }

    @Override
    public boolean isOpen() {
        return mTargetLine.isOpen();
    }
}
