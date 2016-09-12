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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import org.tuntuni.models.Logs;
import org.tuntuni.video.StreamSocket;
import static org.tuntuni.video.image.ImageServer.WAIT_INTERVAL;

/**
 * 
 * @author Sudipto Chandra
 */
public class AudioServer extends StreamSocket {

    private AudioSource mSource;

    public AudioServer(AudioSource source) {
        mSource = source;
    } 

    @Override
    public void doWork() {
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
            Logs.error(getClass(), "Thread sleep was interrupted. ERROR: {0}", ex);
        }
    }

    // gets the next packet to send
    private DatagramPacket getNextPacket() {
        // check if image is available
        if (!mSource.isAudioNew()) {
            return null;
        }
        // get next image 
        AudioFrame audioFrame = mSource.getFrame();
        if (audioFrame == null) {
            return null;
        } 
        // convert to bytes 
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            // write packet
            oos.writeObject(audioFrame);
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
