package com.qunhe.sketch.action;

/**
 * @author dq
 */
public class TransitionAction extends SketchAction {
    private int mOffsetX;
    private int mOffsetY;

    public TransitionAction() {
        super(TRANSITION_ACTION);
    }

    public int getOffsetX() {
        return mOffsetX;
    }

    public TransitionAction setOffsetX(final int offsetX) {
        mOffsetX = offsetX;
        return this;
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    public TransitionAction setOffsetY(final int offsetY) {
        mOffsetY = offsetY;
        return this;
    }
}
