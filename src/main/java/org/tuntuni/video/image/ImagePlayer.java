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
package org.tuntuni.video.image;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.tuntuni.image.FrameBuilder;
import org.tuntuni.image.FrameListener;
import org.tuntuni.image.ImageFrame;

/**
 *
 * @author Sudipto Chandra
 */
public class ImagePlayer extends ImageServer implements FrameListener {

    private final ImageView mViewer;
    private final FrameBuilder mFramer;

    public ImagePlayer(ImageView viewer) {
        mViewer = viewer;
        mFramer = new FrameBuilder();
        mFramer.setListener(this);
    }

    @Override
    public void displayImage(ImageFrame image) {
        //mFramer.putFrame(image);
        imageUpdated(SwingFXUtils.toFXImage(image.getImage(), null));
    }

    @Override
    public void imageUpdated(Image image) {
        if (mViewer != null && image != null) {
            Platform.runLater(() -> {
                mViewer.setImage(image);
            });
        }
    }

    @Override
    public void frameUpdated(ImageFrame frame) {
    }

}
