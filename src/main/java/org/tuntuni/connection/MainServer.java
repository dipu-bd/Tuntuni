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
import javafx.util.Callback;
import org.tuntuni.Core;
import org.tuntuni.models.Logs;
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
        // log this connection
        Logs.info(this.name(), Logs.SERVER_RECEIVED_CLIENT,
                new Object[]{status, data.length, from});

        switch (status) {
            case STATE:
                return Core.instance().user().getState();
            case PROFILE: // send user data
                return Core.instance().user().getData();
            case IMAGE_PORT:
                return Core.instance().dialer().player().getImagePort();
            case AUDIO_PORT:
                return Core.instance().dialer().player().getAudioPort();
            case MESSAGE: // a message arrived
                return message(getClient(from), data);
            case DIAL:
                return dial(getClient(from));
            case END_CALL:
             return Core.instance().dialer().endCall(getClient(from)); 
        }
        return null;
    }

    public Client getClient(Socket socket) {
        return Core.instance().scanner().getClient(socket.getInetAddress());
    }

    // display the message 
    public Object message(Client client, Object[] data) {
        // get client 
        if (client == null) {
            return false;
        }
        try {
            // get message
            Message message = Message.class.cast(data[0]);
            // add this message
            message.setReceiver(true);
            message.setClient(client);
            message.setTime(new Date());
            client.addMessage(message);
            return true;
        } catch (Exception ex) {
            // response failure
        }
        return false;
    }
  
    public Object dial(Client client) {
        if (client == null) {
            return false;
        }
        try {
            Core.instance().dialer().receiveCallAsync(client, (ex) -> {
                if (ex != null) {
                    client.endCall();
                }
                return null;
            });
            return true;
        } catch (Exception ex) {
            // response failure
            return false;
        }
    }
}
