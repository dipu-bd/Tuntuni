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
 
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays; 

/**
 * Single data frame
 */
public class DataFrame implements Externalizable {

    private byte[] mBuffer;

    public DataFrame() {
    }

    public DataFrame(byte[] data, int size) {
        mBuffer = Arrays.copyOf(data, size);
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeInt(mBuffer.length);
        oo.write(mBuffer);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        mBuffer = new byte[oi.readInt()];
        oi.readFully(mBuffer);
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public int getLength() {
        return mBuffer == null ? 0 : mBuffer.length;
    }

    public final void setBuffer(byte[] data) {
        mBuffer = data;
    }
}
