package com.main.toledo.gymtrackr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditExerciseHistoryFragment extends ListFragment {
    private EditExerciseHistoryAdapter mAdapter;
    //private ArrayList<ExerciseHistory> mTempHistory;
    private ArrayList<ExerciseHistory> mHistory;
    private Context mContext;
    private boolean mBoot;
    private String mName;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //sets the list adapter to the one we made in the browse activity

    }

    public void setAdapter(EditExerciseHistoryAdapter adapter){
        mAdapter = adapter;
    }

    public void refreshAdapter(){
        /*
        mHistory.clear();
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] exerciseHistories = db.loadHistoryByExerciseName(mName);
        for (ExerciseHistory eh : exerciseHistories)
            mHistory.add(eh);
            */
    }

    public void setAnimationDataSet(ArrayList<ExerciseHistory> history, Context c, boolean b, String name){
        mBoot = b;
        mContext = c;
        mHistory = history;
        mName = name;
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

    public void animateOut() {
        /*
        final int totalDelay = 2000;
        ListView thisListView = getListView();

        int lastIndex = thisListView.getLastVisiblePosition();
        int firstIndex = thisListView.getFirstVisiblePosition();
        int numVisibleItems = lastIndex - firstIndex;

        final int delayPerItem = totalDelay / numVisibleItems;

        //int delay = 0;

        final Animation anim = AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.slide_out_right
        );

        anim.setDuration(delayPerItem);

        int delay = 0;
        int mIndex = lastIndex;

        while (mIndex >= 0) {
            final int whileIndex = mIndex;
            new Handler().postDelayed(new Runnable() {
                final int index = whileIndex;

                public void run() {
                    if (getListView().getChildAt(index) != null)
                        (getListView().getChildAt(index)).startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            //removeItem(index);
                            getListView().getChildAt(index).setVisibility(View.INVISIBLE);

                        }

                    }, (delayPerItem/2));
                }

            }, delay);


            delay = delay + delayPerItem;
            mIndex--;
        }


        new Handler().postDelayed(new Runnable() {

            public void run() {

                refreshAdapter();


            }

        }, totalDelay + (int)(totalDelay*.3));
        */
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
