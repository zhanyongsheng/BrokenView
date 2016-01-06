package com.zys.brokenview;

import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class BrokenTouchListener implements View.OnTouchListener  {

    private BrokenAnimator brokenAnim;

    private BrokenView brokenView;
    private BrokenConfig config;
    private BrokenTouchListener(Builder builder) {
        brokenView = builder.brokenView;
        config = builder.config;
    }
    public static class Builder {
        private BrokenConfig config;
        private BrokenView brokenView;
        public Builder(BrokenView brokenView) {
            this.brokenView = brokenView;
            config = new BrokenConfig();
            config.complexity = 12;
            config.breakDuration = 700;
            config.fallDuration = 2000;
            config.paint = null;
        }

        public Builder setComplexity(int complexity) {
            if(complexity < 6)
                complexity = 6;
            else if(complexity > 20)
                complexity = 20;
            config.complexity = complexity;
            return this;
        }
        public Builder setBreakDuration(int breakDuration) {
            if(breakDuration < 200)
                breakDuration = 200;
            config.breakDuration = breakDuration;
            return this;
        }
        public Builder setFallDuration(int fallDuration) {
            if(fallDuration < 200)
                fallDuration = 200;
            config.fallDuration = fallDuration;
            return this;
        }
        public Builder setPaint(Paint paint) {
            config.paint = paint;
            return this;
        }
        public BrokenTouchListener build() {
            return new BrokenTouchListener(this);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(brokenView.isEnable()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Point point = new Point((int)event.getRawX(),(int)event.getRawY());
                    brokenAnim = brokenView.getAnimator(v);
                    if(brokenAnim == null)
                        brokenAnim = brokenView.createAnimator(v, point, config);
                    if(brokenAnim == null)
                        return true;
                    if (!brokenAnim.isStarted()) {
                        brokenAnim.start();
                        brokenView.onBrokenStart(v);
                    } else if (brokenAnim.doReverse()) {
                        brokenView.onBrokenRestart(v);
                    }
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    brokenAnim = brokenView.getAnimator(v);
                    if (brokenAnim != null && brokenAnim.isStarted()) {
                        if(brokenAnim.doReverse())
                            brokenView.onBrokenCancel(v);
                    }
                    break;
            }
            return true;
        }
        else
            return false;
    }
}
