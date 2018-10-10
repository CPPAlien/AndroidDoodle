package com.qunhe.sketch;

import com.qunhe.sketch.action.SketchAction;
import com.qunhe.sketch.base.BaseSketchData;

/**
 * @author dq
 */
public interface OnSketchListener<T extends BaseSketchData> {
     void onSketchComplete(T data);
     void onAction(SketchAction sketchAction);
     void onSketchTouched(int layer);
     void onSketchClicked(int layer);
}
