package com.qunhe.hacksketch;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

/**
 * @author dq
 */
public class Util {
    /**
     * dp转成px
     *
     * @param dp dp值
     * @return px值
     */
    public static int dp2Px(final float dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    /**
     * 隐藏键盘
     * http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in
     * -android
     *
     * @param activity activity
     * @return true if the previous state is visible, false otherwise.
     */
    public static boolean hideKeyboard(final Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            if (getKeyboardState(activity)) {
                final InputMethodManager imm = (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取键盘的状态
     *
     * @param activity activity
     * @return 显示true，没显示false
     */
    public static boolean getKeyboardState(final Activity activity) {
        final View activityRootView = ((ViewGroup) activity.findViewById(android.R.id.content))
                .getChildAt(0);
        final Rect r = new Rect();
        activityRootView.getWindowVisibleDisplayFrame(r);
        final int screenHeight = getScreenHeight(activity);
        final int heightDiff = screenHeight - (r.bottom - r.top);
        return heightDiff > screenHeight / 4;
    }

    private static Display getScreenDisplay(final Activity activity) {
        return activity.getWindowManager().getDefaultDisplay();
    }

    /**
     * 获取屏幕高度
     *
     * @param activity activity
     * @return 屏幕高度
     */
    public static int getScreenHeight(final Activity activity) {
        // This warning is to be compatible with old target Android APIs.
        return getScreenDisplay(activity).getHeight();
    }
}
