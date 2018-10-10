package com.qunhe.hacksketch;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.qunhe.sketch.SketchBoardLayout;

public class SimpleTestActivity extends AppCompatActivity {
    private SketchBoardLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_test);
        mFrameLayout = (SketchBoardLayout) findViewById(R.id.content);
        mFrameLayout.setSketchType(SketchBoardLayout.LINE_SKETCH);
        mFrameLayout.setLineSketchPaint(ContextCompat.getColor(this, R.color.line_border_color), 100);
        findViewById(R.id.add_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mFrameLayout.addTextSketch("测试测试", R.color.colorPrimary);
            }
        });
        findViewById(R.id.pen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mFrameLayout.setSketchType(SketchBoardLayout.LINE_SKETCH);
            }
        });
        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mFrameLayout.undo();
            }
        });
        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mFrameLayout.delete();
            }
        });
    }
}
