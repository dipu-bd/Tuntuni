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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.tuntuni.connection.StreamServer;

/**
 *
 * @author Sudipto Chandra
 */
public class ImagePlayer extends StreamServer {

    static final int QUEUE_SIZE = 5;

    private final ImageView mViewer;
    private ConcurrentLinkedQueue<Image> mQueue;
    
    @Deprecated
    private BufferedImage mImage; 

    public ImagePlayer(int port, ImageView viewer) {
        super(port);
        mViewer = viewer;
        mViewer.setSmooth(true);
        mQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public String getName() {
        return "ImagePlayer";
    }

    @Override
    public void dataReceived(Object data) {
        if (data != null && data instanceof ImageFrame) {
            ImageFrame frame = (ImageFrame) data;
            mQueue.add(frame.getImage());
            if (mViewer != null && mQueue.size() > QUEUE_SIZE) {
                Platform.runLater(() -> {
                    mViewer.setImage(mQueue.remove());
                });
            }
        }
    }

    @Deprecated
    private void applyImage(BufferedImage image) {
        if (mImage == null
                || mImage.getWidth() != image.getWidth()
                || mImage.getHeight() != image.getHeight()) {
            mImage = image;
            return;
        }
        int p = 2, q = 1, s = p + q;
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                Color color1 = new Color(image.getRGB(x, y));
                Color color2 = new Color(mImage.getRGB(x, y));
                int r = (p * color1.getRed() + q * color2.getRed()) / s;
                int g = (p * color1.getGreen() + q * color2.getGreen()) / s;
                int b = (p * color1.getBlue() + q * color2.getBlue()) / s;
                Color color3 = new Color(r, g, b);
                mImage.setRGB(x, y, color3.getRGB());
            }
        }
    }

}
