package com.qunhe.sketch.pen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.qunhe.sketch.base.BaseScaleRotateSketchView;
import com.qunhe.sketch.base.BaseSketchView;

/**
 * @author ly
 */
public class ImageSketchView extends BaseScaleRotateSketchView<ImageSketchData> {
    private Bitmap mOriginImage;
    private ImageSketchData mData = new ImageSketchData();
    private final float mStartX = 350;
    private final float mStartY = 500;
    private float mTransitionX;
    private float mTransitionY;
    private float mScale = 1.0F;
    private float mDegree;
    private int mImageWidth;
    private int mImageHeight;
    private Bitmap mImage;

    public ImageSketchView(final Context context) {
        super(context);
    }

    public ImageSketchView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initPaint() {
        super.initPaint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected Rect getValidRect() {
        return new Rect((int) (mTransitionX + mStartX), (int) (mTransitionY + mStartY), (int)
                (mTransitionX + mStartX) + mImage.getWidth(), (int) (mTransitionY + mStartY) +
                mImage.getHeight());
    }

    @Override
    public void onScale(final float ratio) {
        mScale = ratio;
        mImage = Bitmap.createScaledBitmap(mOriginImage, (int) (mImageWidth * mScale), (int)
                (mImageHeight * mScale), true);
        invalidate();
    }

    @Override
    public void onRotate(final float degree) {
        mDegree = degree;
        invalidate();
    }

    @Override
    public boolean isValidArea(final float x, final float y) {
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
     * @param image image
     */
    public void setImage(Bitmap image) {
        Matrix matrix = new Matrix();
        mOriginImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix,
                true);
        mImage = Bitmap.createBitmap(mOriginImage);

        mImageWidth = mOriginImage.getWidth();
        mImageHeight = mOriginImage.getHeight();
        if (mOnSketchListener != null) {
            mOnSketchListener.onSketchComplete(mData);
        }
        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mImage != null) {
            canvas.save();
            int x = getValidRect().left;
            int y = getValidRect().top;
            canvas.rotate(mDegree, getValidRect().centerX(), getValidRect().centerY());
            if (mIsSelected) {
                canvas.drawRect(getBorderRect(), mBorderPaint);
            }
            canvas.drawBitmap(mImage, x, y, mPaint);

            mData.setImage(mImage).setCenterX(getValidRect().centerX())
                    .setCenterY(getValidRect().centerY())
                    .setRotate(mDegree)
                    .setStartX(x)
                    .setStartY(y)
                    .setPaint(mPaint);
            canvas.restore();
        }
    }
}
