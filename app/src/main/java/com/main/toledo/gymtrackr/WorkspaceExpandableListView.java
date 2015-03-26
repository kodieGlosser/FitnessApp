package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Adam on 2/22/2015.
 */
public class WorkspaceExpandableListView extends ExpandableListView {
    boolean mDragMode;
    boolean mToggle = true;

    int m_startGroupPosition;
    int m_startChildPosition;

    int m_endGroupPosition;
    int m_endChildPosition;

    int mStartPosition;
    int mEndPosition;
    int mDragPointOffset;        //Used to adjust drag view location

    int mLastPosition;
    int mPosition;

    final int DOWN = 2;
    final int UP = 1;
    final int CIRCUIT = 1;
    final int EXERCISE = 2;

    int mDraggedItemType;
    int mDirection;
    int mLastY;
    LinearLayout mLayoutHandle;
    LinearLayout.LayoutParams mOpenParams;
    LinearLayout.LayoutParams mClosedParams;

    ImageView mDragView;
    GestureDetector mGestureDetector;

    DropListener mDropListener;
    RemoveListener mRemoveListener;
    DragListener mDragListener;

    Context mContext;

    public WorkspaceExpandableListView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext = context;
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void setRemoveListener(RemoveListener l) {
        mRemoveListener = l;
    }

    public void setDragListener(DragListener l) {
//        Log.d("TOUCH TESTS", "DRAG LISTENER INITIATED");
        mDragListener = l;
    }

    public void toggleListeners(boolean b){
        mToggle = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();



        if ((action == MotionEvent.ACTION_DOWN && x < this.getWidth() / 4) && mToggle) {
            if( getPackedPositionType(getExpandableListPosition(pointToPosition(x, y))) == PACKED_POSITION_TYPE_GROUP ) {
                if (!WorkoutData.get(mContext).getWorkout()
                        .get(getPackedPositionGroup(getExpandableListPosition(pointToPosition(x, y)))).isOpen()) {
                    Log.d("OPEN TESTS", "TRIED TO DRAG CLOSED CIRCUIT HEADER");
                    mDragMode = false;
                } else {
                    mDragMode = true;
                }
            } else {
                mDragMode = true;
            }
        }

        //Log.d("TOUCH TESTS", "TRUE IF: " + x + "<" + this.getWidth()/4 );
        if (!mDragMode)
            return super.onTouchEvent(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: //mouse button is initially pressed
                //Log.d("TOUCH TESTS", "MOTION EVENT IS ACTION_DOWN");
                //maps a point to an integer position on list
                mStartPosition = pointToPosition(x, y); //mstartposition is the TRUE position

                if (mStartPosition != INVALID_POSITION) {
                    //first item visible
                    //get firstvisible position returns integer pointing to first
                    //thing displayed on screen

                    //openUI(getPackedPositionGroup(getExpandableListPosition(mStartPosition))
                    //        ,getPackedPositionChild(getExpandableListPosition(mStartPosition)));
                    //openUI code

                    int mItemPosition = mStartPosition - getFirstVisiblePosition();
                    //ADAM: may need code for group here
                    //get child at returns view at position
                    mDragPointOffset = y - getChildAt(mItemPosition).getTop(); //returns top position of this view relative to parent in pixels
                    //mLayoutHeight = getChildAt(mItemPosition).getHeight();
                    mDragPointOffset -= ((int) ev.getRawY()) - y;
                    startDrag(mItemPosition, y);
                    //mItemPosition is the RELATIVE position on the list, 2nd item ON SCREEN vs 12th item
                    drag(0, y);// replace 0 with x if desired
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_MOVE: //mose if moved
                //Log.d("TOUCH TESTS", "MOTION EVENT IS ACTION_MOVE");
                /*
                Log.d("TOUCH TESTS", "---------------------------------------------------------------------------------------------");

                Log.d("TOUCH TESTS", "CURRENTLY OVER CHILD: " + getPackedPositionChild(getExpandableListPosition(pointToPosition(x, y)))
                       + " -- IN GROUP: " + getPackedPositionGroup(getExpandableListPosition(pointToPosition(x, y))));
                if(mDragView != null && getChildAt(pointToPosition(x, y) - getFirstVisiblePosition()) != null) {
                    Log.d("TOUCH TESTS", "IMAGE AT Y VALUE: " + ev.getY() + " -- OVER VIEW AT Y VALUE: " + getChildAt(pointToPosition(x, y) - getFirstVisiblePosition()).getY());
                }
                Log.d("TOUCH TESTS", "---------------------------------------------------------------------------------------------");

                child = getPackedPositionChild(getExpandableListPosition(pointToPosition(x, y)));
                group = getPackedPositionGroup(getExpandableListPosition(pointToPosition(x, y)));
                */
                //mPosition = pointToPosition(x, y);
                if(pointToPosition(x, y) != INVALID_POSITION) {
                    if (y > mLastY) {
                        mDirection = DOWN;
                    } else if (y < mLastY) {
                        mDirection = UP;
                    }

                    mLastY = y;


                /*

                Log.d("TOUCH TESTS", child + " " + lastChild + " - groups - " + group + " " + lastGroup);
                if(!((child == lastChild) && (group == lastGroup))){// || (group == -1 && child == -1)){
                    openUI(group, child, getChildAt(pointToPosition(x, y) - getFirstVisiblePosition()));
                }// else {
                    //openUI(group, child, getChildAt(pointToPosition(x, y) - getFirstVisiblePosition()));

                 */
                    // }

                    if (mDirection == DOWN) {
                        mPosition = pointToPosition(x, (y + 300));
                        Log.d("TOUCH TESTS", "DIRECTION IS DOWN");
                    } else {
                        mPosition = pointToPosition(x, y);
                    }
                    Log.d("TOUCH TESTS", "DRAG VIEW TOP: " + x + " -- DRAG VIEW BOTTOM: " + y);
                    if (!(mPosition == mLastPosition)) {
                        switch (mDraggedItemType) {
                            case EXERCISE:
                                openUI(mPosition, getChildAt(mPosition - getFirstVisiblePosition()));
                                break;
                            case CIRCUIT:
                                if (getPackedPositionType(getExpandableListPosition(mPosition)) == PACKED_POSITION_TYPE_GROUP) {
                                    openUI(mPosition, getChildAt(mPosition - getFirstVisiblePosition()));
                                }
                                break;
                        }
                    }
                }
                    drag(0, y);// replace 0 with x if desired

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: //mouse button is released
            default:
                //Log.d("TOUCH TESTS", "MOTION EVENT IS DEFAULT");
                mDragMode = false;
                mLayoutHandle.setLayoutParams(mClosedParams);
                //mEndPosition = pointToPosition(x, y);
                stopDrag(mStartPosition - getFirstVisiblePosition());

                if (mDropListener != null && mStartPosition != INVALID_POSITION)//edit 3/19 && mEndPosition != INVALID_POSITION)
                    //Convert list position to expandable list positions
                    //SOME TRY CATCH CRAPS NEED TO GO HERE
                    //CAN REPLACE THESE WITH CALLS TO TEMP VALUES 3/24
                /*
                    if ( getPackedPositionType(getExpandableListPosition(mStartPosition)) == PACKED_POSITION_TYPE_CHILD ) {
                        m_startChildPosition = getPackedPositionChild(getExpandableListPosition(mStartPosition));
                        m_startGroupPosition = getPackedPositionGroup(getExpandableListPosition(mStartPosition));
                    } else if ( getPackedPositionType(getExpandableListPosition(mStartPosition)) == PACKED_POSITION_TYPE_GROUP){
                        m_startChildPosition = -1;
                        m_startGroupPosition = getPackedPositionGroup(getExpandableListPosition(mStartPosition));
                    }
                */
                    m_endChildPosition = getPackedPositionChild(getExpandableListPosition(mLastPosition));
                    m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mLastPosition));
                    //done
                    //Log.d("TOUCH TESTS", "MOVING CHILD: " + m_startChildPosition + " FROM GROUP: " + m_startGroupPosition);
                    Log.d("TOUCH TESTS", "TO CHILD: " + m_endChildPosition + " FROM GROUP: " + m_endGroupPosition);
                    if (!(m_endGroupPosition == -1 && m_endChildPosition == -1)) {
                        //3/19****  if (m_endChildPosition < 0)
                        //          m_endChildPosition = 0;

                        if (m_endGroupPosition < 0)
                            m_endGroupPosition = 0;

                        mDropListener.onDrop(mDraggedItemType, m_endChildPosition, m_endGroupPosition); //this gets passed the start and end LIST positions
                    }
                break;
        }
        return true;
    }
    private void openUI(int position, View v){//int child, View v){

        //Log.d("TOUCH TESTS", "***********************************************************************************************************************************");
        //Log.d("TOUCH TESTS", "POSITION CHANGED OPEN UI CALLED GROUP: " + group + " -- CHILD: " + child);
        //Log.d("TOUCH TESTS", "***********************************************************************************************************************************");
        /*
        WorkoutData.get(mContext).getWorkout().get(lastGroup).removeExercise(lastChild);
        WorkoutData.get(mContext).shiftTempExercise(group, child);


        */
        if (v != null) {
            Log.d("OPEN TESTS", "SHOW NOW CLOSE LAST PADDING");
            if (mLayoutHandle != null)
                mLayoutHandle.setLayoutParams(mClosedParams);

            mLayoutHandle = (LinearLayout) v.findViewById(R.id.paddingViewLayout);
            Log.d("OPEN TESTS", "SHOW NOW OPEN PADDING AT: " + position);
            mLayoutHandle.setLayoutParams(mOpenParams);

            mLastPosition = position;
        }
        //lastChild = child;
        //lastGroup = group;
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

        if ( getPackedPositionType(getExpandableListPosition(mStartPosition)) == PACKED_POSITION_TYPE_CHILD ) {
            m_startChildPosition = getPackedPositionChild(getExpandableListPosition(mStartPosition));
            m_startGroupPosition = getPackedPositionGroup(getExpandableListPosition(mStartPosition));
            WorkoutData.get(mContext).setTempExercise(m_startGroupPosition, m_startChildPosition);
            mDraggedItemType = EXERCISE;
        } else if ( getPackedPositionType(getExpandableListPosition(mStartPosition)) == PACKED_POSITION_TYPE_GROUP) {
            m_startChildPosition = -1;
            m_startGroupPosition = getPackedPositionGroup(getExpandableListPosition(mStartPosition));
            WorkoutData.get(mContext).setTempCircuit(m_startGroupPosition);
            mDraggedItemType = CIRCUIT;
        }
        ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();
            /*
            lastGroup = m_startGroupPosition;
            lastChild = m_startChildPosition;
            */
        Log.d("OPEN TESTS", "*****************************SHOW NOW EXPAND PADDING AT: " + itemIndex);
        mClosedParams = new LinearLayout.LayoutParams(1, 0);
        mOpenParams = new LinearLayout.LayoutParams(1, 300);

        mLayoutHandle = (LinearLayout)getChildAt(itemIndex).findViewById(R.id.paddingViewLayout);
        mLayoutHandle.setLayoutParams(mOpenParams);
        mLastPosition = itemIndex;
        //delete item from list, save in a temp location, refresh adapter


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
    /*
    @Override

    public boolean collapseGroup(int groupPosition){
        boolean b = super.collapseGroup(groupPosition);
        Log.d("PAD BUG", "collapse override called.");
        ((WorkspaceActivity) mContext).getAdapter().hideKeyboard();

        return b;
    }
    */
}
