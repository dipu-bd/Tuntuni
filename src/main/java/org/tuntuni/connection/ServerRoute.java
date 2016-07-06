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

import java.net.Socket;
import java.util.Date;
import org.tuntuni.Core;
import org.tuntuni.models.Message;
import org.tuntuni.util.SocketUtils;

/**
 * Extended by Server. It provides functions to deal with a request arrived in
 * Server from a client socket.
 */
public abstract class ServerRoute {

    /**
     * A function to which the server requests to get response.
     *
     * @param status the status of the getResponse
     * @param from The socket involving this request.
     * @param data parameters sent by client
     * @return The getResponse object. Can be {@code null}.
     */
    public Object getResponse(Status status, Socket from, Object[] data) {
        switch (status) {
            case EMPTY:
                return true;
            case PROFILE: // send user data
                return profile(from);
            case MESSAGE: // a message arrived
                return message(from, data);
        }
        return null;
    }

    // what to do when Status.PROFILE getResponse arrived
    public Object profile(Socket from) {
        Core.instance().subnet().addAsClient(
                SocketUtils.getRemoteHost(from));
        return Core.instance().user().getData();
    }

    // display the message 
    public Object message(Socket from, Object[] data) {
        try {
            // get message
            Message message = (Message) data[0];
            // sender's address
            String remote = SocketUtils.getRemoteHost(from);
            // get client
            Client client = Core.instance().subnet().getClient(remote);
            // add this message
            client.addMessage(message);
            message.setReceiver(true);
            message.setClient(client);
            message.setTime(new Date());
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            // response failure
            return false;
        }
    }

}
