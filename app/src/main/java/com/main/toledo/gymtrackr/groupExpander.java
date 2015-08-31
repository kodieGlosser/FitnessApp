package com.main.toledo.gymtrackr;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Adam on 8/9/2015.
 */
/*
public class groupExpander {
    private animatedExpandableListView mListView;
    private HashMap<View, int[]> mStartViewPositions;
    private HashMap<View, int[]> mExpandingViewFinalCoords;
    private ArrayList<View> mOrderedExpandingViews;
    private ArrayList<Integer> mExpandingViewCutoffs;
    private ArrayList<Boolean> mIsItemExpanded;
    private long mStartTime;
    private int mAnimationTime = 500;
    private int mTopOfExpandingItems;
    private int mBottomOfExpandingItems;
    private int mLastViewExpanded;
    private int mNumExpandingViews;
    private float mPixelsExpandedPerMS;
    private boolean mAnimate = false;
    private static final String tag = "groupExpander";
    public groupExpander(animatedExpandableListView listView){
        mListView = listView;
    }

    public void setTime(int animationTime){
        mAnimationTime = animationTime;
    }

    public void setOffsetViews(HashMap<View, int[]> startViewPositions){
        Log.d(tag, "OffsetViews should be set.");
        if(startViewPositions == null) Log.d(tag, "OffsetView hashmap is null");
        mStartViewPositions = startViewPositions;
    }

    public void setExpandingViews(HashMap<View, int[]> expandingViewFinalCoords, ArrayList<View> orderedExpandingViews){
        mExpandingViewFinalCoords = expandingViewFinalCoords;
        mOrderedExpandingViews = orderedExpandingViews;
        mExpandingViewCutoffs = new ArrayList<>();
        mIsItemExpanded = new ArrayList<>();
        int topOfExpandingItems = 0;
        int bottomOfExpandingItems = 0;
        mNumExpandingViews = orderedExpandingViews.size();

        for(int i = 0; i < mNumExpandingViews; i++){
            int[] old = expandingViewFinalCoords.get(orderedExpandingViews.get(i));
            int top = old[0];
            int bottom = old[1];
            mExpandingViewCutoffs.add(i, bottom);
            mIsItemExpanded.add(i, false);
            if(i == 0){
                mTopOfExpandingItems = top;
            }

            if(i == mNumExpandingViews - 1){
                mBottomOfExpandingItems = bottom;
            }

        }

        int totalOffset = bottomOfExpandingItems - topOfExpandingItems;

        mPixelsExpandedPerMS = totalOffset / mAnimationTime;
    }

    public void startSingleExpansion(){
        Log.d(tag, "startSingleExpansion() called.");
        //mStartTime = System.currentTimeMillis();
        //set expanding views to height 0
        for(View v : mOrderedExpandingViews){
            int[] old = mExpandingViewFinalCoords.get(v);
            v.setBottom(old[0]);
        }
        mLastViewExpanded = 0;
        mAnimate = true;
        mListView.invalidate();
    }

    public void animate(){
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - mListView.mStartTime;
        Log.d(tag, currentTime + " - " + mListView.mStartTime + " = " + timeElapsed);
        int totalOffset = (int)(timeElapsed * mPixelsExpandedPerMS);
        int actualOffset = totalOffset + mTopOfExpandingItems;
        Log.d(tag, "In animate().  Total offset = " + totalOffset + " Top of expanding items = " + mTopOfExpandingItems
            + " actual offset: " + actualOffset);
        //figure out which view we are on
        for(int i = 0; i<mNumExpandingViews; i++){
            if(actualOffset < mExpandingViewCutoffs.get(i)){
                //we are on this view set the bottom
                mOrderedExpandingViews.get(i).setBottom(actualOffset);
                break;
            } else if((mExpandingViewCutoffs.get(i)<=actualOffset)&&(!mIsItemExpanded.get(i))){
                mIsItemExpanded.remove(i);
                mIsItemExpanded.add(i, true);
                mOrderedExpandingViews.get(i).setBottom(mExpandingViewCutoffs.get(i));
            }
        }

        animateOffsetViews(totalOffset);

        mListView.invalidate();

        //Log.d(tag, "Time elapsed: " + timeElapsed + " Animation Time: " + mAnimationTime);
        if(timeElapsed > mAnimationTime) {
            mAnimate = false;
            mListView.onFinishAnimation();
        }
    }

    public boolean shouldAnimate(){
        Log.d(tag, "shouldAnimate called, returning: " + mAnimate);
        return mAnimate;
    }

    public void animateOffsetViews(int offset){//todo

        //for (final View OffsetView : mStartViewPositions.keySet()){
        for (final View OffsetView : mListView.mStartViewPositions.keySet()){
            int[] coords = mListView.mStartViewPositions.get(OffsetView);
            int bottom = coords[1];
            int top = coords[0];
            OffsetView.setTop(top + offset);
            OffsetView.setBottom(bottom + offset);
        }
    }
}
*/