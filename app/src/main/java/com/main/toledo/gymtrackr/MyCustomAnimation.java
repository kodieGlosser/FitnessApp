package com.main.toledo.gymtrackr;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class MyCustomAnimation extends Animation {

    private View mCurrentView;
    private View mTransitionView;

    private int mEndHeight;
    private LinearLayout.LayoutParams mCurrentLayoutParams;
    private LinearLayout.LayoutParams mTransitionLayoutParams;

    public MyCustomAnimation(View currentView, View transitionView, int duration, int height) {

        setDuration(duration);
        mCurrentView = currentView;
        mTransitionView = transitionView;

        mEndHeight = height;

        mCurrentLayoutParams = ((LinearLayout.LayoutParams) currentView.getLayoutParams());
        mTransitionLayoutParams = ((LinearLayout.LayoutParams) currentView.getLayoutParams());
        //SetTransition stuffs
        mTransitionLayoutParams.height = 0;
        mTransitionView.setVisibility(View.VISIBLE);
        //SetCurrent stuffs
        mCurrentLayoutParams.height = height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        Log.d("4/8" , "apply Transformation called");
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {

            mTransitionLayoutParams.height =  (int)(mEndHeight * interpolatedTime);
            mCurrentLayoutParams.height = (int) (mEndHeight * (1 - interpolatedTime));

            mCurrentView.requestLayout();
            mTransitionView.requestLayout();
        } else {
            mTransitionLayoutParams.height = LayoutParams.MATCH_PARENT;
            mTransitionView.requestLayout();
            mCurrentView.setVisibility(View.GONE);
        }
    }
}