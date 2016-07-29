package com.gugu.picker;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class SamplePicker extends BasePicker {

    private WheelView mWheelView;
    private TextView mTitleView;
    private TextView mCancelView;
    private TextView mConfirmView;

    private OnSelectedListener mOnSelectedListener;

    public interface OnSelectedListener {

        void onSelectIndex(int index);
    }

    public SamplePicker(Activity activity) {
        super(activity);

        View view = LayoutInflater.from(activity).inflate(R.layout.layout_picker, null);
        initViews(view);

        view.setLayoutParams( new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        setContentView(view);

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> onDismiss());
        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> onConfirm());
    }

    public void setItems(List<String> list) {
        mWheelView.setItems(list);
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }

    public void setSelectedIndex(int index) {
        mWheelView.setSelectedIndex(index);
    }

    public void setTextSize(int textSize) {
        mWheelView.setTextSize(textSize);
    }

    /**
     * Sets text color.
     *
     * @param textColorNormal the text color normal
     * @param textColorFocus  the text color focus
     */
    public void setTextColor(@ColorInt int textColorNormal, @ColorInt int textColorFocus) {
        mWheelView.setTextColor(textColorNormal, textColorFocus);
    }

    public void setTitleText(int res) {
        mTitleView.setText(res);
    }

    private void onDismiss() {
        dismiss();
    }

    private void onConfirm() {
        if(mOnSelectedListener != null) {
            mOnSelectedListener.onSelectIndex(mWheelView.getSelectedIndex());
        }
        dismiss();
    }

    private void initViews(View rootView) {
        mWheelView = (WheelView) rootView.findViewById(R.id.wheel_view);
        mTitleView = (TextView) rootView.findViewById(R.id.text_title);
        mCancelView = (TextView) rootView.findViewById(R.id.btn_cancel);
        mConfirmView = (TextView) rootView.findViewById(R.id.btn_confirm);
    }
}
