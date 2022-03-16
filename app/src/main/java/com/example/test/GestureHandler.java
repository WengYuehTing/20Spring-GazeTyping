package com.example.test;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class GestureHandler implements View.OnTouchListener {

    // 长按时间界限（超过此时间则为长按）
    private static final long LONGPRESS_TIME_THRESHOLD = 300;

    // 双击事件界限（两次点击间隔在此时间以内则为双击）
    private static final long DOUBLECLICK_TIME_THRESHOLD = 300;

    // 长按手抖容忍界限（距离超过此距离则不为长按）
    private static final float LONGPRESS_SHAKE_LIMIT = 40.0f;

    // 点击手抖容忍界限（距离超过此距离则不为长按）
    private static final float PRESS_SHAKE_LIMIT = 40.0f;

    // 上下滑距离界限（超过此距离则为合法滑动）
    private static final float GESTURE_VERTICAL_THREOLD = 200.0f;
    private static final float GESTURE_LONG_VERTICAL_THREOLD = 600.0f;

    // 左右滑距离界限（超过此距离则为合法滑动）
    private static final float GESTURE_HORIZONTAL_THREOLD = 200.0f;

    // 判断上滑下滑时对左右移动的容忍界限
    private static final float GESTURE_VERTICAL_LIMIT = 200.0f;

    // 判断左滑右滑时对上下移动的容忍界限
    private static final float GESTURE_HORIZONTAL_LIMIT = 200.0f;

    private Handler handler = new Handler();

    private class Point {
        public float x;
        public float y;

        public Point() {}

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        float distance(Point p) {
            return (float)Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
        }

        float verticalDistance(Point p) {
            return Math.abs(p.y - y);
        }

        float horizontalDistance(Point p) {
            return Math.abs(p.x - x);
        }

        boolean leftOf(Point p) {
            return x < p.x;
        }

        boolean rightOf(Point p) {
            return x > p.x;
        }

        boolean above(Point p) {
            return y < p.y;
        }

        boolean under(Point p) {
            return y > p.y;
        }

        void print() {
            Log.d("YueTing", x + "\t" + y);
        }
    }

    private Point startPoint = new Point();
    private Point endPoint = new Point();

    private class LongPressRunnable implements Runnable {
        public boolean pressing = false;
        public boolean added = false;
        public long startTime = 0;

        @Override
        public void run() {
            pressing = true;
            Log.d("YueTing", "Long Press");
            CommandNetwork.getInstance().send("lp");
        }

        public void end() {
            String exeTime = String.valueOf((System.currentTimeMillis() - startTime));
        }
    }

    private LongPressRunnable longPressRunnable = new LongPressRunnable();

    private class SingleClickRunnable implements Runnable {
        public boolean finished;

        @Override
        public void run() {
            Log.d("YueTing", "Single Click");
            CommandNetwork.getInstance().send("sc");
            finished = true;

        }
    }

    private SingleClickRunnable singleClickRunnable = new SingleClickRunnable();

    private class FlipGestureRunnable implements Runnable {
        public Point startPoint;
        public Point endPoint;

        @Override
        public void run() {

            if (endPoint.leftOf(startPoint)) {
                Log.d("YueTing", "Left Swipe");
                CommandNetwork.getInstance().send("lw");
            } else {
                Log.d("YueTing", "Right Swipe");
                CommandNetwork.getInstance().send("rw");
            }
        }
    }

    private FlipGestureRunnable flipGestureRunnable = new FlipGestureRunnable();


    private class DoubleClickRunnable implements Runnable {

        @Override
        public void run() {
            Log.d("YueTing", "Double Click");
            CommandNetwork.getInstance().send("dc");
        }
    }

    private DoubleClickRunnable doubleClickRunnable = new DoubleClickRunnable();

    public GestureHandler() {
        singleClickRunnable.finished = true;
    }

    private boolean handled = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                Log.d("Debug","Action Down");
                startPoint.x = event.getRawX();
                startPoint.y = event.getRawY();
                addLongPressCallback();
                handled = false;
                break;

            case MotionEvent.ACTION_MOVE:

                Log.d("Debug","Action Move");
                endPoint.x = event.getRawX();
                endPoint.y = event.getRawY();

                if (!longPressRunnable.pressing) {
                    if (startPoint.distance(endPoint) > LONGPRESS_SHAKE_LIMIT) {
                        removeLongPressCallBack();
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                Log.d("Debug","Action Up");
                endPoint.x = event.getRawX();
                endPoint.y = event.getRawY();

                if(!handled){
                    if (longPressRunnable.pressing) {
//                        handleLongGesture(startPoint,endPoint);
                        removeLongPressCallBack();
                    }

                    else if(endPoint.distance(startPoint) >= PRESS_SHAKE_LIMIT) {
                        flipGestureRunnable.startPoint = startPoint;
                        flipGestureRunnable.endPoint = endPoint;
                        flipGestureRunnable.run();
                    }

                    else if (singleClickRunnable.finished){
                        removeLongPressCallBack();
                        addSingleClickCallback();
                    }
                    else {
                        removeSingleClickCallback();
                        removeLongPressCallBack();
                        doubleClickRunnable.run();
                    }
                } else {
                    removeLongPressCallBack();
                }

                break;

            default:
                break;
        }

        return true;
    }

    private void addSingleClickCallback() {

        singleClickRunnable.finished = false;
        handler.postDelayed(singleClickRunnable, DOUBLECLICK_TIME_THRESHOLD);
    }

    private void removeSingleClickCallback() {
        handler.removeCallbacks(singleClickRunnable);
        singleClickRunnable.finished = true;
    }

    private void addLongPressCallback() {
        longPressRunnable.pressing = false;
        handler.postDelayed(longPressRunnable, LONGPRESS_TIME_THRESHOLD);
        longPressRunnable.added = true;

    }

    private void removeLongPressCallBack() {
        handler.removeCallbacks(longPressRunnable);
        longPressRunnable.added = false;
        longPressRunnable.pressing = false;
    }

//    private int onBorder(Point p) {
//
//        if(p.x < GESTURE_BORDER_WIDTH) {
//            return 2;
//        } else if(p.x > windowWidth - GESTURE_BORDER_WIDTH) {
//            return 1;
//        } else {
//            return 0;
//        }
//    }

    private void handleLongGesture(Point startPoint, Point endPoint, int fingers) {

        // 标记是否在边缘
//        int border = onBorder(startPoint);


        // 判断上下滑
        if (startPoint.verticalDistance(endPoint) > GESTURE_LONG_VERTICAL_THREOLD &&
                startPoint.horizontalDistance(endPoint) < GESTURE_VERTICAL_LIMIT) {
            if (endPoint.above(startPoint)) {
            }
            else if (endPoint.under(startPoint)) {

            }
        }

        else if (startPoint.horizontalDistance(endPoint) > GESTURE_HORIZONTAL_THREOLD &&
                startPoint.verticalDistance(endPoint) < GESTURE_HORIZONTAL_LIMIT) {
            if (endPoint.leftOf(startPoint)) {

            }
            else if (endPoint.rightOf(startPoint)) {

            }
        }

        longPressRunnable.end();
    }

}
