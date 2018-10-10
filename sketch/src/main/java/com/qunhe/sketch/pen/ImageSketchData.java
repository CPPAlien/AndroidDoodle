package com.qunhe.sketch.pen;

import android.graphics.Bitmap;

import com.qunhe.sketch.base.BaseScaleRotateData;

/**
 * 注意，恢复时
 *
 * @author ly
 */
public class ImageSketchData extends BaseScaleRotateData {
    private Bitmap mImage;
    private float mCenterX;
    private float mCenterY;

    public Bitmap getImage() {
        return mImage;
    }

    public ImageSketchData setImage(final Bitmap image) {
        mImage = image;
        return this;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public ImageSketchData setCenterX(final float centerX) {
        mCenterX = centerX;
        return this;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public ImageSketchData setCenterY(final float centerY) {
        mCenterY = centerY;
        return this;
    }
}
