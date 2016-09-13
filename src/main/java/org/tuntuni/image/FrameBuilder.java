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
import java.util.ArrayList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.tuntuni.video.VideoFormat;

/**
 *
 * @author dipu
 */
public class FrameBuilder {

    // original masks
    static final int RED_MASK = 0x00FF0000;
    static final int GREEN_MASK = 0x0000FF00;
    static final int BLUE_MASK = 0x000000FF;

    int skipIndex = 0;

    final int mWidth;
    final int mHeight;
    final int mImageWidth;
    final int mImageHeight;
    final int mTileX;
    final int mTileY;
    BufferedImage mImage;
    ArrayList<FrameListener> mListener;

    public FrameBuilder() {
        mWidth = VideoFormat.FRAME_WIDTH;
        mHeight = VideoFormat.FRAME_HEIGHT;
        mImageWidth = VideoFormat.WIDTH;
        mImageHeight = VideoFormat.HEIGHT;
        mTileX = mImageWidth / mWidth;
        mTileY = mImageHeight / mHeight;

        mListener = new ArrayList<>();
        mImage = new BufferedImage(mImageWidth, mImageHeight, BufferedImage.TYPE_INT_RGB);
    }

    public int minFrameRate() {
        return mTileX * mTileY / 4;
    }

    public int maxFrameRate() {
        return mTileX * mTileY * 2;
    }

    public int getFrameSize() {
        return mWidth * mHeight;
    }

    public void addListener(FrameListener listener) {
        mListener.add(listener);
    }

    public void removeListener(FrameListener listener) {
        mListener.remove(listener);
    }

    public void putImage(BufferedImage image) {
        processImage(image);
    }

    public void putFrame(ImageFrame frame) {
        processFrame(frame);
    }

    private void raiseFrameUpdateEvent(ImageFrame frame) {
        mListener.forEach((listener) -> {
            listener.frameUpdated(frame);
        });
    }

    private void raiseImageUpdateEvent(Image image) {
        mListener.forEach((listener) -> {
            listener.imageUpdated(image);
        });
    }

    private void processImage(BufferedImage image) {
        // allocate
        int x, y, fn;
        int i, j, k, l, off = 0;
        int rgb, r, g, b, b1, b2;
        byte[] data = new byte[2 * mWidth * mHeight];
        // change skip index
        skipIndex = (skipIndex + 1) & 3;
        // for each tile...
        for (i = 0; i < mTileX; i++) {
            for (j = 0; j < mTileY; ++j) {
                // should skip?
                x = i * mWidth;
                y = j * mHeight;
                fn = y * mImageWidth + x;
                if ((fn & 3) != skipIndex) {
                    continue;
                }
                // send the tile
                for (k = 0; k < mWidth; ++k, ++x) {
                    for (l = 0; l < mHeight; ++l, ++y) {
                        rgb = image.getRGB(x, y);
                        r = (rgb & RED_MASK) >> 16;
                        g = (rgb & GREEN_MASK) >> 8;
                        b = (rgb & BLUE_MASK);
                        // r=5,g=6,b=5 color model
                        b1 = (r & 0xF8) | (g >> 5);
                        b2 = (b & 0xF8) | ((g >> 2) & 0x7);
                        data[off++] = (byte) b1;
                        data[off++] = (byte) b2;
                    }
                }
                raiseFrameUpdateEvent(new ImageFrame(fn, data));
            }
        }
    }

    private void processFrame(ImageFrame frame) {
        int i, b1, b2;
        int x, y, ix, iy, fn;
        int r, g, b, rgb, or, og, ob;
        byte[] data = frame.getBuffer();

        fn = frame.frameNumber;
        ix = fn % mImageWidth;
        iy = fn / mImageWidth;
        for (i = 1; i < data.length; i += 2) {
            x = ix + (i >> 1) % mWidth;
            y = iy + (i >> 1) / mWidth;

            b1 = data[i - 1];
            b2 = data[i];
            r = (b1 & 0xF8);
            b = (b2 & 0xF8);
            g = (b1 & 0x7) | (b2 & 0x7);

            rgb = mImage.getRGB(x, y);
            or = (rgb & RED_MASK) >> 16;
            og = (rgb & GREEN_MASK) >> 8;
            ob = rgb & BLUE_MASK;

            r = (or + r) >> 1;
            g = (og + g) >> 1;
            b = (ob + b) >> 1;
            rgb = (r << 16) | (b << 8) | g;

            mImage.setRGB(x, y, rgb);
        }

        raiseImageUpdateEvent(SwingFXUtils.toFXImage(mImage, null));
    }
}
