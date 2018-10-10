package com.qunhe.sketch.action;

/**
 * @author dq
 */
public class RotateAction extends SketchAction {
    private float mOldDegree;
    private float mNewDegree;

    public RotateAction() {
        super(ROTATE_ACTION);
    }

    public float getOldDegree() {
        return mOldDegree;
    }

    public RotateAction setOldDegree(final float oldDegree) {
        mOldDegree = oldDegree;
        return this;
    }

    public float getNewDegree() {
        return mNewDegree;
    }

    public RotateAction setNewDegree(final float newDegree) {
        mNewDegree = newDegree;
        return this;
    }
}
