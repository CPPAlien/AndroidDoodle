package com.qunhe.sketch.base;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qunhe.sketch.R;
import com.qunhe.sketch.action.RotateAction;
import com.qunhe.sketch.action.ScaleAction;

/**
 * @author dq
 */
public abstract class BaseScaleRotateSketchView<T extends BaseSketchData> extends
        BaseSketchView<T> {
    protected Paint mBorderPaint;
    private float mStartX;
    private float mStartY;
    private float mLastX;
    private float mLastY;
    private boolean mIsScaleOrRotate = false;
    private boolean mIsTransition = false;
    private float mScale = 1F;
    private float mOldScale = 1F;
    private float mRotate = 0;
    private float mOldRotate = 0;

    public BaseScaleRotateSketchView(final Context context) {
        super(context);
    }

    public BaseScaleRotateSketchView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @CallSuper
    @Override
    protected void initPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(ContextCompat.getColor(getContext(), R.color.line_border_color));
        mBorderPaint.setStrokeWidth(getResources().getDimension(R.dimen.line_border_size));
        PathEffect pathEffect = new DashPathEffect(new float[]{10, 5}, 10);
        mBorderPaint.setPathEffect(pathEffect);
    }

    @Override
    public void onSketchClicked(boolean isSelected) {
        mIsSelected = isSelected;
        invalidate();
    }

    @Override
    public void onTransition(final boolean isTransition) {
        mIsTransition = isTransition;
    }

    protected abstract Rect getValidRect();

    protected Rect getBorderRect() {
        int border = getResources().getDimensionPixelOffset(R.dimen.scale_rotate_border_area);
        Rect validRect = getValidRect();
        return new Rect(validRect.left - border, validRect.top - border, validRect.right +
                border, validRect.bottom + border);
    }

    public abstract boolean isValidArea(final float x, final float y);

    private void rotatePoint(float[] point) {
        Matrix matrix = new Matrix();
        matrix.postRotate(-mRotate, getValidRect().centerX(), getValidRect().centerY());
        matrix.mapPoints(point);
    }

    @Override
    public boolean isValidArea(final MotionEvent event) {
        float[] point = new float[]{event.getX(), event.getY()};
        rotatePoint(point);
        return isValidArea(point[0], point[1]);
    }

    private boolean canScaleAndRotate(float[] point) {
        int size = getResources().getDimensionPixelSize(R.dimen.scale_rotate_border_area);
        Rect extendBoard = new Rect(getBorderRect().left - size, getBorderRect().top - size,
                getBorderRect().right + size, getBorderRect().bottom + size);
        return mIsScaleOrRotate ||
                (extendBoard.contains((int) point[0], (int) point[1]) &&
                        !getValidRect().contains((int) point[0], (int) point[1])
                        && !mIsTransition);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        float[] point = new float[]{event.getX(), event.getY()};
        rotatePoint(point);
        if (canScaleAndRotate(point)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mScale = mOldScale;
                    mRotate = mOldRotate;
                    mLastX = event.getX();
                    mLastY = event.getY();
                    mStartX = mLastX;
                    mStartY = mLastY;
                    mIsScaleOrRotate = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mScale += ((event.getX() - mLastX) / 100F);
                    if (mScale < 0.5) {
                        mScale = 0.5F;
                    } else if (mScale > 3) {
                        mScale = 3;
                    }
                    onScale(mScale);
                    mRotate += ((event.getY() - mLastY) / 5F);
                    if (mRotate > 360) {
                        mRotate = 360F;
                    } else if (mRotate < -360) {
                        mRotate = -360F;
                    }
                    onRotate(mRotate);
                    mLastX = event.getX();
                    mLastY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    mIsScaleOrRotate = false;
                    if ((Math.abs(mScale - mOldScale) < 0.01)) {
                        onScale(mOldScale);
                    } else {
                        mOnSketchListener.onAction(new ScaleAction().setOldScale(mOldScale)
                                .setNewScale(mScale));
                        mOldScale = mScale;
                    }
                    if ((Math.abs(mRotate - mOldRotate) < 0.1)) {
                        onRotate(mOldRotate);
                    } else {
                        mOnSketchListener.onAction(new RotateAction().setOldDegree(mOldRotate)
                                .setNewDegree(mRotate));
                        mOldRotate = mRotate;
                    }

                    break;
                default:
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public abstract void onScale(float ratio);

    public abstract void onRotate(float degree);

    public void setScale(float ratio) {
        mScale = mOldScale = ratio;
        onScale(mScale);
    }

    public void setRotate(float rotate) {
        mRotate = mOldRotate = rotate;
        onRotate(mRotate);
    }
}
