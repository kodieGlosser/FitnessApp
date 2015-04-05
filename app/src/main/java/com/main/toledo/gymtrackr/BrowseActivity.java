package com.main.toledo.gymtrackr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Adam on 2/10/2015.
 */
public class BrowseActivity extends ActionBarActivity {
    //fragments needed for the browse activity
    BrowseFilterFragment FilterFragment;
    BrowseListFragment ListFragment;

    //this is the stub list
    private static ArrayList<Exercise> StubExercises = new ArrayList<Exercise>();
    //the adapter is responsible for populating the browse list
    public static BrowseAdapter adapter;


    //needed for add exercise functionality
    private int circuitNumber;
    private boolean circuitOpen;
    private int slideVal = -200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("BROWSE FLOW", "onCreate");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_browse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_exercise_item:
                Intent i = new Intent(this, CreateExerciseActivity.class);
                //MONKEY CODE
                i.putExtra("EXTRA_CIRCUIT_NUMBER", circuitNumber);
                i.putExtra("EXTRA_CIRCUIT_OPEN", circuitOpen);
                //END MONKEY CODE
                startActivity(i);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            /*
            Metric weight = new Metric();
            weight.setType(metricType.WEIGHT);
            Metric reps = new Metric();
            reps.setType(metricType.REPETITIONS);

            StubExercises.get(i).addMetrics(weight);
            StubExercises.get(i).addMetrics(reps);
            */
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
            /*
            Metric weight = new Metric();
            weight.setType(metricType.WEIGHT);
            Metric reps = new Metric();
            reps.setType(metricType.REPETITIONS);
            StubExercises.get(i).addMetrics(weight);
            StubExercises.get(i).addMetrics(reps);
            */
        }
    }

    public class BrowseAdapter extends ArrayAdapter<Exercise>{
        private Context mContext;
        private SwipableLinearLayout mTextViewHandle;

        public BrowseAdapter(Context context, int resource, ArrayList<Exercise> exercises){
            super(context, resource, exercises);
            mContext = context;
        }

        private swipeLayoutListener listener =
                new swipeLayoutListener() {

                    public void CloseTextViewHandle(){
                        if (mTextViewHandle != null){
                            mTextViewHandle.close();
                        }
                    }

                    public void setTextViewHandle(SwipableLinearLayout l){
                        mTextViewHandle = l;
                    }

                    public void clearHandle(){
                        mTextViewHandle = null;
                    };
                };

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){

            if ((convertView == null)) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.b_frag_exercise_list_item, null);
            }


            final SwipableLinearLayout swipableLinearLayout =
                    (SwipableLinearLayout) convertView.findViewById(R.id.swipeLayoutHandle);
            swipableLinearLayout.setSwipeOffset(slideVal);
            swipableLinearLayout.setSwipeLayoutListener(listener);
            swipableLinearLayout.percentageToDragEnable(75f);

            final Exercise e = getItem(position);
            TextView nameTextView =
                    (TextView)convertView.findViewById(R.id.browseMenuExerciseNameView);
            nameTextView.setText(e.getName());
            TextView muscleTextView =
                    (TextView)convertView.findViewById(R.id.browseMenuExerciseMuscleView);
            muscleTextView.setText(e.getMuscleGroup());
            TextView equipmentTextView =
                    (TextView)convertView.findViewById(R.id.browseMenuExerciseEquip);
            equipmentTextView.setText(e.getEquipment());

            if(swipableLinearLayout.getX() != 0){
                swipableLinearLayout.setX(0f);
                mTextViewHandle.setOpen(false);
                mTextViewHandle = null;
            }

            swipableLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Exercise exercise = new Exercise();
                    exercise.setName(e.getName());
                    exercise.setId(e.getId());
                    //Stubs
                    exercise.instantiateStubMetrics();

                    WorkoutData.get(mContext).setToggledExerciseExplicit(e);//Sets this as the 'last' item added


                    //if circuit is open
                    if (((BrowseActivity) mContext).isCircuitOpen()) {
                        //addToOpenCircuit to that circuit
                        WorkoutData.get(mContext).addExerciseToOpenCircuit(exercise,
                                ((BrowseActivity) mContext).getCircuitValue());
                        //if circuit is closed
                    } else if (!((BrowseActivity) mContext).isCircuitOpen()) {
                        //add a closed circuit, with the exercise in it
                        WorkoutData.get(mContext).addClosedCircuit(exercise,
                                ((BrowseActivity) mContext).getCircuitValue());
                    }
                    //return to workspace
                    Intent i = new Intent(mContext, WorkspaceActivity.class);
                    startActivity(i);
                }
            });

            Button delete = (Button) convertView.findViewById(R.id.deleteButton);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //.resetPosition();
                    mTextViewHandle = null;

                    //CODE TO REMOVE ITEM FROM DB GOES HERE
                    DatabaseWrapper db = new DatabaseWrapper();
                    db.deleteExerciseInExerciseTable(e.getId());
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    }

