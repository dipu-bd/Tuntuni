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
import javafx.beans.property.SimpleListProperty;
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.tuntuni.Core;
import org.tuntuni.models.Message; 
import org.tuntuni.models.UserData;

/**
 *
 * @author Sudipto Chandra
 */
public class ClientDataTest {

    public ClientDataTest() {
        
    }

    @Before
    public void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
    }

    @Test
    public void testGetAddress() {
        System.out.println("getAddress");
        InetSocketAddress expResult = new InetSocketAddress(2332);
        ClientData instance = new ClientData(expResult);
        InetSocketAddress result = instance.getAddress();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetHostString() {
        System.out.println("getHostString");
        String expResult = "192.45.21.2";
        ClientData instance = new ClientData(new InetSocketAddress(expResult, 1223));
        String result = instance.getHostString();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetHostName() {
        System.out.println("getHostName");
        String host = "192.45.21.2";
        ClientData instance = new ClientData(new InetSocketAddress(host, 1223));
        String result = instance.getHostName();
        assertEquals("", result);
    }

    @Test
    public void testGetPort() {
        System.out.println("getPort");
        String host = "192.45.21.2";
        ClientData instance = new ClientData(new InetSocketAddress(host, 1223));
        int expResult = 1223;
        int result = instance.getPort();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTimeout() {
        System.out.println("getTimeout");
        String host = "192.45.21.2";
        ClientData instance = new ClientData(new InetSocketAddress(host, 1223));
        instance.setTimeout(233);
        int expResult = 233;
        int result = instance.getTimeout();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetUserData() {
        System.out.println("getUserData");
        ClientData instance = new ClientData(new InetSocketAddress(1223));
        instance.setUserData(Core.instance().user().getData());
        UserData expResult = Core.instance().user().getData();
        UserData result = instance.getUserData();
        assertEquals(expResult.getUserName(), result.getUserName());
        assertEquals(expResult.getStatus(), result.getStatus());
        assertEquals(expResult.getAboutMe(), result.getAboutMe());
    } 

    @Test
    public void testMessageProperty() {
        System.out.println("messageProperty");
        ClientData instance = new ClientData(new InetSocketAddress(1223));;
        SimpleListProperty<Message> result = instance.messageProperty();
        assertNotNull(result);
    }

    @Test
    public void testAddMessage() {
        System.out.println("addMessage");
        Message message = new Message();
        ClientData instance = new ClientData(new InetSocketAddress(1223));;
        instance.messageProperty().add(message);
        instance.addMessage(message);
    } 
}
