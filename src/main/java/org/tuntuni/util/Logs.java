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

/**
 * Lists some log messages used through-out the application.
 */
public abstract class Logs {

    public static final String INITIALIZE = "Log initialized",
            
            SERVER_PRIMARY_PORT_FAILS = "Failed to start server on primary port. Switching to backup port",
            SERVER_BACKUP_PORT_FAILS = "Server fails to start on backup port. Server is not running.",
            SERVER_IS_CLOSING = "Waiting until the server is stopped.",
            SERVER_FAILED_CLOSING = "Failed to close the server",
            SERVER_SOCKET_EXCEPTION = "Failed to set timeout option to the server",
            SERVER_FAILS_ACCEPTING_CLIENT = "Failed to accept client socket",
            
            FINALIZE = "Log finalized ";

}
