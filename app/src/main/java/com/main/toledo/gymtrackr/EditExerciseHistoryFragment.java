package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditExerciseHistoryFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //sets the list adapter to the one we made in the browse activity

    }

    public void setAdapter(EditExerciseHistoryAdapter adapter){

        setListAdapter(adapter);
    }

    public void animateOut(){
        int totalDelay = 2000;
        int numVisibleItems = getListView().getLastVisiblePosition();
        int lastItemIndex = getListView().getLastVisiblePosition();
        final int delayPerItem = totalDelay/numVisibleItems;

        int delay = 0;

        final Animation anim = AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.slide_out_right
        );
        anim.setDuration(delayPerItem);

        //getListView().getChildAt(2).startAnimation(anim);

        //while(lastItemIndex >= 0) {
            final int index = lastItemIndex;

            new Handler().postDelayed(new Runnable() {

                public void run() {
                    if (getListView().getChildAt(index) != null)
                        (getListView().getChildAt(index)).startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            if (getListView().getChildAt(index) != null)
                                getListView().getChildAt(index).setVisibility(View.GONE);

                        }

                    }, delayPerItem);
                }

            }, delay);


            delay = delay + delayPerItem;
            lastItemIndex--;
        //}


    }

    public void animateIn(){

    }

}
