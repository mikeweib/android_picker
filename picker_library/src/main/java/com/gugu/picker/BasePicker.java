package com.gugu.picker;


import android.app.Activity;
import android.view.View;

public abstract class BasePicker {

    private Popup mPopup;

    public BasePicker(Activity activity) {
        mPopup = new Popup(activity);
        setAnimationStyle(R.style.Animation_GuguDialog);
    }

    public void show() {
        mPopup.show();
    }

    public void dismiss() {
        mPopup.dismiss();
    }

    public void setContentView(View view) {
        mPopup.setContentView(view);
    }

    public void setAnimationStyle(int style) {
        mPopup.setAnimationStyle(style);
    }
}
