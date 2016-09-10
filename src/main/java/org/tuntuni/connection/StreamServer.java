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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.video.AudioFrame;
import org.tuntuni.video.DataFrame;
import org.tuntuni.video.ImageFrame;

/**
 * To listen and respond to clients sockets.
 */
public class StreamServer extends TCPServer {

    // separate audio video frames 
    private final ObjectProperty<AudioFrame> mAudio;
    private final ObjectProperty<ImageFrame> mImage;

    /**
     * Creates a new stream Server.
     */
    public StreamServer() {
        super("Stream Server", null);
        mAudio = new SimpleObjectProperty<>(null);
        mImage = new SimpleObjectProperty<>(null);
    }

    void processFrame(DataFrame frame) {
        switch (frame.connectedFor()) {
            case AUDIO:
                if (mAudio.get() != null || frame.getTime() > mAudio.get().getTime()) {
                    mAudio.set((AudioFrame) frame);
                }
                Logs.info(getClass(), "Audio frame received: Time = " + frame.getTime());
                break;

            case IMAGE:
                if (mImage.get() != null || frame.getTime() > mImage.get().getTime()) {
                    mImage.set((ImageFrame) frame);
                }
                Logs.info(getClass(), "Image frame received: Time = " + frame.getTime());
                break;
        }
    }

    public ObjectProperty<AudioFrame> getAudio() {
        return mAudio;
    }

    public ObjectProperty<ImageFrame> getImage() {
        return mImage;
    }

    @Override
    Object getResponse(ConnectFor status, Socket socket, Object[] data) {
        processFrame((DataFrame) data[0]);
        return null;
    }
}
