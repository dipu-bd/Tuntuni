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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import org.tuntuni.models.Logs;
import org.tuntuni.connection.StreamSocket;

/**
 *
 * @author Sudipto Chandra
 */
public class ImageServer extends StreamSocket {

    // time to wait between to successive send operation
    public static final int WAIT_INTERVAL = 25; // milliseconds

    private ImageSource mSource;

    /**
     * Creates a new Image Server
     *
     * @param source
     */
    public ImageServer(ImageSource source) {
        mSource = source;
    }

    @Override
    public String getName() {
        return "Image Server";
    }

    // sends a packet
    @Override
    public void doWork() {
        if (!isConnected()) {
            return;
        }
        try {
            // send a packet
            DatagramPacket packet = getNextPacket();
            if (packet != null) {
                getSocket().send(packet);
            }
            // wait WAIT_INTERVAL time before next send
            Thread.sleep(WAIT_INTERVAL);
        } catch (IOException ex) {
            Logs.error(getClass(), "Failed to send packet. ERROR: {0}", ex);
        } catch (InterruptedException ex) {
            Logs.error(getClass(), "{0}", ex);
        }
    }

    // gets the next packet to send
    private DatagramPacket getNextPacket() {
        // check if image is available
        if (!mSource.isImageNew()) {
            return null;
        }
        // get next image 
        BufferedImage image = mSource.getImage();
        if (image == null) {
            return null;
        }
        // make image frame
        ImageFrame imageFrame = new ImageFrame(image);
        // convert to bytes 
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            // write packet
            oos.writeObject(imageFrame);
            // get converted bytes
            byte[] data = baos.toByteArray();
            // create and return datagram packet
            return new DatagramPacket(data, data.length, getRemoteAddress());
        } catch (IOException ex) {
            Logs.error(getClass(), "Failed to create packet. ERROR: {0}", ex);
            return null;
        }
    }

}
