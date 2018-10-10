package com.qunhe.sketch.action;

import com.qunhe.sketch.base.BaseSketchData;
import com.qunhe.sketch.base.BaseSketchView;

/**
 * @author dq
 */
public class DeleteAction extends SketchAction {
    private BaseSketchView mView;
    private BaseSketchData mData;

    public DeleteAction() {
        super(DELETE_ACTION);
    }

    public BaseSketchView getView() {
        return mView;
    }

    public DeleteAction setView(final BaseSketchView view) {
        mView = view;
        return this;
    }

    public BaseSketchData getData() {
        return mData;
    }

    public DeleteAction setData(final BaseSketchData data) {
        mData = data;
        return this;
    }
}
