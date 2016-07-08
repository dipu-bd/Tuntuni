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

import java.nio.ByteBuffer;
import javafx.scene.image.Image;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.util.Commons;

/**
 * Image data frame. Holds image information.
 */
public class ImageFrame extends DataFrame {

    public ImageFrame() {
        this(null);
    }

    public ImageFrame(ByteBuffer buffer) {
        super(ConnectFor.IMAGE);
        setBuffer(buffer.duplicate().array());
    }

    public Image getImage() {
        return Commons.bytesToImage(getBuffer());
    }

    public ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(getBuffer());
    }
}
