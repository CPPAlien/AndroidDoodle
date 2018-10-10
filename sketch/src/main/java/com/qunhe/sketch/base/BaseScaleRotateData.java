package com.qunhe.sketch.base;

/**
 * @author dq
 */
public class BaseScaleRotateData extends BaseSketchData {
    private float mScale;
    private float mRotate;

    public float getScale() {
        return mScale;
    }

    public BaseScaleRotateData setScale(final float scale) {
        mScale = scale;
        return this;
    }

    public float getRotate() {
        return mRotate;
    }

    public BaseScaleRotateData setRotate(final float rotate) {
        mRotate = rotate;
        return this;
    }
}
