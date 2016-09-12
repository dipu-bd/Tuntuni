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
package org.tuntuni.video.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import org.tuntuni.models.Logs;
import org.tuntuni.connection.StreamSocket;
import static org.tuntuni.video.image.ImagePlayer.MAX_BUFFER;

/**
 *
 * @author Sudipto Chandra
 */
public class ImageClient extends StreamSocket {

    public static final int QUEUE_SIZE = 15; // almost 60K    
    public static final int MAX_BUFFER = 60_000; // almost 60K

    private final byte[] mBuffer;
    private volatile int mImageTime;
    private final ObjectProperty<Image> mImage;
    private final ConcurrentLinkedQueue<ImageFrame> mFrames;

    public ImageClient() {
        mBuffer = new byte[MAX_BUFFER];
        mImage = new SimpleObjectProperty<>(null);
        mFrames = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String getName() {
        return "Image Client";
    }

    // receive a packet
    @Override
    public void doWork() {
        try {
            DatagramPacket packet = new DatagramPacket(mBuffer, mBuffer.length);
            getSocket().receive(packet);
            packetReceived(packet);
        } catch (IOException ex) {
            Logs.error(getClass(), "Failed to receive packet. ERROR: {0}", ex);
        }
    }

    private void packetReceived(DatagramPacket packet) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
                ObjectInputStream ois = new ObjectInputStream(bais)) {

            Object data = ois.readObject();
            if (data instanceof ImageFrame) {
                updateImage((ImageFrame) data);
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logs.error(getClass(), "Failed to read received data. Error: {0}", ex);
        }
    }

    private void updateImage(ImageFrame frame) {
        // replace if no last image available, or last image is older
        mFrames.add(frame);
        if (mFrames.size() > QUEUE_SIZE) {
            frame = mFrames.poll();
            if (frame.getTime() > mImageTime) {
                mImageTime = frame.getTime();
                mImage.set(frame.getImage());
            }
        }
    }
 
    /**
     * Gets the image property
     *
     * @return
     */
    public ObjectProperty<Image> imageProperty() {
        return mImage;
    }

    @Override
    public String toString() {
        return String.format("ImageClient:%d:%s", getPort(), getRemoteAddress().toString());
    }

}
