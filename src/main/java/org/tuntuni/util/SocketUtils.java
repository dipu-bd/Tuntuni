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
import java.net.InterfaceAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Utility functions used in socket connection.
 */
public abstract class SocketUtils {

    /**
     * Calculate the integer number from an array of bytes.
     * <p>
     * IPv4 address byte array must be 4 bytes long</p>
     *
     * @param bytes Array of bytes to convert.
     */
    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Converts an integer to byte array.
     * <p>
     * IPv4 address byte array must be 4 bytes long</p>
     *
     * @param number Integer number to convert.
     * @return
     */
    public static byte[] intToBytes(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

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
        return bytesToInt(address.getAddress());
    }

    /**
     * Gets textual representation of host IP address.
     *
     * @param host The host IP to convert.
     * @return
     */
    public static String addressAsString(int host) {
        String[] str = new String[4];
        byte[] bytes = intToBytes(host);
        for (int i = 0; i < 4; ++i) {
            str[i] = String.valueOf(Byte.toUnsignedInt(bytes[i]));
        }
        return String.join(".", str);
    }

    /**
     * Gets the default gateway from the interface address.
     * <p>
     * The returned address is in integer format. To convert it to normal
     * address you can use {@linkplain SocketUtils.getAddress()} method.</p>
     *
     * @param ia InterfaceAddress to get default gateway of.
     * @return
     */
    public static int getDefaultGateway(InterfaceAddress ia) {
        int ip = addressAsInteger(ia.getAddress());
        int mask = getSubnetMask(ia.getNetworkPrefixLength());
        return (ip & mask) + 1;
    }

    /**
     * Gets the first host from the interface address.
     * <p>
     * The returned address is in integer format. To convert it to normal
     * address you can use {@linkplain SocketUtils.getAddress()} method.</p>
     *
     * @param ia InterfaceAddress to get first host of.
     * @return
     */
    public static int getFirstHost(InterfaceAddress ia) {
        return getDefaultGateway(ia);
    }

    /**
     * Gets the last host from the interface address.
     * <p>
     * The returned address is in integer format. To convert it to normal
     * address you can use {@linkplain SocketUtils.getAddress()} method.</p>
     *
     * @param ia InterfaceAddress to get last host of.
     * @return
     */
    public static int getLastHost(InterfaceAddress ia) {
        return bytesToInt(ia.getBroadcast().getAddress()) - 1;
    }
}
