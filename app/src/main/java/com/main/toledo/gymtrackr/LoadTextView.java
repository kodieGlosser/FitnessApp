package com.main.toledo.gymtrackr;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.jar.Attributes;

/**
 * Created by Adam on 3/18/2015.
 */
public class LoadTextView extends TextView {

    int mStartPosition;
    float mDragPointOffset;
    ImageView mDragView;
    ListDragListener mDragListener;
    swipeListener mSwipeListener;
    float mLastTouchX;
    float mLastTouchY;
    float mPosX;
    float mPosY;
    float mRightX;
    float mLeftX;
    float mStartX;
    int slideVal = -650;
    boolean open = false;

    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    Context mContext;

    public LoadTextView(Context c, AttributeSet attrs, int defStyle){
        super(c, attrs, defStyle);
        mContext = c;
    }

    public LoadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadTextView(Context context) {
        super(context);
    }
    /*
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();
        final float dif = x - mStartX;

        switch (action) {
            case MotionEvent.ACTION_DOWN: //mouse button is initially pressed
                //Log.d("TOUCH TESTS", "MOTION EVENT IS ACTION_DOWN");
                //maps a point to an integer position on list
                mStartX = x;
                mStartY = y;
                //mStartPosition = pointToPosition(x, y); //mstartposition is the TRUE position
                //if (mStartPosition != INVALID_POSITION) {
                    //first item visible
                    //get firstvisible position returns integer pointing to first
                    //thing displayed on screen
                    //int mItemPosition = mStartPosition - getFirstVisiblePosition();

                    mDragPointOffset = y - this.getTop(); //returns top position of this view relative to parent in pixels
                    mDragPointOffset -= ((int) ev.getRawY()) - y;
                    //startDrag(this, y);
                    //mItemPosition is the RELATIVE position on the list, 2nd item ON SCREEN vs 12th item
                    drag(x, y);// replace 0 with x if desired
                //}
                break;
            case MotionEvent.ACTION_MOVE: //mose if moved
                //Log.d("TOUCH TESTS", "MOTION EVENT IS ACTION_MOVE");
                drag(dif, y);// replace 0 with x if desired
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: //mouse button is released
            default:
                //Log.d("TOUCH TESTS", "MOTION EVENT IS DEFAULT");
                stopDrag(this);//stopDrag(mStartPosition - getFirstVisiblePosition());
                mEndX = x;
                isSwiped();

                break;
        }
        return true;
    }
    */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        //mScaleDetector.onTouchEvent(ev);

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
                // Find the index of the active pointer and fetch its position
               /*
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

                setX(mPosX);

                invalidate();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;
            */
                if ((ev.getX() < mLeftX) && !open) {
                    ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(this, "translationX", slideVal);
                    mSlidInAnimator.setDuration(300);
                    mSlidInAnimator.start();
                    open = true;
                }
                if ((ev.getX() > mRightX) && open) {
                    ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(this, "translationX", 0);
                    mSlidInAnimator.setDuration(300);
                    mSlidInAnimator.start();
                    open = false;
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
    private void isSwiped(){
        /*
        int difference = mEndX - mStartX;
        if (difference < 0){
            difference = difference * -1;
        }
        if (mSwipeListener != null) {
            if (difference > (this.getWidth() / 4)) {
                mSwipeListener.onSwipe(mStartPosition);
            }
        }
        */
    }
    // move the drag view
    /*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void drag(float x, float y) {
        //if (mDragView != null) {
            //WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.getLayoutParams();
            Log.d("LOAD TEXT TESTS", "----------------------------------------------------------------------");
            Log.d("LOAD TEXT TESTS", "START X: " + mStartX + " -- START Y: " + mStartY);
            Log.d("LOAD TEXT TESTS", "X: " + x + " -- Y:" + y);
            this.setX(x);//layoutParams.x = x;
            this.setY(mStartY - mDragPointOffset);//layoutParams.y = mStartY - mDragPointOffset;//y - mDragPointOffset;
            invalidate();
            Log.d("LOAD TEXT TESTS", "DEST X: " + x + " -- DEST Y:" + (mStartY - mDragPointOffset));
            Log.d("LOAD TEXT TESTS", "----------------------------------------------------------------------");
            //WindowManager mWindowManager = (WindowManager) getContext()
                   // .getSystemService(Context.WINDOW_SERVICE);
            //mWindowManager.updateViewLayout(this, layoutParams);

            //if (mDragListener != null)
            //    mDragListener.onDrag(x, y, null);// change null to "this" when ready to use
        //}
    }
    */
    // enable the drag view for dragging
    private void startDrag(View v, int y) {
        stopDrag(v);

        if (v == null) return;
        v.setDrawingCacheEnabled(true);
        if (mDragListener != null)
            mDragListener.onStartDrag(v);

        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        /*
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPointOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView IV = new ImageView(context);
        IV.setImageBitmap(bitmap);

        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(IV, mWindowParams);
        mDragView = IV;
        this.setVisibility(INVISIBLE);
        */
    }

    // destroy drag view
    private void stopDrag(View v){      //int itemIndex) {
        if (mDragView != null) {
            //if (mDragListener != null)
            //    mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
            this.setVisibility(VISIBLE);
        }
    }


}
