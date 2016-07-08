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

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetSocketAddress;

/**
 * To manage connection with server sockets.
 * <p>
 * You can not create new client directly. To create a client use
 * {@linkplain Client.open()} method.</p>
 */
public class StreamClient extends AbstractClient {

    // hidesthe constructor and handle it with static open() method
    public StreamClient(InetSocketAddress socket) {
        super(socket, 0);
    }

    @Override
    void socketReceived(ObjectInput oi, ObjectOutput oo) {
        
    }
 

}
