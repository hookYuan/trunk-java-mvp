package com.yuan.base.widget.title.titlebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yuan.base.R;
import com.yuan.base.tools.adapter.BaseListAdapter;
import com.yuan.base.tools.other.Kits;
import com.yuan.base.tools.other.Views;

import java.util.List;

/**
 * Created by YuanYe on 2017/8/17.
 * Title基本布局加载
 * 默认布局：左侧可放置TextView、ImageView
 * 中间支持主副标题、右侧可放置TextView、ImageView
 * 右侧支持菜单
 * 如果需要放置多个图标，可以动态添加图标
 */
public class TitleBar extends AbsTitle<TitleBar> {

    private CharSequence leftText = ""; //左侧文字
    private CharSequence centerText = "";//中间文字
    private CharSequence rightText = "";//右边文字
    private Drawable leftIcon;//左侧图标
    private Drawable rightIcon;//右侧图标

    private int leftFontColor = ContextCompat.getColor(context, R.color.colorFont33); //左侧文字颜色
    private int centerFontColor = ContextCompat.getColor(context, R.color.colorFont33);//中间文字颜色
    private int rightFontColor = ContextCompat.getColor(context, R.color.colorFont33);//右侧间文字颜色

    private float leftFontSize = 16 * context.getResources().getDisplayMetrics().scaledDensity; //左侧文字大小
    private float centerFontSize = 18 * context.getResources().getDisplayMetrics().scaledDensity; //中间文字大小
    private float rightFontSize = 16 * context.getResources().getDisplayMetrics().scaledDensity; //右侧文字大小

    private boolean leftClickFinish;//点击左侧图标返回

    protected TextView leftTextView;//左侧
    protected TextView titleTextView;//主标题
    protected TextView subtitleTextView;//副标题
    protected TextView rightTextView;//右侧

    private PopupWindow popupWindowMenu;//弹窗菜单

    public TitleBar(Context _context, @Nullable AttributeSet attrs) {
        super(_context, attrs);
        this.context = _context;
        obtainAttributes(_context, attrs);
    }


    public TitleBar(Context _context) {
        super(_context);
        this.context = _context;
        drawTitle();
    }

    /**
     * 绑定自定义属性
     *
     * @param context
     */
    public void obtainAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        leftText = ta.getText(R.styleable.TitleBar_leftText);
        centerText = ta.getText(R.styleable.TitleBar_centerText);
        rightText = ta.getText(R.styleable.TitleBar_rightText);
        leftIcon = ta.getDrawable(R.styleable.TitleBar_leftDrawable);
        rightIcon = ta.getDrawable(R.styleable.TitleBar_rightDrawable);
        leftClickFinish = ta.getBoolean(R.styleable.TitleBar_leftClickFinish, false);
        leftFontColor = ta.getColor(R.styleable.TitleBar_leftTextColor, ContextCompat.getColor(context, R.color.colorFont33));
        centerFontColor = ta.getColor(R.styleable.TitleBar_centerTextColor, ContextCompat.getColor(context, R.color.colorFont33));
        rightFontColor = ta.getColor(R.styleable.TitleBar_rightTextColor, ContextCompat.getColor(context, R.color.colorFont33));
        leftFontSize = ta.getDimension(R.styleable.TitleBar_leftTextSize, 16 * context.getResources().getDisplayMetrics().scaledDensity);
        centerFontSize = ta.getDimension(R.styleable.TitleBar_centerTextSize, 18 * context.getResources().getDisplayMetrics().scaledDensity);
        rightFontSize = ta.getDimension(R.styleable.TitleBar_rightTextSize, 16 * context.getResources().getDisplayMetrics().scaledDensity);
        ta.recycle();
        drawTitle();
    }


    /**
     * 在Abs布局添加默认需要添加的内容
     */
    private void drawTitle() {
        if (leftTextView == null) {
            leftTextView = new TextView(context);
            leftTextView.setPadding((int) (16 * context.getResources()
                    .getDisplayMetrics().scaledDensity), 0, 0, 0);
            addLeftView(leftTextView);
        }
        setLeftTextColor(leftFontColor);
        setLeftTextSize(leftFontSize);
        setLeftText(leftText);
        setLeftIcon(leftIcon);

        if (titleTextView == null) {
            View view = Views.inflate(context, R.layout.title_center);
            titleTextView = Views.find(view, R.id.tv_title);
            subtitleTextView = Views.find(view, R.id.tv_subtitle);
            subtitleTextView.setVisibility(GONE);
            addCenterView(view);
        }
        setTitleTextColor(centerFontColor);
        setTitleTextSize(centerFontSize);
        setTitleText(centerText);

        if (rightTextView == null) {
            rightTextView = new TextView(context);
            rightTextView.setPadding(0, 0,
                    (int) (16 * context.getResources().getDisplayMetrics().scaledDensity), 0);
            addRightView(rightTextView);
        }
        setRightTextColor(rightFontColor);
        setRightTextSize(rightFontSize);
        setRightIcon(rightIcon);
        setRightText(rightText);

        if (leftClickFinish) setLeftClickFinish();
    }


    /**
     * ------------------------------------左侧内容设置----------------------------------------
     **/
    public TitleBar setLeftText(CharSequence text) {
        leftTextView.setText(text);
        return this;
    }

    public TitleBar setLeftIcon(Drawable icon) {
        if (icon != null) {
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight()); //设置边界
            leftTextView.setCompoundDrawables(icon, null, null, null);//画在左边
        }
        return this;
    }

    public TitleBar setLeftIcon(@DrawableRes int drawableId) {
        Drawable icon = getResources().getDrawable(drawableId);
        setLeftIcon(icon);
        return this;
    }

    public TitleBar setLeftOnClickListener(OnClickListener listener) {
        if (leftTextView != null && listener != null) leftTextView.setOnClickListener(listener);
        return this;
    }

    public TitleBar setLeftClickFinish() {
        setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                if (activity != null)
                    activity.finish();
            }
        });
        return this;
    }

    /**
     * ------------------------------------中间toolbar按钮设置----------------------------------------
     **/
    public TitleBar setTitleText(CharSequence text) {
        if (titleTextView != null && text != null) {
            titleTextView.setText(text);
        }
        return this;
    }

    //设置二级标题
    public TitleBar setSubtitleText(CharSequence text) {
        if (subtitleTextView != null && text != null) {
            subtitleTextView.setText(text);
            subtitleTextView.setVisibility(VISIBLE);
        }
        return this;
    }

    /**
     * ------------------------------------右侧toolbar按钮设置----------------------------------------
     **/
    public TitleBar setRightIcon(Drawable icon) {
        if (rightTextView != null && icon != null) {
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight()); //设置边界
            rightTextView.setCompoundDrawables(icon, null, null, null);//画在左边
        }
        return this;
    }

    public TitleBar setRightIcon(@DrawableRes int drawableId) {
        Drawable icon = getResources().getDrawable(drawableId);
        return setRightIcon(icon);
    }

    public TitleBar setRightText(CharSequence text) {
        rightTextView.setText(text);
        return this;
    }

    public TitleBar setRightOnClickListener(OnClickListener listener) {
        if (listener != null) {
            rightTextView.setOnClickListener(listener);
        }
        return this;
    }

    public TitleBar setRightClickEnable(boolean clickAble) {
        rightTextView.setEnabled(clickAble);
        return this;
    }

    public TitleBar setRightAsButton(@DrawableRes int res) {
        rightTextView.setBackgroundResource(res);
        int left = Kits.Dimens.dpToPxInt(context, 8);
        int top = Kits.Dimens.dpToPxInt(context, 4);
        int right = Kits.Dimens.dpToPxInt(context, 8);
        int bottom = Kits.Dimens.dpToPxInt(context, 4);
        rightTextView.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * ------------------------------------右侧toolbar的menu菜单按钮设置----------------------------------------
     **/
    public TitleBar setRightMenu(final List<String> popupData, final OnMenuItemClickListener listener) {
        setRightOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopMenu(rightTextView, popupData, listener);
            }
        });
        return this;
    }

    /**
     * @param view
     * @param popupData
     * @param listener
     */
    private void showPopMenu(View view, List<String> popupData, final OnMenuItemClickListener listener) {
        if (popupWindowMenu != null && popupWindowMenu.isShowing()) {
            //关闭popupWindow
            popupWindowMenu.dismiss();
        } else {
            final View popupView = LayoutInflater.from(context).inflate(R.layout.title_menu, null);
            popupWindowMenu = new PopupWindow(popupView, ListPopupWindow.WRAP_CONTENT, ListPopupWindow.WRAP_CONTENT, true);

            //设置弹出的popupWindow不遮挡软键盘
            popupWindowMenu.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindowMenu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            //设置点击外部让popupWindow消失
            popupWindowMenu.setFocusable(true);//可以试试设为false的结果
            popupWindowMenu.setOutsideTouchable(true); //点击外部消失

            //必须设置的选项
            popupWindowMenu.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent));
            popupWindowMenu.setTouchInterceptor(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                    // 这里如果返回true的话，touch事件将被拦截
                    // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                }
            });
            //将window视图显示在点击按钮下面(向上偏移20像素)
            popupWindowMenu.showAsDropDown(view, 0, 0);
            ListView listView = (ListView) popupView.findViewById(R.id.pop_listView);
            listView.setAdapter(new BaseListAdapter<String>(popupData, R.layout.title_menu_item) {
                @Override
                public void bindView(ViewHolder holder, String obj) {
                    holder.setText(R.id.tv_item_content, obj);
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    listener.onItemClick(position);
                    popupWindowMenu.dismiss();
                }
            });
        }
    }

    /**
     * -------------------------------------设置字体颜色---------------------------------------------
     **/
    public TitleBar setLeftTextColor(@ColorInt int textColor) {
        if (leftTextView != null && textColor != 0)
            leftTextView.setTextColor(textColor);
        return this;
    }

    public TitleBar setTitleTextColor(@ColorInt int textColor) {
        if (titleTextView != null && textColor != 0)
            titleTextView.setTextColor(textColor);
        return this;
    }

    public TitleBar setRightTextColor(@ColorInt int textColor) {
        if (rightTextView != null && textColor != 0)
            rightTextView.setTextColor(textColor);
        return this;
    }

    public TitleBar setTextColor(@ColorInt int textColor) {
        setLeftTextColor(textColor);
        setTitleTextColor(textColor);
        setRightTextColor(textColor);
        return this;
    }

    /**
     * -------------------------------------设置字体的大小---------------------------------------------------
     */
    public TitleBar setLeftTextSize(float size) {
        if (leftTextView != null && size != 0)
            leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        return this;
    }

    public TitleBar setTitleTextSize(float size) {
        if (titleTextView != null && size != 0)
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        return this;
    }

    public TitleBar setRightTextSize(float size) {
        if (rightTextView != null && size != 0)
            rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        return this;
    }


    public interface OnMenuItemClickListener {
        void onItemClick(int position);
    }
}
