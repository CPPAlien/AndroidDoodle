package com.qunhe.sketch.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qunhe.sketch.OnSketchListener;
import com.qunhe.sketch.R;
import com.qunhe.sketch.action.TransitionAction;

/**
 * @author dq
 */
public abstract class BaseSketchView<T extends BaseSketchData> extends View {
    private float mLastX;
    private float mLastY;
    private float mStartX;
    private float mStartY;
    private boolean isValid;
    protected OnSketchListener<T> mOnSketchListener;
    private int mLayer;
    protected Paint mPaint = new Paint();
    protected boolean mIsSelected;
    private float mTransitionX;
    private float mTransitionY;

    public BaseSketchView(final Context context) {
        super(context);
        initPaint();
    }

    public BaseSketchView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public BaseSketchView(final Context context, @Nullable final AttributeSet attrs, final int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseSketchView(final Context context, @Nullable final AttributeSet attrs, final int
            defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    protected abstract void initPaint();

    public void setOnSketchListener(OnSketchListener<T> listener) {
        mOnSketchListener = listener;
    }

    public void setLayer(int layer) {
        mLayer = layer;
    }

    public void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                mStartX = mLastX;
                mStartY = mLastY;
                isValid = isValidArea(event);
                if (isValid && mOnSketchListener != null) {
                    mOnSketchListener.onSketchTouched(mLayer);
                } else if (mOnSketchListener != null) {
                    mOnSketchListener.onSketchTouched(-1);
                }
                onSketchClicked(isValid);
                onTransition(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isValid) {
                    float dx = event.getX() - mLastX;
                    float dy = event.getY() - mLastY;
                    mTransitionX += dx;
                    mTransitionY += dy;
                    onTransition(mTransitionX, mTransitionY);
                }

                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (isValid && mOnSketchListener != null) {
                    mOnSketchListener.onAction(new TransitionAction().setOffsetX((int)(mLastX - mStartX))
                            .setOffsetY((int)(mLastY - mStartY)));
                    int pixels = getResources().getDimensionPixelOffset(R.dimen.ignore_point_size);
                    if (Math.abs(mStartX - mLastX) < pixels && Math.abs(mStartY - mLastY) < pixels) {
                        // 点击事件
                        mOnSketchListener.onSketchClicked(mLayer);
                        break;
                    }
                }
                onTransition(false);
                break;
            default:
                break;
        }
        return isValid;
    }

    public void reLayout(int offsetX, int offsetY) {
        mTransitionY -= offsetY;
        mTransitionX -= offsetX;
        onTransition(mTransitionX, mTransitionY);
    }

    public abstract boolean isValidArea(MotionEvent event);

    public abstract void onTransition(float x, float y);

    public void onSketchClicked(boolean isSelected) {

    }

    public void onTransition(boolean isTransition) {

    }
}
