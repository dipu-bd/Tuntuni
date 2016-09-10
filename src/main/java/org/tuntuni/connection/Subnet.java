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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.tuntuni.Core;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;

/**
 * Search all subnet masks for active users and update user-list periodically.
 */
public class Subnet {

    public static final int SCAN_START_DELAY_MILLIS = 1_000;
    public static final int SCAN_INTERVAL_MILLIS = 5_000;

    private DatagramSocket mSocket;
    private final ScheduledExecutorService mSchedular;
    private final HashSet<String> myAddress;
    private final SimpleMapProperty<String, Client> mUserList;

    /**
     * Creates a new instance of Subnet.
     */
    public Subnet() {
        myAddress = new HashSet<>();
        mUserList = new SimpleMapProperty<>(FXCollections.observableHashMap());
        mSchedular = Executors.newSingleThreadScheduledExecutor();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties and public methods
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

    ////////////////////////////////////////////////////////////////////////////
    // MOST IMPORTANT: Start or stop subnet scans
    ////////////////////////////////////////////////////////////////////////////    
    /**
     * Start the periodic task that scan through all possible subnets.
     * <p>
     * After calling the {@linkplain start()} method, the first scan starts
     * after around {@value #SCAN_START_DELAY_MILLIS} milliseconds and repeat
     * once every {@value #SCAN_INTERVAL_MILLIS} milliseconds. </p>
     */
    public void start() {
        // start periodic check to get active user list        
        mSchedular.scheduleAtFixedRate(performScan, SCAN_START_DELAY_MILLIS,
                SCAN_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
    }

    /**
     * Cancel the scheduled task.
     */
    public void stop() {
        mSchedular.shutdownNow();
    }

    // to scan over whole subnet of all networks for active users
    // if new network interfaces are added, it also includes them on the fly
    private final Runnable performScan = () -> {
        Logs.info(getClass(), Logs.SUBNET_SCAN_START);
        try {
            // open a datagram socket at any random port
            mSocket = new DatagramSocket();
            mSocket.setBroadcast(true);

            // get all network interfaces
            Enumeration<NetworkInterface> ne
                    = NetworkInterface.getNetworkInterfaces();

            // loop through all of them
            while (ne.hasMoreElements()) {
                checkNetworkInterface(ne.nextElement());
            }
            Logs.info(getClass(), Logs.SUBNET_SCAN_SUCCESS);

            // Wait for a response
            recieveBroadcastResponse();

            // Close the socket!
            mSocket.close();
        } catch (Exception ex) {
            Logs.error(getClass(), Logs.SUBNET_SCAN_FAILED, ex);
        }

    };

    // check all address avaiable in a network interface
    private void checkNetworkInterface(NetworkInterface ni) {
        try {
            // loopbacks are not necessary to check
            // only check the interface that is up or active.
            if (ni.isLoopback() || !ni.isUp()) {
                return;
            }

            // list of all tasks
            //ArrayList<Callable<Integer>> taskList = new ArrayList<>();
            // loop through addresses assigned to this interface. (usually 1)
            ni.getInterfaceAddresses().stream().forEach((ia) -> {
                // get network address
                InetAddress address = ia.getAddress();

                // address must be an IPv4 address.
                // and it should be a site local address.
                // --- Site Local Address ---
                // These have the scope of an entire site, or organization. 
                // They allow addressing within an organization without need for
                // using a public prefix. 
                // Routers will forward datagrams using site-local addresses 
                // within the site, but not outside it to the public Internet.
                if (!address.isSiteLocalAddress()
                        || !(address instanceof Inet4Address)) {
                    return;
                }

                //send the broadcast signal
                sendBroadcastRequest(ia);
            });

        } catch (Exception ex) {
            Logs.error(getClass(), Logs.SUBNET_INTERFACE_CHECK_ERROR, ex);
        }
    }

    // send broadcast request to given address domain
    private void sendBroadcastRequest(InterfaceAddress ia) {
        try {
            Logs.info(getClass(), "Sending a port request to ", ia.getBroadcast());

            // add to my address list
            myAddress.add(ia.getAddress().getHostAddress());

            // data to send
            byte[] sendData = {ConnectFor.PORT.data()};

            // Send the broadcast package!
            for (int port : Core.PORTS) {
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length, ia.getBroadcast(), port);
                mSocket.send(sendPacket);
            }
        } catch (Exception ex) {
            Logs.error(getClass(), "Broadcast request failure. {0}", ex);
        }
    }

    // recieve broadcast response and process it
    private void recieveBroadcastResponse() {
        try {
            // recieve response
            byte[] data = new byte[4];
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);
            mSocket.receive(receivePacket);

            // convert response data
            int port = Commons.bytesToInt(data);

            // We have a response
            Logs.info(getClass(), "Broadcast response from server: {0}. Response = {1}",
                    receivePacket.getAddress().getHostAddress(), port);

            // check validity of the address
            if (port == Core.instance().server().getPort()
                    && myAddress.contains(receivePacket.getAddress().getHostAddress())) {
                return;
            }

            // add new client
            addUser(new Client(new InetSocketAddress(receivePacket.getAddress(), port)));
        } catch (Exception ex) {
            Logs.error(getClass(), "Error recieving broadcast response: {0}", ex);
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
