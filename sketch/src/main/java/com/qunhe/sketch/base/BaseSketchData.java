package com.qunhe.sketch.base;

import android.graphics.Paint;

/**
 * @author dq
 */
public class BaseSketchData {
    private Paint mPaint;
    // 从哪里开始绘制，
    // 文字的话，绘制起点是左下角
    // 图片左上，普通画笔，则未translate画布的距离
    private float mStartX;
    private float mStartY;

    public Paint getPaint() {
        return mPaint;
    }

    public BaseSketchData setPaint(final Paint paint) {
        mPaint = paint;
        return this;
    }

    public float getStartX() {
        return mStartX;
    }

    public BaseSketchData setStartX(final float startX) {
        mStartX = startX;
        return this;
    }

    public float getStartY() {
        return mStartY;
    }

    public BaseSketchData setStartY(final float startY) {
        mStartY = startY;
        return this;
    }
}
