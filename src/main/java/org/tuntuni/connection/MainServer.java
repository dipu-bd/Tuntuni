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
package org.tuntuni.connection;

import org.tuntuni.models.ConnectFor;
import java.net.Socket;
import java.util.Date;
import org.tuntuni.Core;
import org.tuntuni.models.Message;

/**
 * Extended by MainServer. It provides functions to deal with a request arrived
 * in MainServer from a client socket.
 */
public class MainServer extends TCPServer {

    public MainServer() {
        super("Main Server", null);
    }

    /**
     * A function to which the server requests to get response.
     *
     * @param status the status of the getResponse
     * @param from The socket involving this request.
     * @param data parameters sent by client
     * @return The getResponse object. Can be {@code null}.
     */
    @Override
    Object getResponse(ConnectFor status, Socket from, Object[] data) {
        switch (status) {
            case STATE:
                return state();
            case PROFILE: // send user data
                return profile(from);
            case MESSAGE: // a message arrived
                return message(from, data); 
            case DIAL:
                return dial(from);
            case STREAM_PORT:
                return streamPort(from);
        }
        return null;
    }

    public Object state() {
        return Core.instance().user().getState();
    }

    // what to do when ConnectFor.PROFILE getResponse arrived
    public Object profile(Socket from) {
        return Core.instance().user().getData();
    }

    // display the message 
    public Object message(Socket from, Object[] data) {
        try {
            // get message
            Message message = Message.class.cast(data[0]);
            // get client
            Client client = Core.instance().scanner().getClient(from.getInetAddress());
            if (client != null) {
                // add this message
                message.setReceiver(true);
                message.setClient(client);
                message.setTime(new Date());
                client.addMessage(message);
                return true;
            }
        } catch (Exception ex) {
            // response failure
        }
        return false;
    }

    public Object dial(Socket from) {
        try {
            // get client
            Client client = Core.instance().scanner().getClient(from.getInetAddress());
            if (client == null) {
                return false;
            }
            // start call
            Core.instance().dialer().receiveCall(client);
            return true;
        } catch (Exception ex) {
            // response failure
            return false;
        }
    }

    public Object streamPort(Socket from) {
        try {
            // get client
            Client client = Core.instance().scanner().getClient(from.getInetAddress());
            if (client != null) {
                return Core.instance().dialer().getStreamPort(client);
            }
            return -1;
        } catch (Exception ex) {
            // response failure
            return null;
        }
    }
}
