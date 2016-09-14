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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Sudipto Chandra
 */
public class MessageTest {
    
    public MessageTest() {
    }
 
    public void testMessage(String message) throws Exception {

        System.out.println("writeMessage");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Message write = new Message();
        write.setText(message);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        long ago = System.nanoTime();
        oos.writeObject(write);
        long after = System.nanoTime();
        oos.close();
        baos.close();

        byte[] array = baos.toByteArray();
        System.out.println("++size=" + array.length);
        System.out.println("++time=" + (after - ago) / 1e6 + " ms");
        //System.out.println();

        System.out.println("readMessage");
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ago = System.nanoTime();
        Message read = (Message) (ois.readObject());
        after = System.nanoTime();
        ois.close();
        bais.close();

        assertEquals(read.getText(), write.getText());  
        System.out.println("++time=" + (after - ago) / 1e6 + " ms");

        System.out.println();
    }
    
 
    @Test
    public void testMessage() throws Exception {
        testMessage("");
        testMessage("Hello How are you?");
        testMessage("This is good test. Goooood test");
        testMessage("A very loooooooooooooooooooooooooooooooooooooooooong message");
    }

    
}
