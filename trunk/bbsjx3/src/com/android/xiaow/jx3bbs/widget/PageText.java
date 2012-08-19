/**   
 * @Title: PageText.java
 * @Package com.android.xiaow.jx3bbs.widget
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-18 下午2:42:09
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * @ClassName: PageText
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-18 下午2:42:09
 * 
 */
public class PageText extends EditText {

    private int off; // 字符串的偏移值

    public PageText(Context context) {
        super(context);
        initialize();
    }

    public PageText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public PageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        // 不做任何处理，为了阻止长按的时候弹出上下文菜单
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("MSG", "onTouchEvent--------->");
        int action = event.getAction();
        Layout layout = getLayout();
        int line = 0;
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            line = layout.getLineForVertical(getScrollY() + (int) event.getY());
            off = layout.getOffsetForHorizontal(line, (int) event.getX());
            Selection.setSelection(getEditableText(), off);
            break;
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_UP:
            line = layout.getLineForVertical(getScrollY() + (int) event.getY());
            int curOff = layout.getOffsetForHorizontal(line, (int) event.getX());
            Selection.setSelection(getEditableText(), off, curOff);
            break;
        }
        return true;
    }

}
