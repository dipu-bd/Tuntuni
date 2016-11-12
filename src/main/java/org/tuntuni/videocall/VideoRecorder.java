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
import java.net.InetAddress;
import java.net.SocketException;
import org.tuntuni.videocall.audio.AudioSource;
import org.tuntuni.videocall.audio.MicrophoneAudio;
import org.tuntuni.videocall.video.ImageSource; 

/**
 *
 */
public class VideoRecorder {

    private final InetAddress mAddress;
    private final AudioSource mAudioSource;
    private final ImageSource mImageSource;

    public VideoRecorder(InetAddress address) {
        mAddress = address;
        mImageSource = new ImageSource();
        mAudioSource = new MicrophoneAudio();
    }

    public void start() throws SocketException, IOException {
        mAudioSource.connect(mAddress, Dialer.AUDIO_PORT);
        mAudioSource.start();
 
        mImageSource.connect(mAddress, Dialer.IMAGE_PORT);
        mImageSource.start();
    }

    public void stop() {
        mImageSource.stop();
        mImageSource.close();

        mAudioSource.stop();
        mAudioSource.close();
    }
 
    public void setScreen(boolean shareScreen) {
        mImageSource.setScreen(shareScreen);
    }
}
