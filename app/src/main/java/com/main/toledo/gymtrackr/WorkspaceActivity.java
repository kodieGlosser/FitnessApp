package com.main.toledo.gymtrackr;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends ExpandableListActivity {


    ArrayList<Circuit> workout = new ArrayList<Circuit>();
    ArrayList<Circuit> singletonWorkout = new ArrayList<Circuit>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TEST ", "CALLED ONCREATE IN WORKSPACE ACTIVITY");
        //setContentView(R.layout.activity_workspace);

        // get the listview


        //expListView = (ExpandableListView) findViewById(R.id.workspaceListContainer);
        //testDataset();
        //final WorkspaceExpandableListAdapter listAdapter = new WorkspaceExpandableListAdapter(
        //this,  workout);
        final WorkspaceExpandableListAdapter listAdapter = new WorkspaceExpandableListAdapter(
                this,  WorkoutData.get(this).getWorkout());
        //setting list adapter

        this.setListAdapter(listAdapter);
        expandLists(listAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        //if (listAdapter != null) {
        //    listAdapter.notifyDataSetChanged();
        //}
    }

    public void expandLists(WorkspaceExpandableListAdapter listAdapter){
        int count = listAdapter.getGroupCount();
        for (int position = 1; position <= count; position++){
            this.getExpandableListView().expandGroup(position - 1);
        }
    }

    public void testDataset(){
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
        CircuitC.add( new Exercise("shrug", "shoulders", 7, "barbell"));

        CircuitA.setName("testcircuitA");
        CircuitB.setName("testcircuitB");
        CircuitC.setName("testcircuitC");

        CircuitA.setOrder(0);
        CircuitB.setOrder(1);
        CircuitC.setOrder(2);

        workout.add( CircuitA );
        workout.add( CircuitB );
        workout.add( CircuitC );
    }

    public void incrementPlaceholderCircuit(){
        //WorkoutData.get(this).getWorkout()
    }


    @Override
    public void onGroupCollapse(int groupPosition){
        //Log.d("TEST ", "onGroupCollapse called from work act");
    }

}
