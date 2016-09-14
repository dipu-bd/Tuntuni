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
package org.tuntuni.videocall.audio;

import java.util.LinkedList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.tuntuni.connection.DataFrame;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.models.Logs;
import org.tuntuni.videocall.VideoFormat;

/**
 *
 * @author Sudipto Chandra
 */
public class AudioPlayer extends StreamServer implements Runnable {

    static final int QUEUE_SIZE = 1;

    private SourceDataLine mSourceLine;
    private Thread mPlayerThread;
    private final LinkedList<byte[]> mData;

    public AudioPlayer(int port) {
        super(port);
        mData = new LinkedList<>();
    }

    /**
     * Starts the player
     */
    public void start() {
        try {
            // start source line
            DataLine.Info info = new DataLine.Info(
                    SourceDataLine.class, VideoFormat.getAudioFormat());
            mSourceLine = (SourceDataLine) AudioSystem.getLine(info);
            mSourceLine.open(VideoFormat.getAudioFormat());

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
        try {
            mPlayerThread.interrupt();
            // close player 
            mSourceLine.close();
        } catch (Exception ex) {
        }
    }

    @Override
    public String getName() {
        return "AudioPlayer";
    }

    @Override
    public void dataReceived(Object data) {
        if (data != null && data instanceof DataFrame) {
            DataFrame frame = (DataFrame) data;
            // play the audio data   
            synchronized (mData) {
                mData.add(frame.getBuffer());
                if (mData.size() > QUEUE_SIZE) {
                    mData.poll();
                }
                mData.notify();
            }
        }
    }

    @Override
    public void run() {
        mSourceLine.start();
        while (!Thread.interrupted()) {
            synchronized (mData) {
                if (mData.isEmpty()) {
                    try {
                        mData.wait();
                    } catch (InterruptedException ex) {
                        Logs.error(getName(), "Wait interrupted. {0}", ex);
                    }
                }
                byte[] data = mData.poll();
                if (data != null) {
                    // play the audio data 
                    mSourceLine.write(data, 0, data.length);
                }
            }
        }
    }

}
