package com.main.toledo.gymtrackr;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Adam on 2/22/2015.
 * WARNING:  THIS CODE IS BUTT
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

    int mDraggedItemDestination;
    int mPosition;

    final int DOWN = 2;
    final int UP = 1;
    final int CIRCUIT = 1;
    final int EXERCISE = 2;
    final int PLAN = 1, WORKOUT = 2;

    int mDraggedItemType;
    int mDirection;
    int mLastY;

    int swipeX;
    int swipeY;

    int currentX;
    int currentY;

    final int mCheckedIndentation = 100;

    private int mMode;

    Exercise mToggledExerciseHandle;

    ArrayList<Circuit> Workout = new ArrayList<>();
    LinearLayout mLayoutHandle;
    LinearLayout.LayoutParams mOpenParams;
    LinearLayout.LayoutParams mClosedParams;

    ImageView mDragView;

    DropListener mDropListener;


    Context mContext;

    //DRAG COUNTDOWN VARS
    private boolean dragInProgress = false;
    private int currentXPos;
    private int currentYPos;
    private int dragRawY;
    private ImageView DragIcon;
    private int dragTimerInterval = 150;
    private int dragHoldBounds = 50;
    private boolean cancelDrag;

    public WorkspaceExpandableListView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mMode = ((WorkspaceActivity) context).getAppMode();
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

                    currentXPos = x;
                    currentYPos = y;
                    dragRawY = (int) ev.getRawY();
                    boolean okayToDrag = checkIfValidPosition();
                    if(okayToDrag) {
                        dragDelayIcon(x, y);
                        Log.d("DRAG DELAY TESTS", "DOWN -- X: " + currentXPos + " -- Y: " + currentYPos);
                        dragTimer(x, y);
                    }
                    break;
                case MotionEvent.ACTION_MOVE: //mose if moved

                    currentXPos = x;
                    currentYPos = y;
                    dragRawY = (int) ev.getRawY();
                    //if(dragInProgress)
                    //W    Log.d("41", "MOVE-OPENUI");
                    if(dragInProgress)
                        dragHandling();

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: //mouse button is released
                default:
                    //Log.d("TOUCH TESTS", "MOTION EVENT IS DEFAULT");
                    Log.d("DRAG DELAY TESTS", "DEFAULT -- X: " + x + " -- Y: " + y);
                    mDragMode = false;
                    abortCountdown();
                    if (dragInProgress) {

                        mLayoutHandle.setLayoutParams(mClosedParams);
                        //mEndPosition = pointToPosition(x, y);
                        stopDrag();//mStartPosition - getFirstVisiblePosition());

                        //if (mDropListener != null && mStartPosition != INVALID_POSITION)//edit 3/19 && mEndPosition != INVALID_POSITION)

                        m_endChildPosition = getPackedPositionChild(getExpandableListPosition(mDraggedItemDestination));
                        m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination));
                        //done
                        //Log.d("TOUCH TESTS", "MOVING CHILD: " + m_startChildPosition + " FROM GROUP: " + m_startGroupPosition);
                        Log.d("FINAL TESTS", "TO CHILD: " + m_endChildPosition + " FROM GROUP: " + m_endGroupPosition + " LAST POSITION: " + mDraggedItemDestination);
                        if (!(m_endGroupPosition == -1 && m_endChildPosition == -1)) {
                            if (m_endGroupPosition < 0)
                                m_endGroupPosition = 0;
                            mDropListener.onDrop(mDraggedItemType, m_endChildPosition, m_endGroupPosition); //this gets passed the start and end LIST positions
                        }
                        if (mDraggedItemType == CIRCUIT) {
                            ((WorkspaceActivity) mContext).ListFragment.onItemDrop();
                            ((WorkspaceActivity) mContext).ListFragment.setDragInProgress(false);
                        }
                        dragInProgress = false;
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

        if(!mRemoveMode && (mMode == WORKOUT)) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mStartPosition = pointToPosition(x, y);
                    Log.d("DOUBLTETAP TESTS", "DOWN CALLED");
                    if (mStartPosition != INVALID_POSITION) {
                        swipeX = x;
                        swipeY = y;
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

                    if (mStartPosition != -1) {
                        if (pointToPosition(x, y) != mStartPosition) {
                            mStartPosition = -1;
                        }
                        if ((x > (swipeX + 150)) && mStartPosition != -1) {
                            check(mStartPosition);
                            mStartPosition = -1;
                        }
                        if ((x < (swipeX - 150)) && mStartPosition != -1){
                            uncheck(mStartPosition);
                            mStartPosition = -1;
                        }
                    }

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

    private void check(int position){
        Log.d("check tests", "TOGGLE CALLED");
        if (getPackedPositionType(getExpandableListPosition(position)) != PACKED_POSITION_TYPE_GROUP){
            LinearLayout layout = (LinearLayout)getChildAt(position- getFirstVisiblePosition())
                    .findViewById(R.id.exercise_data_layout);

            ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(layout, "translationX", mCheckedIndentation);
            mSlidInAnimator.setDuration(300);
            mSlidInAnimator.start();

            RelativeLayout relativeLayout = (RelativeLayout) getChildAt(position- getFirstVisiblePosition())
                    .findViewById(R.id.exercise_relative_layout_handle);

            ImageView mChecked;

            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mChecked = (ImageView) inflater.inflate(R.layout.w_check, null);

            relativeLayout.addView(mChecked);

            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) mChecked.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);


            int group = getPackedPositionGroup(getExpandableListPosition(position));
            int child = getPackedPositionChild(getExpandableListPosition(position));

            Workout.get(group).getExercise(child).setSaveToHistory(true);
        }
    }

    private void uncheck(int position){
        if(getPackedPositionType(getExpandableListPosition(position))==PACKED_POSITION_TYPE_CHILD){
            int group = getPackedPositionGroup(getExpandableListPosition(position));
            int child = getPackedPositionChild(getExpandableListPosition(position));
            if(Workout.get(group).getExercise(child).isSaveToHistorySet()){
                LinearLayout layout = (LinearLayout)getChildAt(position- getFirstVisiblePosition())
                        .findViewById(R.id.exercise_data_layout);

                ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(layout, "translationX", 0);
                mSlidInAnimator.setDuration(300);
                mSlidInAnimator.start();

                ImageView check = (ImageView) getChildAt(position- getFirstVisiblePosition())
                        .findViewById(R.id.check);

                ((RelativeLayout)check.getParent()).removeView(check);

                Workout.get(group).getExercise(child).setSaveToHistory(false);

            }
        }
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
        //Log.d("FINAL TESTS", "OPEN CALLED");
        if (v != null && position != -1) {
            Log.d("OPEN TESTS", "SHOW NOW CLOSE LAST PADDING");

            int group = getPackedPositionGroup(getExpandableListPosition(position));
            while(group == -1){
                position--;
                group = getPackedPositionGroup(getExpandableListPosition(position));
            }

            if (!(!Workout.get(group).isOpen()
                    && (getPackedPositionChild(getExpandableListPosition(position)) == 0))) {

                if (mLayoutHandle != null)
                    mLayoutHandle.setLayoutParams(mClosedParams);

                mLayoutHandle = (LinearLayout) v.findViewById(R.id.paddingViewLayout);
                Log.d("OPEN TESTS", "SHOW NOW OPEN PADDING AT: " + position);
                mLayoutHandle.setLayoutParams(mOpenParams);

                mDraggedItemDestination = position;
            }
        }
    }

    private void deleteItem(final int itemPosition){

        final int group = getPackedPositionGroup(getExpandableListPosition(itemPosition));
        int child = getPackedPositionChild(getExpandableListPosition(itemPosition));
        if (getPackedPositionType(getExpandableListPosition(itemPosition)) == PACKED_POSITION_TYPE_GROUP) {
            if(group < Workout.size() -1 ) {
                Log.d("4/6", "IN GROUP HEADER: " + group + " SHOULD FALL OFF ITEM POS: " + itemPosition);

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
        } else if (getPackedPositionType(getExpandableListPosition(itemPosition)) == PACKED_POSITION_TYPE_CHILD) {
            //Log.d("SWIPE TESTS", "CHILD " + child + " SHOULD FALL OFF ITEM POS: " + itemPosition);
            if(!Workout.get(group).getExercise(child).getName().equals("test")) {
                Log.d("4/6", "IN CHILD HEADER: " + group + " SHOULD FALL OFF ITEM POS: " + itemPosition);
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

            //if (mDragListener != null)
            //    mDragListener.onDrag(x, y, null);// change null to "this" when ready to use
        }
    }

    // enable the drag view for dragging
    private void startDrag(int InitialItemPositionOnScreen, int x, int y) {
        stopDrag();//InitialItemPositionOnScreen);

        View item = getChildAt(InitialItemPositionOnScreen);
        if (item == null) return;
        item.setDrawingCacheEnabled(true);
        //if (mDragListener != null)
        //   mDragListener.onStartDrag(item);


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
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);

        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(imageView, mWindowParams);
        mDragView = imageView;

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


        mClosedParams = new LinearLayout.LayoutParams(1, 0);
        mOpenParams = new LinearLayout.LayoutParams(1, 300);


        mDraggedItemDestination = pointToPosition(x , y) - getFirstVisiblePosition();

        if (mDraggedItemType == CIRCUIT) {
            ((WorkspaceActivity) mContext).ListFragment.setDragInProgress(true);
            ((WorkspaceActivity) mContext).ListFragment.collapseAllGroups();
            mDraggedItemDestination = pointToPosition(x, y) - getFirstVisiblePosition();
        }

        m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination));
        while(m_endGroupPosition == -1){
            mDraggedItemDestination--;
            m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination));
        }

        Log.d("41", "SETTING LAYOUT PARAMS IN STARTDRAG @ " + mDraggedItemDestination);
        Log.d("41", "START DRAG ITEM: " + m_startChildPosition
                +" -- GROUP: " + m_startGroupPosition);

        Log.d("41", "CURRENT GROUP " + getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination))
            + "CURRENT CHILD" + getPackedPositionChild(getExpandableListPosition(mDraggedItemDestination)));

        //openInitialPosition()

        setSpaceToOpen();
        View v = getChildAt(mDraggedItemDestination);
        mLayoutHandle = (LinearLayout) v.findViewById(R.id.paddingViewLayout);
        mLayoutHandle.setLayoutParams(mOpenParams);

        //delete item from list, save in a temp location, refresh adapter
    }

    private void setSpaceToOpen(){
        if(m_startChildPosition == -1){ //if group
            if (m_startGroupPosition == Workout.size()-2){ //last circuit
                //Expand padding at bottom.
            } else { //all other circuits

            }
        } else { //if exercise

        }
    }

    private void stopDrag(){//int itemIndex) {
        if (mDragView != null) {
            //if (mDragListener != null)
            //    mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
    }

    private void dragDelayIcon(int x, int y){
        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        //mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = x- 650;
        mWindowParams.y = y - 800;

        mWindowParams.height = 100;
        mWindowParams.width = 100;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        DragIcon = new ImageView(context);

        DragIcon.setImageDrawable(
                context.getResources().getDrawable(R.drawable.drag));


        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(DragIcon, mWindowParams);

    }

    private void dragTimer(final int x, final int y){

        cancelDrag = false;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(inBounds(x, y) && !cancelDrag){
                    Log.d("DRAG DELAY TESTS", "5...");
                }
            }
        }, dragTimerInterval);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(inBounds(x, y) && !cancelDrag){
                    Log.d("DRAG DELAY TESTS", "4...");
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval *2);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(inBounds(x, y) && !cancelDrag){
                    Log.d("DRAG DELAY TESTS", "3...");
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval*3);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(inBounds(x, y) && !cancelDrag){
                    Log.d("DRAG DELAY TESTS", "2...");
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval*4);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(inBounds(x, y) && !cancelDrag){
                    Log.d("DRAG DELAY TESTS", "1...");
                    beginDragChecks();
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval*5);

    }

    private boolean inBounds(int x, int y){
        //X and Y are start x and Y values
        if((currentXPos < dragHoldBounds + x) && (currentXPos > x - dragHoldBounds) &&
                (currentYPos < dragHoldBounds + y) && (currentYPos > y - dragHoldBounds)){
            return true;
        } else {
            return false;
        }
    }

    private void abortCountdown() {
        cancelDrag = true;
        if (DragIcon != null){
            DragIcon.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(DragIcon);
            DragIcon.setImageDrawable(null);
            DragIcon = null;
        }
    }

    private void beginDragChecks(){
        abortCountdown();
        mStartPosition = pointToPosition(currentXPos, currentYPos); //mstartposition is the TRUE position
        if (mStartPosition != INVALID_POSITION) {
            int ItemPositionOnSreen = mStartPosition - getFirstVisiblePosition();
            mDragPointOffset = currentYPos - getChildAt(ItemPositionOnSreen).getTop(); //returns top position of this view relative to parent in pixels
            //mLayoutHeight = getChildAt(mItemPosition).getHeight();
            mDragPointOffset -= dragRawY - currentYPos;
            mLastY = currentYPos;
            dragInProgress = true;
            startDrag(ItemPositionOnSreen, currentXPos, currentYPos);
            //mItemPosition is the RELATIVE position on the list, 2nd item ON SCREEN vs 12th item

            drag(0, currentYPos);// replace 0 with x if desired
        }
    }

    private void dragHandling(){
        if (pointToPosition(currentXPos, currentYPos) != INVALID_POSITION) {

            if (currentYPos > mLastY) {
                mDirection = DOWN;
            } else if (currentYPos < mLastY) {
                mDirection = UP;
            }

            mLastY = currentYPos;

            if (mDirection == DOWN) {
                mPosition = pointToPosition(currentXPos, (currentYPos + 300));
                Log.d("OPEN TESTS", "POSITION: " + mPosition + " -- LAST VISIBLE POSITION: " + getLastVisiblePosition());
                //Log.d("TOUCH TESTS", "DIRECTION IS DOWN");
            } else {
                mPosition = pointToPosition(currentXPos, currentYPos);
            }

            //Log.d("TOUCH TESTS", "DRAG VIEW TOP: " + x + " -- DRAG VIEW BOTTOM: " + y);
            if (!(mPosition == mDraggedItemDestination)) {
                switch (mDraggedItemType) {
                    case EXERCISE:
                        if (mPosition == getLastVisiblePosition()){
                            View v = getChildAt(mPosition - getFirstVisiblePosition());
                            if (v.getId() == R.id.finalItem){
                                mPosition--;
                            }
                        }
                        openUI(mPosition, getChildAt(mPosition - getFirstVisiblePosition()));
                        break;
                    case CIRCUIT:
                        if (mPosition == getLastVisiblePosition()){
                            View v = getChildAt(mPosition - getFirstVisiblePosition());
                            if (v.getId() == R.id.finalItem){
                                mPosition--;
                            }
                        }

                        if (getPackedPositionType(getExpandableListPosition(mPosition)) == PACKED_POSITION_TYPE_GROUP) {
                            openUI(mPosition, getChildAt(mPosition - getFirstVisiblePosition()));
                        }
                        break;
                }
            }
        }
        drag(0, currentYPos);// replace 0 with x if desired
    }
    private boolean checkIfValidPosition(){
        boolean okayToDrag = false;
        int position = pointToPosition(currentXPos, currentYPos);
        int groupPosition;
        int childPosition;
        Exercise e;
        Circuit c;
        if(position != INVALID_POSITION) {
            if (getPackedPositionType(getExpandableListPosition(position)) == PACKED_POSITION_TYPE_CHILD) {
                childPosition = getPackedPositionChild(getExpandableListPosition(position));
                groupPosition = getPackedPositionGroup(getExpandableListPosition(position));
                e = Workout.get(groupPosition).getExercise(childPosition);
                if (!e.getName().equals("test")){
                    okayToDrag = true;
                }
            } else if (getPackedPositionType(getExpandableListPosition(mStartPosition)) == PACKED_POSITION_TYPE_GROUP) {
                childPosition = -1;
                groupPosition = getPackedPositionGroup(getExpandableListPosition(position));
                c = Workout.get(groupPosition);
                int workoutLength = Workout.size();
                if(c.isOpen() || groupPosition != (workoutLength-1)){
                    okayToDrag = true;
                }
            }
        }
        return okayToDrag;
    }
}
