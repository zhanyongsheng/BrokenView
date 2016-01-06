package com.zys.brokenview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.util.Random;

public class Utils {

    private Utils() {}
    static int screenWidth;
    static int screenHeight;
    private static Random random = new Random();
    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final Canvas mCanvas = new Canvas();

    static int dp2px(int dp) {
        return Math.round(dp * DENSITY);
    }

    static Bitmap convertViewToBitmap(View view) {
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_4444, 2);
        if (bitmap != null) {
            mCanvas.setBitmap(bitmap);
            mCanvas.translate(-view.getScrollX(), -view.getScrollY());
            view.draw(mCanvas);
            mCanvas.setBitmap(null);
        }
        return bitmap;
    }

    static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        while(retryCount-- > 0) {
            try {
                return Bitmap.createBitmap(width, height, config);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                System.gc();
            }
        }
        return null;
    }

    static int nextInt(int a, int b){
        return Math.min(a,b) + random.nextInt(Math.abs(a - b));
    }

    static int nextInt(int a){
        return random.nextInt(a);
    }

    static float nextFloat(float a, float b){
        return Math.min(a,b) + random.nextFloat() * Math.abs(a - b);
    }

    static float nextFloat(float a){
        return random.nextFloat() * a;
    }

    static boolean nextBoolean(){
        return random.nextBoolean();
    }

}
