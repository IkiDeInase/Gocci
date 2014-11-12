package com.example.kinagafuji.gocci.data;


import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.kinagafuji.gocci.R;

public class ToukouPopup {

    public static PopupWindow newBasicPopupWindow(Context context) {
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
        window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setOutsideTouchable(true);

        return window;
    }
    /**
     * Displays like a QuickAction from the anchor view.
     *
     * @param xOffset
     * offset in the X direction
     * @param yOffset
     * offset in the Y direction
     */
    public static void showLikeQuickAction(PopupWindow window, View root, View anchor, WindowManager windowManager, int xOffset, int yOffset) {
        window.setAnimationStyle(R.style.Animations_GrowFromBottom);
        int[] location = new int[2];

        anchor.getLocationOnScreen(location);
        Rect anchorRect =
                new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
                        + anchor.getHeight());
        root.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int rootWidth = root.getMeasuredWidth();
        int rootHeight = root.getMeasuredHeight();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
        int yPos = anchorRect.top - rootHeight + yOffset;
        Log.d("あんカーレクト", String.valueOf(anchorRect.top));
        Log.d("ルートヘイト", String.valueOf(rootHeight));
        Log.d("わいオフセット", String.valueOf(yOffset));
        window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }
}
