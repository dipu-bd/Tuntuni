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
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.tuntuni.Core;
import org.tuntuni.models.DiscoveryData;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;

/**
 * Search all subnet masks for active users and update user-list periodically.
 */
public class Subnet {

    public static final int SCAN_START_DELAY_MILLIS = 1_000;
    public static final int SCAN_INTERVAL_MILLIS = 12_000;

    private DatagramSocket mSocket;
    private final DiscoveryData mDataToSend;
    private final ScheduledExecutorService mSchedular;
    private final HashSet<String> myAddress;

    /**
     * Creates a new instance of Subnet.
     */
    public Subnet() {
        myAddress = new HashSet<>();
        mDataToSend = new DiscoveryData();
        mSchedular = Executors.newSingleThreadScheduledExecutor();
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
        mDataToSend.setPort(Core.instance().server().getPort());
        // start periodic check to get active user list        
        mSchedular.scheduleAtFixedRate(performScan, SCAN_START_DELAY_MILLIS,
                SCAN_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
    }

    /**
     * Cancel the scheduled task.
     */
    public void stop() {
        mSchedular.shutdownNow();
        mDataToSend.setPort(-1);
        mDataToSend.changeState();
        new Thread(performScan).start();
    }

    // to scan over whole subnet of all networks for active users
    // if new network interfaces are added, it also includes them on the fly
    private final Runnable performScan = () -> {
        //Logs.info(getClass(), Logs.SUBNET_SCAN_START);
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
            //Logs.info(getClass(), "Performed a broadcast sequence");

            // Close the socket!
            mSocket.close();
        } catch (Exception ex) {
            Logs.error(getClass(), "Scan failed. {0}", ex);
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
                // Send the broadcast signal to an IPv4 address. 
                if (address instanceof Inet4Address) {
                        //&& address.isSiteLocalAddress()) {
                    sendBroadcastRequest(ia);
                }
                /*
                 --- Site Local Address ---
                 These have the scope of an entire site, or organization. 
                 They allow addressing within an organization without need for
                 using a public prefix. 
                 Routers will forward datagrams using site-local addresses 
                 within the site, but not outside it to the public Internet. 
                 */
            });

        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to check interface. {0}", ex);
        }
    }

    // send broadcast request to given address domain
    private void sendBroadcastRequest(InterfaceAddress ia) {
        try {
            // add to my address list
            myAddress.add(ia.getAddress().getHostAddress());
            // data to send
            byte[] sendData = Commons.toBytes(mDataToSend);
            // Send the broadcast package!  
            for (int port : SubnetServer.PORTS) {
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length,
                        ia.getBroadcast(), port);
                mSocket.send(sendPacket);
            }

        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to send broadcast. {0}", ex);
        }
    }

    /**
     * Checks if the given address is one of this PC's address
     *
     * @param address Address to check
     * @return
     */
    public boolean isLocalhost(String address) {
        return myAddress.contains(address);
    }

    /**
     * Gets a list of all different addresses this PC has on different network
     * interfaces.
     *
     * @return
     */
    public HashSet<String> getMyAddresses() {
        return myAddress;
    }

    /**
     * Changes the state of data to change
     */
    public void changeState() {
        mDataToSend.changeState();
    }

}
