package com.qunhe.hacksketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author ly
 */
public class CircleView extends View {
    private static final int DEFAULT_RADIUS_IN_DP = 18;//默认半径
    private static final int DEFAULT_BORDER_WIDTH_IN_DP = 4;//默认边框2dp
    private Paint mPaint;
    private Paint mBorderPaint;
    private int mDefaultRadius;
    private int mDefaultBorderWidth;
    private int mCurrentColor;

    public CircleView(final Context context) {
        this(context, null);
    }

    public CircleView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = createPaint(Color.WHITE);
        mBorderPaint = createPaint(Color.parseColor("#66ffffff"));
        mDefaultRadius = Util.dp2Px(DEFAULT_RADIUS_IN_DP);
        mDefaultBorderWidth = Util.dp2Px(DEFAULT_BORDER_WIDTH_IN_DP);
    }

    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        return paint;
    }

    public void setColor(int color) {
        mCurrentColor = color;
        mPaint.setColor(color);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int defaultWidth = mDefaultRadius;
        int defaultHeight = mDefaultRadius;

        int width;
        int height;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(defaultWidth, widthSize);
        } else {
            width = defaultWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(defaultHeight, heightSize);
        } else {
            height = defaultHeight;
        }

        setMeasuredDimension(width, height);
    }

    public int getCurrentColor() {
        return mCurrentColor;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (isSelected()) {
            mBorderPaint.setColor(Color.WHITE);
        } else {
            mBorderPaint.setColor(Color.parseColor("#66ffffff"));
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mBorderPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mDefaultBorderWidth /
                2, mPaint);
    }
}
