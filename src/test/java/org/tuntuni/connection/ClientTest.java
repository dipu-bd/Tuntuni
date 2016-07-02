/*
 * Copyright 2016 Sudipto Chandra.
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
import java.net.SocketAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sudipto Chandra
 */
public class ClientTest {
    
    public ClientTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of connect method, of class Client.
     */
    @Test
    public void testConstructor() throws Exception {
        System.out.println("---- open -----");
        Client instance = new Client(new InetSocketAddress(Server.PORTS[0]));
        assertNotNull(instance);
    }
    
    @Test
    public void testServer() throws Exception {
        System.out.println("---- test -----");
        Client instance = new Client(new InetSocketAddress(Server.PORTS[0]));
        assertNotNull(instance);
        boolean result = instance.test();
        System.out.println("+++ Result found = " + result);
        assertTrue(result);
    }
    
    @Test
    public void testRandom() throws Exception {
        System.out.println("---- random -----");
        Client instance = new Client(
                new InetSocketAddress("192.121.11.23", Server.PORTS[0])); 
        assertNotNull(instance);
        boolean result = instance.test();
        System.out.println("+++ Result found = " + result);
        assertFalse(result);
    }
    
    @Test
    public void testInvalid() throws Exception {
        System.out.println("---- invalid -----");
        Client instance = new Client(
                new InetSocketAddress(22));
        assertNotNull(instance);
        boolean result = instance.test();
        System.out.println("+++ Result found = " + result);
        assertFalse(result);
    }
    
}
