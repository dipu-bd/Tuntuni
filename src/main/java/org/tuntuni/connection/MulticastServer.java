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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.models.Logs;

/**
 * http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
 */
public class MulticastServer implements Runnable {

    int mPort;
    Thread mServerThread;
    DatagramSocket mSocket;

    /**
     * Creates a MulticastServer by given port to listen.
     *
     * @param port PORT address to listen
     */
    public MulticastServer(int port) {
        mPort = port;
    }

    @Override
    public void run() {
        try {
            // Listen to all UDP trafic that is destined for mPort
            mSocket = new DatagramSocket(mPort, InetAddress.getByName("0.0.0.0"));
            mSocket.setBroadcast(true);

            while (true) {
                System.out.println(getClass().getName() + ">>> Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                mSocket.receive(packet);

                //Packet received
                System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    mSocket.send(sendPacket);

                    System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Starts the server thread.
     */
    public void start() {
        try {
            stop();
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
            mServerThread.interrupt();
        } catch (Exception ex) {
            Logs.severe("Failed to STOP multicast server.", ex);
        }
    }
}
