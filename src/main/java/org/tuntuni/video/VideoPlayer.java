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
package org.tuntuni.video;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.Callable;
import javafx.scene.image.ImageView;
import org.tuntuni.models.Logs;
import org.tuntuni.video.audio.AudioPlayer;
import org.tuntuni.video.image.ImagePlayer;

/**
 *
 * @author Sudipto Chandra
 */
public class VideoPlayer {

    private final ImagePlayer mImagePlayer;
    private final AudioPlayer mAudioPlayer;

    public VideoPlayer(ImageView viewer) {
        mImagePlayer = new ImagePlayer(viewer);
        mAudioPlayer = new AudioPlayer();
    }

    public void start() throws SocketException, IOException {
        mImagePlayer.open();
        mImagePlayer.start();

        mAudioPlayer.open();
        mAudioPlayer.start();
    }

    public void stop() {
        mImagePlayer.close();
        mImagePlayer.stop();

        mAudioPlayer.close();
        mAudioPlayer.stop();
    }

//                                                                            //
////////////////////////////////////////////////////////////////////////////////
//                                                                            //  
    public int getImagePort() {
        return portGetter(() -> {
            return mImagePlayer.getPort();
        });
    }

    public int getAudioPort() {
        return portGetter(() -> {
            return mAudioPlayer.getPort();
        });
    }

    private int portGetter(Callable<Integer> callable) {
        try {
            // wait 1500 milliseconds
            for (int i = 0; i < 30; ++i) {
                int port = callable.call();
                if (port != -1) {
                    return port;
                }
                Thread.sleep(50);
            }
        } catch (Exception ex) {
            Logs.error(getClass(), "Could not get port", ex);
        }
        return -1;
    }
}
