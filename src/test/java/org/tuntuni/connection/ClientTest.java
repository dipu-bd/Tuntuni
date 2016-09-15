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

import java.net.InetSocketAddress; 
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.tuntuni.Core;
import org.tuntuni.models.Message;
import org.tuntuni.models.UserData;

/**
 *
 * @author Sudipto Chandra
 */
public class ClientTest {
     
    public ClientTest() {

    }

    @Before
    public void setUp() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
    }

    @After
    public void tearDown() {
    }
   
    @Test
    public void testGetAddress() {
        System.out.println("getAddress");
        InetSocketAddress expResult = new InetSocketAddress(2332);
        Client instance = new Client(expResult);
        InetSocketAddress result = instance.getSocketAddress();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetHostString() {
        System.out.println("getHostString");
        String expResult = "192.45.21.2";
        Client instance = new Client(new InetSocketAddress(expResult, 1223));
        String result = instance.getHostString();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetHostName() {
        System.out.println("getHostName");
        String host = "192.45.21.2";
        Client instance = new Client(new InetSocketAddress(host, 1223));
        String result = instance.getHostName(); 
    }

    @Test
    public void testGetPort() {
        System.out.println("getPort");
        String host = "192.45.21.2";
        Client instance = new Client(new InetSocketAddress(host, 1223));
        int expResult = 1223;
        int result = instance.getPort();
        assertEquals(expResult, result);
    }


    @Test
    public void testGetUserData() {
        System.out.println("getUserData");
        Client instance = new Client(new InetSocketAddress(1223));
        instance.setUserData(Core.instance().user().getData());
        UserData expResult = Core.instance().user().getData();
        UserData result = instance.getUserData();
        assertEquals(expResult.getUserName(), result.getUserName());
        assertEquals(expResult.getStatus(), result.getStatus());
        assertEquals(expResult.getAboutMe(), result.getAboutMe());
    }

    @Test
    public void testAddMessage() {
        System.out.println("addMessage");
        Message message = new Message();
        Client instance = new Client(new InetSocketAddress(1223));;
        instance.messageList().add(message);
        instance.addMessage(message);
    }

}
