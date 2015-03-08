package com.main.toledo.gymtrackr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Adam on 2/10/2015.
 */
public class BrowseActivity extends FragmentActivity {
    //fragments needed for the browse activity
    BrowseFilterFragment FilterFragment;
    BrowseListFragment ListFragment;

    //this is the stub list
    private static ArrayList<Exercise> StubExercises = new ArrayList<Exercise>();
    //the adapter is responsible for populating the browse list
    public static BrowseAdapter adapter;
    private int circuitNumber;
    private boolean circuitOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            circuitNumber = extras.getInt("EXTRA_CIRCUIT_NUMBER");
            circuitOpen = extras.getBoolean("EXTRA_CIRCUIT_OPEN");
        }
        setContentView(R.layout.b_activity);
        //Log.d("test", " Looking for exercise for circuit" + circuitNumber);

        //populates our display initially
        initializeBrowseList();
        //initiates filter
        FilterFragment = new BrowseFilterFragment();
        ListFragment = new BrowseListFragment();

        //creates a list adapter for our stub exercises
        adapter = new BrowseAdapter(this, 0, StubExercises);

        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.exerciseFiltersContainer, FilterFragment);
        transaction.add(R.id.exerciseListContainer, ListFragment);
        transaction.commit();

    }
    //Adam:  This is a stub list of exercises used for the browse menu
    //in the format "Name, Muscle group, last used, equip used"
    //used to fetch the adapter from this activity in fragments.  the list fragment gets the adapter
    //via this method, then uses it to populate its list view.

    public BrowseAdapter getAdapter(){
        return this.adapter;
    }

    //used to addToOpenCircuit data to the display list from fragments, this method is called from from the filter
    //fragment.  queries to populate the the list could go here.  we could set addItem to take some
    //params to specify things.

    public int getCircuitValue(){
        return circuitNumber;
    }

    public boolean isCircuitOpen(){
        return circuitOpen;
    }

    public void searchForItem(String search_value){
        StubExercises.clear();
        DatabaseWrapper db = new DatabaseWrapper();
        Exercise[] exercises = db.browseExercisesByName(search_value);
        for (int i = 0; i < exercises.length; i++) {
            StubExercises.add(exercises[i]);
            //THIS IS WHERE IM ADDING SET METRICS STUB
            //adds weight and reps for everything
            Metric weight = new Metric();
            weight.setType(metricType.WEIGHT);
            Metric reps = new Metric();
            reps.setType(metricType.REPETITIONS);
            StubExercises.get(i).addMetrics(weight);
            StubExercises.get(i).addMetrics(reps);
        }
        //this lets the adapter know that it's data is different, display wont update otherwise
        adapter.notifyDataSetChanged();
    }

    public void initializeBrowseList(){
       // Log.d("test", "InitializeBrowseList called.");
        DatabaseWrapper db = new DatabaseWrapper();
        Exercise[] exercises = db.browseExercisesByName("");
        for (int i = 0; i < exercises.length; i++) {
            StubExercises.add(exercises[i]);
            Metric weight = new Metric();
            weight.setType(metricType.WEIGHT);
            Metric reps = new Metric();
            reps.setType(metricType.REPETITIONS);
            StubExercises.get(i).addMetrics(weight);
            StubExercises.get(i).addMetrics(reps);
        }
    }

    //this is the actual adapter class used to populate layout/b_frag_exercise_list.xml
    public class BrowseAdapter extends ArrayAdapter<Exercise> {

        public BrowseAdapter(Context context, int resource, ArrayList<Exercise> exercises){
            super(context, resource, exercises);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if (convertView == null) {
                                convertView = getLayoutInflater()
                        .inflate(R.layout.b_frag_exercise_list, null);

            }

            Exercise e = getItem(position);

            TextView nameTextView =
                    (TextView)convertView.findViewById(R.id.browseMenuExerciseNameView);
            nameTextView.setText(e.getName());
            TextView muscleTextView =
                    (TextView)convertView.findViewById(R.id.browseMenuExerciseMuscleView);
            muscleTextView.setText(e.getMuscleGroup());
            TextView equipmentTextView =
                    (TextView)convertView.findViewById(R.id.browseMenuExerciseEquip);
            equipmentTextView.setText(e.getEquipment());

            return convertView;
        }

    }
}
