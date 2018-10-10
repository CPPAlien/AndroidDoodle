package com.qunhe.sketch.pen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qunhe.sketch.R;
import com.qunhe.sketch.base.BaseSketchView;

/**
 * @author dq
 */
public class LineSketchView extends BaseSketchView<LineSketchData> {
    private final Path mPath = new Path();
    private Paint mBorderPaint;
    private float mX;
    private float mY;
    private float mStartX;
    private float mStartY;
    private float mTransitionX;
    private float mTransitionY;
    private boolean mIsFirstCreated = true;
    private LineSketchData mLineSketchData = new LineSketchData();

    public LineSketchView(final Context context) {
        super(context);
        initPaint();
    }

    public LineSketchView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    protected void initPaint() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private int dp2Px(final float dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    public void setColorAndStroke(int color, float strokeWidth) {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(dp2Px(strokeWidth));
        mBorderPaint.setStrokeWidth(dp2Px(strokeWidth) + getResources().getDimensionPixelSize(R.dimen
                .line_border_size));
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // 第一次创建，并且未点击在其他sketch的区域上
        if (mIsFirstCreated) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPath.reset();
                    mPath.moveTo(x, y);
                    mX = x;
                    mY = y;
                    mStartX = x;
                    mStartY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mPath.quadTo(mX, mY, x, y);
                    mX = x;
                    mY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    int pixels = getResources().getDimensionPixelOffset(R.dimen.ignore_point_size);
                    if (Math.abs(mStartX - mX) < pixels && Math.abs(mStartY - mY) < pixels) {
                        // 忽略点击
                        mPath.reset();
                        break;
                    }
                    mIsFirstCreated = false;
                    if (mOnSketchListener != null) {
                        mOnSketchListener.onSketchComplete(mLineSketchData);
                    }
                    break;
                default:
                    break;
            }
            invalidate();
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean isValidArea(final MotionEvent event) {
        int rX = (int) event.getX();
        int rY = (int) event.getY();
        Bitmap bitmap = getBitmap();
        if (rX < 0 || rX > bitmap.getWidth() || rY < 0 || rY > bitmap.getHeight()) {
            return false;
        }
        int color = bitmap.getPixel(rX, rY);
        mIsSelected = color < 0;
        invalidate();
        return mIsSelected;
    }

    @Override
    public void onTransition(final float x, final float y) {
        mTransitionX = x;
        mTransitionY = y;
        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.save();
        canvas.translate(mTransitionX, mTransitionY);
        if (mIsSelected) {
            canvas.drawPath(mPath, mBorderPaint);
        }
        canvas.drawPath(mPath, mPaint);
        mLineSketchData.setPath(mPath).setPaint(mPaint).setStartX(mTransitionX).setStartY
                (mTransitionY);
        canvas.restore();
    }

    private Bitmap getBitmap() {
        int width = getRight() - getLeft();
        int height = getBottom() - getTop();
        final boolean opaque = getDrawingCacheBackgroundColor() != 0 || isOpaque();
        Bitmap.Config quality;
        if (!opaque) {
            switch (getDrawingCacheQuality()) {
                case DRAWING_CACHE_QUALITY_AUTO:
                case DRAWING_CACHE_QUALITY_LOW:
                case DRAWING_CACHE_QUALITY_HIGH:
                default:
                    quality = Bitmap.Config.ARGB_8888;
                    break;
            }
        } else {
            quality = Bitmap.Config.RGB_565;
        }
        Bitmap bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics(), width, height,
                quality);
        if (opaque) {
            bitmap.setHasAlpha(false);
        }
        bitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        boolean clear = getDrawingCacheBackgroundColor() != 0;
        Canvas canvas = new Canvas(bitmap);
        if (clear) {
            bitmap.eraseColor(getDrawingCacheBackgroundColor());
        }
        computeScroll();
        final int restoreCount = canvas.save();
        canvas.translate(-getScrollX(), -getScrollY());
        draw(canvas);
        canvas.restoreToCount(restoreCount);
        canvas.setBitmap(null);
        return bitmap;
    }
}
