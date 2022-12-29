package com.kyee.iwis.nana;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class LoginListenerView extends LinearLayout {
    private static final String TAG = "LoginListenerView";
    private boolean isIntercept;
    private LoginListener loginListener;
    private int measuredHeight;

    public LoginListenerView(Context context) {
        this(context, null);
    }

    public LoginListenerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginListenerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float x = 0, y = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1) {
            Log.i(TAG, "onInterceptTouchEvent: 多指拦截");
            return isIntercept;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = ev.getX();
                y = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onInterceptTouchEvent: Action_up");
                measuredHeight = getRootView().findViewById(R.id.footer).getMeasuredHeight();
                if (Math.abs(ev.getX() - x) < 20
                        && Math.abs(ev.getY() - y) < 20
                        && (getHeight() - ev.getY()) > measuredHeight) {
                    if (loginListener != null && isIntercept) {
                        loginListener.showLoginView();
                    }
                    // 拦截子view的点击事件
                    return isIntercept;
                }

            default:
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    interface LoginListener {
        void showLoginView();
    }

    /**
     * 是否拦截点击事件
     *
     * @param isIntercept 是否拦截
     */
    public void setIntercept(boolean isIntercept) {
        this.isIntercept = isIntercept;
    }
}
