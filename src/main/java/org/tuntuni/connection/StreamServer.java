/*
 * Copyright 2016 Sudipto Chandra.
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
package org.tuntuni.connection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.TreeSet;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;
import org.tuntuni.video.AudioFrame;
import org.tuntuni.video.DataFrame;
import org.tuntuni.video.ImageFrame;

/**
 * To listen and respond to clients sockets.
 */
public class StreamServer implements Runnable {

    private final int AUDIO_BUFFER_SIZE = 20;
    private final int IMAGE_BUFFER_SIZE = 100;

    // separate audio video frames
    private int mPort;
    private Thread mServerThread;
    private DatagramSocket mSocket;

    private long mAudioTime;
    private long mImageTime;
    private TreeSet<AudioFrame> mAudio;
    private TreeSet<ImageFrame> mImage;

    private boolean onRun;

    /**
     * Creates a new stream Server.
     */
    public StreamServer() {
        onRun = false;
        mAudioTime = 0;
        mImageTime = 0;
        mAudio = new TreeSet<>();
        mImage = new TreeSet<>();
    }

    public void start() throws Exception {
        stop();
        createSocket();
        mAudioTime = 0;
        mImageTime = 0;
        mAudio.clear();
        mImage.clear();
        mServerThread = new Thread(this);
        mServerThread.setDaemon(true);
        mServerThread.start();
    }

    public void stop() {
        if (mServerThread != null && mServerThread.isAlive()) {
            mServerThread.interrupt();
            closeSocket();
        }
    }

    public void createSocket() throws Exception {
        // create new instance of socket 
        for (int i = 0; i < 10; ++i) {
            try {
                mPort = Commons.getRandom(10_000, 65500);
                mSocket = new DatagramSocket(mPort);
                break;
            } catch (SocketException ex) {
                mSocket = null;
            }
        }
        if (mSocket == null) {
            throw new Exception("Failed to create socket 10 times");
        }
    }

    public void closeSocket() {
        try {
            mSocket.close();
        } catch (Exception ex) {
        }
    }

    @Override
    public void run() {
        int failCounter = 0;
        while (failCounter < 100) {
            onRun = true;
            try {
                if (mSocket == null || mSocket.isClosed()) {
                    break;
                }

                // receive a packet
                byte[] data = new byte[10_000];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                mSocket.receive(packet);

                // convert response data
                DataFrame frame = Commons.fromBytes(data, DataFrame.class);
                if (frame == null) {
                    continue;
                }

                // check validity of data-frame
                if (frame.connectedFor() == ConnectFor.AUDIO
                        && frame.getTime() > mAudioTime) {
                    mAudio.add((AudioFrame) frame);
                    if (mAudio.size() > AUDIO_BUFFER_SIZE) {
                        mAudio.remove(mAudio.first());
                    }
                }
                if (frame.connectedFor() == ConnectFor.IMAGE
                        && frame.getTime() > mImageTime) {
                    mImage.add((ImageFrame) frame);
                    if (mImage.size() > IMAGE_BUFFER_SIZE) {
                        mImage.remove(mImage.first());
                    }
                }
                failCounter = 0;

            } catch (Exception ex) {
                Logs.severe(null, ex);
                ++failCounter;
            }
        }
        onRun = false;
    }

    /**
     * Gets the current port of the running server
     *
     * @return
     */
    public int getPort() {
        return mSocket.getPort();
    }

    public AudioFrame getAudioFrame() {
        if (mAudio.isEmpty()) {
            return null;
        }
        AudioFrame audFrame = mAudio.first();
        mAudio.remove(audFrame);
        mAudioTime = Math.max(mAudioTime, audFrame.getTime());
        return audFrame;
    }

    /**
     * Returns the first frame in time order. If none found a null is returned.
     *
     * @return
     */
    public ImageFrame getImageFrame() {
        if (mImage.isEmpty()) {
            return null;
        }
        ImageFrame imgFrame = mImage.first();
        mImage.remove(imgFrame);
        mImageTime = Math.max(mImageTime, imgFrame.getTime());
        return imgFrame;
    }

    public boolean isRunning() {
        return onRun;
    }
}
