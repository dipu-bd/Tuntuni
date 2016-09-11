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
import java.net.UnknownHostException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.tuntuni.connection.StreamClient;
import org.tuntuni.connection.StreamServer;

/**
 *
 * @author dipu
 */
public class VideoStreamerTest {

    private StreamServer mServer;

    public VideoStreamerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Before
    public void setUp() {
        mServer = new StreamServer();
        mServer.start();
    }

    @After
    public void tearDown() {
        mServer.stop();
    }

    /**
     * Test of start method, of class VideoStreamer.
     */
    @Test
    public void testStart() throws Exception {
        System.out.println(":: Video Streamming Test ::");
        VideoStreamer instance = new VideoStreamer(mServer);
        instance.initialize();
        instance.start();

        Thread.sleep(2000);
        
        AudioFrame aframe = mServer.getAudio();
        assertNotNull(aframe);
        System.out.println(">> Audio in server: " + aframe.getBuffer().length);
        
        ImageFrame iframe = mServer.getImage();
        assertNotNull(iframe);
        System.out.println(">> Image in server: " + iframe.getBuffer().length); 
        
        testServer();

        instance.stop();
    }

    public void testServer() throws UnknownHostException {
        StreamClient client = new StreamClient(InetAddress.getLocalHost(), mServer.getPort());
        
        AudioFrame aframe = client.getAudio();
        assertNotNull(aframe);
        System.out.println(">> Audio received: " + aframe.getBuffer().length);
        
        ImageFrame iframe = client.getImage();
        assertNotNull(iframe);
        System.out.println(">> Image Received: " + iframe.getBuffer().length); 
        
    }

}
