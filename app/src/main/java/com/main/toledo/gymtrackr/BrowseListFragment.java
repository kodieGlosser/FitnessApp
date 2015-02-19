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
        //gets the exercise we selected
        Exercise exercise = (Exercise)(getListAdapter()).getItem(position);
        //adds the exercise we selected to our workout data singleton
        //NOTE TO SELF: MAY BE BEST TO MODIFY THIS.  PASS EXERCISE TO ACTIVITY AND ADD IT TO
        //SINGLETON THERE.
        //test 2/17 WorkoutData.get(getActivity()).increment();

        WorkoutData.get(getActivity()).addExercise(exercise,
                ((BrowseActivity)getActivity()).getCircuitValue());

        //Calls the workspace activity
        Intent i = new Intent(getActivity(), WorkspaceActivity.class);

        //i.putExtra("EXTRA_CIRCUIT_NUMBER",
        //        ((BrowseActivity)getActivity()).getCircuitValue());

        startActivity(i);
    }
}
