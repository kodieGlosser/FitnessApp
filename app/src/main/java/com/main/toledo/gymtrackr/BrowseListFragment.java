package com.main.toledo.gymtrackr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Adam on 2/10/2015.
 * this is where the code for our browse list of exercises lives
 */
public class BrowseListFragment extends ListFragment{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setListAdapter(((BrowseActivity)getActivity()).getAdapter());
    }
}
