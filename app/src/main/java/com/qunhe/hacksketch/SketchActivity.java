package com.qunhe.hacksketch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.qunhe.sketch.SketchBoardLayout;

/**
 * @author ly
 */
public class SketchActivity extends AppCompatActivity {
    interface ColorListener {
        void onColorSelected(int color);
    }

    private static final int STROKE_WIDTH = 10;
    private static final int ANIMATION_DURATION_IN_MILLS = 300;
    private LinearLayout mColorPickLayoutView;//颜色选择
    private LinearLayout mTextLayoutView;//创建文字
    private LinearLayout mStickPicLayoutView;//贴图选择
    private int[] mColors;
    private int[] mSticks = {R.drawable.furniture_1, R.drawable.furniture_2, R.drawable
            .furniture_3, R.drawable.furniture_4, R.drawable.furniture_5, R.drawable.furniture_6, R
            .drawable.furniture_7, R.drawable.furniture_8, R.drawable.furniture_9, R.drawable
            .furniture_10, R.drawable.furniture_11, R.drawable.furniture_12, R.drawable
            .furniture_13, R.drawable.furniture_14, R.drawable.furniture_15, R.drawable
            .furniture_16, R.drawable.furniture_17, R.drawable.furniture_18, R.drawable
            .furniture_19, R.drawable.furniture_20};
    private int mTextColor;
    private boolean mIsResetText = false;
    private SketchBoardLayout mSketchBoard;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_sketch);
        mSketchBoard = findViewById(R.id.sketch_board);
        mColors = getResources().getIntArray(R.array.sketch_colors);
        mTextColor = mColors[0];

        mSketchBoard.setSketchType(SketchBoardLayout.LINE_SKETCH);
        mSketchBoard.setLineSketchPaint(mTextColor, STROKE_WIDTH);

        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                undo();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                deleteSketch();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (ActivityCompat.checkSelfPermission(SketchActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveSketchData();
                } else {
                    ActivityCompat.requestPermissions(SketchActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });

        findViewById(R.id.type_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mIsResetText = false;
                showAddTextView();
                selectSketchType(findViewById(R.id.type_text));
            }
        });

        findViewById(R.id.type_free_style).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showColorPickView();
                selectSketchType(findViewById(R.id.type_free_style));
            }
        });

        findViewById(R.id.type_stick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showStickView();
                selectSketchType(findViewById(R.id.type_stick));
            }
        });

        mSketchBoard.setOnSketchBoardListener(new SketchBoardLayout.OnSketchBoardListener
                () {
            @Override
            public void onSketchSelected(final boolean isSelected) {

            }

            @Override
            public void onTextSketchClicked(final String text, final int color) {
                mIsResetText = true;
                editText(text, color);
            }
        });
    }

    /**
     * 显示贴图view
     */
    private void showStickView() {
        hideColorPickView();
        ViewStub viewStub = findViewById(R.id.stick_pick_stub);
        if (viewStub != null && viewStub.getParent() != null) {
            mStickPicLayoutView = (LinearLayout) viewStub.inflate();
            final LinearLayout stickPickLayout = mStickPicLayoutView.findViewById(R.id
                    .stick_pick_layout);

            for (final int stick : mSticks) {
                addStickView(stick, stickPickLayout);
            }
        } else {
            mStickPicLayoutView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏贴图view
     */
    private void hideStickView() {
        if (mStickPicLayoutView != null) {
            mStickPicLayoutView.setVisibility(View.GONE);
        }
    }

    private void addStickView(@DrawableRes final int stick, ViewGroup parent) {
        ImageView stickView = (ImageView) LayoutInflater.from(this).inflate(R.layout.sketch_stick_item, parent, false);
        stickView.setBackgroundResource(stick);
        stickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Bitmap image = BitmapFactory.decodeResource(getResources(), stick);
                mSketchBoard.addImageSketch(image);
            }
        });
        parent.addView(stickView);
    }

    /**
     * 选择涂鸦类型
     *
     * @param view
     */
    private void selectSketchType(View view) {
        LinearLayout sketchType = findViewById(R.id.sketch_type);
        for (int i = 0; i < sketchType.getChildCount(); i++) {
            sketchType.getChildAt(i).setSelected(false);
        }

        view.setSelected(true);
    }

    /**
     * 显示选择颜色view
     */
    private void showColorPickView() {
        hideStickView();

        mSketchBoard.setSketchType(SketchBoardLayout.LINE_SKETCH);
        ViewStub colorPickStub = findViewById(R.id.color_pick_stub);
        if (colorPickStub != null && colorPickStub.getParent() != null) {
            mColorPickLayoutView = (LinearLayout) colorPickStub.inflate();
            final LinearLayout colorPickLayout = mColorPickLayoutView.findViewById(R.id
                    .color_pick_layout);

            for (final int color : mColors) {
                addColorItemView(color, colorPickLayout, new ColorListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mSketchBoard.setLineSketchPaint(color, STROKE_WIDTH);
                    }
                });
            }
        } else {
            mColorPickLayoutView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏选择颜色view
     */
    private void hideColorPickView() {
        if (mColorPickLayoutView != null) {
            mColorPickLayoutView.setVisibility(View.GONE);
        }
    }

    private void addColorItemView(int color, final ViewGroup parent, final ColorListener listener) {
        final CircleView colorView = (CircleView) LayoutInflater.from(this).inflate(R.layout.sketch_color_pick_item,
                parent, false);
        colorView.setColor(color);
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    CircleView child = (CircleView) parent.getChildAt(i);
                    child.setSelected(false);
                }
                colorView.setSelected(true);
                if (listener != null) {
                    listener.onColorSelected(colorView.getCurrentColor());
                }
            }
        });
        parent.addView(colorView);
    }

    /**
     * 显示添加文字页面
     */
    private void showAddTextView() {
        hideColorPickView();
        hideStickView();
        hideTopAndBottomView();
        ViewStub viewStub = findViewById(R.id.edit_stub);
        if (viewStub != null && viewStub.getParent() != null) {
            mTextLayoutView = (LinearLayout) viewStub.inflate();
            final EditText editText = mTextLayoutView.findViewById(R.id.text);
            editText.requestFocus();

            mTextLayoutView.findViewById(R.id.cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            hideAddTextView();
                            showTopAndBottomView();
                        }
                    });
            mTextLayoutView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    hideAddTextView();
                    showTopAndBottomView();
                    setText(editText.getText().toString(), mTextColor, mIsResetText);
                }
            });

            final LinearLayout colorPickLayout = mTextLayoutView.findViewById(R.id
                    .sketch_color_pick_layout).findViewById(R.id.color_pick_layout);

            for (final int color : mColors) {
                addColorItemView(color, colorPickLayout, new ColorListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mTextColor = color;
                        editText.setTextColor(color);
                    }
                });
            }
        } else {
            mTextLayoutView.setVisibility(View.VISIBLE);
            ((EditText) mTextLayoutView.findViewById(R.id.text)).setText("");
        }
    }

    /**
     * 编辑文字
     */
    void editText(String text, int textColor) {
        if (mTextLayoutView != null) {
            hideColorPickView();
            hideStickView();
            hideTopAndBottomView();

            mTextLayoutView.setVisibility(View.VISIBLE);
            final EditText editText = mTextLayoutView.findViewById(R.id.text);
            editText.setText(text);
            editText.setTextColor(textColor);
            editText.requestFocus();
        }
    }

    /**
     * 隐藏顶部和底部layout
     */
    private void hideTopAndBottomView() {
        findViewById(R.id.top_layout).animate().translationY(-findViewById(R.id.top_layout).getHeight() - Util
                .dp2Px(12)).setDuration(ANIMATION_DURATION_IN_MILLS);
        findViewById(R.id.bottom_layout).animate().translationY(findViewById(R.id.bottom_layout).getHeight())
                .setDuration(ANIMATION_DURATION_IN_MILLS);
    }

    /**
     * 隐藏编辑文字页面
     */
    private void hideAddTextView() {
        Util.hideKeyboard(this);
        mTextLayoutView.setVisibility(View.GONE);
    }

    /**
     * 添加文字
     *
     * @param text text
     */
    private void setText(String text, int color, boolean isReset) {
        if (!isReset) {
            mSketchBoard.addTextSketch(text, color);
        } else {
            mSketchBoard.resetTextSketch(text, color);
        }
    }

    /**
     * 撤销
     */
    private void undo() {
        mSketchBoard.undo();
    }

    private void saveSketchData() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test);
        Bitmap newBitmap = mSketchBoard.frozen(bitmap);

        CapturePhotoUtils.insertImage(getContentResolver(), newBitmap, "Sketch" , "Sketch");
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除选择控件
     */
    private void deleteSketch() {
        mSketchBoard.delete();
    }

    /**
     * 显示顶部和底部layout
     */
    private void showTopAndBottomView() {
        findViewById(R.id.top_layout).animate().translationY(0).setDuration(ANIMATION_DURATION_IN_MILLS);
        findViewById(R.id.bottom_layout).animate().translationY(0).setDuration(ANIMATION_DURATION_IN_MILLS);
    }
}
