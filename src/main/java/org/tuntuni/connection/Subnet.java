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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import org.tuntuni.models.Logs;
import org.tuntuni.util.SocketUtils;

/**
 * Search all subnet masks for active users and update user-list periodically.
 */
public class Subnet {

    public final int SCAN_START_DELAY_MILLIS = 5_000;  // 5 seconds.
    public final int SCAN_INTERVAL_MILLIS = 15_000;    // 15 seconds.
    public final int REACHABLE_TIMEOUT_MILLIS = 300;   // 300 milliseconds.

    private final Logger logger = Logger.getLogger(Subnet.class.getName());

    private boolean mOnProgress; // to indicate an update on progress
    private final ExecutorService mExecutor;
    private final SetProperty<InetAddress> mUserList;
    private final ReadOnlySetProperty<InetAddress> mUserListReadOnly;

    /**
     * Creates a new instance of Subnet.
     */
    public Subnet() {
        mOnProgress = false;
        mUserList = new SimpleSetProperty<>();
        mUserListReadOnly = new ReadOnlySetWrapper<>(mUserList);
        mExecutor = Executors.newFixedThreadPool(10);
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
    public ReadOnlySetProperty<InetAddress> userListProperty() {
        return mUserListReadOnly;
    }

    /**
     * Gets the current user list.
     * <p>
     * It will return list of all sockets connected to this application at the
     * time of call. </p>
     *
     * @return
     */
    public InetAddress[] getUserList() {
        return mUserListReadOnly.toArray(new InetAddress[0]);
    }

    private boolean isBusy() {
        return mOnProgress;
    }

    // to scan over whole subnet of all networks for active users
    private final Runnable performScan = () -> {
        if (isBusy()) {
            return;
        }
        mOnProgress = true;
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
        mOnProgress = false;
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
                System.out.println("Loop Begin " + netFace.getName() + " " + ia.getAddress().toString());
                // get network address
                InetAddress address = ia.getAddress();

                // address must be an IPv4 address.
                // and it should be a site local address.
                // Site Local Address:
                // These have the scope of an entire site, or organization. 
                // They allow addressing within an organization without need for
                // using a public prefix. 
                // Routers will forward datagrams using site-local addresses 
                // within the site, but not outside it to the public Internet.
                if (!address.isSiteLocalAddress()
                        || !(address instanceof Inet4Address)) {
                    return;
                }

                logger.log(Level.INFO, Logs.SUBNET_CHECKING_SUBNETS + address.getHostAddress());

                // get network's first and last hosts
                int first = SocketUtils.getFirstHost(ia);
                int last = SocketUtils.getLastHost(ia);
                int current = SocketUtils.addressAsInteger(address);

                System.out.println("  First Address : " + SocketUtils.addressAsString(first));
                System.out.println("   Last Address : " + SocketUtils.addressAsString(last));
                System.out.println("Current Address : " + SocketUtils.addressAsString(current));

                // find all active hosts in the same local network
                for (int i = first; i <= last; ++i) {
                    // skip current address
                    if (i == current) {
                        continue;
                    }
                    taskList.add(checkAddress(i));
                }

                System.out.println("Loop End " + netFace.getName() + " " + ia.getAddress().toString());
            });
            System.out.println("Finished! " + netFace.getName());

            // wait until all tasks are done
            mExecutor.invokeAll(taskList);
            System.out.println("Done waiting! " + netFace.getName());

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
            System.out.println("Checking " + address);            
            // check if the server is up in any port of other server
            for (int i = 0; i < Server.PORTS.length; ++i) {
                InetSocketAddress remote
                        = new InetSocketAddress(address, Server.PORTS[i]);
                // blocking call to check if reachable
                if (isReachable(remote, REACHABLE_TIMEOUT_MILLIS)) {
                    addAddress(remote);
                    break;
                } else {
                    removeAddress(remote);
                }
            }
            return 0;
        };
    }

    // synchrously checks if the socket is reachable
    public boolean isReachable(InetSocketAddress socket, int timeout) {
        try {
            Client client = new Client();
            return client.test(socket, timeout);
        } catch (IOException ex) {
            logger.log(Level.FINE, Logs.SUBNET_CHECK_ERROR + socket.getHostString(), ex);
        }
        return false;
    }

    private void addAddress(InetSocketAddress remote) {
        logger.info("Add " + remote.getHostString() + ":" + remote.getPort());
    }

    private void removeAddress(InetSocketAddress remote) {
        //logger.info("Remove " + remote.getHostString() + ":" + remote.getPort());
    }

}
