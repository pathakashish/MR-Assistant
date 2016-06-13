package com.aviras.mrassistant.ui.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Utility class for commom UI tasks
 * <p/>
 * Created by ashish on 12/6/16.
 */
public class UiUtil {

    public static void showToast(Context context, int messageResId) {
        showToast(context, messageResId, true);
    }

    public static void showToast(Context context, String message) {
        showToast(context, message, true);
    }

    public static void showToast(Context context, int messageResId, boolean showLong) {
        showToast(context, context.getString(messageResId), showLong);
    }

    public static void showToast(Context context, CharSequence message, boolean showLong) {
        Toast toast = Toast.makeText(context, message, showLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
