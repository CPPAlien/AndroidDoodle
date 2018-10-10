package com.qunhe.sketch;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qunhe.sketch.action.DeleteAction;
import com.qunhe.sketch.action.LayerAction;
import com.qunhe.sketch.action.RotateAction;
import com.qunhe.sketch.action.ScaleAction;
import com.qunhe.sketch.action.SketchAction;
import com.qunhe.sketch.action.TransitionAction;
import com.qunhe.sketch.base.BaseScaleRotateData;
import com.qunhe.sketch.base.BaseScaleRotateSketchView;
import com.qunhe.sketch.base.BaseSketchData;
import com.qunhe.sketch.base.BaseSketchView;
import com.qunhe.sketch.pen.ImageSketchData;
import com.qunhe.sketch.pen.ImageSketchView;
import com.qunhe.sketch.pen.LineSketchData;
import com.qunhe.sketch.pen.LineSketchView;
import com.qunhe.sketch.pen.TextSketchData;
import com.qunhe.sketch.pen.TextSketchView;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author dq
 */
public class SketchBoardLayout extends FrameLayout {
    public static final int LINE_SKETCH = 1;
    public static final int TEXT_SKETCH = 2;
    public static final int IMAGE_SKETCH = 3;

    @Retention(SOURCE)
    @IntDef({LINE_SKETCH, TEXT_SKETCH, IMAGE_SKETCH})
    public @interface SketchType {
    }

    private
    @SketchType
    int mSketchType;
    private Stack<SketchAction> mSketchActions = new Stack<>();
    private List<BaseSketchData> mSketchData = new ArrayList<>();
    private OnSketchBoardListener mOnSketchBoardListener;
    private boolean mIsSketchSelected;
    private int mLineColor;
    private float mLineStrokeWidth;

    public SketchBoardLayout(@NonNull final Context context) {
        super(context);
    }

    public SketchBoardLayout(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public SketchBoardLayout(@NonNull final Context context, @Nullable final AttributeSet attrs,
                             @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SketchBoardLayout(@NonNull final Context context, @Nullable final AttributeSet attrs,
                             @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean isChildrenDrawingOrderEnabled() {
        return true;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return i;
    }

    /**
     * switch sketch way
     *
     * @param sketchType sketch type
     */
    public void setSketchType(@SketchType int sketchType) {
        if (mSketchType == sketchType) {
            return;
        }
        if (mSketchType == LINE_SKETCH) {
            // 如果前一步为LINE_SKETCH，则删除蒙层
            removeViewAt(getChildCount() - 1);
        } else if (sketchType == LINE_SKETCH) {
            // 如果到LINE_SKETCH，则加入蒙层
            addLineSketch();
        }
        mSketchType = sketchType;
    }

    public void setOnSketchBoardListener(OnSketchBoardListener listener) {
        mOnSketchBoardListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        return mSketchType == LINE_SKETCH;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean isIntercept = false;
        for (int i = getTopLayer(); i >= 0; --i) {
            if (getChildAt(i).onTouchEvent(event)) {
                isIntercept = true;
                break;
            }
        }
        // 如果是Line状态，则蒙层最后一个获得事件
        if (mSketchType == LINE_SKETCH && !isIntercept) {
            getChildAt(getTopLayer() + 1).onTouchEvent(event);
        }
        return true;
    }

    /**
     * update paint info of line sketch
     *
     * @param color       color
     * @param strokeWidth stroke width
     */
    public void setLineSketchPaint(int color, float strokeWidth) {
        mLineColor = color;
        mLineStrokeWidth = strokeWidth;
        if (mSketchType == LINE_SKETCH) {
            ((LineSketchView) getChildAt(getTopLayer() + 1)).setColorAndStroke(mLineColor,
                    mLineStrokeWidth);
        }
    }

    private void addLineSketch() {
        LineSketchView lineSketchView = new LineSketchView(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lineSketchView.setLayer(getChildCount());
        addView(lineSketchView, lp);
        lineSketchView.setColorAndStroke(mLineColor, mLineStrokeWidth);
        lineSketchView.setOnSketchListener(new SketchBoardListener<LineSketchData>() {
            @Override
            public void onSketchComplete(final LineSketchData data) {
                super.onSketchComplete(data);
                mSketchData.add(data);
                addLineSketch();
            }
        });
    }

    public void addTextSketch(String text, int color) {
        setSketchType(TEXT_SKETCH);
        if (getTopLayer() >= 0) {
            BaseSketchView baseSketchView = (BaseSketchView) getChildAt(getTopLayer());
            baseSketchView.setSelected(false);
        }
        TextSketchView textSketchView = new TextSketchView(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textSketchView.setLayer(getChildCount());
        textSketchView.setOnSketchListener(new SketchBoardListener<TextSketchData>() {
            @Override
            public void onSketchComplete(final TextSketchData data) {
                super.onSketchComplete(data);
                mSketchData.add(data);
            }

            @Override
            public void onSketchClicked(final int layer) {
                if (mOnSketchBoardListener != null) {
                    String text = ((TextSketchData) mSketchData.get(layer)).getText();
                    int color = (mSketchData.get(layer)).getPaint().getColor();
                    mOnSketchBoardListener.onTextSketchClicked(text, color);
                }
            }
        });
        textSketchView.setText(text, color, false);
        addView(textSketchView, lp);
    }

    public void addImageSketch(Bitmap bitmap) {
        setSketchType(IMAGE_SKETCH);
        if (getTopLayer() >= 0) {
            BaseSketchView baseSketchView = (BaseSketchView) getChildAt(getTopLayer());
            baseSketchView.setSelected(false);
        }
        ImageSketchView imageSketchView = new ImageSketchView(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageSketchView.setLayer(getChildCount());
        imageSketchView.setOnSketchListener(new SketchBoardListener<ImageSketchData>() {
            @Override
            public void onSketchComplete(final ImageSketchData data) {
                super.onSketchComplete(data);
                mSketchData.add(data);
            }
        });
        imageSketchView.setImage(bitmap);
        addView(imageSketchView, lp);
    }

    /**
     * reset an existing sketch
     *
     * @param text  text
     * @param color color
     */
    public void resetTextSketch(String text, int color) {
        if (!(getChildAt(getTopLayer()) instanceof TextSketchView)) {
            return;
        }
        TextSketchView textSketchView = (TextSketchView) getChildAt(getTopLayer());
        textSketchView.setText(text, color, true);
    }

    private class SketchBoardListener<T extends BaseSketchData> implements OnSketchListener<T> {
        @Override
        public void onSketchComplete(final T data) {
            mSketchActions.add(new SketchAction(SketchAction.CREATE_ACTION));
            mIsSketchSelected = false;
        }

        @Override
        public void onAction(final SketchAction sketchAction) {
            mSketchActions.add(sketchAction);
        }

        @Override
        public void onSketchTouched(final int layer) {
            mIsSketchSelected = layer >= 0;
            if (mOnSketchBoardListener != null) {
                mOnSketchBoardListener.onSketchSelected(mIsSketchSelected);
            }
            if (!mIsSketchSelected || layer == getTopLayer()) {
                return;
            }
            View view = getChildAt(layer);
            BaseSketchData data = mSketchData.get(layer);
            for (int i = layer + 1; i < getChildCount(); ++i) {
                // 所有覆盖在上面的层降级
                ((BaseSketchView) getChildAt(i)).setLayer(i - 1);
            }
            BaseSketchView baseSketchView = (BaseSketchView) getChildAt(getTopLayer());
            // 上层去除状态
            baseSketchView.setSelected(false);
            mSketchData.remove(layer);
            removeView(view);
            addView(view, getTopLayer() + 1);
            ((BaseSketchView) view).setLayer(getTopLayer());
            mSketchActions.add(new LayerAction().setOldLayer(layer).setNewLayer(getTopLayer()));
            mSketchData.add(data);
        }

        @Override
        public void onSketchClicked(final int layer) {

        }
    }

    /**
     * delete top layer
     * only there is a sketch selected, you can then delete it
     */
    public void delete() {
        if (!mIsSketchSelected) {
            return;
        }
        mIsSketchSelected = false;
        BaseSketchView baseSketchView = (BaseSketchView) getChildAt(getTopLayer());
        baseSketchView.setSelected(false);
        mSketchActions.add(new DeleteAction().setData(mSketchData.get(getTopLayer())).setView
                (baseSketchView));
        mSketchData.remove(getTopLayer());
        removeViewAt(getTopLayer());
    }

    public void undo() {
        if (mSketchActions.isEmpty()) {
            return;
        }
        SketchAction action = mSketchActions.pop();
        switch (action.getActionType()) {
            case SketchAction.LAYER_ACTION:
                // new 和 old 交换，数据和view都要交换
                int oldLayer = ((LayerAction) action).getOldLayer();
                int newLayer = ((LayerAction) action).getNewLayer();
                BaseSketchData data = mSketchData.get(oldLayer);
                mSketchData.set(oldLayer, mSketchData.get(newLayer));
                mSketchData.set(oldLayer, data);

                View oldView = getChildAt(oldLayer);
                View newView = getChildAt(newLayer);
                removeView(oldView);
                removeView(newView);
                addView(newView, oldLayer);
                addView(oldView, newLayer);

                break;
            case SketchAction.TRANSITION_ACTION:
                BaseSketchView view = (BaseSketchView) getChildAt(getTopLayer());
                int oldX = ((TransitionAction) action).getOffsetX();
                int oldY = ((TransitionAction) action).getOffsetY();
                mSketchData.get(getTopLayer()).setStartX(oldX).setStartY(oldY);
                view.reLayout(oldX, oldY);
                break;
            case SketchAction.CREATE_ACTION:
                mSketchData.remove(getTopLayer());
                removeViewAt(getTopLayer());
                if (mSketchType == LINE_SKETCH) {
                    // 蒙层降级刷新
                    ((BaseSketchView) getChildAt(getTopLayer() + 1)).setLayer(getTopLayer() + 1);
                }
                break;
            case SketchAction.DELETE_ACTION:
                mSketchData.add(((DeleteAction) action).getData());
                addView(((DeleteAction) action).getView(), getTopLayer() + 1);
                break;
            case SketchAction.ROTATE_ACTION:
                float oldRotate = ((RotateAction) action).getOldDegree();
                ((BaseScaleRotateData) mSketchData.get(getTopLayer())).setRotate(oldRotate);
                ((BaseScaleRotateSketchView) getChildAt(getTopLayer())).setRotate(oldRotate);
                break;
            case SketchAction.SCALE_ACTION:
                float oldScale = ((ScaleAction) action).getOldScale();
                ((BaseScaleRotateData) mSketchData.get(getTopLayer())).setScale(oldScale);
                ((BaseScaleRotateSketchView) getChildAt(getTopLayer())).setScale(oldScale);
                break;
            default:
                break;
        }
    }

    /**
     * get sketch data, use the data to draw on picture
     * to combine to on pic
     *
     * @return list of sketch data
     */
    public List<BaseSketchData> getSketchData() {
        return mSketchData;
    }

    /**
     * get the top valid layer
     *
     * @return -1, means no child
     */
    private int getTopLayer() {
        return mSketchType == LINE_SKETCH ? getChildCount() - 2 : getChildCount() - 1;
    }

    public interface OnSketchBoardListener {
        /**
         * is any sketch selected
         *
         * @param isSelected is selected
         */
        void onSketchSelected(boolean isSelected);

        /**
         * when text sketch clicked
         *
         * @param text  text
         * @param color color
         */
        void onTextSketchClicked(String text, int color);
    }

    public Bitmap frozen(Bitmap rawBitmap) {
        Bitmap newBitmap = rawBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        float scale = getWidth() * 1.0F / newBitmap.getWidth();

        Canvas canvas = new Canvas(newBitmap);
        canvas.scale(1.0F / scale, 1.0F / scale);

        int offset = (int) ((getHeight() - newBitmap.getHeight() * scale) / 2);
        canvas.translate(0, -offset);

        List<BaseSketchData> sketchDataList = getSketchData();
        for (int i = 0; i < sketchDataList.size(); i++) {
            BaseSketchData sketchData = sketchDataList.get(i);
            canvas.save();
            if (sketchData instanceof LineSketchData) {
                LineSketchData lineSketchData = (LineSketchData) sketchData;
                canvas.translate(lineSketchData.getStartX(), lineSketchData
                        .getStartX());
                canvas.drawPath(lineSketchData.getPath(), lineSketchData.getPaint());
            } else if (sketchData instanceof TextSketchData) {
                TextSketchData textSketchData = (TextSketchData) sketchData;
                canvas.rotate(textSketchData.getRotate(), textSketchData.getCenterX(),
                        textSketchData.getCenterY());

                canvas.drawText(textSketchData.getText(), textSketchData.getStartX(),
                        textSketchData.getStartY(), textSketchData.getPaint());
            } else if (sketchData instanceof ImageSketchData) {
                ImageSketchData imageSketchData = (ImageSketchData) sketchData;
                canvas.rotate(imageSketchData.getRotate(), imageSketchData.getCenterX(),
                        imageSketchData.getCenterY());

                canvas.drawBitmap(imageSketchData.getImage(), imageSketchData.getStartX(),
                        imageSketchData.getStartY(), imageSketchData.getPaint());
            }
            canvas.restore();
        }
        return newBitmap;
    }
}
