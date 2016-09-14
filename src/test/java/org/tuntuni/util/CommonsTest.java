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
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 * to test this file input the path
 */
public class CommonsTest {

    private final File inputFile = new File("D:\\Pictures\\Monogram.PNG");
    private final File outputFile = new File("C:\\Users\\Dipu\\Desktop\\test.png");

    public CommonsTest() {
    }

    @Before
    public void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
    }
 
    @Test
    public void testBytesToInt() {
        System.out.println("bytesToInt");
        byte[] bytes = {0, 0, 15, 3};
        int expResult = 0x0F03;
        int result = Commons.bytesToInt(bytes);
        assertEquals(expResult, result);
    }
 
    @Test
    public void testIntToBytes() {
        System.out.println("intToBytes");
        int number = 0x0F03;
        byte[] expResult = {0, 0, 15, 3};
        byte[] result = Commons.intToBytes(number);
        assertArrayEquals(expResult, result);
    }
 
    @Test
    public void testImageToBytes() throws URISyntaxException, FileNotFoundException, IOException {
        System.out.println("imageToBytes");

        if (!inputFile.exists()) {
            System.out.println("--Set input file first.");
            return;
        }
        FileInputStream fis = new FileInputStream(inputFile);
        Image img = new Image(fis);
        fis.close();

        byte[] result = Commons.imageToBytes(img);
        assertNotNull(result);
    }

    @Test
    public void testBytesToImage() throws FileNotFoundException, IOException {
        System.out.println("bytesToImage");

        if (!inputFile.exists()) {
            System.out.println("--Set input file first.");
            return;
        }
        FileInputStream fis = new FileInputStream(inputFile);
        Image img = new Image(fis);
        fis.close();

        byte[] data = Commons.imageToBytes(img);
        assertNotNull(data);

        Image result = Commons.bytesToImage(data);
        assertNotNull(result);

        if (!outputFile.exists()) {
            System.out.println("--Set output file first.");
            return;
        }
        ImageIO.write(SwingFXUtils.fromFXImage(result, null), "png", outputFile);
    }

}
