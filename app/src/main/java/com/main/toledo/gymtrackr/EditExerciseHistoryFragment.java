package com.main.toledo.gymtrackr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
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
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        //LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.list_layout_controller);
        //getListView().setLayoutAnimation(animation);
        if (mBoot)
            new Handler().postDelayed(new Runnable() {

                public void run() {

                    animateIn();


                }

            }, 100);

    }

    public void animateOut() {

        int totalDelay = 2000;
        ListView thisListView = getListView();

        int lastIndex = thisListView.getLastVisiblePosition();
        int firstIndex = thisListView.getFirstVisiblePosition();
        int numVisibleItems = lastIndex - firstIndex;

        int delayPerItem = totalDelay / numVisibleItems;

        //int delay = 0;



        int delay = 0;

        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(200);
        set.addAnimation(animation);
        /*
        animation = new AlphaAnimation(0.0f, 0.0f);
        animation.setDuration(1000);
        set.addAnimation(animation);
        */

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 5.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(1000);

        set.addAnimation(animation);




        LayoutAnimationController controller = new LayoutAnimationController(set, 0.1f);
        controller.setOrder(LayoutAnimationController.ORDER_REVERSE);
        ListView listView = getListView();
        listView.setLayoutAnimation(controller);
        listView.startLayoutAnimation();
        /*
        while (lastIndex >= 0) {
            final int index = lastIndex;



            TranslateAnimation anim = new TranslateAnimation(0, 1080, 0, 0);

            anim.setDuration(delayPerItem);
            anim.setStartTime(delay);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //getListView().getChildAt(index).clearAnimation();
                    getListView().getChildAt(index).setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            //if (getListView().getChildAt(lastIndex) != null)
                getListView().getChildAt(index).startAnimation(anim);

            delay = delay + delayPerItem;
            lastIndex--;
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
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.list_layout_controller);
        getListView().setLayoutAnimation(animation);
        getListView().startLayoutAnimation();
    }

    public void removeItem(int index){
        mAdapter.remove(mAdapter.getItem(index));
        mAdapter.notifyDataSetChanged();
    }
}
