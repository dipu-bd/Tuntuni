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
package org.tuntuni.image;

import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.tuntuni.util.Commons;

/**
 *
 * @author dipu
 */
public class ImageFrame implements Externalizable {

    private int mX;
    private int mY;
    private byte[] mBuffer;

    public ImageFrame(int posX, int posY, BufferedImage image) {
        mX = posX;
        mY = posY;
        mBuffer = Commons.imageToBytes(image);
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeInt(mX);
        oo.writeInt(mY);
        oo.writeInt(mBuffer.length);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        mX = oi.readInt();
        mY = oi.readInt();
        int length = oi.readInt();
        mBuffer = new byte[length];
        oi.readFully(mBuffer);
    }

    public BufferedImage getImage() {
        return Commons.bytesToBufferedImage(mBuffer);
    }

    public void setBuffer(byte[] data) {
        mBuffer = data;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        mX = x;
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        mY = y;
    }
}
