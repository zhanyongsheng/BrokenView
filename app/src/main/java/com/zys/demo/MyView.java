package com.zys.demo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {
    private Paint paint;
    private final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    public MyView(Context context) {
        this(context, null);
    }
    public MyView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }
    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // If don't close hardware acceleration int Android 4.4,
        // the ViewGroup will not clip canvas for child when create bitmap from it(R.id.demo_parent)
        // therefore the canvas has wrong width and height.
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffff0000);
        canvas.drawRect(dp2px(16),dp2px(16),dp2px(50),dp2px(50),paint);
        paint.setColor(0xffcc9900);
        canvas.drawRect(dp2px(100),dp2px(16),dp2px(133),dp2px(50),paint);
        paint.setColor(0xff00ff00);
        canvas.drawRect(dp2px(16),dp2px(106),dp2px(50),dp2px(140),paint);
        paint.setColor(0xff6600ff);
        canvas.drawRect(dp2px(100),dp2px(106),dp2px(133),dp2px(140),paint);

        canvas.translate(canvasWidth / 2, canvasHeight / 2);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(dp2px(18));
        paint.setColor(0xffffff00);
        canvas.drawText("Custom View",-dp2px(53),dp2px(3),paint);
    }
    private int dp2px(int dp) {
        return Math.round(dp * DENSITY);
    }
}
