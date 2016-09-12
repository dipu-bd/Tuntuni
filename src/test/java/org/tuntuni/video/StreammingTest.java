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
import java.net.UnknownHostException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author dipu
 */
public class StreammingTest {

    private VideoPlayer mPlayer;
    private VideoRecorder mRecorder;

    public StreammingTest() {
    }

    @Before
    public void setUp() throws SocketException, UnknownHostException {
        System.out.println(":: Video Streamming Test ::");
        
        System.out.println(">>> starting player");        
        mPlayer = new VideoPlayer(null);
        mPlayer.start();
        System.out.println("Image port = " + mPlayer.getImagePort());
        System.out.println("Audio port = " + mPlayer.getAudioPort());
        
        System.out.println(">>> starting recorder");
        mRecorder = new VideoRecorder(
                InetAddress.getLocalHost(),
                mPlayer.getImagePort(),
                mPlayer.getAudioPort());
        mRecorder.start();
    }

    @After
    public void tearDown() {
        System.out.println(">>> stopping");
        mPlayer.stop();
        mRecorder.stop();
    }

    /**
     * Test of start method, of class VideoStreamer.
     */
    @Test
    public void testStart() throws Exception {
        // take 10 sec rest
        Thread.sleep(20_000);
    }

}
