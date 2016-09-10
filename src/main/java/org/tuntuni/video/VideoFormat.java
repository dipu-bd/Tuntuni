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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.sound.sampled.AudioFormat;

/**
 *
 * @author Sudipto Chandra
 */
public abstract class VideoFormat {

    // frame width
    public static final int WIDTH = 640;
    // frame height
    public static final int HEIGHT = 480;
    // image type
    public static final int TYPE = BufferedImage.TYPE_INT_RGB;

    //sampleRate - the number of samples per second
    public static final float SAMPLE_RATE = 44100;
    //sampleSizeInBits - the number of bits in each sample
    public static final int SAMPLE_SIZE = 16;
    //channels - the number of channels (1 for mono, 2 for stereo, and so on)
    public static final int CHANNEL = 2;
    //signed - indicates whether the data is signed or unsigned
    public static final boolean SIGNED = true;
    //bigEndian - indicates whether the data for a single sample is stored in big-endian byte order
    public static final boolean BIG_ENDIAN = true;

    /**
     * @return the dimension of the video frame
     */
    public static Dimension getViewSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * @return the audio format of this video
     */
    public static AudioFormat getAudioFormat() {
        return new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNEL, SIGNED, BIG_ENDIAN);
    }

}
