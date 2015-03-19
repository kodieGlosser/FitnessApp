package com.main.toledo.gymtrackr;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Adam on 3/18/2015.
 */
public class LoadTextView extends TextView {

    float mLastTouchX;
    float mLastTouchY;
    float mRightX;
    float mLeftX;
    float mStartX;
    int slideVal = -650;
    boolean open = false;

    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    Context mContext;
    LoadActivity.LoadAdapter mAdapter;

    public LoadTextView(Context c, AttributeSet attrs, int defStyle){
        super(c, attrs, defStyle);
        mContext = c;
    }

    public LoadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(LoadActivity.LoadAdapter loadAdapter){
        mAdapter = loadAdapter;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void resetPosition(){
        setX(0);
        open = false;
    }

    public LoadTextView(Context context) {
        super(context);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                Log.d("VIEWTEST", "ACTION_DOWN");
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                //final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mStartX = x;
                //mLastTouchY = y;
                mLeftX = mStartX - 100;
                mRightX = mStartX + 100;

                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.d("VIEWTEST", "ACTION_MOVE");
                if ((ev.getX() < mLeftX) && !open) {
                    open();
                }
                if ((ev.getX() > mRightX) && open) {
                    close();
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                Log.d("VIEWTEST", "ACTION_UP");
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                Log.d("VIEWTEST", "ACTION_CANCEL");
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                Log.d("VIEWTEST", "ACTION_POINTER_UP");
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void open(){
        ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(this, "translationX", slideVal);
        mSlidInAnimator.setDuration(300);
        mSlidInAnimator.start();
        mAdapter.CloseTextViewHandle();
        mAdapter.setTextViewHandle(this);


        open = true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void close(){
        ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(this, "translationX", 0);
        mSlidInAnimator.setDuration(300);
        mSlidInAnimator.start();
        open = false;
        mAdapter.clearHandle();
    }
}
