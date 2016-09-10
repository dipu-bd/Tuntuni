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
import java.net.SocketException;
import java.net.UnknownHostException;
import org.tuntuni.Core;
import org.tuntuni.models.ConnectFor;
import org.tuntuni.models.Logs;
import org.tuntuni.util.Commons;

/**
 * http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
 */
public class MulticastServer implements Runnable {

    private final int mPort;
    private Thread mServerThread;
    private DatagramSocket mSocket;

    /**
     * Creates a MulticastServer by given port to listen.
     *
     * @param port PORT address to listen
     * @throws java.net.SocketException
     */
    public MulticastServer(int port) throws SocketException {
        mPort = port;

        // bind a datagram socket to the given port address
        try {
            mSocket = new DatagramSocket(mPort, InetAddress.getByName("0.0.0.0"));
            mSocket.setBroadcast(true);
        } catch (UnknownHostException ex) {
            Logs.error(getClass(), null, ex);
        }
    }

    @Override
    public void run() {
        Logs.info(getClass(), "Listening for broadcast packets at {0}", mPort);

        while (true) {
            try {
                // receive a packet
                byte[] data = new byte[1];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                mSocket.receive(packet);

                Logs.info(getClass(), "Discovery packet received from: " + packet.getAddress().getHostAddress());

                // convert data & check packet validity
                ConnectFor state = ConnectFor.from(data[0]);
                if (state != ConnectFor.PORT) {
                    continue;
                }

                // send response packet
                sendResponsePacket(packet);
            } catch (Exception ex) {
                Logs.error(getClass(), null, ex);
            }
        }
    }

    // called from the run() function
    private void sendResponsePacket(DatagramPacket packet) {
        try {
            // send response
            byte[] sendData = Commons.intToBytes(Core.instance().server().getPort());

            //Send a response
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, packet.getAddress(), packet.getPort());
            mSocket.send(sendPacket);

            Logs.info(getClass(), "Response sent to {0}", packet.getAddress());

        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to send reponse", ex);
        }

    }

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
}
