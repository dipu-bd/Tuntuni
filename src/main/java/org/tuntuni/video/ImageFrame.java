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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 */
public class ImageFrame implements Externalizable, Comparable<ImageFrame> {

    private long mTime;
    private int mType;
    private int mWidth;
    private int mHeight;
    private int[] mBuffer;

    public ImageFrame() {

    }

    public ImageFrame(long time, BufferedImage img) {
        mTime = time;
        mType = img.getType();
        mWidth = img.getWidth();
        mHeight = img.getHeight();
        mBuffer = img.getRGB(0, 0, mWidth, mHeight, null, 0, mWidth);
        //mBuffer = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
    }

    public BufferedImage getImage() {
        BufferedImage bi = new BufferedImage(mWidth, mHeight, mType);
        bi.setRGB(0, 0, mWidth, mHeight, mBuffer, 0, mWidth);
        WritableRaster newRaster = bi.getRaster();
        newRaster.setDataElements(0, 0, mWidth, mHeight, mBuffer);
        bi.setData(newRaster);
        return bi;
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {

    }

    @Override
    public int compareTo(ImageFrame t) {
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
        return hash;
    }

    public long getTime() {
        return mTime;
    }
    
    public int getWidth() {
        return mWidth;
    }
    
    public int getHeight() {
        return mHeight;
    }
    
    public int getType() {
        return mType;
    }
}
