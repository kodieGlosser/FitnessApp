package com.main.toledo.gymtrackr;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Adam on 3/18/2015.
 */
public class SwipableLinearLayout extends LinearLayout {

    float mLastTouchX;
    float mLastTouchY;
    float mRightX;
    float mLeftX;
    float mStartX;
    int slideVal = 0;
    boolean open = false;
    private float selectableArea;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    Context mContext;
    swipeLayoutListener mListener;
    LoadActivity.LoadAdapter mLoadAdapter;
    BrowseActivity.BrowseAdapter mBrowseAdapter;

    public SwipableLinearLayout(Context c, AttributeSet attrs, int defStyle){
        super(c, attrs, defStyle);
        mContext = c;
    }

    public SwipableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setSwipeLayoutListener(swipeLayoutListener l){
        mListener = l;
    }
    //public void setAdapter(LoadActivity.LoadAdapter loadAdapter){
    //    mLoadAdapter = loadAdapter;
    //}
    public void percentageToDragEnable(float i){
        selectableArea = i;
    }

    public void setOpen(boolean b){
        open = b;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void resetPosition(){
        setX(0);
        open = false;
    }

    public SwipableLinearLayout(Context context) {
        super(context);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        float area =( this.getWidth() * selectableArea )/100;
        Log.d("4/4", "" + area + " " + MotionEventCompat.getX(ev, pointerIndex));
        if((MotionEventCompat.getX(ev, pointerIndex) > area)) {
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    Log.d("VIEWTEST", "ACTION_DOWN");
                    final int pointerIndex2 = MotionEventCompat.getActionIndex(ev);
                    final float x = MotionEventCompat.getX(ev, pointerIndex2);
                    //final float y = MotionEventCompat.getY(ev, pointerIndex);

                    // Remember where we started (for dragging)
                    mStartX = x;
                    //mLastTouchY = y;
                    mLeftX = mStartX - 50;
                    mRightX = mStartX + 50;

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
                    final int pointerIndex2 = MotionEventCompat.getActionIndex(ev);
                    final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex2);

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
        return super.onTouchEvent(ev);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void open(){
        ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(this, "translationX", slideVal);
        mSlidInAnimator.setDuration(300);
        mSlidInAnimator.start();
        mListener.CloseTextViewHandle();
        mListener.setTextViewHandle(this);

        open = true;

        ImageView img_selection = (ImageView) findViewById(R.id.Arrow_in_plan);
        int imageResourceId = R.drawable.ic_ic_collapse_arrow_side_50;

        if (img_selection != null)
            img_selection.setImageResource(imageResourceId);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void close(){
        ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(this, "translationX", 0);
        mSlidInAnimator.setDuration(300);
        mSlidInAnimator.start();
        open = false;

        mListener.clearHandle();

        ImageView img_selection = (ImageView) findViewById(R.id.Arrow_in_plan);
        int imageResourceId = R.drawable.ic_ic_expand_arrow_side_50;

        if (img_selection != null)
            img_selection.setImageResource(imageResourceId);


    }
    public void setSwipeOffset(int i){
        slideVal = i;
    }
}
