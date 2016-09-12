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

import java.io.IOException;
import java.net.DatagramPacket;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;
import org.tuntuni.video.StreamServer;
import org.tuntuni.video.StreamSocket;

/**
 *
 * @author Sudipto Chandra
 */
public class ImageServer extends StreamSocket {

    private final ImageCapture mCapture;

    /**
     * Creates a new Image Server
     *
     * @param capture Capture
     */
    public ImageServer(ImageCapture capture) {
        mCapture = capture;
    }

    // send a packet
    @Override
    public void doWork() {
        try {
            DatagramPacket packet = getNextPacket();
            if (packet != null) {
                getSocket().send(packet);
            }
        } catch (IOException ex) {
            Logs.error(getClass(), "Failed to send packet. ERROR: {0}", ex);
        }
    }

    @Override
    // gets the next packet to send
    public DatagramPacket getNextPacket() {
        // get next image frame
        ImageFrame imageFrame = mCapture.getFrame();
        if (imageFrame == null) {
            return null;
        }
        // convert to bytes
        byte[] data = Commons.toBytes(imageFrame);
        // create and return datagram packet
        return new DatagramPacket(data, data.length, getRemoteAddress());
    }
}
