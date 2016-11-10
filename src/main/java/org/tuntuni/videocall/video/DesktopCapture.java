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
package org.tuntuni.videocall.video;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;
import org.tuntuni.videocall.VideoFormat;

/**
 *
 * @author dipu
 */
public class DesktopCapture extends ImageSource {
 
    private final ScheduledExecutorService mExecutor;

    public DesktopCapture() {
        mExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void start() {
        mExecutor.scheduleAtFixedRate(threadRunner, 0, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        mExecutor.shutdown();
    }

    @Override
    public Dimension getSize() {
        return VideoFormat.getViewSize();
    }

    @Override
    public void setSize(Dimension size) {        
    }

    @Override
    public boolean isOpen() {
        return !mExecutor.isShutdown();
    }

    @Override
    public String getName() {
        return "DesktopCapture";
    }

    private final Runnable threadRunner = new Runnable() {
        @Override
        public void run() {
            try {
                // get desktop
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle screenRectangle = new Rectangle(screenSize);
                Robot robot = new Robot();
                BufferedImage image = robot.createScreenCapture(screenRectangle);

                // send image
                image = Commons.resizeImage(image, 640, 480);
                //image = Commons.resizeImage(image, VideoFormat.WIDTH, VideoFormat.HEIGHT);
                sendImage(image); 

            } catch (Exception ex) {
                Logs.error(getName(), "ERROR: {0}", ex);
            }
        }
    };
}
