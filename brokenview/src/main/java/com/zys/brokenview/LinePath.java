package com.zys.brokenview;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;

class LinePath extends Path {

    private Point endPoint;
    private float startLength;
    private boolean straight;
    public ArrayList<Point> points;

    public LinePath(){
        super();
        points = new ArrayList<>();
        startLength = -1;
        endPoint = new Point();
    }

    public LinePath(LinePath p){
        super(p);
        points = (ArrayList)p.points.clone();
        startLength = p.getStartLength();
        endPoint = new Point(p.getEndPoint());
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
    public void setEndPoint(int endX,int endY) {
        endPoint.set(endX,endY);
    }
    public Point getEndPoint() {
        return endPoint;
    }
    public int getEndX() {
        return endPoint.x;
    }
    public int getEndY() {
        return endPoint.y;
    }
    public boolean isStraight() {
        return straight;
    }
    public void setStraight(boolean straight) {
        this.straight = straight;
    }
    public float getStartLength() {
        return startLength;
    }
    public void setStartLength(float startLength) {
        this.startLength = startLength;
    }

    public void lineToEnd(){
        lineTo(endPoint.x,endPoint.y);
    }
    public void obtainEndPoint(int angleRandom,int[] angleBase,Rect r){
        float gradient = -(float) Math.tan(Math.toRadians(angleRandom));
        int endX = 0, endY = 0;
        if (angleRandom >= 0 && angleRandom < 90) {
            if (angleRandom < angleBase[0]) {
                endX = r.right;
                endY = (int)(endX * gradient);
            } else if (angleRandom > angleBase[0]) {
                endY = r.top;
                endX = (int) (endY / gradient);
            } else if (angleRandom == angleBase[0]) {
                endY = r.top;
                endX = r.right;
            }
        } else if (angleRandom > 90 && angleRandom <= 180) {
            if (180 - angleRandom < angleBase[1]) {
                endX = r.left;
                endY = (int) (endX * gradient);
            } else if (180 - angleRandom > angleBase[1]) {
                endY = r.top;
                endX = (int) (endY / gradient);
            } else if (180 - angleRandom == angleBase[1]) {
                endY = r.top;
                endX = r.left;
            }
        } else if (angleRandom > 180 && angleRandom < 270) {
            if (angleRandom - 180 < angleBase[2]) {
                endX = r.left;
                endY = (int) (endX * gradient);
            } else if (angleRandom - 180 > angleBase[2]) {
                endY = r.bottom;
                endX = (int) (endY / gradient);
            } else if (angleRandom - 180 == angleBase[2]) {
                endY = r.bottom;
                endX = r.left;
            }
        } else if (angleRandom > 270 && angleRandom < 360) {
            if (360 - angleRandom < angleBase[3]) {
                endX = r.right;
                endY = (int) (endX * gradient);
            } else if (360 - angleRandom > angleBase[3]) {
                endY = r.bottom;
                endX = (int) (endY / gradient);
            } else if (360 - angleRandom == angleBase[3]) {
                endY = r.bottom;
                endX = r.right;
            }
        }
        else if(angleRandom == 90) {
            endX = 0;
            endY = r.top;
        }
        else if(angleRandom == 270) {
            endX = 0;
            endY = r.bottom;
        }
        endPoint.set(endX,endY);
    }
}
