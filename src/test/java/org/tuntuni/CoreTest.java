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
package org.tuntuni;

import javafx.embed.swing.JFXPanel; 
import javax.swing.SwingUtilities;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.tuntuni.connection.MainServer;

/**
 *
 * @author Sudipto Chandra
 */
public class CoreTest {

    public CoreTest() {
    }

    @Before
    public void initToolkit() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
    }

    @Test
    public void testInstance() {
        System.out.println("instance");
        Core result = Core.instance();
        assertNotNull(result);
    }

    @Test
    public void testStart() throws InterruptedException {
        System.out.println("start");
        Core instance = Core.instance();
        instance.start();
        Thread.sleep(2000);
        instance.close();
    }

    @Test
    public void testServer() {
        System.out.println("server");
        MainServer result = Core.instance().server();
        assertNotNull(result);
    }

    @Test
    public void testSubnet() {
        System.out.println("subnet");
        Core instance = Core.instance();
        assertNotNull(instance.subnet());
    }

    @Test
    public void testUser() {
        System.out.println("user");
        Core instance = Core.instance();
        assertNotNull(instance.user());
        assertNotNull(instance.user().getData());
        assertNotNull(instance.user().getData().getAvatar());
    }

}
