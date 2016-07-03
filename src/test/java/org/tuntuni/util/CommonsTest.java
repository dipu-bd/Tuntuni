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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * to test this file input the path
 */
public class CommonsTest {

    private final String inputFile = "D:\\Pictures\\Monogram.PNG";
    private final String outputFile = "C:\\Users\\Dipu\\Desktop\\test.png";

    public CommonsTest() {
    }

    @Test
    public void testImageToBytes() throws URISyntaxException, FileNotFoundException, IOException {
        System.out.println("imageToBytes");

        FileInputStream fis = new FileInputStream(new File(inputFile));
        Image img = new Image(fis);
        fis.close();

        byte[] result = Commons.imageToBytes(img);
        assertNotNull(result);
    }

    @Test
    public void testBytesToImage() throws FileNotFoundException, IOException {
        System.out.println("bytesToImage");

        FileInputStream fis = new FileInputStream(new File(inputFile));
        Image img = new Image(fis);
        fis.close();

        byte[] data = Commons.imageToBytes(img);
        assertNotNull(data);

        Image result = Commons.bytesToImage(data);
        assertNotNull(result);

        ImageIO.write(SwingFXUtils.fromFXImage(result, null), "png", new File(outputFile));
    }

}
