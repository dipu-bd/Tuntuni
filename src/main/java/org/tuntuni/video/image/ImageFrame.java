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

import java.awt.image.BufferedImage;
import java.util.Arrays;
import javafx.scene.image.Image;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.util.Commons;
import org.tuntuni.connection.DataFrame;

/**
 * Image data frame. Holds image information.
 */
public class ImageFrame extends DataFrame {

    public ImageFrame() {
    }

    public ImageFrame(byte[] data) {
        super(data, data.length);
    }

    public ImageFrame(BufferedImage image) {
        this(Commons.imageToBytes(image));
    }

    public Image getImage() {
        return Commons.bytesToImage(getBuffer());
    }
}
