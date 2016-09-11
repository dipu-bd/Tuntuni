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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.video.audio.AudioFrame;
import org.tuntuni.video.image.ImageFrame;

/**
 * To manage connection with stream server sockets.
 * <p>
 * To connect with the server you have to call {@linkplain connect()}. </p>
 */
public class StreamClient extends TCPClient {

    private long lastAudio;
    private long lastImage;
    private AudioFrame mAudio;
    private ImageFrame mImage;

    /**
     * Creates a new Stream client.
     *
     * @param address
     * @param port
     */
    public StreamClient(InetAddress address, int port) {
        super(new InetSocketAddress(address, port));
    }

    /**
     * Sends a datagram packet with given DataFrame in separate thread
     *
     * @return
     */
    public AudioFrame getAudio() {
        Object data = request(ConnectFor.AUDIO, lastAudio);
        if (data instanceof AudioFrame) {
            mAudio = (AudioFrame) data;
            lastAudio = Math.max(lastAudio, mAudio.getID());
        }
        return mAudio;
    }

    /**
     * Sends a datagram packet with given DataFrame in separate thread
     *
     * @return
     */
    public ImageFrame getImage() {
        Object data = request(ConnectFor.IMAGE, lastImage);
        if (data instanceof ImageFrame) {
            mImage = (ImageFrame) data;
            lastImage = Math.max(lastImage, mImage.getID());
        }
        return mImage;
    }

}
