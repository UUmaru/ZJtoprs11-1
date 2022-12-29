package com.kyee.iwis.nana.utils;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class NavigationBarUtil {

    public static void hideNavigationBar(Window window) {
        View decorView = window.getDecorView();
        hideNavigationBar(decorView);
    }

    public static void hideNavigationBar(View view) {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        view.setSystemUiVisibility(uiOptions);
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    view.setSystemUiVisibility(uiOptions);
                }
            }
        });
    }


    public static void showNavigationBar(Window window) {
        View decorView = window.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
            }
        });
        decorView.requestFocus();

    }

    /**
     * dialog 需要全屏的时候用，和clearFocusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     * @param window
     */
    static public void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * dialog 需要全屏的时候用，focusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     * @param window
     */
    static public void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }


    public static void showWithoutNavigationBar(Dialog dialog) {
        NavigationBarUtil.hideNavigationBar(dialog.getWindow());
        NavigationBarUtil.clearFocusNotAle(dialog.getWindow());
    }


}
