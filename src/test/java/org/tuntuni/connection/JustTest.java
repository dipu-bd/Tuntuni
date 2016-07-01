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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import static org.junit.Assert.*;
import static java.lang.System.out;

/**
 *
 * @author Sudipto Chandra
 */
public class JustTest {

    public JustTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // get a list of avaiable subnet masts    
    @Test
    public void testSubnet() throws UnknownHostException, SocketException, IOException {
        Enumeration ne = NetworkInterface.getNetworkInterfaces();
        while (ne.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) ne.nextElement();
            if (!ni.isUp() || ni.isLoopback()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {

                InetAddress address = interfaceAddress.getAddress();
                InetAddress broadcast = interfaceAddress.getBroadcast();

                // we need the site local addresses 
                if (!address.isSiteLocalAddress()) {
                    continue;
                }

                // show a little information
                showNetInfo(ni);
                showIpInfo(address);
                showIpInfo(broadcast);

                // calculate network's start and end address
                int p = interfaceAddress.getNetworkPrefixLength();
                int q = 32 - p;
                int ip = byteToInt(address.getAddress());
                int bdc = byteToInt(broadcast.getAddress());
                int start = ((ip >> q) << q) + 1;
                int end = bdc - 1;

                // show data
                out.println();
                out.printf("Network prefix length: %d\n", p);
                out.printf("IP Address: %s\n", formatIP(ip));
                out.printf("Broadcast Address: %s\n", formatIP(bdc));
                out.printf("Default Gateway Address: %s\n", formatIP(start));
                out.printf("Last Valid Address: %s\n", formatIP(end));

                System.out.println("\nSearching for active users...");

                // find all active hosts in the same local network
                int found = 0;
                for (int i = start; i <= end; ++i) {
                    String lanip = formatIP(i);
                    try {

                        InetAddress lanPC = InetAddress.getByAddress(intToByte(i));
                        //if (isReachable(lanip, 22, 100)) {
                        //if (isReachable(lanip)) {  // <<= this one is the best
                        if (lanPC.isReachable(100)) {
                            ++found;
                            out.printf("%s is reachable.\n", lanip);
                        }
                    } catch (Exception ex) {
                        out.println("Count not connect to " + lanip);
                    }
                }

                out.printf("\nSearch completed. %d active PC found.\n\n", found);
                assertTrue(found >= 1);
            }
        }
    }

    boolean isReachable(String addr) {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 " + addr);
            int returnVal = p1.waitFor();
            return (returnVal == 0);
        } catch (InterruptedException | IOException ex) {
            return false;
        }
    }

    boolean isReachable(String addr, int openPort, int timeOutMillis) {
        // Any Open port on other machine
        // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public String formatIP(int ip) {
        byte[] arr = intToByte(ip);
        return String.format("%d.%d.%d.%d",
                Byte.toUnsignedInt(arr[0]),
                Byte.toUnsignedInt(arr[1]),
                Byte.toUnsignedInt(arr[2]),
                Byte.toUnsignedInt(arr[3]));
    }

    public int byteToInt(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).getInt();
    }

    public byte[] intToByte(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public void showIpInfo(InetAddress i) throws IOException {
        if (i == null) {
            return;
        }
        out.print(i.getHostAddress());
        //out.print(" ");
        //out.print(i.getHostName().equals(i.getHostAddress()) ? "" : i.getHostName());
        //out.print(" \t ");
        //out.print(i.isReachable(1000) ? "reachable" : "unreachable");
        out.print(" \t ");
        out.print(i.isSiteLocalAddress() ? "site local address" : "");
        out.print(" \t ");
        out.print(i.isLinkLocalAddress() ? "link local address" : "");
        out.print(" \t ");
        out.print(i.isAnyLocalAddress() ? "wildcard local address" : "");
        out.print(" \t ");
        out.print(i.isLoopbackAddress() ? "loopback" : "");
        out.print(" \t ");
        out.print(i.isMCGlobal() ? "mc global" : "");
        out.print(" \t ");
        out.print(i.isMCLinkLocal() ? "mc link local" : "");
        out.print(" \t ");
        out.print(i.isMCNodeLocal() ? "mc node local" : "");
        out.print(" \t ");
        out.print(i.isMCOrgLocal() ? "mc org local" : "");
        out.print("\n");
    }

    public void showNetInfo(NetworkInterface n) throws SocketException {
        // show information about network interface
        out.printf("Properties of Network Interface: "
                + "\n\t name=%s"
                + "\n\t index=%d"
                + "\n\t MTU=%d"
                + "\n\t toString=%s"
                + "\n\t loopback=%s"
                + "\n\t up=%s"
                + "\n\t point2point=%s"
                + "\n\t virtual=%s"
                + "\n\t support multicast=%s\n\n",
                n.getDisplayName(),
                n.getIndex(),
                n.getMTU(),
                n.toString(),
                n.isLoopback(),
                n.isUp(),
                n.isPointToPoint(),
                n.isVirtual(),
                n.supportsMulticast()
        );
    }
}
