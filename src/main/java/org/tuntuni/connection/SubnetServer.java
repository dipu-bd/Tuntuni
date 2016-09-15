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
import org.tuntuni.models.DiscoveryData;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;
import org.tuntuni.util.SocketUtils;

/**
 *
 * @author Sudipto Chandra
 */
public class SubnetServer implements Runnable {

    public static final int[] PORTS = {
        24914,
        42914,
        //62915
    };

    private Thread mServerThread;
    private DatagramSocket mSocket;
    private final SimpleMapProperty<Integer, Client> mUserList;

    /**
     * Creates a MulticastServer by given port to listen.
     */
    public SubnetServer() {
        mUserList = new SimpleMapProperty<>(FXCollections.observableHashMap());

    }

    ////////////////////////////////////////////////////////////////////////////
    // MOST IMPORTANT: Start or stop subnet scans
    ////////////////////////////////////////////////////////////////////////////  
    /**
     * Starts the server thread.
     */
    public void start() {
        // bind a datagram socket to the given port address 
        for (int port : PORTS) {
            try {
                mSocket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
                mSocket.setBroadcast(true);
                break;
            } catch (UnknownHostException | SocketException ex) {
                mSocket = null;
            }
        }
        if (mSocket == null) {
            Logs.error(getClass(), "Failed to start.");
        }
        mServerThread = new Thread(this);
        mServerThread.setDaemon(true);
        mServerThread.start();
    }

    /**
     * Stops the current server thread, if any.
     */
    public void stop() {
        if (mServerThread != null) {
            mServerThread.interrupt();
            mSocket.close();
        }
    }

    public int getPort() {
        return mSocket == null ? -1 : mSocket.getLocalPort();
    }

    @Override
    public void run() {
        Logs.info(getClass(), "Listening for broadcast packets at {0}", getPort());

        while (!Thread.interrupted()) {
            try {
                // receive a packet
                byte[] data = Commons.toBytes(new DiscoveryData());
                DatagramPacket packet = new DatagramPacket(data, data.length);
                mSocket.receive(packet);

                // convert response data
                DiscoveryData dd = Commons.fromBytes(data, DiscoveryData.class);
                if (dd == null) {
                    continue;
                }

                // We have a response
                Logs.info(getClass(), "Packet received. From: {0}, Data: {1},{2}",
                        packet.getAddress().getHostAddress(), dd.getPort(), dd.getState());

                // check validity of the address
                if (dd.getPort()
                        == Core.instance().server().getPort()
                        && Core.instance().subnet().isLocalhost(packet.getAddress().getHostAddress())) {
                    continue;
                }

                // add new client
                Thread t = new Thread(() -> {
                    addUser(packet.getAddress(), dd);
                });

                t.setDaemon(true);
                t.start();

            } catch (Exception ex) {
                Logs.error(getClass(), "Error processing packet. {0}", ex);
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
    public SimpleMapProperty<Integer, Client> userListProperty() {
        return mUserList;
    }

    /**
     * Search for the user client on the user list and returns the client if
     * found, otherwise a null value is returned
     *
     * @param address Address of the user to search for
     * @return null if not found.
     */
    public Client getClient(final InetAddress address) {
        return mUserList.get(SocketUtils.addressAsInteger(address));
    }

    // add new client to the list
    private void addUser(InetAddress address, DiscoveryData dd) {

        int port = dd.getPort();
        int key = SocketUtils.addressAsInteger(address);
        Client client = mUserList.get(key);

        // add new user
        if (client == null && port != -1) {
            client = new Client(new InetSocketAddress(address, port));
            mUserList.put(key, client);
        }

        if (client != null) {

            // remove the disconnected user
            if (port == -1) {
                client.setConnected(false);
                mUserList.remove(key);
            }

            // update user port
            if (port != -1 && client.getPort() != port) {
                client.updateAddress(new InetSocketAddress(address, port));
            }

            // check server
            if (!client.isConnected() || dd.getState() != client.getState()) {
                client.downloadProfile(dd.getState());
            }
        }
    }

}
