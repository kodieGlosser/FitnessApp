package com.main.toledo.gymtrackr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private boolean mExpanding = false;
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
        }
    }

    @Override
    public boolean expandGroup(int groupPos, boolean animate){
        Log.d(logTag, "expandGroup(int groupPos, boolean animate) called on group: " + groupPos);
        return super.expandGroup(groupPos, animate);
    }

    @Override
    public boolean expandGroup(int groupPos){
        Log.d(logTag, "expandGroup(int groupPos) called on group: " + groupPos);
        return super.expandGroup(groupPos);
    }

    @Override
    public boolean collapseGroup(int groupPos){
        Log.d(logTag, "collapseGroup(int groupPos) called on group: " + groupPos);
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
    /*
    private BitmapDrawable getAndAddHoverView(View v) {

        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();
        //Log.d(logTag, "getandaddhoverview - w: " + w + "; h: " + h + "; top: " + top + "; left: " + left);
        Bitmap b = getBitmapFromView(v);//eating this method

        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
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
    */

    public void animateOffsetViews(HashMap<View, int[]> viewsToOffset, int offset){//todo
        for (final View OffsetView : viewsToOffset.keySet()){
            int[] coords = viewsToOffset.get(OffsetView);
            int bottom = coords[1];
            int top = coords[0];
            OffsetView.setTop(top + offset);
            OffsetView.setBottom(bottom + offset);
        }
        invalidate();
    }

    public void collapseGroupWithAnimation(int groupPos){
        collapseGroup(groupPos);
        //Log.d(logTag, "collapseGroupWithAnimation(int groupPos) called on group: " + groupPos);
        final ViewTreeObserver observer = getViewTreeObserver();

        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);

                return true;
            }
        });
    }

    public void expandGroupWithAnimation(final int groupPos){


        final WorkspaceExpandableListAdapterMKIII adapter =
                (WorkspaceExpandableListAdapterMKIII)getExpandableListAdapter();
        final animatedExpandableListView listView = this;
        final int groupSize = mWorkout.get(groupPos).getSize();
        final HashMap<View, int[]> expandingViewFinalCoords = new HashMap<View, int[]>();
        final HashMap<View, int[]> startViewPositions = new HashMap<View, int[]>();
        final long groupHeaderId = adapter.getGroupId(groupPos);

        //Log.d(logTag, "expandGroupWithAnimation(int groupPos) called on group: " + groupPos);

        //Get locations of views to be offset by expansion
        int childCount = getChildCount();
        int firstVisible = getFirstVisiblePosition();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            long expLstPos = getExpandableListPosition(i + firstVisible);
            int group = getPackedPositionGroup(expLstPos);
            if (group > groupPos) {
                v.setHasTransientState(true);
                startViewPositions.put(v, new int[]{v.getTop(), v.getBottom()});
            }
        }

        //Figure out where the group footer will be located, and it's size
        final View collapsedGroupView = getViewForID(groupHeaderId);
        View headerPortionOfGroupView = collapsedGroupView.findViewById(R.id.circuitHeader);
        int collapsedGroupViewTop = collapsedGroupView.getTop();
        int headerPortionOfGroupViewHeight = headerPortionOfGroupView.getBottom() - headerPortionOfGroupView.getTop();
        final int footerPortionOfGroupViewTop = collapsedGroupViewTop + headerPortionOfGroupViewHeight + collapsedGroupView.getPaddingTop() - 2; // -2 for reasons?

        View footerPortionOfGroupView = collapsedGroupView.findViewById(R.id.circuitFooter);
        final int footerHeight = footerPortionOfGroupView.getBottom() - footerPortionOfGroupView.getTop();

        final int footerPortionOfGroupViewBottom = footerPortionOfGroupViewTop + footerHeight;

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
                ArrayList<View> orderedExpandingViews = new ArrayList<View>();
                for (int i = 0; i < childCount; i++) {
                    expLstPos = getExpandableListPosition(i + firstVisible);
                    group = getPackedPositionGroup(expLstPos);
                    if (group == groupPos) {
                        child = getPackedPositionChild(expLstPos);
                        if (child > headerIndex && child < footerChildIndex) {
                            View v = getChildAt(i);
                            //v.setVisibility(View.GONE);
                            //v.setHasTransientState(true);
                            expandingViewFinalCoords.put(v, new int[]{v.getTop(), v.getBottom()});
                            orderedExpandingViews.add(v);
                            //Log.d(logTag, "Expanding item: " + i + " Top: " + v.getTop() + " Bottom: " + v.getBottom());
                        } else if (child == footerChildIndex) {
                            footerView = getChildAt(i);//we'll need this later
                            expandingViewFinalCoords.put(footerView, new int[]{footerView.getTop(), footerView.getBottom()});//8415
                        }
                    }
                }

                //figure out which offset views will not be displayed after expansion
                //store them in mViewsToDraw so we can reference them for animation
                for (View offsetView : startViewPositions.keySet()) {
                    int[] offsetViewOld = startViewPositions.get(offsetView);
                    offsetView.setTop(offsetViewOld[0]);
                    offsetView.setBottom(offsetViewOld[1]);
                    if (offsetView.getParent() == null) {
                        mViewsToDraw.add(offsetView);
                    } else {
                        offsetView.setHasTransientState(false);
                    }
                }

                //Set initial footer position
                if (footerView == null) { //if list footer is not on screen after expansion
                    //make a mock one to use
                    LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View mockFooter = layoutInflater.inflate(R.layout.w_circuit_menu_buttons, listView);
                    //give mock view the collapsedGroupView-footers' coords, add to offset view list for animations
                    startViewPositions.put(mockFooter, new int[]{footerPortionOfGroupViewTop, footerPortionOfGroupViewBottom});
                    //view isn't drawn by list view normally, add to views to draw
                    mViewsToDraw.add(mockFooter);
                } else {//list footer is on screen after expansion, we can just animate it
                    footerView.setTop(footerPortionOfGroupViewTop);
                    footerView.setBottom(footerPortionOfGroupViewBottom);
                    startViewPositions.put(footerView, new int[]{footerPortionOfGroupViewTop, footerPortionOfGroupViewBottom});
                }

                //Disable Listview for animation
                setEnabled(false);
                setClickable(false);

                //setup the animator set;
                AnimatorSet animSet = new AnimatorSet();

                ArrayList<Animator> ExpansionAnimatorArray = new ArrayList<Animator>();
                int expandingPortionTop = 0;
                int expandingPortionBottom = 0;
                int size = orderedExpandingViews.size();
                final long startTime = System.currentTimeMillis();//DEBUG VAR
                //get expansion animator for each expanding view
                /*
                for (int i = 0; i < size; i++){
                    final View expandingView = orderedExpandingViews.get(i);
                    ValueAnimator expansionAnimator;
                    int[] old = expandingViewFinalCoords.get(expandingView);

                    int height = old[1] - old[0];
                    expandingView.setTop(old[0]);
                    expandingView.setBottom(old[0]);

                    expansionAnimator = getAnimation(expandingView, 0, height);

                    expansionAnimator.setDuration(1000);
                    ExpansionAnimatorArray.add(expansionAnimator);
                    //if(i == 0) expandingPortionBottom = old[0]; //Get start y coord

                    //if(i == size - 1) expandingPortionTop = old[1]; //Get end y coord


                    expansionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            //Log.d(logTag, "ExpandingView bottom: " + (expandingView.getBottom() - footerPortionOfGroupViewTop));
                            listView.animateOffsetViews(startViewPositions, expandingView.getBottom() - footerPortionOfGroupViewTop);
                        }
                    });
                }
                */
                groupExpander expander = new groupExpander();
                expander.setTime(EXPANSION_TIME);
                expander.setOffsetViews(startViewPositions);
                expander.setExpandingViews(expandingViewFinalCoords, orderedExpandingViews);
                expander.setListView(listView);
                //int totalOffset = expandingPortionTop - expandingPortionBottom;
                //play expansion animators in sequence
                /*
                for(int i = 0; i<ExpansionAnimatorArray.size(); i++){
                    if (i > 0) {
                        animSet.play(ExpansionAnimatorArray.get(i)).after(ExpansionAnimatorArray.get(i-1)).after(0);
                    }
                }

                //animSet.playSequentially(ExpansionAnimatorArray);
                //create offset animations for views below last expanding view
                //play the offset animations with the first expansion animator

                boolean debugFlag = true; //DEBUG SHIT
                for (final View OffsetView : startViewPositions.keySet()){
                    Animator offsetAnimation = getAnimation(OffsetView,
                            totalOffset, totalOffset);
                    offsetAnimation.setDuration(550);
                    animSet.play(offsetAnimation).with(ExpansionAnimatorArray.get(0));
                    //DEBUG SHIT

                    if(debugFlag){
                        offsetAnimation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                Log.d(logTag, "Offset animator start @ " + (System.currentTimeMillis() - startTime));
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                Log.d(logTag, "Offset animator finish @ " + (System.currentTimeMillis() - startTime));
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                    }
                    debugFlag = false;

                    //END DEBUG SHIT
                }
                */
                //

                /*
                    //TODO WE ARE HERE
                    totalOffset += height;
                    for (final View offsetView : startViewPositions.keySet()) {
                        //animate each views top and bottom by height

                        if(!dFlag){
                            dTestView = offsetView; //set our sample view
                            dFlag = true;
                        }

                        Animator dTestAnimator;
                        if(offsetView == dTestView) {
                            Log.d(logTag, "totalOffset: " + totalOffset);
                            final int dOffset = totalOffset;
                            dTestAnimator = getAnimation(offsetView, totalOffset, totalOffset, true);
                            dTestAnimator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    Log.d(logTag, "Animator Fired for test view.  Total offset at fire is " + dOffset);
                                }
                            });
                        } else {
                            dTestAnimator = getAnimation(offsetView, totalOffset, totalOffset);
                        }

                        animSet.play(dTestAnimator).with(lastExpansionAnimation);
                    }
                    */

                //TEST THINGS

                //END TEST THINGS
                animSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //super.onAnimationEnd(animation);  PRY NOT NEEDED?
                        //restore functionality
                        setEnabled(true);
                        setClickable(true);
                        if (mViewsToDraw.size() > 0) {
                            for (View v : mViewsToDraw) {
                                v.setHasTransientState(false);
                            }
                        }
                        mViewsToDraw.clear();
                    }
                });

                //animSet.start();

                return true;
            }
        });
    }
    /**
     * This method takes some view and the values by which its top and bottom bounds
     * should be changed by. Given these params, an animation which will animate
     * these bound changes is created and returned.
     */
    private ValueAnimator getAnimation(final View view, float translateTop, float translateBottom) {

        int top = view.getTop();
        int bottom = view.getBottom();

        int endTop = (int)(top + translateTop);
        int endBottom = (int)(bottom + translateBottom);

        PropertyValuesHolder translationTop = PropertyValuesHolder.ofInt("top", top, endTop);
        PropertyValuesHolder translationBottom = PropertyValuesHolder.ofInt("bottom", bottom,
                endBottom);

        return ObjectAnimator.ofPropertyValuesHolder(view, translationTop, translationBottom);
    }
}
