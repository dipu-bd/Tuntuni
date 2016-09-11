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
package org.tuntuni.video;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.tuntuni.models.ConnectFor;

/**
 * Single data frame
 */
public class DataFrame implements Externalizable, Comparable<DataFrame> {

    public static long FRAME_NUMBER = 1;

    private long mTime;
    private byte[] mBuffer;
    private ConnectFor mType;

    public DataFrame(ConnectFor frameType) {
        mTime = FRAME_NUMBER++;
        mType = frameType;
    }

    public DataFrame(ConnectFor frameType, long time) {
        mTime = time;
        mType = frameType;
    }

    public DataFrame() {
        this(ConnectFor.INVALID);
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeByte(mType.data());
        oo.writeLong(mTime);
        oo.writeInt(mBuffer.length);
        oo.write(mBuffer);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        mType = ConnectFor.from(oi.readByte());
        mTime = oi.readLong();
        mBuffer = new byte[oi.readInt()];
        oi.readFully(mBuffer);
    }

    public ConnectFor connectedFor() {
        return mType;
    }

    public long getTime() {
        return mTime;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public final void setBuffer(byte[] data) {
        mBuffer = data;
    }

    @Override
    public int compareTo(DataFrame t) {
        return mTime == t.getTime() ? 0 : (mTime < t.getTime() ? -1 : 1);
    }

    @Override
    public boolean equals(Object t) {
        if (t == null || !(t instanceof ImageFrame)) {
            return false;
        }
        return ((ImageFrame) t).getTime() == mTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.mTime ^ (this.mTime >>> 32));
        hash = 29 * hash + Arrays.hashCode(this.mBuffer);
        return hash;
    }

}
