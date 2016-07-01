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

import org.junit.Test;

/**
 *
 * @author Sudipto Chandra
 */
public class ServerTest {

    public ServerTest() {
    }

    @Test
    public void testServer() throws Exception {

        System.out.println("---------start----------------");

        Server server = new Server();
        System.out.println("Created server");

        server.start();
        System.out.println("Started server. Server active = " + server.isOpen());

        if (server.isOpen()) {
            Thread.sleep(3600_000);
            System.out.println(">> after a while... ");
        }

        server.stop();
        System.out.println("Stopped server. Server active = " + server.isOpen());
    }

}
