package com.qunhe.sketch.pen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.qunhe.sketch.base.BaseScaleRotateSketchView;
import com.qunhe.sketch.base.BaseSketchView;

/**
 * @author dq
 */
public class TextSketchView extends BaseScaleRotateSketchView<TextSketchData> {
    private String mText;
    private TextSketchData mData = new TextSketchData();
    private int mStartX = 200;
    private int mStartY = 300;
    private final float mTextSize = 100;
    private float mTransitionX;
    private float mTransitionY;
    private float mScale = 1.0F;
    private float mDegree;

    public TextSketchView(final Context context) {
        super(context);
    }

    public TextSketchView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initPaint() {
        super.initPaint();
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected Rect getValidRect() {
        Rect bounds = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), bounds);
        return new Rect(bounds.left + mStartX + (int) mTransitionX,
                bounds.top + mStartY + (int) mTransitionY,
                bounds.right + mStartX + (int) mTransitionX,
                bounds.bottom + mStartY + (int) mTransitionY);
    }

    @Override
    public void onScale(final float ratio) {
        mScale = ratio;
        mPaint.setTextSize(mTextSize * mScale);
        invalidate();
    }

    @Override
    public void onRotate(final float degree) {
        mDegree = degree;
        invalidate();
    }

    @Override
    public boolean isValidArea(float x, float y) {
        return getBorderRect().contains((int) x, (int) y);
    }

    @Override
    public void onTransition(final float x, final float y) {
        mTransitionX = x;
        mTransitionY = y;
        invalidate();
    }

    /**
     * call it after {@link BaseSketchView#setOnSketchListener}
     *
     * @param text  text
     * @param color color
     * @param isReset 是否为重置行为，重置行为意味着不是新加
     */
    public void setText(String text, int color, boolean isReset) {
        mText = text;
        mPaint.setColor(color);
        invalidate();

        if (mOnSketchListener != null && !isReset) {
            mOnSketchListener.onSketchComplete(mData);
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mText != null) {
            canvas.save();
            int x = getValidRect().left;
            int y = getValidRect().bottom;
            canvas.rotate(mDegree, getValidRect().centerX(), getValidRect().centerY());
            canvas.drawText(mText, x, y, mPaint);
            mData.setText(mText).setCenterX(getValidRect().centerX()).setCenterY(getValidRect()
                    .centerY()).setRotate(mDegree).setPaint(mPaint).setStartX(x).setStartY(y);
            if (mIsSelected) {
                canvas.drawRect(getBorderRect(), mBorderPaint);
            }
            canvas.restore();
        }
    }
}
