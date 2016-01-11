package com.zys.brokenview;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Region;
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
        public Builder setCircleRiftsRadius(int radius) {
            if(radius < 20 && radius != 0)
                radius = 20;
            config.circleRiftsRadius = radius;
            return this;
        }
        /**
         * Be sure the childView in region doesn't intercept any touch event,
         * you can make onTouch-event return false and set clickable to false.
         *
         * @param region The region where can enable break-effect.
         *
         * @return the BrokenTouchListener Builder.
         */
        public Builder setEnableArea(Region region) {
            config.region = region;
            config.childView = null;
            return this;
        }
        /**
         * Be sure the childView doesn't intercept any touch event,
         * you can make onTouch-event return false and set clickable to false.
         *
         * @param childView The view can enable break-effect
         *
         * @return the BrokenTouchListener Builder.
         */
        public Builder setEnableArea(View childView) {
            config.childView = childView;
            config.region = null;
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
                    if(config.childView != null){
                        config.region = new Region(config.childView.getLeft(),
                                config.childView.getTop(),
                                config.childView.getRight(),
                                config.childView.getBottom());
                    }
                    if(config.region == null || config.region.contains((int)event.getX(),(int)event.getY())) {
                        Point point = new Point((int) event.getRawX(), (int) event.getRawY());
                        brokenAnim = brokenView.getAnimator(v);
                        if (brokenAnim == null)
                            brokenAnim = brokenView.createAnimator(v, point, config);
                        if (brokenAnim == null)
                            return true;
                        if (!brokenAnim.isStarted()) {
                            brokenAnim.start();
                            brokenView.onBrokenStart(v);
                        } else if (brokenAnim.doReverse()) {
                            brokenView.onBrokenRestart(v);
                        }
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    else
                        return false;
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
