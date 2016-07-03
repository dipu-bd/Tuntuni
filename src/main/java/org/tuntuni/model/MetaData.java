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

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tuntuni.Core;

/**
 * It has basic information about the application.
 */
public class MetaData implements Serializable {

    private static final Logger logger = Core.logger;

    private final String mVersion = "1.0-SNAPSHOT";
    private final String mTitle = "Tuntuni - A video chatting tool for LAN";

    private String mHostName;

    public MetaData() {
        try {
            mHostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.log(Level.INFO, null, ex);
        }
    }

    /**
     * Gets the title of the application.
     *
     * @return
     */
    public String title() {
        return mTitle;
    }

    /**
     * Gets the version number of the application.
     *
     * @return
     */
    public String version() {
        return mVersion;
    }

    /**
     * Gets the host-name of the computer.
     *
     * @return
     */
    public String hostName() {
        return mHostName;
    }
}
