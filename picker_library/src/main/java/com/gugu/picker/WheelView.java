package com.gugu.picker;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class WheelView extends ScrollView {
    private static final int OFF_SET = 1;
    private static final int DELAY = 50;

    private Context mContext;
    private LinearLayout mLinearLayout;
    private List<String> items = new ArrayList<>();

    private int mOffset = OFF_SET;
    private int mDisplayItemCount; // 每页显示的数量
    private int mSelectedIndex = OFF_SET;

    private int mInitialY;
    private float mPreviousY = 0; // 记录按下时的Y坐标

    private Runnable mScrollerTask = new ScrollerTask();
    private int mItemHeight = 0;
    private int[] mSelectedAreaBorder; // 获取选中区域的边界

    private OnWheelViewListener mOnWheelViewListener;

    private int mTextSize;
    private int mTextColorNormal;
    private int mTextColorFocus;

    private boolean mIsUserScroll = false; // 是否用户手动滚动

    private DisplayMetrics mDisplayMetrics;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                float delta = ev.getY() - mPreviousY;
                if (mSelectedIndex == mOffset && delta > 0) {
                    // 滑动到第一项时，若继续向下滑动，则自动跳到最后一项
                    _setSelectedIndex(items.size() - mOffset * 2 - 1);

                } else if (mSelectedIndex == items.size() - mOffset - 1 && delta < 0) {
                    // 滑动到最后一项时，若继续向上滑动，则自动跳到第一项
                    _setSelectedIndex(0);

                } else {
                    mIsUserScroll = true;
                    startScrollerTask();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * Sets items.
     *
     * @param list the list
     */
    void setItems(List<String> list) {
        setItems(list, 0);
    }

    /**
     * Sets items.
     *
     * @param list  the list
     * @param index the index
     */
    void setItems(List<String> list, int index) {
        _setItems(list);
        _setSelectedIndex(index);
    }

    void setSelectedIndex(int index) {
        _setSelectedIndex(index);
    }

    /**
     * Sets text size.
     *
     * @param textSize the text size
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    /**
     * Sets text color.
     *
     * @param textColorNormal the text color normal
     * @param textColorFocus  the text color focus
     */
    void setTextColor(@ColorInt int textColorNormal, @ColorInt int textColorFocus) {
        mTextColorNormal = textColorNormal;
        mTextColorFocus = textColorFocus;
    }

    /**
     * Sets offset.
     *
     * @param offset the offset
     */
    public void setOffset(@IntRange(from = 1, to = 4) int offset) {
        if (offset < 1 || offset > 4) {
            throw new IllegalArgumentException("Offset must between 1 and 4");
        }
        mOffset = offset;
    }

    /**
     * Gets selected index.
     *
     * @return the selected index
     */
    int getSelectedIndex() {
        return mSelectedIndex - mOffset;
    }

    /**
     * Sets on wheel view listener.
     *
     * @param listener the on wheel view listener
     */
    public void setOnWheelViewListener(OnWheelViewListener listener) {
        mOnWheelViewListener = listener;
    }

    private void init(Context context) {
        mContext = context.getApplicationContext();

        setFillViewport(true);

        mDisplayMetrics = Util.displayMetrics(mContext);

        mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.gugu_text_size);
        mTextColorNormal = mContext.getResources().getColor(R.color.gugu_text_color_normal);
        mTextColorFocus = mContext.getResources().getColor(R.color.gugu_text_color_focus);

        // 2015/12/15 去掉ScrollView的阴影
        setFadingEdgeLength(0);

        // 禁止其阴影的出现
        if (Build.VERSION.SDK_INT >= 9) {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        setVerticalScrollBarEnabled(false);

        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mLinearLayout);
    }

    /**
     * 从0开始计数，所有项包括偏移量
     *
     * @param index 选中第几个
     */
    private void _setSelectedIndex(@IntRange(from = 0) final int index) {
        mIsUserScroll = false;
        post(() -> {// 滚动到选中项的位置
            smoothScrollTo(0, index * mItemHeight);
            // 选中这一项的值
            mSelectedIndex = index + mOffset;
            onSelectedCallBack();
        });
    }

    private void _setItems(List<String> list) {
        items.clear();
        items.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < mOffset; i++) {
            items.add(0, "");
            items.add("");
        }

        initData();
    }

    private void startScrollerTask() {
        mInitialY = getScrollY();
        postDelayed(mScrollerTask, DELAY);
    }

    private void initData() {
        mDisplayItemCount = mOffset * 2 + 1;

        // 2015/12/15 添加此句才可以支持联动效果
        mLinearLayout.removeAllViews();

        for (String item : items) {
            mLinearLayout.addView(createView(item));
        }

        // 2016/1/15 焦点文字颜色高亮位置，逆推“int position = y / mItemHeight + offset”
        refreshItemView(mItemHeight * (mSelectedIndex - mOffset));
    }

    private TextView createView(String text) {
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(new ScrollView.LayoutParams(mDisplayMetrics.widthPixels, ScrollView.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        tv.setGravity(Gravity.CENTER);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.gugu_text_padding);
        tv.setPadding(padding, padding, padding, padding);

        if (0 == mItemHeight) {
            mItemHeight = getViewMeasuredHeight(tv);
            mLinearLayout.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mItemHeight * mDisplayItemCount));
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height = mItemHeight * mDisplayItemCount;
            setLayoutParams(lp);
        }

        return tv;
    }

    private void refreshItemView(int y) {
        int position = y / mItemHeight + mOffset;
        int remainder = y % mItemHeight;
        int divided = y / mItemHeight;

        if (remainder == 0) {
            position = divided + mOffset;
        } else {
            if (remainder > mItemHeight / 2) {
                position = divided + mOffset + 1;
            }
        }

        int childSize = mLinearLayout.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) mLinearLayout.getChildAt(i);
            if (null == itemView) {
                return;
            }
            // 2015/12/15 可设置颜色
            if (position == i) {
                itemView.setTextColor(mTextColorFocus);
            } else {
                itemView.setTextColor(mTextColorNormal);
            }
        }
    }

//    private int[] obtainSelectedAreaBorder() {
//        if (null == mSelectedAreaBorder) {
//            mSelectedAreaBorder = new int[2];
//            mSelectedAreaBorder[0] = mItemHeight * mOffset;
//            mSelectedAreaBorder[1] = mItemHeight * (mOffset + 1);
//        }
//        return mSelectedAreaBorder;
//    }

    /**
     * 选中回调
     */
    private void onSelectedCallBack() {
        if (null != mOnWheelViewListener) {
            // 2015/12/25 真实的index应该忽略偏移量
            int realIndex = mSelectedIndex - mOffset;
            mOnWheelViewListener.onSelected(mIsUserScroll, realIndex, items.get(realIndex));
        }
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }


    /**
     * The interface On wheel view listener.
     */
    public interface OnWheelViewListener {
        /**
         * On selected.
         *
         * @param isUserScroll  the is user scroll
         * @param selectedIndex the selected index
         * @param item          the item
         */
        void onSelected(boolean isUserScroll, int selectedIndex, String item);
    }

    private class ScrollerTask implements Runnable {

        @Override
        public void run() {
            // 2015/12/17 java.lang.ArithmeticException: divide by zero
            if (mItemHeight == 0) {
                return;
            }
            int newY = getScrollY();
            if (mInitialY - newY == 0) { // stopped
                final int remainder = mInitialY % mItemHeight;
                final int divided = mInitialY / mItemHeight;
                if (remainder == 0) {
                    mSelectedIndex = divided + mOffset;
                    onSelectedCallBack();
                } else {
                    if (remainder > mItemHeight / 2) {
                        post(() -> {
                            smoothScrollTo(0, mInitialY - remainder + mItemHeight);
                            mSelectedIndex = divided + mOffset + 1;
                            onSelectedCallBack();
                        });
                    } else {
                        post(() -> {
                            smoothScrollTo(0, mInitialY - remainder);
                            mSelectedIndex = divided + mOffset;
                            onSelectedCallBack();
                        });
                    }
                }
            } else {
                startScrollerTask();
            }
        }
    }
}
