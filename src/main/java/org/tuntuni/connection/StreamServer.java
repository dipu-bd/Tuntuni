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

import java.net.Socket;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.video.AudioFrame;
import org.tuntuni.video.ImageFrame;

/**
 * To listen and respond to clients sockets.
 */
public class StreamServer extends TCPServer {

    // separate audio video frames 
    private AudioFrame mAudio;
    private ImageFrame mImage;

    /**
     * Creates a new stream Server.
     */
    public StreamServer() {
        super("Stream Server", null);
    }

    @Override
    Object getResponse(ConnectFor status, Socket socket, Object[] data) {
        long last = (long) data[0];
        switch (status) {
            case AUDIO:
                if (mAudio != null && last != mAudio.getTime()) {
                    return mAudio;
                }
                break;

            case IMAGE:
                if (mImage != null && last != mImage.getTime()) {
                    return mImage;
                }
                break;
        }
        return null;
    }

    public void addAudio(AudioFrame audio) {
        mAudio = audio;
    }

    public void addImage(ImageFrame image) {
        mImage = image;
    }
}
