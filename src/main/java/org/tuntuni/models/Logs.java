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
package org.tuntuni.models;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.connection.AbstractServer;
import org.tuntuni.connection.Server;

/**
 * Lists some log messages used through-out the application.
 */
public abstract class Logs {

    private static final Logger logger = Logger.getGlobal();

    public static final String //--- begin : log message list ---
            // Program
            PROGRAM_CLOSING = "Closing all active executors and schedulars.",
            // Used in Client
            CLIENT_TEST_FAILED = "Could not parse data sent from server",
            // Used in Server and Client
            SERVER_BIND_SUCCESS = "Server is bound to port {0}",
            SERVER_BIND_FAILS = "Server could not be bound to to port {0}",
            SERVER_LISTENING = "Waiting for clients at {0}",
            SERVER_LISTENING_STOPPED = "Stopped waiting for clients.",
            SERVER_ACCEPT_FAILED = "Failed to accept a channel.",
            SERVER_IO_FAILED = "Failed to read or write from server",
            SOCKET_CLASS_FAILED = "Request data could not be recognized.",
            SERVER_CLOSING_ERROR = "Failed to close the socket",
            SERVER_RECEIVED_CLIENT = "{0} requested with {1} params from {2}",
            // Used in Subnet
            SUBNET_SCAN_START = "Performing a scan for active subnet users",
            SUBNET_SCAN_SUCCESS = "Performed a scan over the whole networks",
            SUBNET_SCAN_FAILED = "Failed to get network interfaces",
            SUBNET_INTERFACE_CHECK_ERROR = "Failed to check network interface",
            SUBNET_CHECKING_SUBNETS = "Checking up all subnets of {0}",
            SUBNET_BUILD_ADDRESS_ERROR = "Failed to build host address from {0}",
            SUBNET_CHECK_ERROR = "Failed to check the subnet: {0}",
            //--- end : log message list ---
            FINALIZE = "Log finalized ";

    public static void log(Level level, String message, Object... data) {
        logger.log(level, message, data);
    }

    public static void info(String message, Object... data) {
        log(Level.INFO, message, data);
    }

    public static void info(AbstractServer from, String message, Object... data) {
        info("[" + from.name() + "]" + message, data);
    }

    public static void severe(String message, Object... data) {
        log(Level.SEVERE, message, data);
    }

    public static void warning(String message, Object... data) {
        log(Level.WARNING, message, data);
    }
    
    public static void warning(AbstractServer from, String message, Object... data) {
        warning("[" + from.name() + "]" + message, data);
    }

    public static void error(String message, Exception ex, Object... data) {
        severe(message, ex, data);
    }

    public static void error(AbstractServer from, String message, Exception ex, Object... data) {
        error("[" + from.name() + "]" + message, ex, data);
    }

    public static void config(String message, Object... data) {
        log(Level.CONFIG, message, data);
    }

}
