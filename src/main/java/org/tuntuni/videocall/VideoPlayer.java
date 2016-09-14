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
package org.tuntuni.videocall;

import java.io.IOException;
import java.net.SocketException;
import javafx.scene.image.ImageView;
import org.tuntuni.videocall.audio.AudioPlayer;
import org.tuntuni.videocall.video.ImagePlayer;

/**
 *
 * @author Sudipto Chandra
 */
public class VideoPlayer {

    private final ImagePlayer mImagePlayer;
    private final AudioPlayer mAudioPlayer;

    public VideoPlayer(ImageView viewer) {
        mImagePlayer = new ImagePlayer(Dialer.IMAGE_PORT, viewer);
        mAudioPlayer = new AudioPlayer(Dialer.AUDIO_PORT);
    }

    public void start() throws SocketException, IOException {
        mImagePlayer.open(); 
        mAudioPlayer.open();
        mAudioPlayer.start();
    }

    public void stop() {
        mImagePlayer.close();
        mAudioPlayer.stop();
        mAudioPlayer.close();
    }

//                                                                            //
////////////////////////////////////////////////////////////////////////////////
//                                                                            //  
    public int getImagePort() {
        return mImagePlayer.getPort();
    }

    public int getAudioPort() {
        return mAudioPlayer.getPort();
    }

}
