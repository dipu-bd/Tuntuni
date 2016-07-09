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
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.video.DataFrame;
import org.tuntuni.video.StreamLine;

/**
 * To manage connection with stream server sockets.
 * <p>
 * To connect with the server you have to call {@linkplain connect()}. </p>
 *
 * @param <T>
 */
public class StreamClient<T extends DataFrame> extends AbstractClient {

    private final ConnectFor mClientFor;
    private final StreamLine<T> mLine;
    private Socket mSocket;

    /**
     * Creates a new Stream client.
     *
     * @param socket Socket address of this client.
     * @param line Line to add incoming data
     * @param clientFor Type of data frame it receives
     */
    public StreamClient(InetSocketAddress socket, StreamLine<T> line, ConnectFor clientFor) {
        super(socket, 0);
        mClientFor = clientFor;
        mLine = line;
    }

    /**
     * To connect with the server with the ConnectFor parameter this was created
     * for.
     */
    public void connect() {
        super.connect(mClientFor);
    }

    /**
     * To close the socket that is in connection, if any.
     */
    public void close() {
        try {
            if (!mSocket.isClosed()) {
                mSocket.close();
            }
        } catch (IOException ex) {
            Logs.log(Level.SEVERE, null, ex);
        }
    }

    // do something with the opened socket 
    @Override
    void socketReceived(ObjectInput oi, ObjectOutput oo, Socket socket) throws IOException {
        boolean isok = oi.readBoolean();
        if (!isok) {
            return;
        }
        mSocket = socket;

        while (!socket.isClosed()) {
            try {
                oo.writeLong(System.currentTimeMillis() - mLine.getStart());
                oo.flush();

                DataFrame frame = (DataFrame) oi.readObject();
                mLine.push(frame.getTime(), (T) frame);

            } catch (ClassNotFoundException ex) {
                Logs.log(Level.SEVERE, null, ex);
            }
        }
    }

}
