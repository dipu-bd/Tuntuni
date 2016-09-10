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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.tuntuni.Core;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.DiscoveryData;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;

/**
 * http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
 */
public class MulticastServer implements Runnable {

    private final int mPort;
    private Thread mServerThread;
    private DatagramSocket mSocket;
    private final SimpleMapProperty<String, Client> mUserList;

    /**
     * Creates a MulticastServer by given port to listen.
     *
     * @param port PORT address to listen
     * @throws java.net.SocketException
     */
    public MulticastServer(int port) throws SocketException {

        mPort = port;
        mUserList = new SimpleMapProperty<>(FXCollections.observableHashMap());

        // bind a datagram socket to the given port address
        try {
            mSocket = new DatagramSocket(mPort, InetAddress.getByName("0.0.0.0"));
            mSocket.setBroadcast(true);
        } catch (UnknownHostException ex) {
            Logs.error(getClass(), null, ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // MOST IMPORTANT: Start or stop subnet scans
    ////////////////////////////////////////////////////////////////////////////  
    /**
     * Starts the server thread.
     */
    public void start() {
        try {
            mServerThread = new Thread(this);
            mServerThread.setDaemon(true);
            mServerThread.start();
        } catch (Exception ex) {
            Logs.severe("Failed to START multicast server.", ex);
        }
    }

    /**
     * Stops the current server thread, if any.
     */
    public void stop() {
        try {
            if (mServerThread != null && mServerThread.isAlive()) {
                mServerThread.interrupt();
            }
        } catch (Exception ex) {
            Logs.severe("Failed to STOP multicast server.", ex);
        }
    }

    @Override
    public void run() {
        Logs.info(getClass(), "Listening for broadcast packets at {0}", mPort);

        while (true) {
            try {
                // receive a packet
                byte[] data = Commons.toBytes(new DiscoveryData());
                DatagramPacket packet = new DatagramPacket(data, data.length);
                mSocket.receive(packet);

                // convert response data
                DiscoveryData dd = Commons.fromBytes(data, DiscoveryData.class);
                if (dd == null || dd.getConnectFor() != ConnectFor.PORT) {
                    continue;
                }

                // We have a response
                Logs.info(getClass(), "Broadcast packet from server: {0}. Response = {1}",
                        packet.getAddress().getHostAddress(), dd.getPort());

                // check validity of the address
                if (dd.getPort() == Core.instance().server().getPort()
                        && Core.instance().subnet().isLocalhost(packet.getAddress().getHostAddress())) {
                    continue;
                }

                // add new client
                addUser(new Client(new InetSocketAddress(packet.getAddress(), dd.getPort())));

            } catch (Exception ex) {
                Logs.error(getClass(), "Error processing multicast socket {0}", ex);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties  and other public methods
    ////////////////////////////////////////////////////////////////////////////     
    /**
     * Gets the read-only user list property.
     * <p>
     * You can bind or attach listener to this property, but since it is
     * readonly, you can not change the values.</p>
     *
     * @return
     */
    public SimpleMapProperty<String, Client> userListProperty() {
        return mUserList;
    }

    /**
     * Search for the user client on the user list and returns the client if
     * found, otherwise a null value is returned
     *
     * @param address Address of the user to search for
     * @return null if not found.
     */
    public Client getClient(String address) {
        synchronized (mUserList) {
            return mUserList.get(address);
        }
    }

    // add new client to the list
    private void addUser(Client client) {
        new Thread(() -> {
            // check the server for connection status and profile information first
            client.checkServer();
            mUserList.put(client.getHostString(), client);
        }).start();
    }
}
