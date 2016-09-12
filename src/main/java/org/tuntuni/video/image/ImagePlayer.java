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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Sudipto Chandra
 */
public class ImagePlayer {

    // time to wait between to successive display operation
    public static final int WAIT_INTERVAL = 25; // milliseconds
    // queue this amount of image before displaying
    public static final int MAX_BUFFER = 15;

    private final ImageView mViewer;
    private final ImageClient mClient;

    public ImagePlayer(ImageClient client, ImageView viewer) {
        mClient = client;
        mViewer = viewer;
        client.imageProperty().addListener((ov, oldImg, newImg) -> {
            displayImage(newImg);
        });
    }

    private void displayImage(Image img) {
        if (img != null && mViewer != null) {
            Platform.runLater(() -> {
                mViewer.setImage(img);
            });
        }
    }

}
