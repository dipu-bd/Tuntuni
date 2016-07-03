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
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sudipto Chandra
 */
public class ResourcesTest {

    public ResourcesTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetPath() {
        System.out.println("+++ getPath");
        String fileName = "avatar.png";
        Resources instance = new Resources();
        String expResult = "/img/avatar.png";
        String result = instance.getPath(fileName);
        System.out.println(result);
        assertTrue(result.endsWith(expResult));
    }

    @Test
    public void testGetImage() throws IOException {
        System.out.println("getImage");
        Resources instance = new Resources();
        Image result = instance.getImage("avatar.png");
        assertNotNull(result);
        ImageIO.write(SwingFXUtils.fromFXImage(result, null), "png",
                new File("C:\\Users\\Dipu\\Desktop\\avatar.png"));

    }

}
