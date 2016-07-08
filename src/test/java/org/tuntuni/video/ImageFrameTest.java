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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sudipto Chandra
 */
public class ImageFrameTest {

    public ImageFrameTest() {
    }

    @Test
    public void testExternalizable() throws IOException, ClassNotFoundException {
        System.out.println("testExternalizableWRITE");
        BufferedImage img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        ImageFrame write = new ImageFrame(System.nanoTime(), img);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        long ago, after;

        ago = System.nanoTime();
        oos.writeObject(write);
        after = System.nanoTime();

        oos.close();
        baos.close();

        byte[] data = baos.toByteArray();
        System.out.println("++size = " + data.length);
        System.out.println("++time = " + (after - ago) / 1e6 + " ms");

        System.out.println("testExternalizableREAD");

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);

        ago = System.nanoTime();
        ImageFrame read = (ImageFrame) ois.readObject();
        after = System.nanoTime();

        ois.close();
        oos.close();

        System.out.println("++time = " + (after - ago) / 1e6 + " ms");

        assertEquals(read.getTime(), write.getTime());
        assertArrayEquals(read.getBuffer(), write.getBuffer());
    }

}
