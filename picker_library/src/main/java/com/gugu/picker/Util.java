package com.gugu.picker;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Util {

    public static DisplayMetrics displayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}
