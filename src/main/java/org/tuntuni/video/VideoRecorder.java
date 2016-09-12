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

import java.net.InetAddress;
import java.net.SocketException;
import org.tuntuni.video.audio.AudioServer;
import org.tuntuni.video.audio.AudioSource;
import org.tuntuni.video.audio.MicrophoneAudio;
import org.tuntuni.video.image.ImageServer;
import org.tuntuni.video.image.ImageSource;
import org.tuntuni.video.image.WebcamCapture;

/**
 *
 */
public class VideoRecorder {

    private final int mAudioPort;
    private final int mImagePort;
    private final InetAddress mAddress;

    private final ImageSource mImageSource;
    private final ImageServer mImageServer;

    private final AudioSource mAudioSource;
    private final AudioServer mAudioServer;

    public VideoRecorder(InetAddress address, int imagePort, int audioPort) {
        mAddress = address;
        mImagePort = imagePort;
        mAudioPort = audioPort;
         
        mImageSource = new WebcamCapture();
        mImageServer = new ImageServer(mImageSource);

        mAudioSource = new MicrophoneAudio();
        mAudioServer = new AudioServer(mAudioSource);
    }

    public void start() throws SocketException {
        // open servers
        mImageServer.open();
        mAudioServer.open();         
        // connect
        mImageServer.connect(mAddress, mImagePort);
        mAudioServer.connect(mAddress, mAudioPort);
        // open sources
        mImageSource.open();
        mAudioSource.open();
    }

    public void stop() {
        // stop sources
        mImageSource.close();
        mAudioSource.close();
        // stop servers
        mImageServer.close();
        mAudioServer.close();
    }
}
