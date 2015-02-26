package com.main.toledo.gymtrackr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Adam on 2/23/2015.
 */
public class LoadListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //sets the list adapter to the one we made in the browse activity
        setListAdapter(((LoadActivity)getActivity()).getAdapter());

    }


    //this is the code that adds an item from the browse list to the workspace
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){


        //will pass plan to workspace, then it can run the DB call

        Intent i = new Intent(getActivity(), WorkspaceActivity.class);
        startActivity(i);

    }
}