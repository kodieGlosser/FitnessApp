package com.main.toledo.gymtrackr;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
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
 * WARNING:  THIS CODE IS BUTT
 * UPDATE: 5/13/2015
 * BUTT FACTOR REDUCED BY 60%
 */
public class WorkspaceExpandableListView extends ExpandableListView {
    private boolean mDragMode;
    private boolean mToggle = true;

    private int m_startGroupPosition;
    private int m_startChildPosition;

    private int m_endGroupPosition;
    private int m_endChildPosition;

    private int mStartPosition;
    private int mDragPointOffset;        //Used to adjust drag view location

    private int mDraggedItemDestination;
    private int mPosition;

    private final int DOWN = 2;
    private final int UP = 1;
    public final int CIRCUIT = 1;
    public final int EXERCISE = 2;

    public int mDraggedItemType;
    private int mDirection;
    //TODO: Maybe 0 is a bad thing
    private int mLastY = 0;

    private Exercise mToggledExerciseHandle;

    private ArrayList<Circuit> Workout = new ArrayList<>();
    private LinearLayout mLayoutHandle;
    private LinearLayout.LayoutParams mOpenParams;
    private LinearLayout.LayoutParams mClosedParams;

    private ImageView mDragView;

    private Context mContext;

    //GLOBAL VARS
    private int SCREENWIDTH;
    private int SCREENHEIGHT;

    //DRAG COUNTDOWN VARS
    private boolean dragInProgress = false;
    public int currentXPos;
    public int currentYPos;
    private int dragRawY;
    private ImageView DragIcon;
    private int dragTimerInterval = 150;
    private int dragHoldBounds;
    private boolean cancelDrag;
    private int mTouchCount = 0;

    //LIST SCROLLERS
    private boolean scroll = false;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable upRunnable = new Runnable() {

        @Override
        public void run() {
            smoothScrollBy(-10, 5);
            handler.postDelayed(this, 5);
        }
    };
    private Runnable downRunnable = new Runnable() {

        @Override
        public void run() {
            smoothScrollBy(10, 5);
            handler.postDelayed(this, 5);
        }
    };
    private boolean scrollUp;

    public WorkspaceExpandableListView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext = context;
        Workout = WorkoutData.get(mContext).getWorkout();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREENWIDTH = size.x;
        SCREENHEIGHT = size.y;

        //DRAG COUNTDOWN VARS
        dragHoldBounds = (int)(SCREENWIDTH * .047);
    }

    public void toggleListeners(boolean b) {
        mToggle = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        if ((action == MotionEvent.ACTION_DOWN && x < this.getWidth() / 4) && mToggle) {
            if (getPackedPositionType(getExpandableListPosition(pointToPosition(x, y))) == PACKED_POSITION_TYPE_GROUP) {
                if (!WorkoutData.get(mContext).getWorkout()
                        .get(getPackedPositionGroup(getExpandableListPosition(pointToPosition(x, y)))).isOpen()) {
                    mDragMode = false;
                } else {
                    mDragMode = true;
                }
            } else {
                mDragMode = true;
            }
        }

        //  DRAG LOGIC

        if (mDragMode) {
            switch (action) {
                case MotionEvent.ACTION_DOWN: //mouse button is initially pressed

                    mTouchCount++;
                    currentXPos = x;
                    currentYPos = y;
                    dragRawY = (int) ev.getRawY();
                    boolean okayToDrag = checkIfValidPosition();
                    if (okayToDrag) {
                        dragDelayIcon(x, y);
                        dragTimer(x, y, mTouchCount);
                    }
                    break;
                case MotionEvent.ACTION_MOVE: //mose if moved

                    currentXPos = x;
                    currentYPos = y;
                    dragRawY = (int) ev.getRawY();
                    if (dragInProgress) {
                        dragHandling(true);
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: //mouse button is released
                default:
                    placeDraggedItem();
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void placeDraggedItem(){
        mDragMode = false;
        abortCountdown();
        if (dragInProgress) {
            //close spacing between items
            mLayoutHandle.setLayoutParams(mClosedParams);
            //remove drag icon
            stopDragAndScroll();

            m_endChildPosition = getPackedPositionChild(getExpandableListPosition(mDraggedItemDestination));
            m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination));

            if (!(m_endGroupPosition == -1 && m_endChildPosition == -1)) {
                if (m_endGroupPosition < 0)
                    m_endGroupPosition = 0;
                onDrop(); //this gets passed the start and end LIST positions
            }
            if (mDraggedItemType == CIRCUIT) {
                ((WorkspaceActivity) mContext).ListFragment.onItemDrop();
                ((WorkspaceActivity) mContext).ListFragment.setDragInProgress(false);
            }
            dragInProgress = false;
        }
    }

    private void onDrop() {
        //synchronized (Workout) {//Save to temp, remove from workout
        if (m_endGroupPosition >= Workout.size() - 1) {             //if the destination circuit is the last or later
            m_endChildPosition = -1;
        }

        switch (mDraggedItemType) {
            case CIRCUIT:
                WorkoutData.get(mContext).placeTempCircuit(m_endGroupPosition);

                break;
            case EXERCISE:  //passed location is a group child
                switch (m_endChildPosition){
                    case -1:
                        WorkoutData.get(mContext).addClosedCircuitWithTempExercise(m_endGroupPosition);
                        break;
                    default:
                        WorkoutData.get(mContext).placeTempExercise(m_endGroupPosition, m_endChildPosition);
                        break;
                }
                break;
        }
        ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();
        ((WorkspaceActivity) mContext).ListFragment.restoreListExpansion();
    }

    public void placeGenericExercise(){

        mLayoutHandle.setLayoutParams(mClosedParams);
        m_endChildPosition = getPackedPositionChild(getExpandableListPosition(mDraggedItemDestination));
        m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination));

        if (!(m_endGroupPosition == -1 && m_endChildPosition == -1)) {

            if (m_endGroupPosition < 0) //beginning of workout
                m_endGroupPosition = 0;

            if (m_endGroupPosition >= Workout.size() - 1)              //if the destination circuit is the last or later
                m_endChildPosition = -1;

            switch (m_endChildPosition){
                case -1:
                    WorkoutData.get(mContext).addClosedCircuitWithGenericExercise(m_endGroupPosition);
                    break;
                default:
                    WorkoutData.get(mContext).placeGenericExercise(m_endGroupPosition, m_endChildPosition);
                    break;
            }
            ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();
        }
    }


    public void clearHandle() {
        if (mToggledExerciseHandle != null) {
            mToggledExerciseHandle.setToggled(false);
            mToggledExerciseHandle = null;
        }
    }

    public void closeUI(){
        if (mLayoutHandle != null){
            mLayoutHandle.setLayoutParams(mClosedParams);
        }
    }
    private void openUI(int position, View v) {
        Log.d("WELV", "OpenUI called->Position: " + position);
        if (v != null && position != -1) {

            int group = getPackedPositionGroup(getExpandableListPosition(position));
            while (group == -1) {
                position--;
                group = getPackedPositionGroup(getExpandableListPosition(position));
            }

            if (!(!Workout.get(group).isOpen()
                    && (getPackedPositionChild(getExpandableListPosition(position)) == 0))) {

                if (mLayoutHandle != null)
                    mLayoutHandle.setLayoutParams(mClosedParams);

                mLayoutHandle = (LinearLayout) v.findViewById(R.id.paddingViewLayout);
                mLayoutHandle.setLayoutParams(mOpenParams);

                mDraggedItemDestination = position;
            }
        }
    }

    synchronized private void drag(int x, int y) {
        if (mDragView != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
            layoutParams.x = x;
            layoutParams.y = y - mDragPointOffset;
            WindowManager mWindowManager = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.updateViewLayout(mDragView, layoutParams);
        }
        //List move codef
        int topThreshHold = this.getHeight()/5;

        if (currentYPos < topThreshHold && !scroll){
            //start scroll
            scroll = true;
            scrollUp = true;
            Log.d("4.22", "scrollup + " + scrollUp);
            handler.postDelayed(upRunnable, 0);
        } else if (currentYPos > topThreshHold && scrollUp){
            //stop scroll
            handler.removeCallbacks(upRunnable);
            scroll = false;
            Log.d("4.22", "scroll set to false in drag" + scroll);
        }

        int bottomThreshHold = this.getHeight()-(this.getHeight()/5);
        if (currentYPos > bottomThreshHold){
            scrollUp = false;
            Log.d("4.22", "scrollup + " + scrollUp);
            scroll = true;
            handler.postDelayed(downRunnable, 0);
        } else if (currentYPos < bottomThreshHold && !scrollUp){
            handler.removeCallbacks(downRunnable);
            scroll = false;
        }
    }

    public void setDragSpacing(int viewHeight){
        mClosedParams = new LinearLayout.LayoutParams(1, 0);
        mOpenParams = new LinearLayout.LayoutParams(1, viewHeight);
    }

    // enable the drag view for dragging
    private void startDrag(int InitialItemPositionOnScreen, int x, int y) {
        int itemHeight;

        stopDragAndScroll();//InitialItemPositionOnScreen);

        View item = getChildAt(InitialItemPositionOnScreen);
        if (item == null) return;
        item.setDrawingCacheEnabled(true);
        itemHeight = item.getHeight();
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

        if (getPackedPositionType(getExpandableListPosition(mStartPosition)) == PACKED_POSITION_TYPE_CHILD) {
            m_startChildPosition = getPackedPositionChild(getExpandableListPosition(mStartPosition));
            m_startGroupPosition = getPackedPositionGroup(getExpandableListPosition(mStartPosition));
            WorkoutData.get(mContext).setTempExercise(m_startGroupPosition, m_startChildPosition);
            mDraggedItemType = EXERCISE;
        } else if (getPackedPositionType(getExpandableListPosition(mStartPosition)) == PACKED_POSITION_TYPE_GROUP) {
            m_startChildPosition = -1;
            m_startGroupPosition = getPackedPositionGroup(getExpandableListPosition(mStartPosition));
            WorkoutData.get(mContext).setTempCircuit(m_startGroupPosition);
            mDraggedItemType = CIRCUIT;
        }
        ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();

        setDragSpacing(itemHeight);

        mDraggedItemDestination = pointToPosition(x, y) - getFirstVisiblePosition();

        if (mDraggedItemType == CIRCUIT) {
            ((WorkspaceActivity) mContext).ListFragment.setDragInProgress(true);
            ((WorkspaceActivity) mContext).ListFragment.collapseAllGroups();
            mDraggedItemDestination = pointToPosition(x, y) - getFirstVisiblePosition();
        }

        m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination));
        while (m_endGroupPosition == -1) {
            mDraggedItemDestination--;
            m_endGroupPosition = getPackedPositionGroup(getExpandableListPosition(mDraggedItemDestination));

        }
        //GET EXERCISE ITEM HERE

        //openInitialPosition()
        //check if item is
        m_endChildPosition = getPackedPositionChild(getExpandableListPosition(mDraggedItemDestination));
        if (!Workout.get(m_endGroupPosition).isOpen()){
            mDraggedItemDestination--;
        }
        //setSpaceToOpen();
        if(getChildAt(mDraggedItemDestination) != null) {
            View v = getChildAt(mDraggedItemDestination);
            mLayoutHandle = (LinearLayout) v.findViewById(R.id.paddingViewLayout);
            mLayoutHandle.setLayoutParams(mOpenParams);
        }
        //delete item from list, save in a temp location, refresh adapter
    }

    private void stopDragAndScroll() {//int itemIndex) {
        if (mDragView != null) {
            //if (mDragListener != null)
            //    mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
        //stops scrolling when dropped
        if(scroll){
            if(scrollUp){
                scroll = false;
                handler.removeCallbacks(upRunnable);
            }else{
                scroll = false;
                handler.removeCallbacks(downRunnable);
            }
        }

    }

    private void dragDelayIcon(int x, int y) {
        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        //mWindowParams.gravity = Gravity.TOP;
        int xLocation;
        if (x < SCREENWIDTH/5) { //200
            xLocation = (int)(-SCREENWIDTH / 2 + SCREENHEIGHT / 19.2);//-500;
        }else {
            xLocation = x - (int) (SCREENWIDTH / 2 + SCREENHEIGHT / 19.2);//650;
        }

        mWindowParams.x = xLocation;
        mWindowParams.y = y - (int)(SCREENHEIGHT*.4166);

        mWindowParams.height = (int)(SCREENHEIGHT/19.2);
        mWindowParams.width = (int)(SCREENHEIGHT/19.2);
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
                context.getResources().getDrawable(R.drawable.drag1));


        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(DragIcon, mWindowParams);

    }

    private void dragTimer(final int x, final int y, final int touchCount) {
        final Context context = getContext();
        cancelDrag = false;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inBounds(x, y) && !cancelDrag && (touchCount == mTouchCount)) {
                    DragIcon.setImageDrawable(
                            context.getResources().getDrawable(R.drawable.drag2));
                }
            }
        }, dragTimerInterval);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inBounds(x, y) && !cancelDrag && (touchCount == mTouchCount)) {
                    DragIcon.setImageDrawable(
                            context.getResources().getDrawable(R.drawable.drag3));
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval * 2);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inBounds(x, y) && !cancelDrag && (touchCount == mTouchCount)) {
                    DragIcon.setImageDrawable(
                            context.getResources().getDrawable(R.drawable.drag4));
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval * 3);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inBounds(x, y) && !cancelDrag && (touchCount == mTouchCount)) {
                    DragIcon.setImageDrawable(
                            context.getResources().getDrawable(R.drawable.drag5));
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval * 4);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inBounds(x, y) && !cancelDrag && (touchCount == mTouchCount)) {
                    DragIcon.setImageDrawable(
                            context.getResources().getDrawable(R.drawable.drag5));
                    beginDragChecks();
                } else {
                    abortCountdown();
                }
            }
        }, dragTimerInterval * 5);
    }

    private boolean inBounds(int x, int y) {
        //X and Y are start x and Y values
        if ((currentXPos < dragHoldBounds + x) && (currentXPos > x - dragHoldBounds) &&
                (currentYPos < dragHoldBounds + y) && (currentYPos > y - dragHoldBounds)) {
            return true;
        } else {
            return false;
        }
    }

    private void abortCountdown() {
        cancelDrag = true;
        if (DragIcon != null) {
            DragIcon.setVisibility(GONE);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(DragIcon);
            DragIcon.setImageDrawable(null);
            DragIcon = null;
        }
    }

    private void beginDragChecks() {
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

    public void dragHandling(boolean fromList) {
        if (pointToPosition(currentXPos, currentYPos) != INVALID_POSITION) {
            if (currentYPos > mLastY) {
                mDirection = DOWN;
            } else if (currentYPos < mLastY) {
                mDirection = UP;
            }

            mLastY = currentYPos;

            if (mDirection == DOWN) {
                if(fromList)
                    mPosition = pointToPosition(currentXPos, (currentYPos + mDragView.getHeight()));
                else
                    mPosition = pointToPosition(currentXPos, (currentYPos + 200));  //todo: make scale
            } else {
                mPosition = pointToPosition(currentXPos, currentYPos);
            }

            if (!(mPosition == mDraggedItemDestination)) {
                switch (mDraggedItemType) {
                    case EXERCISE:
                        if (mPosition == getLastVisiblePosition()) {
                            View v = getChildAt(mPosition - getFirstVisiblePosition());
                            if (v.getId() == R.id.finalItem) {
                                mPosition--;
                            }
                        }
                        openUI(mPosition, getChildAt(mPosition - getFirstVisiblePosition()));
                        break;
                    case CIRCUIT:
                        if (mPosition == getLastVisiblePosition()) {
                            View v = getChildAt(mPosition - getFirstVisiblePosition());
                            if (v.getId() == R.id.finalItem) {
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

        if(fromList)
            drag(0, currentYPos);// replace 0 with x if desired
    }

    private boolean checkIfValidPosition() {
        boolean okayToDrag = false;
        int position = pointToPosition(currentXPos, currentYPos);
        int groupPosition;
        int childPosition;
        Exercise e;
        Circuit c;
        if (position != INVALID_POSITION) {
            if (getPackedPositionType(getExpandableListPosition(position)) == PACKED_POSITION_TYPE_CHILD) {
                childPosition = getPackedPositionChild(getExpandableListPosition(position));
                groupPosition = getPackedPositionGroup(getExpandableListPosition(position));
                e = Workout.get(groupPosition).getExercise(childPosition);
                if (!e.getName().equals("test")) {
                    okayToDrag = true;
                }
            } else if (getPackedPositionType(getExpandableListPosition(position)) == PACKED_POSITION_TYPE_GROUP) {
                groupPosition = getPackedPositionGroup(getExpandableListPosition(position));
                c = Workout.get(groupPosition);
                int workoutLength = Workout.size();
                if (c.isOpen() || groupPosition != (workoutLength - 1)) {
                    okayToDrag = true;
                }
            }
        }
        return okayToDrag;
    }

    public void removeCheckedItems() {

        int first = getFirstVisiblePosition();
        int last = getLastVisiblePosition();
        int viewsVisible = last - first;
        int childPosition;
        int groupPosition;
        ArrayList<View> viewsToRemove = new ArrayList<>();
        Exercise e;

        int startTime = 0;
        //NOTE GET CHILD RELATIVE
        //EXPLISTPOS OVERALL
        for (int i = 0; i <= viewsVisible + 1; i++) {
            if (getPackedPositionType(getExpandableListPosition(i + first)) == PACKED_POSITION_TYPE_CHILD) {

                //child
                childPosition = getPackedPositionChild(getExpandableListPosition(i + first));
                groupPosition = getPackedPositionGroup(getExpandableListPosition(i + first));
                e = Workout.get(groupPosition).getExercise(childPosition);
                if (e.isSaveToHistorySet()) {
                    //do animation

                    if (getChildAt(i) != null) {
                        viewsToRemove.add(0, getChildAt(i));
                    }
                }
            }
        }

        int length = viewsToRemove.size();
        ArrayList<Animator> animators = new ArrayList<Animator>();
        for (int j = 0; j <= length-1; j++) {
            View viewToRemove = viewsToRemove.get(j);

            ObjectAnimator anim = ObjectAnimator.ofFloat(viewToRemove, "translationX", 0, -SCREENWIDTH);
            anim.setDuration(150);
            animators.add(anim);



            if (j == (length - 1)) {
                //we're animating at least one thing
                setEnabled(false);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        WorkoutData.get(mContext).clearCheckedExercises();
                        ((WorkspaceActivity) mContext).getAdapter().notifyDataSetChanged();
                        setEnabled(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                startTime = startTime + 100;
            }
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(animators);
            set.start();
        }


        }
    }

