package com.main.toledo.gymtrackr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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


        //will pass plan to workspace activity, then that can run the DB call

        Intent i = new Intent(getActivity(), WorkspaceActivity.class);
        //gettag should return plan name of clicked item
        String[] planList = ((LoadActivity) getActivity()).getPlanList();
        Log.d("W_HEADER_DEBUG", "Plan name: " + planList[position]);
        i.putExtra("EXTRA_PLAN_NAME", planList[position]);
        //puts actiontoperform (EDIT or WORKOUT) into the intent
        i.putExtra("EXTRA_COURSE_OF_ACTION", ((LoadActivity) getActivity()).getActionToPerform());
        startActivity(i);

    }
}