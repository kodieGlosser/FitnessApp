package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Adam on 3/17/2015.
 */
public class swipableListView extends ListView {

    int mStartPosition;
    int mDragPointOffset;
    ImageView mDragView;
    ListDragListener mDragListener;
    swipeListener mSwipeListener;
    int mStartX;
    int mEndX;
    Context mContext;

    public swipableListView(Context c, AttributeSet attrs){
        super(c, attrs);
        mContext = c;
    }

    public void setSwipeListener(swipeListener l){
        mSwipeListener = l;
    }
    public void setDragListener(ListDragListener l) {
//        Log.d("TOUCH TESTS", "DRAG LISTENER INITIATED");
        mDragListener = l;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();


        switch (action) {
            case MotionEvent.ACTION_DOWN: //mouse button is initially pressed
                //Log.d("TOUCH TESTS", "MOTION EVENT IS ACTION_DOWN");
                //maps a point to an integer position on list
                mStartX = x;
                mStartPosition = pointToPosition(x, y); //mstartposition is the TRUE position
                if (mStartPosition != INVALID_POSITION) {
                    //first item visible
                    //get firstvisible position returns integer pointing to first
                    //thing displayed on screen
                    int mItemPosition = mStartPosition - getFirstVisiblePosition();

                    mDragPointOffset = y - getChildAt(mItemPosition).getTop(); //returns top position of this view relative to parent in pixels
                    mDragPointOffset -= ((int) ev.getRawY()) - y;
                    startDrag(mItemPosition, y);
                    //mItemPosition is the RELATIVE position on the list, 2nd item ON SCREEN vs 12th item
                    drag(x, 0);// replace 0 with x if desired
                }
                break;
            case MotionEvent.ACTION_MOVE: //mose if moved
                //Log.d("TOUCH TESTS", "MOTION EVENT IS ACTION_MOVE");
                drag(x, 0);// replace 0 with x if desired
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: //mouse button is released
            default:
                //Log.d("TOUCH TESTS", "MOTION EVENT IS DEFAULT");
                mEndX = x;
                isSwiped();

                break;
        }
        return true;
    }

    private void isSwiped(){
        int difference = mEndX - mStartX;
        if (difference < 0){
            difference = difference * -1;
        }
        if (mSwipeListener != null) {
            if (difference > (this.getWidth() / 4)) {
                mSwipeListener.onSwipe(mStartPosition);
            }
        }
    }
    // move the drag view
    private void drag(int x, int y) {
        if (mDragView != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
            layoutParams.x = x;
            layoutParams.y = y - mDragPointOffset;
            WindowManager mWindowManager = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.updateViewLayout(mDragView, layoutParams);

            if (mDragListener != null)
                mDragListener.onDrag(x, y, null);// change null to "this" when ready to use
            }
    }

    // enable the drag view for dragging
    private void startDrag(int itemIndex, int y) {
        stopDrag(itemIndex);

        View item = getChildAt(itemIndex);
        if (item == null) return;
        item.setDrawingCacheEnabled(true);
        if (mDragListener != null)
            mDragListener.onStartDrag(item);

        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

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
        ImageView v = new ImageView(context);
        v.setImageBitmap(bitmap);

        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    // destroy drag view
    private void stopDrag(int itemIndex) {
        if (mDragView != null) {
            if (mDragListener != null)
                mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
    }

}
