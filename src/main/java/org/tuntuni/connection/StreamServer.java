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
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.tuntuni.models.Status;

/**
 * To listen and respond to clients sockets.
 */
public class StreamServer extends AbstractServer {

    /**
     * Creates a new stream Server.
     * <p>
     * It does not start the server automatically. Please call
     * {@linkplain start()} to start the server. Or you can initialize the
     * server by {@linkplain initialize()} first, then call {@linkplain start()}
     * to run server.</p>
     */
    public StreamServer() {
        super("Stream Server", null);
    }

    // process the selected socket
    @Override
    void processSocket(Socket socket) {

        try ( // DON'T CHANGE THE ORDER
                InputStream in = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in); // 
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out);) {

            while (readAndWrite(ois, oos)) {
                // continue until all read write is done
            }

        } catch (IOException ex) {
            //logger.log(Level.WARNING, Logs.SERVER_IO_FAILED, ex);
        }
    }

    // read and write single data
    boolean readAndWrite(ObjectInput oi, ObjectOutput oo) throws IOException {
        // read first

        // write after
        // don't forget to flush
        oo.flush();
        return true;
    }

    // not required now
    @Override
    Object getResponse(Status status, Socket socket, Object[] data) {
        return null;
    }
}
