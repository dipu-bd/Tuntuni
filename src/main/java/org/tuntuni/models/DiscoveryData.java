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
package org.tuntuni.models;

import org.tuntuni.connection.ConnectFor;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Used in broadcast request for network discovery.
 */
public class DiscoveryData implements Externalizable {

    private int mPort;
    private ConnectFor mConnectFor;

    public DiscoveryData() {
        mConnectFor = ConnectFor.INVALID;
    }

    public DiscoveryData(int port) {
        mPort = port;
        mConnectFor = ConnectFor.PORT;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(getConnectFor().data());
        out.writeInt(getPort());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setConnectFor(ConnectFor.from(in.readByte()));
        setPort(in.readInt());
    }

    /**
     * @return the port
     */
    public int getPort() {
        return mPort;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.mPort = port;
    }

    /**
     * @return the connectFor
     */
    public ConnectFor getConnectFor() {
        return mConnectFor;
    }

    /**
     * @param connectFor the connectFor to set
     */
    public void setConnectFor(ConnectFor connectFor) {
        this.mConnectFor = connectFor;
    }

}
