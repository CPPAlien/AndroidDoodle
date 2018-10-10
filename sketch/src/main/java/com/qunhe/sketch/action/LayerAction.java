package com.qunhe.sketch.action;

/**
 * @author dq
 */
public class LayerAction extends SketchAction {
    private int mOldLayer;
    private int mNewLayer;

    public LayerAction() {
        super(LAYER_ACTION);
    }

    public int getOldLayer() {
        return mOldLayer;
    }

    public LayerAction setOldLayer(final int oldLayer) {
        mOldLayer = oldLayer;
        return this;
    }

    public int getNewLayer() {
        return mNewLayer;
    }

    public LayerAction setNewLayer(final int newLayer) {
        mNewLayer = newLayer;
        return this;
    }
}
