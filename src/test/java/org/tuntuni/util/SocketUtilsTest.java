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
package org.tuntuni.util;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Sudipto Chandra
 */
public class SocketUtilsTest {

    public SocketUtilsTest() {
    }
 
    @Test
    public void testGetBroadcastMask() {
        System.out.println("getSubnetMask");
        int prefix = 24;
        int expResult = 0xFF;
        int result = SocketUtils.getBroadcastMask(prefix);
        assertEquals(expResult, result);
    }
 
    @Test
    public void testGetSubnetMask() {
        System.out.println("getSubnetMask");
        int prefix = 24;
        int expResult = 0xFFFF_FF00;
        int result = SocketUtils.getSubnetMask(prefix);
        assertEquals(expResult, result);
    }
 
    @Test
    public void testAddressAsInteger() throws UnknownHostException {
        System.out.println("addressAsInteger");
        InetAddress address = InetAddress.getByName("192.168.43.8");
        int expResult = (192 << 24) | (168 << 16) | (43 << 8) | 8;
        int result = SocketUtils.addressAsInteger(address);
        assertEquals(expResult, result);
    }
 
    @Test
    public void testAddressAsString() {
        System.out.println("addressAsString");
        int host = (192 << 24) | (168 << 16) | (43 << 8) | 8;
        String expResult = "192.168.43.8";
        String result = SocketUtils.addressAsString(host);
        assertEquals(expResult, result);
    }
 
    @Test
    public void testGetDefaultGateway() throws SocketException, UnknownHostException {
        System.out.println("getDefaultGateway");
        InetAddress address = InetAddress.getByName("192.168.43.8");
        int expResult = (192 << 24) | (168 << 16) | (43 << 8) | 1;
        int result = SocketUtils.getDefaultGateway(address, 24);
        //System.out.println(SocketUtils.addressAsString(result));
        assertEquals(expResult, result);
    }
 
    @Test
    public void testGetFirstHost() throws UnknownHostException, SocketException {
        System.out.println("getFirstHost");
        InetAddress address = InetAddress.getByName("192.168.43.8");
        int expResult = (192 << 24) | (168 << 16) | (43 << 8) | 1;
        int result = SocketUtils.getFirstHost(address, 24);
        assertEquals(expResult, result);
    }
 
    @Test
    public void testGetLastHost() throws UnknownHostException, SocketException {
        System.out.println("getLastHost");
        InetAddress address = InetAddress.getByName("192.168.43.8");
        int expResult = (192 << 24) | (168 << 16) | (43 << 8) | 254;
        int result = SocketUtils.getLastHost(address, 24);
        assertEquals(expResult, result);
    }

}
