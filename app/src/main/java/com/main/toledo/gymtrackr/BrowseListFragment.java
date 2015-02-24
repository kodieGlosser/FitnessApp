package com.main.toledo.gymtrackr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
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

        //sets the list adapter to the one we made in the browse activity
        setListAdapter(((BrowseActivity)getActivity()).getAdapter());

    }


    //this is the code that adds an item from the browse list to the workspace
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){


        Exercise exercise = (Exercise)(getListAdapter()).getItem(position);
        //if circuit is open
        if (((BrowseActivity)getActivity()).isCircuitOpen()){
            //addToOpenCircuit to that circuit
            WorkoutData.get(getActivity()).addExercise(exercise,
                    ((BrowseActivity) getActivity()).getCircuitValue());
        //if circuit is closed
        } else if (!((BrowseActivity)getActivity()).isCircuitOpen()){
            WorkoutData.get(getActivity()).addClosedCircuit(exercise,
                    ((BrowseActivity) getActivity()).getCircuitValue());
        }

        Intent i = new Intent(getActivity(), WorkspaceActivity.class);
        startActivity(i);

    }
}
