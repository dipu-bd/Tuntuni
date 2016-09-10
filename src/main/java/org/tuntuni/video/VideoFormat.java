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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.sound.sampled.AudioFormat;

/**
 *
 * @author Sudipto Chandra
 */
public class VideoFormat implements Externalizable {

    // image type
    private int mType = BufferedImage.TYPE_INT_ARGB;
    // frame width
    private int mWidth = 640;
    // frame height
    private int mHeight = 480;

    //sampleRate - the number of samples per second
    private float mSampleRate = 44100;
    //sampleSizeInBits - the number of bits in each sample
    private int mSampleSize = 16;
    //channels - the number of channels (1 for mono, 2 for stereo, and so on)
    private int mChannel = 2;
    //signed - indicates whether the data is signed or unsigned
    private boolean mSigned = true;
    //bigEndian - indicates whether the data for a single sample is stored in big-endian byte order
    private boolean mBigEndian = true;

    // port where the server is running 
    private int mPort;

    public VideoFormat() {
    }

    public VideoFormat(int width, int height, AudioFormat audio) {
        this(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB,
                audio.getFrameRate(),
                audio.getSampleSizeInBits(),
                audio.getChannels(),
                true,
                audio.isBigEndian()
        );
    }

    public VideoFormat(int width, int height, int type, float sampleRate,
            int sampleSizeInBits, int channels, boolean signed, boolean bigEndian) {
        mType = type;
        mWidth = width;
        mHeight = height;
        mSampleRate = sampleRate;
        mSampleSize = sampleSizeInBits;
        mChannel = channels;
        mSigned = signed;
        mBigEndian = bigEndian;
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeShort((short) mWidth);
        oo.writeShort((short) mHeight);
        oo.writeByte((byte) mType);

        oo.writeFloat(mSampleRate);
        oo.writeByte((byte) mSampleSize);
        oo.writeByte((byte) mChannel);
        oo.writeBoolean(mSigned);
        oo.writeBoolean(mBigEndian);

        oo.writeShort((short) mPort);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        mWidth = oi.readShort();
        mHeight = oi.readShort();
        mType = oi.readByte();

        mSampleRate = oi.readFloat();
        mSampleSize = oi.readByte();
        mChannel = oi.readByte();
        mSigned = oi.readBoolean();
        mBigEndian = oi.readBoolean();

        mPort = oi.readShort();
    }

    /**
     * @return the dimension of the video frame
     */
    public Dimension getViewSize() {
        return new Dimension(mWidth, mHeight);
    }

    /**
     * @return the audio format of this video
     */
    public AudioFormat getAudioFormat() {
        return new AudioFormat(mSampleRate, mSampleSize, mChannel, mSigned, mBigEndian);
    }

    /**
     * @return the Type
     */
    public int getType() {
        return mType;
    }

    /**
     * @param Type the Type to set
     */
    public void setType(int Type) {
        this.mType = Type;
    }

    /**
     * @return the Width
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * @param Width the Width to set
     */
    public void setWidth(int Width) {
        this.mWidth = Width;
    }

    /**
     * @return the Height
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * @param Height the Height to set
     */
    public void setHeight(int Height) {
        this.mHeight = Height;
    }

    /**
     * @return the SampleRate
     */
    public float getSampleRate() {
        return mSampleRate;
    }

    /**
     * @param SampleRate the SampleRate to set
     */
    public void setSampleRate(float SampleRate) {
        this.mSampleRate = SampleRate;
    }

    /**
     * @return the SampleSize
     */
    public int getSampleSize() {
        return mSampleSize;
    }

    /**
     * @param SampleSize the SampleSize to set
     */
    public void setSampleSize(int SampleSize) {
        this.mSampleSize = SampleSize;
    }

    /**
     * @return the Channel
     */
    public int getChannel() {
        return mChannel;
    }

    /**
     * @param Channel the Channel to set
     */
    public void setChannel(int Channel) {
        this.mChannel = Channel;
    }

    /**
     * @return the Signed
     */
    public boolean isSigned() {
        return mSigned;
    }

    /**
     * @param Signed the Signed to set
     */
    public void setSigned(boolean Signed) {
        this.mSigned = Signed;
    }

    /**
     * @return the BigEndian
     */
    public boolean isBigEndian() {
        return mBigEndian;
    }

    /**
     * @param BigEndian the BigEndian to set
     */
    public void setBigEndian(boolean BigEndian) {
        this.mBigEndian = BigEndian;
    }

    public int getServerPort() {
        return mPort;
    }
}
