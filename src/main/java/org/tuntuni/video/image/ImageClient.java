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
import org.tuntuni.models.Logs;
import org.tuntuni.connection.StreamSocket;

/**
 *
 * @author Sudipto Chandra
 */
public class ImageClient extends StreamSocket {

    public static final int MAX_BUFFER = 60_000; // almost 60K

    private byte[] mBuffer;
    private ImageFrame mImage;
    private volatile boolean mImageNew;

    public ImageClient() {
        mBuffer = new byte[MAX_BUFFER];
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

    private synchronized void updateImage(ImageFrame frame) {
        // replace if no last image available, or last image is older
        if (mImage == null || mImage.getTime() < frame.getTime()) {
            mImage = frame;
            mImageNew = true;
        }
    }

    /**
     * Gets the current image frame. If none available an empty frame is
     * returned
     *
     * @return
     */
    public ImageFrame getFrame() {
        mImageNew = false;
        return mImage;
    }

    /**
     * Checks whether new image has arrived
     *
     * @return True if image is updated
     */
    public boolean isImageNew() {
        return mImageNew;
    }

    @Override
    public String toString() {
        return String.format("ImageClient:%d:%s", getPort(), getRemoteAddress().toString());
    }

}
