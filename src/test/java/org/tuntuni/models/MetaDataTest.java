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
package org.tuntuni.models;

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
public class MetaDataTest {

    public MetaDataTest() {
    }

    /**
     * Test of version method, of class MetaData.
     */
    @Test
    public void testVersion() {
        System.out.println("testVersion()");
        MetaData instance = new MetaData();
        String result = instance.version();
        assertNotNull(result);
        System.out.println("++" + result);
        System.out.println();
    }

    /**
     * Test of hostName method, of class MetaData.
     */
    @Test
    public void testHostName() {
        System.out.println("testHostName()");
        MetaData instance = new MetaData();
        String result = instance.hostName();
        assertNotNull(result);
        System.out.println("++" + result);
        System.out.println();
    }

    /**
     * Test of title method, of class MetaData.
     */
    @Test
    public void testTitle() {
        System.out.println("testTitle()");
        MetaData instance = new MetaData();
        String result = instance.title();
        assertNotNull(result);
        System.out.println("++" + result);
        System.out.println();
    }

    @Test
    public void testSerialie() throws IOException, ClassNotFoundException {
        System.out.println("testSerializable()");
        MetaData meta = new MetaData();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(meta);

        oos.close();
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        ois.close();
        bais.close();

        assertNotNull(ois.readObject());
    }
}
