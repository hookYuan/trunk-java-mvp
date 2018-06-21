package com.yuan.base.tools.system;

/**
 * Created by YuanYe on 2018/4/13.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 状态栏透明
 * Created by SCWANG on 2016/10/26.
 */
@SuppressWarnings("unused")
public class StatusBarUtil {

    private static int DEFAULT_COLOR = 0; //默认颜色
    private static float DEFAULT_ALPHA = 0;//默认透明度 Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 0.2f : 0.3f;

    //设置状态栏颜色
    public static void immersive(Activity activity, boolean isOverlay) {
        immersive(activity, isOverlay, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    //设置状态栏指定颜色和透明度
    public static void immersive(Activity activity, boolean isOverlay, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        immersive(activity.getWindow(), isOverlay, color, alpha);
    }

    //设置状态栏颜色
    public static void immersive(Activity activity, boolean isOverlay, int color) {
        immersive(activity.getWindow(), isOverlay, color, 1f);
    }

    public static void immersive(Window window, boolean isOverlay) {
        immersive(window, isOverlay, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public static void immersive(Window window, boolean isOverlay, int color) {
        immersive(window, isOverlay, color, 1f);
    }

    public static void immersive(Window window, boolean isOverlay, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mixtureColor(color, alpha));

            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            if (isOverlay) {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        } else if (Build.VERSION.SDK_INT >= 16) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            if (isOverlay) {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }

    /**
     * *********************************修改字体颜色************************************************
     */

    //设置状态栏文字是否显示黑色
    public static void darkMode(Activity activity, boolean dark) {
        if (isFlyme4Later()) {
            darkModeForFlyme4(activity.getWindow(), dark);
        } else if (isMIUI6Later()) {
            darkModeForMIUI6(activity.getWindow(), dark);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(activity.getWindow(), dark);
        }
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    public static void darkMode(Activity activity) {
        darkMode(activity.getWindow(), DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public static void darkMode(Activity activity, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        darkMode(activity.getWindow(), color, alpha);
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    public static void darkMode(Window window, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (isFlyme4Later()) {
            darkModeForFlyme4(window, true);
        } else if (isMIUI6Later()) {
            darkModeForMIUI6(window, true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(window, true);
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        }
    }

    //------------------------->

    /**
     * android 6.0设置字体颜色
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private static void darkModeForM(Window window, boolean dark) {
        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        if (dark) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * 设置Flyme4+的darkMode,darkMode时候字体颜色及icon变黑
     * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
     */
    public static boolean darkModeForFlyme4(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams e = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(e);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(e, value);
                window.setAttributes(e);
                result = true;
            } catch (Exception var8) {
                Log.e("StatusBar", "darkIcon: failed");
            }
        }

        return result;
    }

    /**
     * 设置MIUI6+的状态栏是否为darkMode,darkMode时候字体颜色及icon变黑
     * http://dev.xiaomi.com/doc/p=4769/
     */
    public static boolean darkModeForMIUI6(Window window, boolean darkmode) {
        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否Flyme4以上
     */
    public static boolean isFlyme4Later() {
        return Build.FINGERPRINT.contains("Flyme_OS_4")
                || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                || Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find();
    }

    /**
     * 判断是否为MIUI6以上
     */
    public static boolean isMIUI6Later() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method mtd = clz.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            return false;
        }
    }
    //</edior-fold>


    /**
     * 创建假的透明栏
     */
    public static void setTranslucentView(ViewGroup container, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (Build.VERSION.SDK_INT >= 19) {
            int mixtureColor = mixtureColor(color, alpha);
            View translucentView = container.findViewById(android.R.id.custom);
            if (translucentView == null && mixtureColor != 0) {
                translucentView = new View(container.getContext());
                translucentView.setId(android.R.id.custom);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext()));
                container.addView(translucentView, lp);
            }
            if (translucentView != null) {
                translucentView.setBackgroundColor(mixtureColor);
            }
        }
    }

    public static int mixtureColor(int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        int a = (color & 0xff000000) == 0 ? 0xff : color >>> 24;
        return (color & 0x00ffffff) | (((int) (a * alpha)) << 24);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 24;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    result, Resources.getSystem().getDisplayMetrics());
        }
        return result;
    }

    /**
     * 更改View的高度
     */
    public static void setPaddingHeight(View view) {
        setPaddingHeight(view, 0);
    }

    public static void setPaddingHeight(View view, int height) {
        Context context = view.getContext();
        if (Build.VERSION.SDK_INT >= 16) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height >= 0) {
                lp.height += getStatusBarHeight(context);//增高
            }
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context) + height,
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void changeHeight(View view) {
        Context context = view.getContext();
        if (Build.VERSION.SDK_INT >= 16) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height >= 0) {
                lp.height += getStatusBarHeight(context);//增高
            }
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lp.height));
        }
    }
}