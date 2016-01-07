package com.zys.brokenview;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.Arrays;

class BrokenAnimator extends ValueAnimator{
    final int SEGMENT = 66;
    static final int STAGE_BREAKING = 1;
    static final int STAGE_FALLING = 2;
    static final int STAGE_EARLYEND = 3;
    static final int STAGE_TEST = 0;
    private int stage = STAGE_BREAKING;

    private BrokenView mBrokenView;
    private View mView;
    private BrokenConfig mConfig;
    private Bitmap mBitmap;
    private Point mTouchPoint;

    private Paint onDrawPaint;
    private Path onDrawPath;
    private PathMeasure onDrawPM;
    private boolean canReverse = false;
    private boolean bPress = true;
    private LinePath[] lineRifts;
    private Path[] circleRifts;
    private int[] circleWidth;
    private Piece[] pieces;
    private int offsetX;
    private int offsetY;
    private ArrayList<Path> pathArray;

    public BrokenAnimator(BrokenView brokenView,View view,Bitmap bitmap,Point point,BrokenConfig config){
        mBrokenView = brokenView;
        mView = view;
        mBitmap = bitmap;
        mTouchPoint = point;
        mConfig = config;
        
        pathArray = new ArrayList<>();
        onDrawPath = new Path();
        onDrawPM = new PathMeasure();
        lineRifts = new LinePath[mConfig.complexity];
        circleRifts = new Path[mConfig.complexity];
        circleWidth = new int[mConfig.complexity];

        Rect r = new Rect();
        mView.getGlobalVisibleRect(r);
        offsetX = mTouchPoint.x - r.left;
        offsetY = mTouchPoint.y - r.top;
        r.offset(-mTouchPoint.x,-mTouchPoint.y);

        Rect bvR = new Rect();
        mBrokenView.getGlobalVisibleRect(bvR);
        mTouchPoint.x -= bvR.left;
        mTouchPoint.y -= bvR.top;

        buildBrokenLines(r);
        buildBrokenAreas(r);
        buildPieces();
        buildPaintShader();
        warpBrokenLines();

        setFloatValues(0f,1f);
        setInterpolator(new AccelerateInterpolator(2.0f));
        setDuration(mConfig.breakDuration);
    }

    private void buildBrokenLines(Rect r) {
        LinePath[] baseLines = new LinePath[mConfig.complexity];
        buildBaselines(baseLines, r);
        PathMeasure pmTemp = new PathMeasure();
        for (int i = 0; i < mConfig.complexity; i++) {
            lineRifts[i] = new LinePath();
            lineRifts[i].moveTo(0, 0);
            lineRifts[i].setEndPoint(baseLines[i].getEndPoint());
            pmTemp.setPath(baseLines[i], false);
            float length = pmTemp.getLength();
            if (length > Utils.dp2px(SEGMENT)) {
                lineRifts[i].setStraight(false);
                float[] pos = new float[2];
                pmTemp.getPosTan(Utils.dp2px(SEGMENT), pos, null);
                lineRifts[i].lineTo(pos[0], pos[1]);
                lineRifts[i].points.add(new Point((int)pos[0], (int)pos[1]));
                int xRandom, yRandom;
                int step = Utils.dp2px(SEGMENT + 25);
                do{
                    pmTemp.getPosTan(step, pos, null);
                    xRandom = (int) (pos[0] + Utils.nextInt(-Utils.dp2px(3),Utils.dp2px(2)));
                    yRandom = (int) (pos[1] + Utils.nextInt(-Utils.dp2px(2),Utils.dp2px(3)));
                    lineRifts[i].lineTo(xRandom, yRandom);
                    lineRifts[i].points.add(new Point(xRandom, yRandom));
                    step += Utils.dp2px(SEGMENT + 4);
                } while (step < length);
                lineRifts[i].lineToEnd();
            } else {
                lineRifts[i] = baseLines[i];
                lineRifts[i].setStraight(true);
            }
            lineRifts[i].points.add(lineRifts[i].getEndPoint());
        }
    }

    private void buildBaselines(LinePath[] baseLines,Rect r){
        for(int i = 0; i < mConfig.complexity; i++){
            baseLines[i] = new LinePath();
            baseLines[i].moveTo(0,0);
        }
        buildFirstLine(baseLines[0], r);

        int angle = (int)(Math.toDegrees(Math.atan((float)(-baseLines[0].getEndY()) / baseLines[0].getEndX())));
        int angleRandom = angle;
        int[] angleBase = new int[4];
        angleBase[0] = (int)(Math.toDegrees(Math.atan((float)(-r.top) / (r.right))));
        angleBase[1] = (int)(Math.toDegrees(Math.atan((float)(-r.top) / (-r.left))));
        angleBase[2] = (int)(Math.toDegrees(Math.atan((float)(r.bottom) / (-r.left))));
        angleBase[3] = (int)(Math.toDegrees(Math.atan((float)(r.bottom) / (r.right))));

        if(baseLines[0].getEndX() < 0)//2-quadrant,3-quadrant
            angle += 180;
        else if(baseLines[0].getEndX() > 0 && baseLines[0].getEndY() > 0)//4-quadrant
            angle += 360;
        int offset = 360 / mConfig.complexity / 3;
        for(int i = 1; i<mConfig.complexity; i++) {
            angle = angle + 360 / mConfig.complexity;
            if (angle >= 360)
                angle -= 360;
            angleRandom = angle + Utils.nextInt(-offset, offset);
            if (angleRandom >= 360)
                angleRandom -= 360;
            else if (angleRandom < 0)
                angleRandom += 360;
            baseLines[i].obtainEndPoint(angleRandom,angleBase,r);
            baseLines[i].lineToEnd();
        }
    }

    private void buildFirstLine(LinePath path, Rect r){
        int[] range=new int[]{-r.left,-r.top,r.right,r.bottom};
        int max = -1;
        int maxId = 0;
        for(int i = 0; i < 4; i++) {
            if(range[i] > max) {
                max = range[i];
                maxId = i;
            }
        }
        switch (maxId){
            case 0:
                path.setEndPoint(r.left, Utils.nextInt(r.height()) + r.top);
                break;
            case 1:
                path.setEndPoint(Utils.nextInt(r.width()) + r.left, r.top);
                break;
            case 2:
                path.setEndPoint(r.right, Utils.nextInt(r.height()) + r.top);
                break;
            case 3:
                path.setEndPoint(Utils.nextInt(r.width()) + r.left, r.bottom);
                break;
        }
        path.lineToEnd();
    }

    private void buildBrokenAreas(Rect r){
        float linkLen = 0;
        int repeat = 0;
        PathMeasure pmNow = new PathMeasure();
        PathMeasure pmPre = new PathMeasure();
        for(int i = 0; i < mConfig.complexity; i++) {
            if (repeat > 0) {
                repeat--;
            } else {
                linkLen = Utils.nextInt(Utils.dp2px(SEGMENT - 15),Utils.dp2px(SEGMENT));
                repeat = Utils.nextInt(3);
            }
            int iPre = (i - 1) < 0 ? mConfig.complexity - 1 : i - 1;
            pmNow.setPath(lineRifts[i],false);
            pmPre.setPath(lineRifts[iPre], false);

            if (pmNow.getLength() > linkLen && pmPre.getLength() > linkLen) {

                float[] pointNow = new float[2];
                float[] pointPre = new float[2];
                circleWidth[i] = Utils.nextInt(Utils.dp2px(1)) + 1;
                circleRifts[i] = new Path();
                pmNow.getPosTan(linkLen, pointNow, null);
                circleRifts[i].moveTo(pointNow[0], pointNow[1]);
                pmPre.getPosTan(linkLen, pointPre, null);
                circleRifts[i].lineTo(pointPre[0], pointPre[1]);

                if (linkLen > lineRifts[i].getStartLength())
                    lineRifts[i].setStartLength(linkLen);
                if (linkLen > lineRifts[iPre].getStartLength())
                    lineRifts[iPre].setStartLength(linkLen);

                Path pathArea = new Path();
                pmPre.getSegment(linkLen, pmPre.getLength(), pathArea, true);
                pathArea.rLineTo(0, 0);
                drawBorder(pathArea,lineRifts[iPre].getEndPoint(),
                        lineRifts[i].points.get(lineRifts[i].points.size()-1),r);
                for (int j =  lineRifts[i].points.size() - 2; j >= 0; j--)
                    pathArea.lineTo(lineRifts[i].points.get(j).x, lineRifts[i].points.get(j).y);
                pathArea.lineTo(pointNow[0], pointNow[1]);
                pathArea.lineTo(pointPre[0], pointPre[1]);
                pathArea.close();
                pathArray.add(pathArea);

                pathArea = new Path();
                pathArea.moveTo(0,0);
                pathArea.lineTo(pointPre[0],pointPre[1]);
                pathArea.lineTo(pointNow[0],pointNow[1]);
                pathArea.close();
                pathArray.add(pathArea);
            }
            else{
                Path pathArea = new Path();
                pmPre.getSegment(0, pmPre.getLength(), pathArea, false);
                pathArea.rLineTo(0, 0);
                drawBorder(pathArea, lineRifts[iPre].getEndPoint(), lineRifts[i].points.get( lineRifts[i].points.size()-1),r);
                for (int j = lineRifts[i].points.size() - 2; j >= 0; j--)
                    pathArea.lineTo(lineRifts[i].points.get(j).x,  lineRifts[i].points.get(j).y);
                pathArea.close();
                pathArray.add(pathArea);
            }
        }
    }

    private void buildPieces(){
        pieces = new Piece[pathArray.size()];
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        for(int i = 0; i < pieces.length; i++) {
            int shadow = Utils.nextInt(Utils.dp2px(2),Utils.dp2px(9));
            Path path = pathArray.get(i);
            RectF r = new RectF();
            path.computeBounds(r, true);

            Bitmap pBitmap = Utils.createBitmapSafely((int)r.width() + shadow * 2,
                    (int)r.height() + shadow * 2, Bitmap.Config.ARGB_4444,1);
            if(pBitmap == null){
                pieces[i] = new Piece(-1, -1, null, shadow);
                continue;
            }
            pieces[i] = new Piece((int)r.left + mTouchPoint.x - shadow,
                    (int)r.top + mTouchPoint.y - shadow, pBitmap, shadow);
            Canvas canvas = new Canvas(pieces[i].bitmap);
            BitmapShader mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            matrix.reset();
            matrix.setTranslate(-r.left - offsetX + shadow, -r.top - offsetY + shadow);
            mBitmapShader.setLocalMatrix(matrix);

            paint.reset();
            Path offsetPath = new Path();
            offsetPath.addPath(path, -r.left + shadow, -r.top + shadow);

            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(shadow,0,0,0xff333333);
            canvas.drawPath(offsetPath,paint);
            paint.setShadowLayer(0,0,0,0);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.drawPath(offsetPath,paint);
            paint.setXfermode(null);

            paint.setShader(mBitmapShader);
            paint.setAlpha(0xcc);
            canvas.drawPath(offsetPath, paint);
        }
        Arrays.sort(pieces);
    }

    private void buildPaintShader(){
        if(mConfig.paint == null) {
            onDrawPaint = new Paint();
            BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Matrix matrix = new Matrix();
            matrix.setTranslate(-offsetX - 10, -offsetY - 7);
            shader.setLocalMatrix(matrix);
            ColorMatrix cMatrix = new ColorMatrix();
            cMatrix.set(new float[]{
                    2.5f, 0, 0, 0, 100,
                    0, 2.5f, 0, 0, 100,
                    0, 0, 2.5f, 0, 100,
                    0, 0, 0, 1, 0});
            onDrawPaint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
            onDrawPaint.setShader(shader);
            onDrawPaint.setStyle(Paint.Style.FILL);
        }
        else
            onDrawPaint = mConfig.paint;
    }

    private void warpBrokenLines() {
        PathMeasure pmTemp = new PathMeasure();
        for (int i = 0; i < mConfig.complexity; i++) {
            if(lineRifts[i].isStraight())
            {
                pmTemp.setPath(lineRifts[i], false);
                lineRifts[i].setStartLength(pmTemp.getLength() / 2);
                float[] pos = new float[2];
                pmTemp.getPosTan(pmTemp.getLength() / 2, pos, null);
                int xRandom = (int) (pos[0] + Utils.nextInt(-Utils.dp2px(1), Utils.dp2px(1)));
                int yRandom = (int) (pos[1] + Utils.nextInt(-Utils.dp2px(1), Utils.dp2px(1)));
                lineRifts[i].reset();
                lineRifts[i].moveTo(0,0);
                lineRifts[i].lineTo(xRandom,yRandom);
                lineRifts[i].lineToEnd();
            }
        }
    }

    public void drawBorder(Path path,Point pointStart,Point pointEnd,Rect r){
        if(pointStart.x == r.right) {
            if(pointEnd.x == r.right)
                path.lineTo(pointEnd.x, pointEnd.y);
            else if(pointEnd.y == r.top) {
                path.lineTo(r.right, r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
            else if(pointEnd.x == r.left){
                path.lineTo(r.right, r.top);
                path.lineTo(r.left, r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.bottom){
                path.lineTo(r.right, r.top);
                path.lineTo(r.left, r.top);
                path.lineTo(r.left, r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
        else if(pointStart.y == r.top) {
            if(pointEnd.y == r.top){
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.left){
                path.lineTo(r.left,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.bottom){
                path.lineTo(r.left,r.top);
                path.lineTo(r.left,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.right){
                path.lineTo(r.left,r.top);
                path.lineTo(r.left,r.bottom);
                path.lineTo(r.right,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
        else if(pointStart.x == r.left) {
            if(pointEnd.x == r.left){
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.bottom){
                path.lineTo(r.left,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.right){
                path.lineTo(r.left,r.bottom);
                path.lineTo(r.right,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.top){
                path.lineTo(r.left,r.bottom);
                path.lineTo(r.right,r.bottom);
                path.lineTo(r.right,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
        else if(pointStart.y == r.bottom) {
            if(pointEnd.y == r.bottom) {
                path.lineTo(pointEnd.x, pointEnd.y);
            }
            else if(pointEnd.x == r.right){
                path.lineTo(r.right,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.top){
                path.lineTo(r.right,r.bottom);
                path.lineTo(r.right,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.left){
                path.lineTo(r.right,r.bottom);
                path.lineTo(r.right,r.top);
                path.lineTo(r.left,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
    }

    public void setStage(int s){
        stage = s;
    }
    public int getStage(){
        return stage;
    }

    public void setFallingDuration(){
        setDuration(mConfig.fallDuration);
    }

    @Override
    public void start() {
        super.start();
        canReverse = true;
        mBrokenView.invalidate();
    }

    public boolean doReverse() {
        if(canReverse) {
            bPress = !bPress;
            reverse();
        }
        return canReverse;
    }
    public boolean draw(Canvas canvas) {

        if (!isStarted()) {
            return false;
        }

        float fraction = getAnimatedFraction();
        if(getStage() == STAGE_BREAKING) {
            canvas.save();
            canvas.translate(mTouchPoint.x, mTouchPoint.y);

            for (int i = 0; i < mConfig.complexity; i++) {
                onDrawPaint.setStyle(Paint.Style.FILL);
                onDrawPath.reset();
                onDrawPM.setPath(lineRifts[i], false);
                float pathLength = onDrawPM.getLength();
                float startLength = lineRifts[i].getStartLength();
                if(!lineRifts[i].isStraight())
                    startLength += Utils.dp2px(7);
                float drawLength = startLength + fraction * (pathLength - startLength);
                if (drawLength > pathLength)
                    drawLength = pathLength;
                onDrawPM.getSegment(0, drawLength, onDrawPath, false);
                onDrawPath.rLineTo(0, 0);
                canvas.drawPath(onDrawPath, onDrawPaint);

                if (circleRifts[i] != null && fraction > 0.1) {
                    onDrawPaint.setStyle(Paint.Style.STROKE);
                    float t = (fraction - 0.1f) * 2;
                    if(t > 1) t = 1;
                    onDrawPaint.setStrokeWidth(circleWidth[i] * t);
                    canvas.drawPath(circleRifts[i], onDrawPaint);
                }
            }
            if (fraction > 0.8 && bPress) {
                canReverse = false;
                setRepeatCount(1);
            }
            canvas.restore();
        }

        else if(getStage() == STAGE_FALLING) {
            int piecesNum = pieces.length;
            for(Piece p : pieces){
                if (p.bitmap != null && p.advance(fraction))
                    canvas.drawBitmap(p.bitmap, p.matrix, null);
                else
                    piecesNum--;
            }
            if(piecesNum == 0) {
                setStage(STAGE_EARLYEND);
                mBrokenView.onBrokenFallingEnd(mView);
            }
        }
        else if(getStage() == STAGE_TEST) {
            float t = (float)1 / pieces.length;
            int drawNum = (int)(fraction / t);
            for(int i = 0; i <= drawNum; i++) {
                if(pieces[i].bitmap != null)
                    canvas.drawBitmap(pieces[i].bitmap, pieces[i].matrix, null);
            }
        }
        mBrokenView.invalidate();
        return true;
    }
}