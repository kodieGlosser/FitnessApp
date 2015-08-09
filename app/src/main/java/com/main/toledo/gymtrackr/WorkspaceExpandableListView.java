package com.main.toledo.gymtrackr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/22/2015.
 * WARNING:  THIS CODE IS BUTT
 * UPDATE: 5/13/2015
 * BUTT FACTOR REDUCED BY 60%
 * UPDATE: 5/19/2015 BUTT FACTOR REDUCED BY AN ADDITIONAL 15%
 * UPDATE: 6/27/2015 MOSTLY NOT BUTT
 */
public class WorkspaceExpandableListView extends animatedExpandableListView {

    //DEBUG SHIT
        //private boolean testflag = false;
    //

    private final String logTag = "WorkspaceExpandableList";

    private final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 15;
    private final int MOVE_DURATION = 150;
    private final int LINE_THICKNESS = 15;

    private ArrayList<Circuit> Workout = new ArrayList<>();

    private Context mContext;

    //SCROLL STUFF
    private boolean mDragMode = false;
    private boolean mIsMobileScrolling = false;
    private boolean mIsWaitingForScrollFinish = false;
    private int mSmoothScrollAmountAtEdge = 0;
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    //GLOBAL VARS
    private int SCREENWIDTH;
    private int SCREENHEIGHT;

    //DRAG COUNTDOWN VARS
    private boolean mDragInProgress = false;
    public int mDownY;
    public int mLastEventY;

    private int mTotalOffset = 0;
    //HoverView
    private BitmapDrawable mHoverCell;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;

    //pointer id
    private final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private final long INVALID_ID = -1;
    private long mAboveItemId = INVALID_ID;
    private long mMobileItemId = INVALID_ID;
    private long mBelowItemId = INVALID_ID;

    //SWAP VARS
    final private int INVALID_POSITION = -1;
    final private int ABOVE = 1;
    final private int BELOW = 2;

    private boolean mSwapInProgress = false;

    private int ABOVE_VALID_GROUP = INVALID_POSITION;
    private int ABOVE_VALID_POSITION = INVALID_POSITION;
    private boolean ABOVE_GROUP_IS_OPEN = false;

    private int BELOW_VALID_POSITION = INVALID_POSITION;
    private int BELOW_VALID_GROUP = INVALID_POSITION;
    private boolean BELOW_GROUP_IS_OPEN = false;

    private int CURRENT_CHILD = INVALID_POSITION;
    private int CURRENT_GROUP = INVALID_POSITION;
    private boolean CURRENT_GROUP_IS_OPEN = false;

    //private int LAST_CHILD = INVALID_POSITION;
    //private int LAST_GROUP = INVALID_POSITION;
   //private boolean LAST_GROUP_IS_OPEN = false;

    //TYPE
    private final int DRAGGED_ITEM_INVALID = -1;
    private int mDraggedItemType = DRAGGED_ITEM_INVALID;

    //ITEM IDS


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
        //dragHoldBounds = (int)(SCREENWIDTH * .047);
        //setOnItemLongClickListener(mOnItemLongClickListener);
        //setOnScrollListener(mScrollListener);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mSmoothScrollAmountAtEdge = (int)(SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        if ((action == MotionEvent.ACTION_DOWN && x > this.getWidth() * 9 / 10)) {
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
                    mDownY = y;
                    mTotalOffset = 0;
                    mActivePointerId = event.getPointerId(0);
                    int startPosition = pointToPosition(x, y);

                    if (checkIfValidPosition(startPosition)) {
                        View selectedView = getChildAt(startPosition - getFirstVisiblePosition());
                        //get item id
                        long expListPos = getExpandableListPosition(startPosition);
                        mDraggedItemType = getPackedPositionType(expListPos);

                        CURRENT_GROUP = getPackedPositionGroup(expListPos);
                        CURRENT_CHILD = getPackedPositionChild(expListPos);
                        CURRENT_GROUP_IS_OPEN = Workout.get(CURRENT_GROUP).isOpen();

                        WorkspaceExpandableListAdapterMKIII adapter =
                                (WorkspaceExpandableListAdapterMKIII) getExpandableListAdapter();

                        if(mDraggedItemType == PACKED_POSITION_TYPE_CHILD){
                            //\\\6/25mMobileItemId = Workout.get(CURRENT_GROUP).getExercise(CURRENT_CHILD).getStableID();
                            mMobileItemId = adapter.getChildId(CURRENT_GROUP, CURRENT_CHILD);
                            //Log.d(logTag, "Current Item id = " + mMobileItemId);
                        } else if(mDraggedItemType == PACKED_POSITION_TYPE_GROUP){
                            //\\\mMobileItemId = Workout.get(CURRENT_GROUP).getStableID();
                            mMobileItemId = adapter.getGroupId(CURRENT_GROUP);
                        }

                        mHoverCell = getAndAddHoverView(selectedView);

                        selectedView.setVisibility(INVISIBLE);
                        getNeighborPositions();
                        updateNeighborIDsForCurrentPosition();

                        mDragInProgress = true;
                        //startDrag();
                    }

                    break;
                case MotionEvent.ACTION_MOVE: //mose if moved
                    if (mActivePointerId == INVALID_POINTER_ID) {
                        break;
                    }

                    int pointerIndex = event.findPointerIndex(mActivePointerId);

                    mLastEventY = (int) event.getY(pointerIndex);
                    int deltaY = mLastEventY - mDownY;

                    if (mDragInProgress) {
                        mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left,
                                mHoverCellOriginalBounds.top + deltaY + mTotalOffset);
                        mHoverCell.setBounds(mHoverCellCurrentBounds);
                        invalidate();

                        handleCellSwitch();

                        mIsMobileScrolling = false;
                        handleMobileCellScroll();

                        return false;
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                    touchEventsCancelled();
                    break;
                case MotionEvent.ACTION_UP: //mouse button is released
                    touchEventsEnded();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                /* If a multitouch event took place and the original touch dictating
                 * the movement of the hover cell has ended, then the dragging event
                 * ends and the hover cell is animated to its corresponding position
                 * in the listview. */
                    pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                            MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        touchEventsEnded();
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void handleCellSwitch() {
        //Log.d(logTag, "handleCellSwitch()");
        final int deltaY = mLastEventY - mDownY;
        int deltaYTotal = mHoverCellOriginalBounds.top + mTotalOffset + deltaY;
        //Log.d(logTag, "top of handleCellSwitch() - mBelowItemId: " + mBelowItemId);
        View belowView = getViewForID(mBelowItemId);
        final View mobileView = getViewForID(mMobileItemId);
        View aboveView = getViewForID(mAboveItemId);

        boolean isBelow = (belowView != null) && (deltaYTotal > belowView.getTop());
        boolean isAbove = (aboveView != null) && (deltaYTotal < aboveView.getTop());
        //i like the use above and below thing
        if (isBelow || isAbove) {

            //Log.d(logTag, "is below: " + isBelow + "; is above: " + isAbove);
            //Log.d(logTag, "moving from circuit-group: " + CURRENT_GROUP + "-" + CURRENT_CHILD);
            final long switchItemID = isBelow ? mBelowItemId : mAboveItemId;
            final long currentId = mMobileItemId;

            //Log.d(logTag, "handleCellSwitch(), is below or above, switchItemID = " + switchItemID);
            View switchView = isBelow ? belowView : aboveView;
            //final int originalItem = getPositionForView(mobileView);
            /*
            if (switchView == null) {
                getNeighborPositions();
                updateNeighborIDsForCurrentPosition();
                return;
            }
            */
            ///old current
            if(isBelow) moveElement(BELOW);

            if(isAbove) moveElement(ABOVE);
            ///new current
            //for(Circuit c: Workout)
            //    for(Exercise e : c.getExercises())
            //       Log.d(logTag, c.getName() + " " + e.getName());

            mobileView.setVisibility(View.VISIBLE);
            ((WorkspaceExpandableListAdapterMKIII) getExpandableListAdapter()).notifyDataSetChanged();
            restoreListExpansion();

            mDownY = mLastEventY;

            final int switchViewStartTop = switchView.getTop();


            //Log.d(logTag, "handleCellSwitch(), getViewForID called on id: " + mMobileItemId);
            //testflag = true;
            //mobileView = getViewForID(mMobileItemId);
            //testflag = false;
            //Log.d(logTag, "handleCellSwitch(), MOBILE VIEW INVISIBLE!");
            //mobileView.setVisibility(View.INVISIBLE);
            //Alteration 6/5
            //mobileView.setVisibility(View.VISIBLE);
            //switchView.setVisibility(View.INVISIBLE);

            getNeighborPositions();
            updateNeighborIDsForCurrentPosition();

            final ViewTreeObserver observer = getViewTreeObserver();

            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {



                    observer.removeOnPreDrawListener(this);
                    //Log.d(logTag, "Current item ID: " + currentId);
                    //Log.d(logTag, "PreDraw Switch item ID: " + switchItemID);
                    /*
                    Log.d(logTag, "CurrentGroup: "
                            + CURRENT_GROUP
                            + " BelowGroup "
                            + BELOW_VALID_GROUP);
                    */


                    //testflag = true;
                    getViewForID(currentId).setVisibility(View.INVISIBLE);



                    View switchView = getViewForID(switchItemID);

                    //testflag = false;

                    mTotalOffset += deltaY;
                    int switchViewNewTop = switchView.getTop();
                    int delta = switchViewStartTop - switchViewNewTop;

                    switchView.setTranslationY(delta);

                    ObjectAnimator animator = ObjectAnimator.ofFloat(switchView,
                            View.TRANSLATION_Y, 0);
                    animator.setDuration(MOVE_DURATION);
                    animator.start();
                    return true;
                }
            });

        }
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHoverCell != null) {
            mHoverCell.draw(canvas);
        }
    }
    //TODO MODIFY DATASET VIA ADAPTER
    private void moveElement(int useWhich) {
        //Log.d(logTag, "moveElement() - called");
        int TARGET_GROUP;
        int TARGET_POSITION;
        boolean TARGET_GROUP_IS_OPEN;
        WorkspaceExpandableListAdapterMKIII adapter =
                (WorkspaceExpandableListAdapterMKIII) getExpandableListAdapter();
        //Exercise originalExercise;
        //Exercise copyOfExercise;

        Exercise temp = Workout.get(CURRENT_GROUP).getExercise(CURRENT_CHILD);
        //Log.d(logTag, "Moving item: " + temp.getName() + " with id " + temp.getStableID() + " CURRENT GROUP: " + CURRENT_GROUP +" CURRENT CHILD " + CURRENT_CHILD);
        switch(useWhich){
            case ABOVE:
                //Log.d(logTag, "addElement - using above");
                TARGET_GROUP = ABOVE_VALID_GROUP;
                TARGET_POSITION = ABOVE_VALID_POSITION;
                TARGET_GROUP_IS_OPEN = ABOVE_GROUP_IS_OPEN;

                if(TARGET_GROUP_IS_OPEN){
                    //moving to open group
                    Workout.get(TARGET_GROUP).add(TARGET_POSITION, temp);
                    if(CURRENT_GROUP_IS_OPEN){
                        //target open, current open
                        Workout.get(CURRENT_GROUP).removeExercise(CURRENT_CHILD + 1);
                        //WORKS
                    }else{
                        //target open, current closed
                        //\\\Workout.get(CURRENT_GROUP).removeExercise(CURRENT_CHILD);
                        //\\\Workout.remove(CURRENT_GROUP); 
                        adapter.removeGroup(CURRENT_GROUP);
                    }
                    CURRENT_GROUP_IS_OPEN = TARGET_GROUP_IS_OPEN;
                    CURRENT_GROUP = TARGET_GROUP;
                    CURRENT_CHILD = TARGET_POSITION;
                } else {
                    //MOVING TO NEW CLOSED GROUP
                    //\\\WorkoutData.get(mContext).placeClosedCircuitWithExercise(TARGET_GROUP, temp);

                    adapter.addGroup(TARGET_GROUP, WorkoutData.getNewClosedCircuit(temp));

                    if(CURRENT_GROUP_IS_OPEN){
                        //target closed, current open
                        Workout.get(CURRENT_GROUP + 1).removeExercise(CURRENT_CHILD);
                        CURRENT_GROUP_IS_OPEN = false;
                        CURRENT_GROUP = TARGET_GROUP;
                        CURRENT_CHILD = 0;
                    }else{
                        //target closed, current closed
                        Workout.get(CURRENT_GROUP + 1).removeExercise(CURRENT_CHILD);
                        //\\\Workout.remove(CURRENT_GROUP + 1);
                        adapter.removeGroup(CURRENT_GROUP + 1);
                        CURRENT_GROUP_IS_OPEN = false;
                        CURRENT_GROUP = TARGET_GROUP;
                        CURRENT_CHILD = 0;
                    }
                }

                break;
            case BELOW:

                TARGET_GROUP = BELOW_VALID_GROUP;
                TARGET_POSITION = BELOW_VALID_POSITION;
                TARGET_GROUP_IS_OPEN = BELOW_GROUP_IS_OPEN;

                if(TARGET_GROUP_IS_OPEN){
                    //Target open

                    if(CURRENT_GROUP_IS_OPEN){
                        //Target open, current open
                        Workout.get(TARGET_GROUP).add(TARGET_POSITION + 1, temp);
                        Workout.get(CURRENT_GROUP).removeExercise(CURRENT_CHILD);
                        CURRENT_CHILD = TARGET_POSITION;
                        CURRENT_GROUP = TARGET_GROUP;
                        CURRENT_GROUP_IS_OPEN = true;
                    } else {
                        //target open, current closed
                        if(Workout.get(TARGET_GROUP).isExpanded()){
                            //open target group is expanded
                            Workout.get(TARGET_GROUP).add(0, temp);
                            Workout.get(CURRENT_GROUP).removeExercise(CURRENT_CHILD);
                            //\\\Workout.remove(CURRENT_GROUP);
                            adapter.removeGroup(CURRENT_GROUP);
                            CURRENT_GROUP_IS_OPEN = true;
                        }else{
                            //open target group is collapsed
                            //\\\WorkoutData.get(mContext).placeClosedCircuitWithExercise(TARGET_GROUP + 1, temp);
                            adapter.addGroup(TARGET_GROUP + 1, WorkoutData.getNewClosedCircuit(temp));
                            Workout.get(CURRENT_GROUP).removeExercise(CURRENT_CHILD);
                            //\\\Workout.remove(CURRENT_GROUP);
                            adapter.removeGroup(CURRENT_GROUP);
                            CURRENT_GROUP_IS_OPEN = false;
                            /*
                            Log.d(logTag, "CLOSED CIRCUIT PLACED AT POSITION: "
                                    +(TARGET_GROUP + 1)
                                    +" ITEM ID IS: "
                                    +Workout.get(TARGET_GROUP + 1).getExercise(0).getStableID());

                            for(Circuit c : Workout){
                                Log.d(logTag, c.getName());
                                for(Exercise e : c.getExercises())
                                    Log.d(logTag, e.getName());
                            }
                            */
                            CURRENT_GROUP = TARGET_GROUP;
                            CURRENT_CHILD = 0;
                        }
                    }
                } else {
                    //target group closed.
                    //Log.d(logTag, "moveElement() - moving from: " + CURRENT_GROUP + " - child:" + CURRENT_CHILD);
                    //Log.d(logTag, "moveElement() - moving to group: " + TARGET_GROUP + " - child:" + TARGET_POSITION);
                    if(CURRENT_GROUP_IS_OPEN){
                        //\\\WorkoutData.get(mContext).placeClosedCircuitWithExercise(TARGET_GROUP, temp);
                        adapter.addGroup(TARGET_GROUP, WorkoutData.getNewClosedCircuit(temp));
                        Workout.get(CURRENT_GROUP).removeExercise(CURRENT_CHILD);
                    //open to closed
                    } else {
                        //\\\WorkoutData.get(mContext).placeClosedCircuitWithExercise(TARGET_GROUP + 1, temp);
                        adapter.addGroup(TARGET_GROUP + 1, WorkoutData.getNewClosedCircuit(temp));
                        Workout.get(CURRENT_GROUP).removeExercise(CURRENT_CHILD);
                        //closed to closed
                        //Log.d(logTag, "CLOSED TO CLOSED");
                        //\\\Workout.remove(CURRENT_GROUP);
                        adapter.removeGroup(CURRENT_GROUP);
                    }
                    CURRENT_GROUP_IS_OPEN = false;
                    CURRENT_GROUP = TARGET_GROUP;
                    CURRENT_CHILD = TARGET_POSITION;
                    //Log.d(logTag, "CURRENT_GROUP: " + CURRENT_GROUP + " CURRENT_CHILD: "+ CURRENT_CHILD);
                }

                break;
            default:
                return;
        }
        //Log.d(logTag, "moveElement() CURRENT POSITIONS SET - CURRENT_GROUP: " + CURRENT_GROUP + " - CURRENT_CHILD: " + CURRENT_CHILD);
        //for(Circuit c: Workout)
         //   for(Exercise e : c.getExercises())
         //       Log.d(logTag, "IN MOVE ELEMENT - " + c.getName() + " " + e.getName());
        //Log.d(logTag, "moveElement() BELOW POSITIONS SET - BELOW_GROUP: " + BELOW_VALID_GROUP + " - BELOW_CHILD: " + BELOW_VALID_POSITION);
    }

    private BitmapDrawable getAndAddHoverView(View v) {

        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();
        //Log.d(logTag, "getandaddhoverview - w: " + w + "; h: " + h + "; top: " + top + "; left: " + left);
        Bitmap b = getBitmapWithBorder(v);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

        drawable.setBounds(mHoverCellCurrentBounds);

        return drawable;
    }

    /** Draws a black border over the screenshot of the view passed in. */
    private Bitmap getBitmapWithBorder(View v) {
        Bitmap bitmap = getBitmapFromView(v);
        Canvas can = new Canvas(bitmap);

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_THICKNESS);
        paint.setColor(Color.BLACK);

        can.drawBitmap(bitmap, 0, 0, null);
        can.drawRect(rect, paint);

        return bitmap;
    }

    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void updateNeighborIDsForCurrentPosition() {
        //above id

        WorkspaceExpandableListAdapterMKIII adapter =
                (WorkspaceExpandableListAdapterMKIII) getExpandableListAdapter();

        if(CURRENT_GROUP == INVALID_POSITION) return;


        if(CURRENT_CHILD == INVALID_POSITION){
            //group header
            mAboveItemId = INVALID_ID;
            mBelowItemId = INVALID_ID;
            return;
        }

        if (CURRENT_GROUP_IS_OPEN) {
            //current group is open
            if (CURRENT_CHILD == 0) {
                //switch view id is group header id
                //\\\mAboveItemId = Workout.get(CURRENT_GROUP).getStableID();
                mAboveItemId = adapter.getGroupId(CURRENT_GROUP);
            } else {
                //regular group scenario use above
                //\\\mAboveItemId = Workout.get(ABOVE_VALID_GROUP).getExercise(ABOVE_VALID_POSITION).getStableID();
                mAboveItemId = adapter.getChildId(ABOVE_VALID_GROUP, ABOVE_VALID_POSITION);
            }
        } else {
            //current group is closed
            //if top group is invalid (Current is first)
            if (ABOVE_VALID_GROUP == INVALID_POSITION) {
                mAboveItemId = INVALID_ID;
            } else {
                //swap with above valid group
                Circuit c = Workout.get(ABOVE_VALID_GROUP);
                if(c.isOpen() && !c.isExpanded()){
                    //group header id
                    //\\\mAboveItemId = c.getStableID();
                    mAboveItemId = adapter.getGroupId(ABOVE_VALID_GROUP);
                }else{
                    //child id
                    //\\\mAboveItemId = c.getExercise(ABOVE_VALID_POSITION).getStableID();
                    mAboveItemId = adapter.getChildId(ABOVE_VALID_GROUP, ABOVE_VALID_POSITION);
                }

            }
        }

        //below id
        if (!CURRENT_GROUP_IS_OPEN) {
            //closed group
            if (BELOW_GROUP_IS_OPEN) {
                //switch item is group header
                //\\\mBelowItemId = Workout.get(BELOW_VALID_GROUP).getStableID();
                mBelowItemId = adapter.getGroupId(BELOW_VALID_GROUP);
            } else {
                //below group is closed get next item
                //\\\mBelowItemId = Workout.get(BELOW_VALID_GROUP).getExercise(BELOW_VALID_POSITION).getStableID();
                mBelowItemId = adapter.getChildId(BELOW_VALID_GROUP, BELOW_VALID_POSITION);
            }

        } else {
            //from open circuit
            if (!BELOW_GROUP_IS_OPEN) {
                //to closed circuit.  return final item
                //\\\mBelowItemId = Workout.get(CURRENT_GROUP).getExercise(CURRENT_CHILD + 1).getStableID();
                mBelowItemId = adapter.getChildId(CURRENT_GROUP, CURRENT_CHILD + 1);
            } else {
                //otherwise return next
                //\\\mBelowItemId = Workout.get(BELOW_VALID_GROUP).getExercise(BELOW_VALID_POSITION).getStableID();
                mBelowItemId = adapter.getChildId(BELOW_VALID_GROUP, BELOW_VALID_POSITION);
            }
        }
        //Log.d(logTag, "updateNeighborIDsForCurrentPosition called: mBelowItemId = " + mBelowItemId);
        //Log.d(logTag, "updateNeighborIDsForCurrentPosition called: mAboveItemId = " + mAboveItemId);
    }
    /*
    public View getViewForID (long itemID){
        //Log.d(logTag, "getViewForId() looking for id: " + itemID);
        WorkspaceExpandableListAdapterMKIII adapter =
                (WorkspaceExpandableListAdapterMKIII) getExpandableListAdapter();

        int firstVisible = getFirstVisiblePosition();
        for(int i = 0; i < getChildCount(); i++) {

            Long expLstPos = getExpandableListPosition(firstVisible + i);
            int group = getPackedPositionGroup(expLstPos);
            int type = getPackedPositionType(expLstPos);
            int child = getPackedPositionChild(expLstPos);
            //we're gonna get the position of the list item in exp list speak
            //then check that id vs the id of what we think the id should be
            //if(testflag)Log.d(logTag, "getViewForID - looking for id: " + itemID);
            //Log.d(logTag, "getViewForID - i: " + i);
            //Log.d(logTag, "getViewForID - explstpos: " + expLstPos);
            //if(testflag)Log.d(logTag, "getViewForID - LOOKING FOR VIEW AT GROUP: " + group + " + CHILD: " + child);
            if (type == PACKED_POSITION_TYPE_GROUP){
                //Log.d(logTag, "getViewForID - type is group");
                //Log.d(logTag, "getViewForID, group: " + group + " found, id == " + Workout.get(group).getStableID());
                //\\\if(Workout.get(group).getStableID() == itemID) {
                if(adapter.getGroupId(group) == itemID){
                    //if(testflag)Log.d(logTag, "FOUND GROUP! ID: " + itemID);
                    return getChildAt(i);
                }

            } else if (type == PACKED_POSITION_TYPE_CHILD){
                //Log.d(logTag, "getViewForID, group/child/name " + group +"/" + child + "/" + Workout.get(group).getExercise(child).getName() + " found, id == " + Workout.get(group).getExercise(child).getStableID() + " - i : " + i);
                //\\\if(Workout.get(group).getExercise(child).getStableID() == itemID) {
                if(adapter.getChildId(group, child) == itemID){
                    //if(testflag)Log.d(logTag, "FOUND CHILD! ID: " + itemID);

                    //if (testflag) return getChildAt(i - 1);
                    return getChildAt(i);
                }
            }
        }
        //Log.d(logTag, "getViewForId() returning null!!!");
        return null;
    }
    */
    private void getNeighborPositions(){

        boolean curGrpLast;

        curGrpLast = CURRENT_GROUP == Workout.size()-1;

        //DEFAULT VALS

        ABOVE_VALID_GROUP = INVALID_POSITION;
        ABOVE_VALID_POSITION = INVALID_POSITION;
        ABOVE_GROUP_IS_OPEN = false;

        BELOW_VALID_GROUP = INVALID_POSITION;
        BELOW_VALID_POSITION = INVALID_POSITION;
        BELOW_GROUP_IS_OPEN = false;

        //LAST_GROUP = CURRENT_GROUP;
        //LAST_CHILD = CURRENT_CHILD;
        //LAST_GROUP_IS_OPEN = CURRENT_GROUP_IS_OPEN;
        //*****
        //
        //CODE TO GET NEXT POSITION OF ITEM
        //
        //****

        //CIRCUIT IS OPEN?
        if (CURRENT_GROUP_IS_OPEN) {
            Circuit curCircuit = Workout.get(CURRENT_GROUP);
            //Next item is last (name eq test)
            if(curCircuit.getExercise(CURRENT_CHILD+1).getName().equals("test")){
                //next pos will be new closed circuit
                BELOW_GROUP_IS_OPEN = false;
                BELOW_VALID_POSITION = 0;
                BELOW_VALID_GROUP = CURRENT_GROUP + 1;
            } else {
                //next item is neighbor
                BELOW_GROUP_IS_OPEN = true;
                BELOW_VALID_POSITION = CURRENT_CHILD + 1;
                BELOW_VALID_GROUP = CURRENT_GROUP;
            }
        } else {
            //in closed circuit
            //is last circuit?
            if(curGrpLast){
                //at end, do nothing
                BELOW_VALID_GROUP = INVALID_POSITION;
                BELOW_VALID_POSITION = INVALID_POSITION;
            } else {
                //not at end
                if(Workout.get(CURRENT_GROUP + 1).isOpen()){
                    //Next circuit open
                    //add to beginning
                    BELOW_VALID_GROUP = CURRENT_GROUP + 1;
                    BELOW_VALID_POSITION = 0;
                    BELOW_GROUP_IS_OPEN = true;


                } else {
                    //next circuit is closed
                    BELOW_VALID_GROUP = CURRENT_GROUP + 1;
                    BELOW_VALID_POSITION = 0;
                    BELOW_GROUP_IS_OPEN = false;
                }
            }
        }

        //*****
        //
        //CODE TO GET PREV POSITION OF ITEM
        //
        //****
        //if item is first
        if(CURRENT_CHILD == 0){
            if(CURRENT_GROUP_IS_OPEN){
                //first item in open circuit
                //  make new closed
                ABOVE_VALID_GROUP = CURRENT_GROUP;
                ABOVE_VALID_POSITION = 0;
                ABOVE_GROUP_IS_OPEN = false;
            } else {
                //item in closed circuit
                if(CURRENT_GROUP == 0){
                    //  do nothing if first circuit
                    ABOVE_VALID_POSITION = INVALID_POSITION;
                    ABOVE_VALID_GROUP = INVALID_POSITION;
                }else {
                    Circuit c = Workout.get(CURRENT_GROUP -1);
                    if(c.isOpen()){
                        //  add to bottom of prev open
                        if(c.isExpanded()) {
                            //prev group is expanded
                            ABOVE_VALID_GROUP = CURRENT_GROUP - 1;
                            ABOVE_VALID_POSITION = Workout.get(CURRENT_GROUP - 1).getSize() - 1;
                            ABOVE_GROUP_IS_OPEN = true;
                        } else {
                            //prev group is not expanded

                            ABOVE_VALID_GROUP = CURRENT_GROUP - 1;
                            ABOVE_VALID_POSITION = 0;
                            ABOVE_GROUP_IS_OPEN = false;
                        }
                    } else{
                        //  swap with prev closed
                        ABOVE_VALID_GROUP = CURRENT_GROUP - 1;
                        ABOVE_VALID_POSITION = 0;
                        ABOVE_GROUP_IS_OPEN = false;
                    }
                }
            }
        }else{
            //swap with prev child
            ABOVE_VALID_GROUP = CURRENT_GROUP;
            ABOVE_VALID_POSITION = CURRENT_CHILD - 1;
            ABOVE_GROUP_IS_OPEN = true;
        }
        //Log.d(logTag, "above group/position/open: " + ABOVE_VALID_GROUP + "/" + ABOVE_VALID_POSITION + "/" + ABOVE_GROUP_IS_OPEN);
        //Log.d(logTag, "current group/position/open: " + CURRENT_GROUP + "/" + CURRENT_CHILD + "/" + CURRENT_GROUP_IS_OPEN);
        //Log.d(logTag, "below group/position/open: " + BELOW_VALID_GROUP + "/" + BELOW_VALID_POSITION + "/" + BELOW_GROUP_IS_OPEN);
    }

    private void touchEventsEnded () {
        /*
        for(Circuit c : Workout){
            Log.d(logTag, c.getName());
            for(Exercise e : c.getExercises())
                Log.d(logTag, e.getName());
        }
        */
        //Log.d(logTag, "touchEventsEnded()");
        final View mobileView = getViewForID(mMobileItemId);
        if (mDragInProgress|| mIsWaitingForScrollFinish) {
            mDragInProgress = false;
            mIsWaitingForScrollFinish = false;
            mIsMobileScrolling = false;
            mActivePointerId = INVALID_POINTER_ID;

            // If the autoscroller has not completed scrolling, we need to wait for it to
            // finish in order to determine the final location of where the hover cell
            // should be animated to.
            if (mScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                mIsWaitingForScrollFinish = true;
                return;
            }

            mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left, mobileView.getTop());

            ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(mHoverCell, "bounds",
                    sBoundEvaluator, mHoverCellCurrentBounds);
            hoverViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    invalidate();
                }
            });
            hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAboveItemId = INVALID_ID;
                    mMobileItemId = INVALID_ID;
                    mBelowItemId = INVALID_ID;
                    mobileView.setVisibility(VISIBLE);
                    mDragMode = false;
                    mHoverCell = null;
                    setEnabled(true);
                    invalidate();
                }
            });
            hoverViewAnimator.start();
        } else {
            touchEventsCancelled();
        }
    }

    /**
     * Resets all the appropriate fields to a default state.
     */
    private void touchEventsCancelled () {
       // Log.d(logTag, "touchEventsCancelled() mobileItemID: " + mMobileItemId);

        View mobileView = getViewForID(mMobileItemId);
        if (mDragMode) {
            mAboveItemId = INVALID_ID;
            mMobileItemId = INVALID_ID;
            mBelowItemId = INVALID_ID;
            mobileView.setVisibility(VISIBLE);

            mHoverCell = null;
            invalidate();
        }
        mDragMode = false;
        mIsMobileScrolling = false;
        mActivePointerId = INVALID_POINTER_ID;
    }

    private final static TypeEvaluator<Rect> sBoundEvaluator = new TypeEvaluator<Rect>() {
        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            return new Rect(interpolate(startValue.left, endValue.left, fraction),
                    interpolate(startValue.top, endValue.top, fraction),
                    interpolate(startValue.right, endValue.right, fraction),
                    interpolate(startValue.bottom, endValue.bottom, fraction));
        }

        public int interpolate(int start, int end, float fraction) {
            return (int)(start + fraction * (end - start));
        }
    };

    private void handleMobileCellScroll() {
        mIsMobileScrolling = handleMobileCellScroll(mHoverCellCurrentBounds);
    }

    public boolean handleMobileCellScroll(Rect r) {
        int offset = computeVerticalScrollOffset();
        int height = getHeight();
        int extent = computeVerticalScrollExtent();
        int range = computeVerticalScrollRange();
        int hoverViewTop = r.top;
        int hoverHeight = r.height();

        if (hoverViewTop <= 0 && offset > 0) {
            smoothScrollBy(-mSmoothScrollAmountAtEdge, 0);
            return true;
        }

        if (hoverViewTop + hoverHeight >= height && (offset + extent) < range) {
            smoothScrollBy(mSmoothScrollAmountAtEdge, 0);
            return true;
        }

        return false;
    }

    private boolean checkIfValidPosition(int startPosition) {
        boolean okayToDrag = false;
        int groupPosition;
        int childPosition;
        Exercise e;
        Circuit c;
        if (startPosition != INVALID_POSITION) {
            if (getPackedPositionType(getExpandableListPosition(startPosition)) == PACKED_POSITION_TYPE_CHILD) {
                childPosition = getPackedPositionChild(getExpandableListPosition(startPosition));
                groupPosition = getPackedPositionGroup(getExpandableListPosition(startPosition));
                e = Workout.get(groupPosition).getExercise(childPosition);
                if (!e.getName().equals("test")) {
                    okayToDrag = true;
                }
            } else if (getPackedPositionType(getExpandableListPosition(startPosition)) == PACKED_POSITION_TYPE_GROUP) {
                groupPosition = getPackedPositionGroup(getExpandableListPosition(startPosition));
                c = Workout.get(groupPosition);
                int workoutLength = Workout.size();
                if (c.isOpen() || groupPosition != (workoutLength - 1)) {
                    okayToDrag = true;
                }
            }
        }
        return okayToDrag;
    }

    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener () {

        private int mPreviousFirstVisibleItem = -1;
        private int mPreviousVisibleItemCount = -1;
        private int mCurrentFirstVisibleItem;
        private int mCurrentVisibleItemCount;
        private int mCurrentScrollState;

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            mCurrentFirstVisibleItem = firstVisibleItem;
            mCurrentVisibleItemCount = visibleItemCount;

            mPreviousFirstVisibleItem = (mPreviousFirstVisibleItem == -1) ? mCurrentFirstVisibleItem
                    : mPreviousFirstVisibleItem;
            mPreviousVisibleItemCount = (mPreviousVisibleItemCount == -1) ? mCurrentVisibleItemCount
                    : mPreviousVisibleItemCount;

            checkAndHandleFirstVisibleCellChange();
            checkAndHandleLastVisibleCellChange();

            mPreviousFirstVisibleItem = mCurrentFirstVisibleItem;
            mPreviousVisibleItemCount = mCurrentVisibleItemCount;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mCurrentScrollState = scrollState;
            mScrollState = scrollState;
            isScrollCompleted();
        }

        /**
         * This method is in charge of invoking 1 of 2 actions. Firstly, if the listview
         * is in a state of scrolling invoked by the hover cell being outside the bounds
         * of the listview, then this scrolling event is continued. Secondly, if the hover
         * cell has already been released, this invokes the animation for the hover cell
         * to return to its correct position after the listview has entered an idle scroll
         * state.
         */
        private void isScrollCompleted() {
            if (mCurrentVisibleItemCount > 0 && mCurrentScrollState == SCROLL_STATE_IDLE) {
                if (mDragMode && mIsMobileScrolling) {
                    handleMobileCellScroll();
                } else if (mIsWaitingForScrollFinish) {
                    touchEventsEnded();
                }
            }
        }

        /**
         * Determines if the listview scrolled up enough to reveal a new cell at the
         * top of the list. If so, then the appropriate parameters are updated.
         */
        public void checkAndHandleFirstVisibleCellChange() {
            if (mCurrentFirstVisibleItem != mPreviousFirstVisibleItem) {
                if (mDragMode && mMobileItemId != INVALID_ID) {
                    updateNeighborIDsForCurrentPosition();
                    handleCellSwitch();
                }
            }
        }

        /**
         * Determines if the listview scrolled down enough to reveal a new cell at the
         * bottom of the list. If so, then the appropriate parameters are updated.
         */
        public void checkAndHandleLastVisibleCellChange() {
            int currentLastVisibleItem = mCurrentFirstVisibleItem + mCurrentVisibleItemCount;
            int previousLastVisibleItem = mPreviousFirstVisibleItem + mPreviousVisibleItemCount;
            if (currentLastVisibleItem != previousLastVisibleItem) {
                if (mDragMode && mMobileItemId != INVALID_ID) {
                    updateNeighborIDsForCurrentPosition();
                    handleCellSwitch();
                }
            }
        }
    };

    public void restoreListExpansion(){
        int length = Workout.size();
        for (int i = 0; i < length; i++){
            if(Workout.get(i).isExpanded()){
                expandGroup(i);
            } else {
                collapseGroup(i);
            }
        }
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
                        ((WorkspaceExpandableListAdapterMKIII) getExpandableListAdapter()).notifyDataSetChanged();
                        restoreListExpansion();
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

