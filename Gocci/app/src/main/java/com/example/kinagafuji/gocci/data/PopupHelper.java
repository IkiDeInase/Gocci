package com.example.kinagafuji.gocci.data;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.kinagafuji.gocci.R;


public class PopupHelper {

    public static PopupWindow newBasicPopupWindow(Context context, int X, int Y) {
        final PopupWindow window = new PopupWindow(context);

        // when a touch even happens outside of the window
        // make the window go away
        window.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    window.dismiss();
                    return true;
                }
                return false;
            }
        });

        window.setWidth(X);
        window.setHeight(Y);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setOutsideTouchable(false);



        return window;
    }

    /**
     * Displays like a QuickAction from the anchor view.
     *
     * @param xOffset
     *            offset in the X direction
     * @param yOffset
     *            offset in the Y direction
     */
    public static void showLikeQuickAction(PopupWindow window, View root, View anchor, WindowManager windowManager, int xOffset, int yOffset) {

        window.setAnimationStyle(R.style.Animations_GrowFromBottom);

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);

        root.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int rootWidth = root.getMeasuredWidth();
        int rootHeight = root.getMeasuredHeight();

        window.showAtLocation(anchor, Gravity.CENTER, rootWidth, rootHeight);


    }

}
