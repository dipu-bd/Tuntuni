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

import java.net.SocketException;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.tuntuni.models.Logs;
import org.tuntuni.video.audio.AudioClient;
import org.tuntuni.video.audio.AudioPlayer;
import org.tuntuni.video.image.ImageClient;
import org.tuntuni.video.image.ImagePlayer;

/**
 *
 * @author Sudipto Chandra
 */
public class VideoPlayer {

    private ImageView mViewer;

    private final ImageClient mImageClient;
    private final ImagePlayer mImagePlayer;

    private final AudioClient mAudioClient;
    private final AudioPlayer mAudioPlayer;

    public VideoPlayer(ImageView viewer) {
        mViewer = viewer;
        mImageClient = new ImageClient();
        mImagePlayer = new ImagePlayer(mImageClient);
        mAudioClient = new AudioClient();
        mAudioPlayer = new AudioPlayer(mAudioClient);
        mImagePlayer.imageProperty().addListener((ov, o, n) -> displayImage(n));
    }

    public void start() throws SocketException {
        mImageClient.open();
        mAudioClient.open();
        mImagePlayer.start();
        mAudioPlayer.start();
    }

    public void stop() {
        mImageClient.close();
        mAudioClient.close();
        mImagePlayer.stop();
        mAudioPlayer.stop();
    }

    private void displayImage(Image img) {

        if (img != null) {
            Logs.info(getClass(), "Image has arrived! {0}x{0}.", img.getWidth(), img.getHeight());
        }

        if (img != null && mViewer != null) {
            Platform.runLater(() -> {
                mViewer.setImage(img);
            });
        }
    }

//                                                                            //
////////////////////////////////////////////////////////////////////////////////
//                                                                            //  
    private int portGetter(Callable<Integer> callable) {
        try {
            for (int i = 0; i < 30; ++i) {
                int port = callable.call();
                if (port != -1) {
                    return port;
                }
                Thread.sleep(100);
            }
        } catch (Exception ex) {
            Logs.error(getClass(), "Could not get port", ex);
        }
        return -1;
    }

    public int getImagePort() {
        return portGetter(() -> {
            return mImageClient.getPort();
        });
    }

    public int getAudioPort() {
        return portGetter(() -> {
            return mAudioClient.getPort();
        });
    }                                                                          //  

}
