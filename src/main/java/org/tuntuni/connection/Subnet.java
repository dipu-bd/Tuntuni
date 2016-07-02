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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import org.tuntuni.util.Logs;
import org.tuntuni.util.SocketUtils;

/**
 * Search all subnet masks for active users and update user-list periodically.
 */
public class Subnet {

    public static final int SCAN_START_DELAY_MILLIS = 2_000;
    public static final int SCAN_INTERVAL_MILLIS = 15_000;
    public static final int REACHABLE_THREAD_COUNT = 20;
    public static final int REACHABLE_TIMEOUT_MILLIS = 500;

    private static final Logger logger = Logger.getLogger(Subnet.class.getName());

    private final ExecutorService mExecutor;
    private final SimpleSetProperty<Client> mUserList;
    private final ReadOnlySetProperty<Client> mROUserList;

    /**
     * Creates a new instance of Subnet.
     */
    public Subnet() {
        mExecutor = Executors.newFixedThreadPool(REACHABLE_THREAD_COUNT);
        mUserList = new SimpleSetProperty<>(FXCollections.observableSet());
        mROUserList = new ReadOnlySetWrapper<>(mUserList);
        // start periodic check to get active user list        
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(performScan, SCAN_START_DELAY_MILLIS,
                        SCAN_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the read-only user list property.
     * <p>
     * You can bind or attach listener to this property, but since it is
     * readonly, you can not change the values.</p>
     *
     * @return
     */
    public ReadOnlySetProperty<Client> userListProperty() {
        return mROUserList;
    }

    // to scan over whole subnet of all networks for active users
    private final Runnable performScan = () -> {
        logger.log(Level.INFO, Logs.SUBNET_SCAN_START);
        try {
            // get all network interfaces
            Enumeration<NetworkInterface> ne
                    = NetworkInterface.getNetworkInterfaces();
            // loop through all of them
            while (ne.hasMoreElements()) {
                checkNetworkInterface(ne.nextElement());
            }
        } catch (SocketException ex) {
            logger.log(Level.SEVERE, Logs.SUBNET_INTERFACE_ENUMERATION_FAILED, ex);
        }
    };

    // check all address avaiable in a network interface
    private void checkNetworkInterface(NetworkInterface netFace) {
        try {
            // loopbacks are not necessary to check
            // only check the interface that is up or active.
            if (netFace.isLoopback() || !netFace.isUp()) {
                return;
            }

            // list of all tasks
            ArrayList<Callable<Integer>> taskList = new ArrayList<>();

            // loop through addresses assigned to this interface. (usually 1)
            netFace.getInterfaceAddresses().stream().forEach((ia) -> {
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

                logger.log(Level.INFO, Logs.SUBNET_CHECKING_SUBNETS, address.getHostAddress());

                // get network's first and last hosts
                int first = SocketUtils.getFirstHost(ia);
                int last = SocketUtils.getLastHost(ia);
                int current = SocketUtils.addressAsInteger(address);
                /*
                 System.out.println("  First Address : " + SocketUtils.addressAsString(first));
                 System.out.println("   Last Address : " + SocketUtils.addressAsString(last));
                 System.out.println("Current Address : " + SocketUtils.addressAsString(current));
                 */
                // find all active hosts in the same local network
                for (int i = first; i <= last; ++i) {
                    // skip current address
                    if (i == current) {
                        continue;
                    }
                    taskList.add(checkAddress(i));
                }
            });

            // wait until all tasks are done
            mExecutor.invokeAll(taskList);

        } catch (SocketException | InterruptedException ex) {
            logger.log(Level.SEVERE, Logs.SUBNET_INTERFACE_CHECK_ERROR, ex);
        }
    }

    // check if the given address is active. 
    // if it is active add it to user list. 
    // otherwise remove it from user list, if already in it.
    private Callable<Integer> checkAddress(int host) {
        // run reachable check isn another thread
        return () -> {
            // calculate the remote network address
            String address = SocketUtils.addressAsString(host);
            //System.out.println("Checking " + address);  
            // check if the server is up in any port of other server
            for (int i = 0; i < Server.PORTS.length; ++i) {
                InetSocketAddress remote
                        = new InetSocketAddress(address, Server.PORTS[i]);

                // blocking call to check if reachable
                Client client = new Client(remote);
                client.setTimeout(REACHABLE_TIMEOUT_MILLIS);
                if (client.test()) {
                    addAddress(client);
                    break;  // found at least one port reachable
                } else {
                    removeAddress(client);
                }
                System.out.println("Tested " + client.getHostString() + ":" + client.getPort());
            }
            return 0;
        };
    }

    // add address to the observable list
    private void addAddress(Client client) {
        if (mUserList.add(client)) {
            logger.log(Level.INFO, "Added user {0}:{1}",
                    new Object[]{client.getHostString(), client.getPort()});
        }
    }

    // remove object from observable list
    private void removeAddress(Client client) {
        if (mUserList.remove(client)) {
            logger.log(Level.INFO, "Removed user {0}:{1}",
                    new Object[]{client.getHostString(), client.getPort()});
        }
    }

}
