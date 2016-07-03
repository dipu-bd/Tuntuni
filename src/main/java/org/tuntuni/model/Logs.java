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
package org.tuntuni.model;

/**
 * Lists some log messages used through-out the application.
 */
public abstract class Logs {

    public static final String //--- begin : log message list ---
            // Program
            PROGRAM_CLOSING = "Closing all active executors and schedulars.",
            // Used in Server and Client
            SERVER_BIND_SUCCESS = "Server is bound to port {0}",
            SERVER_BIND_FAILS = "Server could not be bound to to port {0}",
            SERVER_LISTENING = "Waiting for clients at {0}",
            SERVER_LISTENING_STOPPED = "Stopped waiting for clients.",
            SERVER_ACCEPT_FAILED = "Failed to accept a channel.",
            SERVER_IO_FAILED = "Failed to read or write from server",
            SOCKET_CLASS_FAILED = "Request data could not be recognized.",
            SERVER_CLOSING_ERROR = "Failed to close the socket",
            SERVER_RECEIVED_CLIENT = "{0} connected with status {1} and {2} params",
            // Used in Subnet
            SUBNET_SCAN_START = "Performing a scan for active subnet users",
            SUBNET_INTERFACE_ENUMERATION_FAILED = "Failed to get network interfaces",
            SUBNET_INTERFACE_CHECK_ERROR = "Failed to check network interface",
            SUBNET_CHECKING_SUBNETS = "Checking up all subnets of {0}",
            SUBNET_BUILD_ADDRESS_ERROR = "Failed to build host address from {0}",
            SUBNET_CHECK_ERROR = "Failed to check the subnet: {0}",
            //--- end : log message list ---
            FINALIZE = "Log finalized ";

}
