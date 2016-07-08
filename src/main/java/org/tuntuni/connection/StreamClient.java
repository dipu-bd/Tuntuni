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

import org.tuntuni.models.Status;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import org.tuntuni.models.Logs;

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

    /**
     * Sends a request to the server.
     *
     * @return
     */
    public Object getFrame(long time) {
        // create a socket
        try (Socket socket = new Socket()) {
            // connect the socket with given address
            socket.connect(getAddress(), getTimeout());            
            processSocket(socket);
            
        } catch (IOException ex) {
            //Logs.severe(null, ex);
        }
        return null;
    }

    void processSocket(Socket socket) {
        try ( // get all input streams from socket
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out);
                InputStream in = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in);) {

        } catch (ClassNotFoundException ex) {
            Logs.severe(Logs.SOCKET_CLASS_FAILED, ex);
        }
    }

}
