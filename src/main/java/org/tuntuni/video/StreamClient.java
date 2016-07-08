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
package org.tuntuni.video;

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
import java.util.logging.Logger;
import org.tuntuni.models.Logs;
import org.tuntuni.models.Message;
import org.tuntuni.models.UserData;

/**
 * To manage connection with server sockets.
 * <p>
 * You can not create new client directly. To create a client use
 * {@linkplain Client.open()} method.</p>
 */
public class StreamClient {

    public static final int DEFAULT_TIMEOUT = 500;

    private static final Logger logger = Logger.getGlobal();

    // hidesthe constructor and handle it with static open() method
    public StreamClient(InetSocketAddress socket) {
        super(socket);
    }
 
    /**
     * Sends a request to the server.
     *
     * @param status Status of the request
     * @param data Any data to pass along the request
     * @return
     */
    public Object request(Status status, Serializable... data) {
        // create a socket
        try (Socket socket = new Socket()) {
            // connect the socket with given address
            socket.connect(getAddress(), getTimeout());

            try ( // get all input streams from socket
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    InputStream in = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(in);) {

                // write status
                oos.writeByte(status.data());
                oos.flush();
                // write data length
                oos.writeInt(data.length);
                oos.flush();
                // send all data
                for (Object o : data) {
                    oos.writeObject(o);
                    oos.flush();
                }

                // return result
                return ois.readObject();

            } catch (IOException | ClassNotFoundException ex) {
                logger.log(Level.SEVERE, Logs.SOCKET_CLASS_FAILED, ex);
            }
        } catch (IOException ex) {
            //logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Test the server is available at the address.
     *
     * @return {@code true} if available, {@code false} otherwise
     */
    public boolean checkServer() {
        if (!isConnected()) {
            return getProfile();
        }
        // check if server is alive
        Object ob = request(Status.STATE);
        if (ob == null || !(ob instanceof String)) {
            setConnected(false);
            return false;
        }
        // now check if states are the same
        String result = (String) ob;
        if (!result.equals(getState())) {
            return getProfile();
        }
        return true;
    }

    public boolean getProfile() {
        // get getProfile data
        try {
            UserData profile = (UserData) request(Status.PROFILE);
            setUserData(profile);
            if (profile != null) {
                setConnected(true);
                return true;
            }
        } catch (NullPointerException ex) {
            //logger.log(Level.SEVERE, Logs.CLIENT_TEST_FAILED, ex);
        }
        return false;
    }

    /**
     * Send a sendMessage to this client
     *
     * @param toSent Message to be sent
     * @return True if success, false otherwise.
     */
    public boolean sendMessage(Message toSent) {
        Object result = request(Status.MESSAGE, toSent);
        return (result instanceof Boolean) ? (boolean) result : false;
    }
}
