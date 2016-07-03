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
package org.tuntuni.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 * Some commonly used functions and methods
 */
public class Commons {
 
    public static byte[] imageToBytes(Image img) {
        try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream()) {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", byteOutput);
            return byteOutput.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    public static Image bytesToImage(byte[] data) {
        try (ByteArrayInputStream byteInput = new ByteArrayInputStream(data)) {
            return new Image(byteInput);
        } catch (IOException ex) {
            return null;
        }
    }

}
