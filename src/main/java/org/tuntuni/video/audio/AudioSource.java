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
package org.tuntuni.video.audio;

import javax.sound.sampled.AudioFormat;

/**
 *
 */
public interface AudioSource {

    /**
     * Gets the name of the source
     *
     * @return
     */
    public String getName();

    /**
     * Opens the audio source for recording
     */
    public void open();

    /*
     * Closes the current source
     */
    public void close();

    /**
     * Gets the audio format used by this source
     *
     * @return
     */
    public AudioFormat getFormat();

    /**
     * Gets currently available audio frame
     *
     * @return
     */
    public AudioFrame getFrame();

    /**
     * Checks if the current frame is new one
     *
     * @return
     */
    public boolean isAudioNew();

    /**
     * Checks if the source is open
     *
     * @return
     */
    public boolean isOpen();
}
