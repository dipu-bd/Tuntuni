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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tuntuni.models.Logs;
import org.tuntuni.connection.StreamSocket;

/**
 *
 * @author Sudipto Chandra
 */
public class AudioClient extends StreamSocket {

    public static final int MAX_BUFFER = 11_200; // almost 25K
    public static final int QUEUE_SIZE = 0;

    private final byte[] mBuffer;
    private volatile int mAudioTime;
    private final ObjectProperty<AudioFrame> mAudio;
    private final ConcurrentLinkedQueue<AudioFrame> mFrameList;

    public AudioClient() {
        mBuffer = new byte[MAX_BUFFER];
        mAudio = new SimpleObjectProperty<>();
        mFrameList = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String getName() {
        return "Audio Client";
    }

    // receive a packet
    @Override
    public void doWork() {
        try {
            DatagramPacket packet = new DatagramPacket(mBuffer, mBuffer.length);
            getSocket().receive(packet);
            new Thread(() -> {
                packetReceived(packet);
            }).start();
        } catch (IOException ex) {
            Logs.error(getClass(), "Failed to receive packet. ERROR: {0}", ex);
        }
    }

    private void packetReceived(DatagramPacket packet) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
                ObjectInputStream ois = new ObjectInputStream(bais)) {

            Object data = ois.readObject();
            if (data instanceof AudioFrame) {
                updateAudio((AudioFrame) data);
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logs.error(getClass(), "Failed to read received data. Error: {0}", ex);
        }
    }

    private void updateAudio(AudioFrame frame) {
        // replace if no last au dio available, or last audio is older
        mFrameList.add(frame);
        if (mFrameList.size() > QUEUE_SIZE) {
            AudioFrame first = mFrameList.poll();
            if (first.getTime() > mAudioTime) {
                mAudioTime = first.getTime();
                mAudio.set(first);
            }
        }
    }

    /**
     * Gets the current audio frame. If none available an empty frame is
     * returned
     *
     * @return
     */
    public AudioFrame getFrame() {
        return mAudio.get();
    }

    /**
     * Gets the audio frame property
     *
     * @return
     */
    public ObjectProperty<AudioFrame> audioProperty() {
        return mAudio;
    }

    @Override
    public String toString() {
        return String.format("%s:%d:%s", getClass().getSimpleName(),
                getPort(), getRemoteAddress().toString());
    }

}
