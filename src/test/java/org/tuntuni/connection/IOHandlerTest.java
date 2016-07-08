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
package org.tuntuni.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.tuntuni.Core;
import org.tuntuni.models.UserData;

/**
 *
 * @author dipu
 */
public class IOHandlerTest {

    public IOHandlerTest() {
    }

    @Before
    public void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
    }

    @Test
    public void testStatus() throws Exception {

        System.out.println("writeStatus");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Status write = Status.PROFILE;
        IOHandler.writeObject(baos, write);
        baos.close();
        byte[] array = baos.toByteArray();
        System.out.println("++size=" + array.length);
        System.out.println();

        System.out.println("readStatus");
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        Status read = IOHandler.readObject(bais, Status.class);
        bais.close();

        assertEquals(read, write);

        System.out.println();
    }

    @Test
    public void testUserData() throws Exception {

        System.out.println("writeUserData");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        UserData write = Core.instance().user().getData();
        IOHandler.writeObject(baos, write);
        baos.close();
        byte[] array = baos.toByteArray();
        System.out.println("++size=" + array.length);
        System.out.println();

        System.out.println("readUserData");
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        UserData read = IOHandler.readObject(bais, UserData.class); 
        bais.close();

        assertEquals(read.getAboutMe(), write.getAboutMe());
        assertEquals(read.getState(), write.getState());
        assertEquals(read.getStatus(), write.getStatus());
        assertEquals(read.getUserName(), write.getUserName());

        System.out.println();
    }
     @Test
    public void testStatusObjectStream() throws Exception {

        System.out.println("writeStatus_OBJECT_OUTPUT_STREAM");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Status write = Status.PROFILE;
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(write);
        oos.close();
        baos.close();
        
        byte[] array = baos.toByteArray();
        System.out.println("++size=" + array.length);
        System.out.println();

        System.out.println("readStatus_OBJECT_INPUT_STREAM");
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Status read =  (Status) (ois.readObject());
        ois.close();
        bais.close();

        assertEquals(read, write);

        System.out.println();
    }
    
    @Test
    public void testUserDataObjectStream() throws Exception {

        System.out.println("writeUserData_OBJECT_OUTPUT_STREAM");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        UserData write = Core.instance().user().getData();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(write);
        oos.close();
        baos.close();
        
        byte[] array = baos.toByteArray();
        System.out.println("++size=" + array.length);
        System.out.println();

        System.out.println("readUserData_OBJECT_INPUT_STREAM");
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        ObjectInputStream ois = new ObjectInputStream(bais);
        UserData read =  (UserData) (ois.readObject());
        ois.close();
        bais.close();

        assertEquals(read.getAboutMe(), write.getAboutMe());
        assertEquals(read.getState(), write.getState());
        assertEquals(read.getStatus(), write.getStatus());
        assertEquals(read.getUserName(), write.getUserName());

        System.out.println();
    }
}
