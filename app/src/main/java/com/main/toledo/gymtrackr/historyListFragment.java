package com.main.toledo.gymtrackr;


import android.os.Bundle;
import android.support.v4.app.ListFragment;

/**
 * Created by Adam on 4/6/2015.
 */
public class historyListFragment extends ListFragment{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //sets the list adapter to the one we made in the browse activity
        setListAdapter(((historyActivity)getActivity()).getHistoryAdapter());

    }
}
