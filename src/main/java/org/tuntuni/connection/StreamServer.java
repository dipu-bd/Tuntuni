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
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.video.DataFrame;
import org.tuntuni.video.StreamLine;

/**
 * To listen and respond to clients sockets.
 *
 * @param <T>
 */
public class StreamServer<T extends DataFrame> extends AbstractServer {

    private final ConnectFor mServerFor;
    private final StreamLine<T> mLine;

    /**
     * Creates a new stream Server.
     * <p>
     * It does not start the server automatically. Please call
     * {@linkplain start()} to start the server. Or you can initialize the
     * server by {@linkplain initialize()} first, then call {@linkplain start()}
     * to run server.</p>
     *
     * @param name Name of the server
     * @param line Line to use to write data from
     * @param serverFor Type of data this server accepts
     */
    public StreamServer(String name, StreamLine<T> line, ConnectFor serverFor) {
        super(name, null);
        mLine = line;
        mServerFor = serverFor;
    }

    // process the selected socket
    @Override
    void processSocket(Socket socket) {

        try ( // DON'T CHANGE THE ORDER
                InputStream in = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in); // 
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out);) {

            // check if connection type is valid
            ConnectFor status = ConnectFor.from(ois.readByte());
            if (status != mServerFor) {
                oos.writeBoolean(false);
                oos.flush();
                throw new IllegalAccessError(status.name());
            } else {
                oos.writeBoolean(true);
                oos.flush();
            }

            // continue data passing  
            readAndWrite(ois, oos);

        } catch (IOException ex) {
            //logger.log(Level.WARNING, Logs.SERVER_IO_FAILED, ex);
        } catch (IllegalAccessError iex) {
            Logs.warning(this.name(), Logs.STREAM_ILLEGAL_ACCESS, mServerFor, iex.getMessage());
        }
    }

    // read and write single data
    boolean readAndWrite(ObjectInput oi, ObjectOutput oo) throws IOException {
        while (isOpen()) {
            // read current time
            long time = oi.readLong();
            System.out.println("Request received at " + time); 
            // write data frame
            DataFrame data = mLine.pop();
            while (data.getTime() < time) {
                data = mLine.pop();
            }
            oo.writeObject(data);
            // don't forget to flush
            oo.flush();
        }
        return true;
    }

    // not required now
    @Override
    Object getResponse(ConnectFor status, Socket socket, Object[] data) {
        return null;
    }
}
