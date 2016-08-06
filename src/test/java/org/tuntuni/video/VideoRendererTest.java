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

import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Sudipto Chandra
 */
public class VideoRendererTest {

    public VideoRendererTest() {
    }

    VideoRenderer instance;

    @Before
    public void testInitialize() {
        System.out.println("initialize");
        
        VideoFormat format = new VideoFormat();
        format.setInetAddress("localhost");
        
        format.setImagePort(34218);
        format.setAudioPort(43853);
        
        instance = new VideoRenderer(format, (Image t) -> {
            System.out.println("Image received: " + t.getWidth() + " " + t.getHeight());
        });
        instance.initialize();
        instance.start();
    }

    @Before
    public void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
    }

    @After
    public void testStart() {
        System.out.println("stop");
        instance.stop();
    }

    @Test
    public void testStop() throws InterruptedException {
        System.out.println("connecting"); 
        Thread.sleep(15_000);
    }

}
