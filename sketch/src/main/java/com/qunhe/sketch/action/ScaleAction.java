package com.qunhe.sketch.action;

/**
 * @author dq
 */
public class ScaleAction extends SketchAction {
    private float mOldScale;
    private float mNewScale;

    public ScaleAction() {
        super(SCALE_ACTION);
    }

    public float getOldScale() {
        return mOldScale;
    }

    public ScaleAction setOldScale(final float oldScale) {
        mOldScale = oldScale;
        return this;
    }

    public float getNewScale() {
        return mNewScale;
    }

    public ScaleAction setNewScale(final float newScale) {
        mNewScale = newScale;
        return this;
    }
}
