package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Adam on 6/27/2015.
 */
public class animatedExpandableListView extends ExpandableListView {

    private static final String logTag = "animExpListVw";

    private boolean mShouldRemoveObserver = false;
    private ArrayList<View> mAnimatedViews;
    private ArrayList<Circuit> mWorkout;
    private Context mContext;
    private List<View> mViewsToDraw = new ArrayList<View>();
    final static int EXPANSION_TIME = 400;


    //ANIMATION VARS
    private BitmapDrawable mHoverCell;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;
    private boolean mExpanding = false;
    private boolean mCollapsing = false;
    //private View mFooterView = null;
    //private int mFooterHeight = 0;
    //private animatedExpandableListView mListView;
    private HashMap<View, int[]> mOffsetViewPositions = new HashMap<>();
    private HashMap<View, int[]> mResizeViewPositions = new HashMap<>();
    private ArrayList<View> mOrderedResizeViews = new ArrayList<>();
    private ArrayList<Integer> mResizeViewCutoffs = new ArrayList<>();
    private ArrayList<Boolean> mIsItemResized = new ArrayList<>();
    private long mStartTime;
    private int mAnimationTime = 2000;
    private int mTopOfExpandingItems;
    private int mBottomOfExpandingItems;
    private int mOffsetOffset = 0;
    //private int mLastViewExpanded;
    private int mNumResizeViews;
    private int mFooterHeight;
    private float mPixelsResizedPerMS;
    private int mTotalOffset;

    public animatedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWorkout = WorkoutData.get(context).getWorkout();
        mContext = context;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mViewsToDraw.size() == 0) {
            return;
        }

        for (View v: mViewsToDraw) {
            canvas.translate(0, v.getTop());
            v.draw(canvas);
            canvas.translate(0, -v.getTop());
            //Log.d(logTag, "ViewHeight: " + v.getHeight());
        }
        //Log.d(logTag, "should check animation now");
        //if(mGroupExpander.shouldAnimate()) mGroupExpander.animate();

        if(mExpanding) animateExpansion();

        if(mCollapsing) animateCollapse();

        if (mHoverCell != null) {
            mHoverCell.draw(canvas);
        }

    }

    @Override
    public boolean expandGroup(int groupPos, boolean animate){
        //Log.d(logTag, "expandGroup(int groupPos, boolean animate) called on group: " + groupPos);
        return super.expandGroup(groupPos, animate);
    }

    @Override
    public boolean expandGroup(int groupPos){
        //Log.d(logTag, "expandGroup(int groupPos) called on group: " + groupPos);
        return super.expandGroup(groupPos);
    }

    @Override
    public boolean collapseGroup(int groupPos){
        //Log.d(logTag, "collapseGroup(int groupPos) called on group: " + groupPos);
        return super.collapseGroup(groupPos);
    }


    protected View getViewForID (long itemID){
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

    public void collapseGroupWithAnimation(final int groupPos){

        if(mWorkout.get(groupPos).getSize()<=1){
            collapseGroup(groupPos);
            return;
        }
        //Log.d(logTag, "collapseGroupWithAnimation(int groupPos) called on group: " + groupPos);
        //Expanded group things
        //final HashMap<View, int[]> collapsingViewInitialCoords = new HashMap<>();
        //final HashMap<View, int[]> offsetViewInitialCoords = new HashMap<>();
        //final ArrayList<View> orderedCollapsingViews = new ArrayList<>();
        int childCount = getChildCount();
        int firstVisible = getFirstVisiblePosition();
        int footerChildIndex = mWorkout.get(groupPos).getSize() - 1;
        View footer = null;
        View header = null;
        for (int i = 0; i < childCount; i++){
            long expLstPos = getExpandableListPosition(i + firstVisible);
            int group = getPackedPositionGroup(expLstPos);
            if(groupPos == group){
                View v = getChildAt(i);
                if (getPackedPositionType(expLstPos) == PACKED_POSITION_TYPE_CHILD) {
                    //group child
                    v.setHasTransientState(true);  //we don't want these recycled\
                    //Log.d(logTag, "childPos > footerChildIndex " + getPackedPositionChild(expLstPos) + " > " + footerChildIndex);
                    if(getPackedPositionChild(expLstPos) < footerChildIndex){//todo this is busted
                        //v is collapsing view
                        //Log.d(logTag, "in collapseGroupWithAnimation(int).  should now add view to mOrderedResiveViews.");
                        mOrderedResizeViews.add(v);
                        mResizeViewPositions.put(v, new int[]{v.getTop(), v.getBottom()});

                    } else {
                        //v is group footer
                        //offsetViewInitialCoords.put(v, new int[]{v.getTop(), v.getBottom()});
                        footer = v;
                    }
                    mViewsToDraw.add(v);
                } else {
                    //group header
                    header = v;
                    mOffsetOffset = v.getBottom();
                }
            }
        }

        final View headerView = header;
        final View footerView = footer;
        final int bottomOfListView = this.getHeight();
        int bottomCutoff = bottomOfListView;
        if(footerView != null) bottomCutoff = footerView.getTop();
        //Log.d(logTag, "bottom of listview: " + bottomOfListView + " bottomcutoff: " + bottomCutoff + " moffsetoffset: " + mOffsetOffset);
        mTotalOffset = bottomCutoff - mOffsetOffset;
        collapseGroup(groupPos);
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                //Collapsed group things
                int childCount = getChildCount();
                int firstVisible = getFirstVisiblePosition();

                for (int i = 0; i < childCount; i++){
                    long expLstPos = getExpandableListPosition(i + firstVisible);
                    int group = getPackedPositionGroup(expLstPos);
                    if(group > groupPos){
                        View v = getChildAt(i);
                        //store final offset
                        mOffsetViewPositions.put(v, new int []{v.getTop(), v.getBottom()});
                    }
                }

                //footer handling code

                View headerFooterView = headerView.findViewById(R.id.circuitFooter);
                //next if there is no footer to animate, add bitmap to represent it
                if (footerView == null){
                    //add hovercell just offscreen
                    mHoverCell = getAndAddHoverView(headerFooterView, bottomOfListView, headerFooterView.getLeft());
                } else {
                    //add footerview to offset view End Position map
                    mOffsetViewPositions.put(footerView, new int[]{footerView.getTop() - mTotalOffset, footerView.getBottom() - mTotalOffset});
                }
                //make headerfooter invis (get image view if footer view is null.)
                headerFooterView.setVisibility(View.INVISIBLE);


                setCollapsingViews();
                startSingleCollapse();

                return true;
            }
        });
    }

    public void expandGroupWithAnimation(final int groupPos){

        //Log.d(logTag, "Circuit pos: " + groupPos + " Workout size: " + mWorkout.get(groupPos).getSize());
        if(mWorkout.get(groupPos).getSize()<=1){
            expandGroup(groupPos);
            return;
        }

        final WorkspaceExpandableListAdapterMKIII adapter =
                (WorkspaceExpandableListAdapterMKIII)getExpandableListAdapter();
        final long groupHeaderId = adapter.getGroupId(groupPos);

        //Log.d(logTag, "expandGroupWithAnimation(int groupPos) called on group: " + groupPos);

        //Get locations of views to be offset by expansion
        int childCount = getChildCount();
        int firstVisible = getFirstVisiblePosition();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            //v.setHasTransientState(true); //ts 8-11-15
            long expLstPos = getExpandableListPosition(i + firstVisible);
            int group = getPackedPositionGroup(expLstPos);
            if (group > groupPos) {
                v.setHasTransientState(true); //ts 8-11-15
                mOffsetViewPositions.put(v, new int[]{v.getTop(), v.getBottom()});
            }
        }

        //Figure out where the group footer will be located, and it's size
        final View collapsedGroupView = getViewForID(groupHeaderId);
        View headerPortionOfGroupView = collapsedGroupView.findViewById(R.id.circuitHeader);
        int collapsedGroupViewTop = collapsedGroupView.getTop();
        int headerPortionOfGroupViewHeight = headerPortionOfGroupView.getBottom() - headerPortionOfGroupView.getTop();
        final int footerPortionOfGroupViewTop = collapsedGroupViewTop + headerPortionOfGroupViewHeight + collapsedGroupView.getPaddingTop() - 2; // -2 for reasons?

        final View footerPortionOfGroupView = collapsedGroupView.findViewById(R.id.circuitFooter);
        mFooterHeight = footerPortionOfGroupView.getBottom() - footerPortionOfGroupView.getTop();
        final int footerPortionOfGroupViewLeft = footerPortionOfGroupView.getLeft();
        final int footerPortionOfGroupViewBottom = footerPortionOfGroupViewTop + mFooterHeight;

        //Knowing things, expand group
        expandGroup(groupPos);
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);

                //get top and bottom for expanding view items and the group footer
                int childCount = getChildCount();
                int firstVisible = getFirstVisiblePosition();
                long expLstPos;
                int group, child, headerIndex = -1;
                int footerChildIndex = mWorkout.get(groupPos).getSize() - 1;
                View footerView = null;
                for (int i = 0; i < childCount; i++) {
                    expLstPos = getExpandableListPosition(i + firstVisible);
                    group = getPackedPositionGroup(expLstPos);
                    if (group == groupPos) {
                        child = getPackedPositionChild(expLstPos);
                        if (child > headerIndex && child < footerChildIndex) {
                            View v = getChildAt(i);
                            //v.setVisibility(View.GONE);
                            //v.setHasTransientState(true);
                            mResizeViewPositions.put(v, new int[]{v.getTop(), v.getBottom()});
                            mOrderedResizeViews.add(v);
                            //Log.d(logTag, "Expanding item: " + i + " Top: " + v.getTop() + " Bottom: " + v.getBottom());
                        } else if (child == footerChildIndex) {
                            footerView = getChildAt(i);//we'll need this later
                            mResizeViewPositions.put(footerView, new int[]{footerView.getTop(), footerView.getBottom()});//8415
                        }
                    }
                }

                //figure out which offset views will not be displayed after expansion
                //store them in mViewsToDraw so we can reference them for animation
                for (View offsetView : mOffsetViewPositions.keySet()) {
                    int[] offsetViewOld = mOffsetViewPositions.get(offsetView);
                    offsetView.setTop(offsetViewOld[0]);
                    offsetView.setBottom(offsetViewOld[1]);
                    if (offsetView.getParent() == null) {
                        mViewsToDraw.add(offsetView);
                    } else {
                        offsetView.setHasTransientState(false); //ts 8-11-15
                    }
                }

                //Set initial footer position
                if (footerView == null) { //if list footer is not on screen after expansion
                    //mFooterView = footerPortionOfGroupView;
                    //mFooterView.setVisibility(View.VISIBLE);
                    //mFooterHeight = footerHeight;
                    mHoverCell = getAndAddHoverView(footerPortionOfGroupView, footerPortionOfGroupViewTop, footerPortionOfGroupViewLeft);
                } else {//list footer is on screen after expansion, we can just animate it
                    footerView.setTop(footerPortionOfGroupViewTop);
                    footerView.setBottom(footerPortionOfGroupViewBottom);
                    mOffsetViewPositions.put(footerView, new int[]{footerPortionOfGroupViewTop, footerPortionOfGroupViewBottom});
                }

                //Disable Listview for animation

                setExpandingViews();

                startSingleExpansion();
                return true;
            }
        });
    }

    public void onFinishAnimation(){
        Log.d(logTag, "On Finish called");
        setEnabled(true);
        setClickable(true);

        if (mViewsToDraw.size() > 0) {
            for (View v : mViewsToDraw) {
                v.setHasTransientState(false); //8-11-2015
            }
        }

        mExpanding = false;
        mViewsToDraw.clear();
        mOffsetViewPositions.clear();
        mResizeViewPositions.clear();
        mOrderedResizeViews.clear();
        mResizeViewCutoffs.clear();
        mIsItemResized.clear();
        mHoverCell = null;
    }

    public void setExpandingViews(){
        mNumResizeViews = mOrderedResizeViews.size();
        mStartTime = System.currentTimeMillis();

        for(int i = 0; i < mNumResizeViews; i++){
            int[] old = mResizeViewPositions.get(mOrderedResizeViews.get(i));
            int top = old[0];
            int bottom = old[1];
            mResizeViewCutoffs.add(i, bottom);
            mIsItemResized.add(i, false);
            if(i == 0){
                mTopOfExpandingItems = top;
            }

            if(i == mNumResizeViews - 1){
                mBottomOfExpandingItems = bottom;
            }
        }

        int totalOffset = mBottomOfExpandingItems - mTopOfExpandingItems;
        //Log.d(logTag, mBottomOfExpandingItems + " - " + mTopOfExpandingItems + " = " + totalOffset);
        mPixelsResizedPerMS = (float) totalOffset / (float) mAnimationTime;
        //Log.d(logTag, totalOffset + " / " + mAnimationTime + " = " + mPixelsResizedPerMS);
    }

    public void setCollapsingViews(){
        mNumResizeViews = mOrderedResizeViews.size();
        //Log.d(logTag, "setCollapsingViews() called.  mNumResizeViews = " + mNumResizeViews);
        mStartTime = System.currentTimeMillis();
        for(int i = 0; i < mNumResizeViews; i++){
            int[] old = mResizeViewPositions.get(mOrderedResizeViews.get(i));
            mResizeViewCutoffs.add(i, old[0]);
            mIsItemResized.add(i, false);
        }
        mPixelsResizedPerMS = (float) mTotalOffset / (float) mAnimationTime;
        //for()
    }

    public void startSingleExpansion(){
        //Log.d(logTag, "startSingleExpansion() called.");
        setEnabled(false);
        setClickable(false);
        //mStartTime = System.currentTimeMillis();
        //set expanding views to height 0
        for(View v : mOrderedResizeViews){
            int[] old = mResizeViewPositions.get(v);
            v.setBottom(old[0]);
        }

        mExpanding = true;
        invalidate();
    }

    public void startSingleCollapse(){

        setEnabled(false);
        setClickable(false);
        //set positions for collapsing views
        for(View v : mOrderedResizeViews){//todo this is weird
            int[] old = mResizeViewPositions.get(v);
            v.setTop(old[0]);
            v.setBottom(old[1]);
            //Log.d(logTag, "top set to: " + old[0] + " bottom set to: " + old[1]);
        }

        mCollapsing = true;
        invalidate();
    }

    private void animateExpansion(){
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - mStartTime;
        //Log.d(logTag, currentTime + " - " + mStartTime + " = " + timeElapsed);
        int totalOffset = (int)(timeElapsed * mPixelsResizedPerMS);
        //Log.d(logTag, "pixels per ms = " + mPixelsResizedPerMS);
        int actualOffset = totalOffset + mTopOfExpandingItems;
        actualOffset = actualOffset + mFooterHeight/2;
        //Log.d(logTag, "In animate().  Total offset = " + totalOffset + " Top of expanding items = " + mTopOfExpandingItems
        //        + " actual offset: " + actualOffset);
        //figure out which view we are on
        for(int i = 0; i< mNumResizeViews; i++){
            if(actualOffset < mResizeViewCutoffs.get(i)){
                //we are on this view set the bottom
                mOrderedResizeViews.get(i).setBottom(actualOffset);
                //mOrderedResizeViews.get(i).setAlpha(0);
                break;
            } else if((mResizeViewCutoffs.get(i)<=actualOffset)&&(!mIsItemResized.get(i))){
                mIsItemResized.remove(i);
                mIsItemResized.add(i, true);
                mOrderedResizeViews.get(i).setBottom(mResizeViewCutoffs.get(i));
            }
        }
        if(totalOffset > (mBottomOfExpandingItems - mTopOfExpandingItems))
            totalOffset = mBottomOfExpandingItems - mTopOfExpandingItems;

        animateOffsetViews(totalOffset);

        invalidate();

        //Log.d(logTag, "Time elapsed: " + timeElapsed + " Animation Time: " + mAnimationTime);
        if(timeElapsed > mAnimationTime) {
            mExpanding = false;
            onFinishAnimation();
        }
    }

    private void animateCollapse(){
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - mStartTime;

        int resizeOffset = (int)(mTotalOffset - (timeElapsed * mPixelsResizedPerMS));
        //Log.d(logTag, "resize offset = " + resizeOffset + " = " + mTotalOffset + " - (" + timeElapsed + " * " + mPixelsResizedPerMS + ")");
        int trueOffsetPos = resizeOffset + mOffsetOffset;
        //go through list of views backwards
        for(int i = mNumResizeViews - 1; i > -1; i--){
            if (trueOffsetPos > mResizeViewCutoffs.get(i)) {
                View v = mOrderedResizeViews.get(i);
                v.setBottom(trueOffsetPos);
                Log.d(logTag, "in animateCollapse() view should be shrinking: trueOffset: " + trueOffsetPos
                        + " view top/bottom:" + v.getTop() + "/" + v.getBottom());
                v.setAlpha(0);
                break;
            } else if ( (trueOffsetPos<=mResizeViewCutoffs.get(i)) && (!mIsItemResized.get(i))){
                View v = mOrderedResizeViews.get(i);
                v.setBottom(v.getTop());
                mIsItemResized.remove(i);
                mIsItemResized.add(i, true);
            }
        }

        animateOffsetViews(resizeOffset);

        invalidate();
        if(timeElapsed > mAnimationTime){
            mCollapsing = false;
            onFinishAnimation();
        }
    }

    public void animateOffsetViews(int offset){
        //for (final View OffsetView : mOffsetViewPositions.keySet()){
        //Log.d(logTag, "animateOffsetViews() offset: " + offset);
        for (final View OffsetView : mOffsetViewPositions.keySet()){
            int[] coords = mOffsetViewPositions.get(OffsetView);
            int bottom = coords[1];
            int top = coords[0];
            OffsetView.setTop(top + offset);
            OffsetView.setBottom(bottom + offset);
        }

        if(mHoverCell != null){
            mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left, mHoverCellOriginalBounds.top + offset);
            mHoverCell.setBounds(mHoverCellCurrentBounds);
        }
    }

    private BitmapDrawable getAndAddHoverView(View v, int footerTop, int footerLeft) {

        int w = v.getWidth();
        int h = v.getHeight();
        //Log.d(logTag, "getandaddhoverview - w: " + w + "; h: " + h + "; top: " + top + "; left: " + left);
        Bitmap b = getBitmapFromView(v);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        mHoverCellOriginalBounds = new Rect(footerLeft, footerTop, footerLeft + w, footerTop + h);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

        drawable.setBounds(mHoverCellCurrentBounds);

        return drawable;
    }

    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
