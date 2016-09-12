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

import org.tuntuni.video.image.ImageFrame;
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

    public static int FRAME_NUMBER = 1;

    private int mID;
    private byte[] mBuffer;
    private ConnectFor mType;

    public DataFrame(ConnectFor frameType) {
        mID = FRAME_NUMBER++;
        mType = frameType;
    }

    public DataFrame(ConnectFor frameType, int time) {
        mID = time;
        mType = frameType;
    }

    public DataFrame() {
        this(ConnectFor.INVALID);
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        //oo.writeByte(mType.data());
        oo.writeInt(mID);
        oo.writeInt(mBuffer.length);
        oo.write(mBuffer);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        //mType = ConnectFor.from(oi.readByte());
        mID = oi.readInt();
        mBuffer = new byte[oi.readInt()];
        oi.readFully(mBuffer);
    }

    public ConnectFor connectedFor() {
        return mType;
    }

    public int getTime() {
        return mID;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public int getBufferLength() {
        return mBuffer == null ? 0 : mBuffer.length;
    }

    public final void setBuffer(byte[] data) {
        mBuffer = data;
    }

    @Override
    public int compareTo(DataFrame t) {
        return mID == t.getTime() ? 0 : (mID < t.getTime() ? -1 : 1);
    }

    @Override
    public boolean equals(Object t) {
        if (t == null || !(t instanceof ImageFrame)) {
            return false;
        }
        return ((ImageFrame) t).getTime() == mID;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.mID ^ (this.mID >>> 32));
        hash = 29 * hash + Arrays.hashCode(this.mBuffer);
        return hash;
    }

}
