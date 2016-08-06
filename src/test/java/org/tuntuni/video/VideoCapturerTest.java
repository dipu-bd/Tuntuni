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
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sudipto Chandra
 */
public class VideoCapturerTest {
 
    VideoCapturer instance;

    public VideoCapturerTest() {
    }

    @Before
    public void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
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
        System.out.println("stop");
        instance.stop();
    }

    @Test
    public void testListen() throws InterruptedException {
        System.out.println("listening");
        Thread.sleep(100_000);
    }

}
