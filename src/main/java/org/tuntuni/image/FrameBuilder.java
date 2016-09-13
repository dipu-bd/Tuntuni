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
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.tuntuni.models.Logs;
import org.tuntuni.video.VideoFormat;

/**
 *
 * @author dipu
 */
public class FrameBuilder {

    int skipIndex = 0;

    final int mWidth;
    final int mHeight;
    final int mImageWidth;
    final int mImageHeight;
    final int mTileX;
    final int mTileY;
    BufferedImage mImage;
    FrameListener mListener;

    public FrameBuilder() {
        mWidth = VideoFormat.FRAME_WIDTH;
        mHeight = VideoFormat.FRAME_HEIGHT;
        mImageWidth = VideoFormat.WIDTH;
        mImageHeight = VideoFormat.HEIGHT;
        mTileX = mImageWidth / mWidth;
        mTileY = mImageHeight / mHeight;
        mImage = new BufferedImage(mImageWidth, mImageHeight, BufferedImage.TYPE_INT_RGB);
    }

    public int minFrameRate() {
        return mTileX * mTileY / 4;
    }

    public int maxFrameRate() {
        return mTileX * mTileY * 2;
    }
 
    public void setListener(FrameListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    public void putImage(BufferedImage image) {
        try {
            processImage(image);
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to process image. {0}", ex);
        }
    }

    public void putFrame(ImageFrame frame) {
        try {
            processFrame(frame);
        } catch (Exception ex) {
            Logs.error(getClass(), "Failed to process frame. {0}", ex);
        }
    }

    private void raiseFrameUpdateEvent(ImageFrame frame) {
        if (mListener != null) {
            mListener.frameUpdated(frame);
        }
    }

    private void raiseImageUpdateEvent(Image image) {
        if (mListener != null) {
            mListener.imageUpdated(image);
        }
    }

    private void processImage(BufferedImage image) {
        // allocate 
        int i, j, x, y, fn;
        byte[] data = new byte[3 * mWidth * mHeight];
        // change skip index
        skipIndex = (skipIndex + 1) & 3;
        // for each tile... 
        for (i = 0; i < mTileX; ++i) {
            for (j = 0; j < mTileY; ++j) {
                // should skip?
                x = i * mWidth;
                y = j * mHeight;
                fn = y * mImageWidth + x;
                if ((fn & 3) == skipIndex) {
                    continue;
                }
                // send the tile 
                BufferedImage sub = image.getSubimage(x, y, mWidth, mHeight);
                raiseFrameUpdateEvent(new ImageFrame(x, y, sub));
            }
        }
    }

    private void processFrame(ImageFrame frame) {
        BufferedImage image = frame.getImage();
        int x = frame.getX();
        int y = frame.getY();
        int p = 1, q = 3, s = p + q;
        for (int i = 0; i < mWidth; ++i) {
            for (int j = 0; j < mHeight; ++j) {
                int rgb1 = image.getRGB(i + x, j + y);
                int rgb2 = mImage.getRGB(i + x, j + y);
                int r = p * ((rgb1 >> 16) & 0xFF) + q * ((rgb2 >> 16) & 0xFF);
                int g = p * ((rgb1 >> 8) & 0xFF) + q * ((rgb2 >> 8) & 0xFF);
                int b = p * ((rgb1) & 0xFF) + q * ((rgb2) & 0xFF);
                int rgb3 = ((r / s) << 16) | ((g / s) << 8) | (b / s);
                mImage.setRGB(x, y, rgb3);
            }
        }
        raiseImageUpdateEvent(SwingFXUtils.toFXImage(mImage, null));
    }
}
