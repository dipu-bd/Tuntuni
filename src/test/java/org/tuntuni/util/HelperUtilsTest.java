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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sudipto Chandra
 */
public class HelperUtilsTest {

    public HelperUtilsTest() {
    }

    @Test
    public void testImageToBytes() throws URISyntaxException, FileNotFoundException, IOException {
        System.out.println("imageToBytes");

        FileInputStream fis = new FileInputStream(
                new File("D:\\Pictures\\Monogram.PNG"));
        Image img = new Image(fis);
        fis.close();

        byte[] result = HelperUtils.imageToBytes(img);
        assertNotNull(result);
    }

    @Test
    public void testBytesToImage() throws FileNotFoundException, IOException {
        System.out.println("bytesToImage");

        FileInputStream fis = new FileInputStream(
                new File("D:\\Pictures\\Monogram.PNG"));
        Image img = new Image(fis);
        fis.close();

        byte[] data = HelperUtils.imageToBytes(img);
        assertNotNull(data);
        
        Image result = HelperUtils.bytesToImage(data);
        assertNotNull(result);

        ImageIO.write(SwingFXUtils.fromFXImage(result, null), "png",
                new File("C:\\Users\\Dipu\\Desktop\\test.png"));        
    }

}
