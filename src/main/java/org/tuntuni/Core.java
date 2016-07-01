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
package org.tuntuni;

import java.io.IOException;
import org.tuntuni.connection.Client;
import org.tuntuni.connection.Server;

/**
 * For inter-process communication
 */
public class Core {

    private final Server mServer;
    private final Client mClient;

    public Core() {
        mServer = new Server();
        mClient = new Client();
    }

    public Server server() {
        return mServer;
    }

    public Client client() {
        return mClient;
    }
}
