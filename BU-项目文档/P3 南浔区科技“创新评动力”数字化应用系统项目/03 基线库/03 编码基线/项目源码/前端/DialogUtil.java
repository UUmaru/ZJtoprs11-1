package com.kyee.iwis.nana.utils;

import android.view.View;

public class DialogUtil {
    public String getDayOfWeek(String str) {
        switch (str) {
            case "1":
                return "一";
            case "2":
                return "二";
            case "3":
                return "三";
            case "4":
                return "四";
            case "5":
                return "五";
            case "6":
                return "六";
            case "7":
                return "日";
        }
        return null;
    }

    public View setIsShow(View view, boolean isShow) {
        if (isShow) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        return view;
    }

    public String TimeDefault(String str) {
        if (str == null) {
            return null;
        }
        switch (str) {
            case "1":
                return "周一";
            case "2":
                return "周二";
            case "3":
                return "周三";
            case "4":
                return "周四";
            case "5":
                return "周五";
            case "6":
                return "周六";
            case "7":
                return "周日";
            case "+1":
                return "下周一";
            case "+2":
                return "下周二";
            case "+3":
                return "下周三";
            case "+4":
                return "下周四";
            case "+5":
                return "下周五";
            case "+6":
                return "下周六";
            case "+7":
                return "下周日";
        }
        return null;
    }

    public String isDay(String str) {
        switch (str) {
            case "周一":
                return "一";
            case "周二":
                return "二";
            case "周三":
                return "三";
            case "周四":
                return "四";
            case "周五":
                return "五";
            case "周六":
                return "六";
            case "周日":
                return "日";
        }
        return null;
    }

}
