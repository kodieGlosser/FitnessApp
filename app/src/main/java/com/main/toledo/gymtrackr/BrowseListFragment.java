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

        //sets the list adapter to the one we made in the browse activity
        setListAdapter(((BrowseActivity)getActivity()).getAdapter());

    }


    //this is the code that adds an item from the browse list to the workspace
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        //PROBABLY WAY BETTER TO PASS EXERCISE TO WORKSPACE ACTIVITY

        Exercise e = (Exercise)(getListAdapter()).getItem(position);
        Exercise exercise = new Exercise();
        exercise.setName(e.getName());
        exercise.setId(e.getId());
        //Stubs
        //THIS NEEDS UPDATING
        Metric weightMetric = new Metric();
        weightMetric.setType(metricType.WEIGHT);
        //weightMetric.setMetricIntValue(weight);

        Metric repMetric = new Metric();
        repMetric.setType(metricType.REPETITIONS);
        exercise.addMetrics(weightMetric);
        exercise.addMetrics(repMetric);

        WorkoutData.get(getActivity()).setToggledExerciseExplicit(e);
        //if circuit is open
        if (((BrowseActivity)getActivity()).isCircuitOpen()){
            //addToOpenCircuit to that circuit
            WorkoutData.get(getActivity()).addExerciseToOpenCircuit(exercise,
                    ((BrowseActivity) getActivity()).getCircuitValue());
        //if circuit is closed
        } else if (!((BrowseActivity)getActivity()).isCircuitOpen()){
            //add a closed circuit, with the exercise in it
            WorkoutData.get(getActivity()).addClosedCircuit(exercise,
                    ((BrowseActivity) getActivity()).getCircuitValue());
        }
        //return to workspace
        Intent i = new Intent(getActivity(), WorkspaceActivity.class);
        startActivity(i);

    }
}
