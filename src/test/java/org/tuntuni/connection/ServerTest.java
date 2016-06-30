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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

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
        Logger.getLogger(Server.class.getName())
                .log(Level.INFO, "Created server: " + server);
        System.out.println(server);
        server.start();
        System.out.println(server);
        while (server.isActive()) {
            Thread.sleep(2000);
            System.out.println(server);
            System.out.println(">> after 2 sec. ");
            break;
        }
        server.stop();
        System.out.println(server);
        Logger.getLogger(Server.class.getName())
                .log(Level.INFO, "Is server active: " + server.isActive());
    }

}
