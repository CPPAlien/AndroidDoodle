package com.qunhe.sketch.pen;

import com.qunhe.sketch.base.BaseScaleRotateData;

/**
 * @author dq
 */
public class TextSketchData extends BaseScaleRotateData {
    private String mText;
    private float mCenterX;
    private float mCenterY;

    public String getText() {
        return mText;
    }

    public TextSketchData setText(final String text) {
        mText = text;
        return this;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public TextSketchData setCenterX(final float centerX) {
        mCenterX = centerX;
        return this;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public TextSketchData setCenterY(final float centerY) {
        mCenterY = centerY;
        return this;
    }
}
