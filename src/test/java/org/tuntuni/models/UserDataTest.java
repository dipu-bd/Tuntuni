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
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.tuntuni.Core;

/**
 *
 * @author Sudipto Chandra
 */
public class UserDataTest {

    public UserDataTest() {
    }

    @Before
    public void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
    }

    @Test
    public void testUserDataObjectStream() throws Exception {

        System.out.println("writeUserData");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        UserData write = Core.instance().user().getData();
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

        System.out.println("readUserData");
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ago = System.nanoTime();
        UserData read = (UserData) (ois.readObject());
        after = System.nanoTime();
        ois.close();
        bais.close();

        assertEquals(read.getAboutMe(), write.getAboutMe()); 
        assertEquals(read.getStatus(), write.getStatus());
        assertEquals(read.getUserName(), write.getUserName());
        assertArrayEquals(read.getAvatarData(), write.getAvatarData());        
        System.out.println("++time=" + (after - ago) / 1e6 + " ms");

        System.out.println();
    }

}
