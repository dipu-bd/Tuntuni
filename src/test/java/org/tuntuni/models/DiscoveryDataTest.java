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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.tuntuni.util.Commons;

/**
 *
 * @author dipu
 */
public class DiscoveryDataTest {

    public DiscoveryDataTest() {
    }

    @Test
    public void testToByte() {
        System.out.println("-testDiscoveryDataToByte");

        byte[] bt;
        DiscoveryData dd, bd;

        for (int port : new int[]{34222, 0, 65535}) {

            dd = new DiscoveryData(port);
            bt = Commons.toBytes(dd);
            assertNotNull(bt);
            System.out.println("-- Size for " + port + " =" + bt.length);
            bd = Commons.fromBytes(bt, DiscoveryData.class);
            assertNotNull(bd);
            assertEquals(dd.getPort(), bd.getPort());
            assertEquals(dd.getState(), bd.getState());

        }

        dd = new DiscoveryData();
        bt = Commons.toBytes(dd);
        assertNotNull(bt);
        System.out.println("-- Size for no port = " + bt.length);
        bd = Commons.fromBytes(bt, DiscoveryData.class);
        assertNotNull(bd);
        assertEquals(dd.getPort(), bd.getPort());
        assertEquals(dd.getState(), bd.getState());
    }

}
