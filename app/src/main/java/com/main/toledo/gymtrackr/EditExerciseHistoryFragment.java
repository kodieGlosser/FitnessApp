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
    private boolean mBoot = false;
    private String mName;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //sets the list adapter to the one we made in the browse activity

    }

    public void setAdapter(EditExerciseHistoryAdapter adapter){
        mAdapter = adapter;
    }

    public void setAnimationDataSet(ArrayList<ExerciseHistory> history, Context c, String name){
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
        mBoot = false;
    }

    public void setFirstFragment(){mBoot = true;}
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

    }

    public void animateIn(){
        setListAdapter(mAdapter);
        //EditExerciseHistoryAdapter adapter = new EditExerciseHistoryAdapter(getActivity(), 0, mHistory);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.list_layout_controller);
        getListView().setLayoutAnimation(animation);
        getListView().startLayoutAnimation();
    }
}
