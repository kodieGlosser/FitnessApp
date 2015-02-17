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
    private static ArrayList<Exercise> StubExercises;
    //the adapter is responsible for populating the browse list
    public static BrowseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_activity);

        //populates the stub list
        implementStubList();

        //initiates filter
        FilterFragment = new BrowseFilterFragment();
        ListFragment = new BrowseListFragment();

        CopyDatabase myDbCopier = new CopyDatabase(this);

        try {
           myDbCopier.createDatabase();
        } catch (IOException io) {
            Log.e("Query Failure", io.getMessage());
            throw new Error("Unable to create database");
        }

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

    private void implementStubList(){
        StubExercises = new ArrayList<Exercise>();
        StubExercises.add( new Exercise("curl", "arms", 1, "dumbbell"));
        StubExercises.add( new Exercise("squat", "legs", 2, "barbell"));
        StubExercises.add( new Exercise("bench press", "chest", 3, "barbell"));
        StubExercises.add( new Exercise("dumb bell row", "back", 4, "dumbell"));
        StubExercises.add( new Exercise("triceps extension", "arms", 5, "dumbell"));
        StubExercises.add( new Exercise("shoulder press", "shoulders", 6, "barbell"));
        StubExercises.add( new Exercise("shrug", "shoulders", 7, "barbell"));
    }

    //used to fetch the adapter from this activity in fragments.  the list fragment gets the adapter
    //via this method, then uses it to populate its list view.

    public BrowseAdapter getAdapter(){
        return this.adapter;
    }

    //used to add data to the display list from fragments, this method is called from from the filter
    //fragment.  queries to populate the the list could go here.  we could set addItem to take some
    //params to specify things.

    public void addItem(){
        //new data to be displayed
        StubExercises.add( new Exercise("test", "test", 666, "hailsatan") );

        //this lets the adapter know that it's data is different, display wont update otherwise
        adapter.notifyDataSetChanged();
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
