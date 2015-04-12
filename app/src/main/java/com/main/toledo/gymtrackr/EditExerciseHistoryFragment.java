package com.main.toledo.gymtrackr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.ArrayList;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditExerciseHistoryFragment extends ListFragment {
    private EditExerciseHistoryAdapter mAdapter;
    private ArrayList<ExerciseHistory> mHistory;
    private Context mContext;
    private boolean mBoot;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //sets the list adapter to the one we made in the browse activity

    }

    public void setAdapter(EditExerciseHistoryAdapter adapter){
        mAdapter = adapter;
    }

    public void refreshAdapter(){
        mAdapter = new EditExerciseHistoryAdapter(mContext, 0, mHistory);
    }

    public void setAnimationDataSet(ArrayList<ExerciseHistory> history, Context c, boolean b){
        mBoot = b;
        mContext = c;
        mHistory = history;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.list_layout_controller);
        getListView().setLayoutAnimation(animation);
        if (mBoot)
            new Handler().postDelayed(new Runnable() {

                public void run() {

                    animateIn();


                }

            }, 100);

    }

    public void animateOut(){
        int totalDelay = 600;
        int numVisibleItems = getListView().getLastVisiblePosition();
        int lastItemIndex = getListView().getLastVisiblePosition();
        final int delayPerItem = totalDelay/numVisibleItems;

        int delay = 0;

        final Animation anim = AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.slide_out_right
        );
        anim.setDuration(delayPerItem);

        //getListView().getChildAt(2).startAnimation(anim);

        while(lastItemIndex >= 0) {
            final int index = lastItemIndex;

            new Handler().postDelayed(new Runnable() {

                public void run() {
                    if (getListView().getChildAt(index) != null)
                        (getListView().getChildAt(index)).startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            removeItem(index);
                        }

                    }, delayPerItem * 3/4);
                }

            }, delay);


            delay = delay + delayPerItem;
            lastItemIndex--;
        }

        new Handler().postDelayed(new Runnable() {

            public void run() {

                refreshAdapter();


            }

        }, totalDelay + (int)(totalDelay*.3));
    }

    public void animateIn(){
        setListAdapter(mAdapter);
        //EditExerciseHistoryAdapter adapter = new EditExerciseHistoryAdapter(getActivity(), 0, mHistory);
        getListView().startLayoutAnimation();
    }

    public void removeItem(int index){
        mAdapter.remove(mAdapter.getItem(index));
        mAdapter.notifyDataSetChanged();
    }
}
