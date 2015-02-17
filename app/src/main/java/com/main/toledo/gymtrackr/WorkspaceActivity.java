package com.main.toledo.gymtrackr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends Activity {

    WorkspaceExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<Circuit> workout = new ArrayList<Circuit>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);
        //test 2/15 WorkoutData.get(this).getWorkout();
        // get the listview
        Circuit CircuitA = new Circuit();
        Circuit CircuitB = new Circuit();
        Circuit CircuitC = new Circuit();

        CircuitA.add( new Exercise("curl", "arms", 1, "dumbbell"));
        CircuitA.add( new Exercise("squat", "legs", 2, "barbell"));
        CircuitA.add( new Exercise("bench press", "chest", 3, "barbell"));
        CircuitB.add( new Exercise("dumb bell row", "back", 4, "dumbell"));
        CircuitB.add( new Exercise("triceps extension", "arms", 5, "dumbell"));
        CircuitC.add( new Exercise("shoulder press", "shoulders", 6, "barbell"));
        CircuitC.add( new Exercise("shrug", "shoulders", 7, "barbell"));

        workout.add( CircuitA );
        workout.add( CircuitB );
        workout.add( CircuitC );

        expListView = (ExpandableListView) findViewById(R.id.workspaceListContainer);

        listAdapter = new WorkspaceExpandableListAdapter(this, workout);

       //setting list adapter
        expListView.setAdapter(listAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

}
