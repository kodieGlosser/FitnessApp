package com.main.toledo.gymtrackr;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Adam on 2/22/2015.
 */
public class WorkspaceExpandableListView extends ExpandableListView {
    boolean mDragMode;
    boolean mRemoveMode;
    boolean mToggle = true;
    boolean justRemovedHeader = false;

    int m_startGroupPosition;
    int m_startChildPosition;

    int m_endGroupPosition;
    int m_endChildPosition;

    int mStartPosition;
    int mDragPointOffset;        //Used to adjust drag view location
    int mCurrentPosition;

    int mLastPosition;
    int mPosition;

    final int DOWN = 2;
    final int UP = 1;
    final int CIRCUIT = 1;
    final int EXERCISE = 2;

    int mDraggedItemType;
    int mDirection;
    int mLastY;

    int swipeX;
    int swipeY;

    int currentX;
    int currentY;

    private int mDelay;
    Exercise mToggledExerciseHandle;

    ArrayList<Circuit> Workout = new ArrayList<>();
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
        Workout = WorkoutData.get(mContext).getWorkout();
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void toggleListeners(boolean b){
        mRemoveMode = false;
        mToggle = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        if ((action == MotionEvent.ACTION_DOWN && x < this.getWidth() / 4) && mToggle) {
            mRemoveMode = false;
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

        if ((action == MotionEvent.ACTION_DOWN && x > this.getWidth() / 4) && mToggle) {
            mRemoveMode = true;
        }
        //Log.d("TOUCH TESTS", "TRUE IF: " + x + "<" + this.getWidth()/4 );
        //if (!mDragMode)
        //    return super.onTouchEvent(ev);

        if (mDragMode) {
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

                    if (pointToPosition(x, y) != INVALID_POSITION) {
                        if (y > mLastY) {
                            mDirection = DOWN;
                        } else if (y < mLastY) {
                            mDirection = UP;
                        }

                        mLastY = y;

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

        if(mRemoveMode) {

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("SELECT TESTS", "DOWN X: " + x + " -- Y: " + y);
                    justRemovedHeader = false;
                    //mouse button is initially pressed

                    mStartPosition = pointToPosition(x, y);

                    if (mStartPosition != INVALID_POSITION) {
                        swipeX = x;
                        swipeY = y;
                        currentX = x;
                        currentY = y;
                    }

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("SELECT TESTS", "RUNNABLE X: " + x + " -- Y: " + y);
                            Log.d("SELECT TESTS", "RUNNABLE X: " + currentX + " -- Y: " + currentY);
                            if(
                                (currentX < (x + 30)) &&
                                (currentX > (x - 30)) &&
                                (currentY < (y + 30)) &&
                                (currentY > (y -30))
                            ){
                                toggle(mStartPosition);
                            }
                        }
                    }, 250);//mDelay);


                    super.onTouchEvent(ev);
                    break;
                case MotionEvent.ACTION_MOVE: //mose if moved

                        currentX = x;
                        currentY = y;
                        Log.d("SELECT TESTS", "MOVE");
                        if (mStartPosition != -1) {
                            if (pointToPosition(x, y) != mStartPosition) {
                                mStartPosition = -1;
                            }
                            if ((x < (swipeX - 150)) && mStartPosition != -1) {
                                int mItemPosition = mStartPosition - getFirstVisiblePosition();
                                deleteItem(mStartPosition);
                                mStartPosition = -1;
                            }
                        }

                        if (!justRemovedHeader) {

                            super.onTouchEvent(ev);
                        }

                    break; //mouse button is released
                default:
                    Log.d("SELECT TESTS", "DEFAULT");
                    if (!justRemovedHeader) {
                        super.onTouchEvent(ev);
                    }
                    break;
            }
        }
        if(justRemovedHeader){
            return true;
        }

        if(!mRemoveMode) {

            switch (action) {
                case MotionEvent.ACTION_DOWN:

                    mStartPosition = pointToPosition(x, y);
                    Log.d("DOUBLTETAP TESTS", "DOWN CALLED");
                    if (mStartPosition != INVALID_POSITION) {
                        currentX = x;
                        currentY = y;
                    }



                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentPosition = -2;
                        }
                    }, 500);//mDelay);

                    if (mStartPosition != mCurrentPosition){
                        mCurrentPosition = mStartPosition;
                    } else {
                        Log.d("DOUBLTETAP TESTS", "Toggle called");
                        toggle(mStartPosition);
                    }

                    super.onTouchEvent(ev);
                    break;
                case MotionEvent.ACTION_MOVE: //mose if moved
                    super.onTouchEvent(ev);

                    break; //mouse button is released
                default:

                    super.onTouchEvent(ev);

                    break;
            }
        }
        if(justRemovedHeader){
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void toggle(int position){
        Log.d("TOGGLE TESTS", "TOGGLE CALLED");
        if(getPackedPositionType(getExpandableListPosition(position))==PACKED_POSITION_TYPE_CHILD){
            int group = getPackedPositionGroup(getExpandableListPosition(position));
            int child = getPackedPositionChild(getExpandableListPosition(position));

            if(!Workout.get(group).getExercise(child).getName().equals("test")) {

                if(!Workout.get(group).getExercise(child).isToggled()) {
                    if (mToggledExerciseHandle != null)
                            mToggledExerciseHandle.setToggled(false);

                    mToggledExerciseHandle = Workout.get(group).getExercise(child);
                    mToggledExerciseHandle.setToggled(true);
                    WorkoutData.get(mContext).setToggledExercise(group, child);
                    ((WorkspaceActivity) mContext).putToggledExerciseCircuit(child, group);

                }else if (Workout.get(group).getExercise(child).isToggled()){
                    Log.d("TOGGLE TESTS", "SHOULD NOW DETOGGLE");
                    Workout.get(group).getExercise(child).setToggled(false);
                    mToggledExerciseHandle = null;
                    WorkoutData.get(mContext).clearToggledExercise();
                    ((WorkspaceActivity) mContext).putToggledExerciseCircuit(-1, -1);
                }

                ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();
            }

        }
    }

    public void clearHandle(){
        if (mToggledExerciseHandle!=null) {
            mToggledExerciseHandle.setToggled(false);
            mToggledExerciseHandle = null;
        }
    }

    private void openUI(int position, View v){//int child, View v){

        if (v != null && position != -1) {
            Log.d("OPEN TESTS", "SHOW NOW CLOSE LAST PADDING");
            if (!(!Workout.get(getPackedPositionGroup(getExpandableListPosition(position))).isOpen()
                    && (getPackedPositionChild(getExpandableListPosition(position)) == 0))) {
                if (mLayoutHandle != null)
                    mLayoutHandle.setLayoutParams(mClosedParams);

                mLayoutHandle = (LinearLayout) v.findViewById(R.id.paddingViewLayout);
                Log.d("OPEN TESTS", "SHOW NOW OPEN PADDING AT: " + position);
                mLayoutHandle.setLayoutParams(mOpenParams);

                mLastPosition = position;
            }
        }
    }

    private void deleteItem(final int itemPosition){

        final int group = getPackedPositionGroup(getExpandableListPosition(itemPosition));
        int child = getPackedPositionChild(getExpandableListPosition(itemPosition));
        if (getPackedPositionType(getExpandableListPosition(itemPosition)) == PACKED_POSITION_TYPE_GROUP) {
            if(group < Workout.size() -1 ) {
                Log.d("SWIPE TESTS", "HEADER: " + group + " SHOULD FALL OFF ITEM POS: " + itemPosition);

                ((WorkspaceActivity) mContext).ListFragment.workspaceListView.collapseGroup(group);

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(getChildAt(itemPosition - getFirstVisiblePosition()), "translationX", -1500);
                        mSlidInAnimator.setDuration(300);
                        mSlidInAnimator.start();
                    }
                }, 500);

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WorkoutData.get(mContext).getWorkout().remove(group);
                        ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();
                        ((WorkspaceActivity) mContext).ListFragment.restoreListExpansion();
                    }
                }, 800);

                justRemovedHeader = true;
            }
        } else {
            Log.d("SWIPE TESTS", "CHILD " + child + " SHOULD FALL OFF ITEM POS: " + itemPosition);
            if(child < Workout.get(group).getExercises().size() -1 || !Workout.get(group).isOpen()) {
                ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(getChildAt(itemPosition- getFirstVisiblePosition()), "translationX", -1500);
                mSlidInAnimator.setDuration(300);
                mSlidInAnimator.start();

                Workout.get(group).removeExercise(child);
                if (!Workout.get(group).isOpen()) {
                    Workout.remove(group);
                }

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();
                    }
                }, 300);
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

        Log.d("OPEN TESTS", "*****************************SHOW NOW EXPAND PADDING AT: " + itemIndex);
        mClosedParams = new LinearLayout.LayoutParams(1, 0);
        mOpenParams = new LinearLayout.LayoutParams(1, 300);

        mLayoutHandle = (LinearLayout)getChildAt(itemIndex).findViewById(R.id.paddingViewLayout);
        mLayoutHandle.setLayoutParams(mOpenParams);
        mLastPosition = itemIndex;
        //delete item from list, save in a temp location, refresh adapter


    }

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
