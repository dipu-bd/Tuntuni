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

import java.util.Arrays;
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
public class AudioPlayer implements Runnable {

    // time to wait between to successive play operation
    public static final int WAIT_INTERVAL = 25; // milliseconds

    private final AudioClient mClient;
    private Thread mPlayerThread;

    private final DataLine.Info mSourceInfo;
    private SourceDataLine mSourceLine;

    public AudioPlayer(AudioClient client) {
        mClient = client;
        mSourceInfo = new DataLine.Info(
                SourceDataLine.class, VideoFormat.getAudioFormat());
    }

    /**
     * Starts the player
     */
    public void start() {
        try { 
            // start source line
            mSourceLine = (SourceDataLine) AudioSystem.getLine(mSourceInfo);
            mSourceLine.open(VideoFormat.getAudioFormat());
            mSourceLine.start();
            // start player thread
            mPlayerThread = new Thread(this);
            mPlayerThread.setDaemon(true);
            mPlayerThread.start();
        } catch (LineUnavailableException ex) {
            Logs.error(getClass(), "Failed to start the audio line. ERROR: {0}", ex);
        } 
    }

    /**
     * Stops the player
     */
    public void stop() { 
        // close player
        mSourceLine.stop();
        mSourceLine.close();
        mPlayerThread.interrupt();
    }

    /**
     * Checks whether player is active
     *
     * @return
     */
    public boolean isActive() {
        return mClient.isOpen() && mSourceLine.isOpen();
    }

    @Override
    public void run() {
        while (isActive()) {
            // play audio
            byte[] data = getdata();
            mSourceLine.write(data, 0, data.length);
        }
    }

    private byte[] getdata() {
        // get new audio data
        if (mClient.isAudioNew()) {
            AudioFrame frame = mClient.getFrame();
            return Arrays.copyOf(frame.getBuffer(), frame.getBufferLength());
        }
        // return 1800bytes of empty data
        // will play for almost 20 milliseconds
        return new byte[1800];
    }
}
