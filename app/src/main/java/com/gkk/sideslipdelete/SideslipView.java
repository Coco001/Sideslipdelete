package com.gkk.sideslipdelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 侧滑的view
 */
public class SideslipView extends ViewGroup {

    private View leftView;
    private View rightView;
    private int rightWidth;
    private ViewDragHelper helper;
    private boolean isOpened;
    private OnSideslipViewListener listenner;

    public SideslipView(Context context) {
        this(context, null);
    }

    public SideslipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        helper = ViewDragHelper.create(this, new MyCallBack());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        leftView = getChildAt(0);
        rightView = getChildAt(1);
        LayoutParams layoutParams = rightView.getLayoutParams();
        rightWidth = layoutParams.width;//获取右侧view的宽度
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        leftView.measure(widthMeasureSpec, heightMeasureSpec);
        int rightMeasureSpec = MeasureSpec.makeMeasureSpec(rightWidth, MeasureSpec.EXACTLY);
        rightView.measure(rightMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        leftView.layout(0, 0, leftView.getMeasuredWidth(), leftView.getMeasuredHeight());
        rightView.layout(leftView.getMeasuredWidth(), 0, leftView.getMeasuredWidth() + rightView.getMeasuredWidth(), rightView.getMeasuredHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        helper.processTouchEvent(event);
        return true;
    }

    class MyCallBack extends ViewDragHelper.Callback{

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == leftView || child == rightView;//需要分析的view
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == leftView) {
                if (left < 0 && left < -rightWidth) {
                    return -rightWidth;
                } else if (left > 0) {
                    return 0;
                }
            } else if (child == rightView) {
                int measuredWidth = leftView.getMeasuredWidth();
                if (left < measuredWidth - rightWidth) {
                    return measuredWidth - rightWidth;
                } else if (left > measuredWidth) {
                    return measuredWidth;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            ViewCompat.postInvalidateOnAnimation(SideslipView.this);
            int leftWidth = leftView.getMeasuredWidth();
            int leftHeight = leftView.getMeasuredHeight();
            if (changedView == leftView) {
                rightView.layout(leftWidth + left, 0, leftWidth + left + rightWidth, leftHeight);
            } else if (changedView == rightView) {
                leftView.layout(left - leftWidth, 0, left, leftHeight);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int left = leftView.getLeft();
            if (-left < rightWidth / 2f) {
                close();
            } else {
                open();
            }
        }
    }

    public void open() {
        isOpened = true;
        if (listenner != null) {
            listenner.onSideslipViewChanged(SideslipView.this, isOpened);
        }
        int leftWidth = leftView.getMeasuredWidth();
        helper.smoothSlideViewTo(leftView, -rightWidth, 0);
        helper.smoothSlideViewTo(rightView, leftWidth - rightWidth, 0);
        ViewCompat.postInvalidateOnAnimation(SideslipView.this);
    }

    public void close() {
        isOpened = false;
        if (listenner != null) {
            listenner.onSideslipViewChanged(SideslipView.this, isOpened);
        }
        helper.smoothSlideViewTo(leftView, 0, 0);
        helper.smoothSlideViewTo(rightView, leftView.getMeasuredWidth(), 0);
        ViewCompat.postInvalidateOnAnimation(SideslipView.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (helper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SideslipView.this);
        }
    }

    public void setOnSideslipViewListener(OnSideslipViewListener listener) {
        this.listenner = listener;
    }
    public interface OnSideslipViewListener{
        void onSideslipViewChanged(SideslipView view,boolean isOpened);
    }
}
