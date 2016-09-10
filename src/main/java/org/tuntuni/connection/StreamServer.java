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
import java.util.TreeSet;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.video.AudioFrame;
import org.tuntuni.video.DataFrame;
import org.tuntuni.video.ImageFrame;

/**
 * To listen and respond to clients sockets.
 */
public class StreamServer extends TCPServer {

    private final int AUDIO_BUFFER_SIZE = 30;
    private final int IMAGE_BUFFER_SIZE = 50;

    // separate audio video frames
    private long mAudioTime;
    private long mImageTime;
    private TreeSet<AudioFrame> mAudio;
    private TreeSet<ImageFrame> mImage;

    /**
     * Creates a new stream Server.
     */
    public StreamServer() {
        super("Stream Server", null);
        mAudioTime = 0;
        mImageTime = 0;
        mAudio = new TreeSet<>();
        mImage = new TreeSet<>();
    }

    @Override
    Object getResponse(ConnectFor status, Socket socket, Object[] data) {
        DataFrame frame = (DataFrame) data[0];
        switch (frame.connectedFor()) {
            case AUDIO:
                if (frame.getTime() > mAudioTime) {
                    mAudio.add((AudioFrame) frame);
                    if (mAudio.size() > AUDIO_BUFFER_SIZE) {
                        mAudio.remove(mAudio.first());
                    }
                }
                Logs.info(getClass(), "Audio frame received: Time = " + frame.getTime());
                break;

            case IMAGE:
                if (frame.getTime() > mImageTime) {
                    mImage.add((ImageFrame) frame);
                    if (mImage.size() > IMAGE_BUFFER_SIZE) {
                        mImage.remove(mImage.first());
                    }
                }
                Logs.info(getClass(), "Image frame received: Time = " + frame.getTime());
                break;
        }
        return null;
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
}
