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
package org.tuntuni.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket; 

/**
 * Utility functions used in socket connection.
 */
public abstract class SocketUtils {

    /**
     * Gets the broadcast address mask.
     * <p>
     * It is calculated by {@code 2^(32-prefix) - 1}. It is the direct inverse
     * of the subnet mask. You can get subnet mast by:
     * <pre>
     *     subnet_mask = ~broadcast_mask
     * </pre> IPv4 address byte array must be 4 bytes long
     * </p>
     *
     * @param prefix The prefix length of the network address.s
     * @return
     */
    public static int getBroadcastMask(int prefix) {
        return ((1 << (32 - prefix)) - 1);
    }

    /**
     * Gets the subnet address mask given network prefix length.
     * <p>
     * It is calculated by {@code ~(2^(32-prefix) - 1)}. It is the direct
     * inverse of the subnet mask. You can get broadcast mast by:
     * <pre>
     *     broadcast_mask = ~subnet_mask
     * </pre> IPv4 address byte array must be 4 bytes long
     * </p>
     *
     * @param prefix The prefix length of the network address.
     * @return
     */
    public static int getSubnetMask(int prefix) {
        return ~((1 << (32 - prefix)) - 1);
    }

    /**
     * Gets the integer representation of the net address.
     *
     * @param address Address to convert.
     * @return
     */
    public static int addressAsInteger(InetAddress address) {
        return Commons.bytesToInt(address.getAddress());
    }

    /**
     * Gets textual representation of host IP address.
     *
     * @param host The host IP to convert.
     * @return
     */
    public static String addressAsString(int host) {
        String[] str = new String[4];
        byte[] bytes = Commons.intToBytes(host);
        for (int i = 0; i < 4; ++i) {
            str[i] = String.valueOf(Byte.toUnsignedInt(bytes[i]));
        }
        return String.join(".", str);
    }

    /**
     * Gets the first host from the interface address.
     * <p>
     * The returned address is in integer format. To convert it to normal
     * address you can use {@linkplain SocketUtils.getAddress()} method.</p>
     *
     * @param address
     * @param prefix
     * @return
     */
    public static int getFirstHost(InetAddress address, int prefix) {
        int ip = Commons.bytesToInt(address.getAddress());
        int mask = getSubnetMask(prefix);
        return (ip & mask) + 1;
    }

    /**
     * Gets the last host from the interface address.
     * <p>
     * The returned address is in integer format. To convert it to normal
     * address you can use {@linkplain SocketUtils.getAddress()} method.</p>
     *
     * @param address
     * @param prefix
     * @return
     */
    public static int getLastHost(InetAddress address, int prefix) {
        int ip = Commons.bytesToInt(address.getAddress());
        int mask = getBroadcastMask(prefix);
        return (ip | mask) - 1;
    }

    /**
     * Gets the default gateway from the interface address.
     * <p>
     * The returned address is in integer format. To convert it to normal
     * address you can use {@linkplain SocketUtils.getAddress()} method.</p>
     *
     * @param address
     * @param prefix
     * @return
     */
    public static int getDefaultGateway(InetAddress address, int prefix) {
        return getFirstHost(address, prefix);
    }

    /**
     * Gets the remote host address of the given socket
     *
     * @param socket To get the remote host address of
     * @return
     */
    public static String getRemoteHost(Socket socket) {
        InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
        return isa.getHostString();
    }
}
