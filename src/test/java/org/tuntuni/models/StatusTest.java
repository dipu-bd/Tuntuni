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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author dipu
 */
public class StatusTest {

    public StatusTest() {
    }

    @Test
    public void testStatus() throws IOException, ClassNotFoundException {
        testStatus(ConnectFor.MESSAGE);
        testStatus(ConnectFor.PROFILE);
        testStatus(ConnectFor.STATE);
    }

    public void testStatus(ConnectFor write) throws IOException, ClassNotFoundException {

        System.out.println("writeStatus_" + write);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        long ago = System.nanoTime();
        oos.writeObject(write);
        long after = System.nanoTime();
        oos.close();
        baos.close();

        byte[] array = baos.toByteArray();
        System.out.println("++size=" + array.length);
        System.out.println("++time=" + (after - ago) / 1e6 + " ms");
        System.out.println();

        System.out.println("readStatus_" + write);
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ago = System.nanoTime();
        ConnectFor read = (ConnectFor) (ois.readObject());
        after = System.nanoTime();
        ois.close();
        bais.close();

        assertEquals(read, write);
        System.out.println("++read=" + read);
        System.out.println("++write=" + write);
        System.out.println("++time=" + (after - ago) / 1e6 + " ms");

        System.out.println();
    }
}
