package com.qunhe.sketch.pen;

import android.graphics.Path;

import com.qunhe.sketch.base.BaseSketchData;

/**
 * @author dq
 */
public class LineSketchData extends BaseSketchData {
    private Path mPath;

    public Path getPath() {
        return mPath;
    }

    public LineSketchData setPath(final Path path) {
        mPath = path;
        return this;
    }
}
