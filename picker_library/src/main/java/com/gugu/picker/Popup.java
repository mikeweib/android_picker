package com.gugu.picker;


import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

public class Popup {

    private Dialog mDialog;
    FrameLayout mContentLayout;

    protected Popup(Activity activity) {
        mDialog = new android.app.Dialog(activity, R.style.Theme_GuguDialog);
        mDialog.setCanceledOnTouchOutside(true);//触摸屏幕取消窗体
        mDialog.setCancelable(true);//按返回键取消窗体

        mContentLayout = new FrameLayout(activity);
        mContentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mContentLayout.setFocusable(true);
        mContentLayout.setFocusableInTouchMode(true);

        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);//位于屏幕底部
        window.setContentView(mContentLayout);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void setContentView(View view) {
        mContentLayout.addView(view);
    }

    public void setAnimationStyle(@StyleRes int animRes) {
        Window window = mDialog.getWindow();
        window.setWindowAnimations(animRes);
    }
}
