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

import java.io.File;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sudipto Chandra
 */
public class VideoCapturerTest {

    static final File savePath = FileUtils.getFile(System.getProperty("user.home"), "/Desktop/video-test");

    VideoCapturer instance;

    public VideoCapturerTest() {
    }

    @Before
    public void setupServer() throws InterruptedException {
        System.out.println("initialize");
        instance = new VideoCapturer();
        instance.initialize();
        instance.start();
    }

    @After
    public void tearUp() {
        instance.stop();
    }

    @Test
    public void testAudio() throws InterruptedException {
        Thread.sleep(5_000);
    }

    @Test
    public void testVideo() {
        savePath.mkdirs();
        
    }

}
