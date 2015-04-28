package com.main.toledo.gymtrackr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/10/2015.
 */
public class BrowseActivity extends ActionBarActivity {
    //fragments needed for the browse activity
    BrowseFilterFragment FilterFragment;
    BrowseListFragment ListFragment;

    //this is the stub list
    private static ArrayList<Exercise> mBrowseExerciseList = new ArrayList<Exercise>();
    //the adapter is responsible for populating the browse list
    public static BrowseAdapter adapter;


    //needed for add exercise functionality
    //private int circuitNumber;
    //private boolean circuitOpen;
    private int SCREENWIDTH;
    private int SCREENHEIGHT;
    private int slideVal; //should change this to some fraction of screen width

    //BROWSE-CREATE TRANSITION
    private final static int NOT_FROM_CREATE = 0, ADDED_EXERCISE_IN_CREATE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.b_activity);
        FilterFragment = new BrowseFilterFragment();
        ListFragment = new BrowseListFragment();

        //creates a list adapter for our stub exercises
        adapter = new BrowseAdapter(this, 0, mBrowseExerciseList);

        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.exerciseFiltersContainer, FilterFragment);
        transaction.add(R.id.exerciseListContainer, ListFragment);
        transaction.commit();

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREENWIDTH = size.x;
        SCREENHEIGHT = size.y;
        slideVal = -(int)(SCREENWIDTH/5);

    }

    @Override
    public void onResume(){
        super.onResume();

        //GET TRANSITION
        int state;
        state = WorkoutData.get(this).getBrowseTransition();
        if(state == NOT_FROM_CREATE)
            initializeBrowseList();
        //initiates filter




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

    //public int getCircuitValue(){
    //    return circuitNumber;
    //}

    //public boolean isCircuitOpen(){
    //    return circuitOpen;
    //}

    public void putBrowseExerciseList(Exercise[] exercises){
        mBrowseExerciseList.clear();
        for (int i = 0; i < exercises.length; i++) {
            mBrowseExerciseList.add(exercises[i]);
        }
        adapter.notifyDataSetChanged();
    }

    public void initializeBrowseList(){
       // Log.d("test", "InitializeBrowseList called.");
        DatabaseWrapper db = new DatabaseWrapper();
        Exercise[] exercises = db.browseExercisesByName("");
        for (int i = 0; i < exercises.length; i++) {
            mBrowseExerciseList.add(exercises[i]);
        }
    }

    public class BrowseAdapter extends ArrayAdapter<Exercise>{
        private Context mContext;
        private SwipableLinearLayout mTextViewHandle;
        private ArrayList<Exercise> mExercises;
        public BrowseAdapter(Context context, int resource, ArrayList<Exercise> exercises){
            super(context, resource, exercises);
            mContext = context;
            mExercises = exercises;
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
                    }
                };

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){

            //BROWSE STATES
            final int NOT_BROWSE = 0, BROWSE_WORKOUT = 1, WORKOUT_BROWSE = 2;

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

                if(mTextViewHandle != null)
                    mTextViewHandle.setOpen(false);

                mTextViewHandle = null;
            }
            swipableLinearLayout.refreshIcon();
            swipableLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Exercise exercise = new Exercise();
                    exercise.setName(e.getName());
                    exercise.setId(e.getId());
                    //Stubs
                    //exercise.instantiateStubMetrics();
                    for (Metric m : e.getMetrics()){
                        Metric nm = new Metric();
                        nm.setType(m.getType());
                        nm.setMetricIntValue(m.getMetricIntValue());
                        exercise.addMetrics(nm);
                    }
                    WorkoutData.get(mContext).setToggledExerciseExplicit(e);//Sets this as the 'last' item added

                    int circuitValue = WorkoutData.get(mContext).getStateCircuit();
                    boolean circuitOpenStatus = WorkoutData.get(mContext).isStateCircuitOpen();
                    //if circuit is open
                    if (circuitOpenStatus) {
                        //addToOpenCircuit to that circuit
                        WorkoutData.get(mContext).addExerciseToOpenCircuit(exercise,
                               circuitValue);
                        //if circuit is closed
                    } else if (!circuitOpenStatus) {
                        //add a closed circuit, with the exercise in it
                        WorkoutData.get(mContext).addClosedCircuit(exercise,
                                circuitValue);
                    }
                    //return to workspace
                    WorkoutData.get(mContext).setBrowseState(BROWSE_WORKOUT);
                    Intent i = new Intent(mContext, WorkspaceActivity.class);
                    startActivity(i);
                }
            });

            ImageButton delete = (ImageButton) convertView.findViewById(R.id.deleteButton);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    WorkoutData.get(mContext).exerciseRemoved(e.getId());
                    DatabaseWrapper db = new DatabaseWrapper();
                    db.deleteExerciseInExerciseTable(e.getId());
                    mExercises.remove(e);
                    notifyDataSetChanged();
                    if(WorkoutData.get(mContext).isAnExerciseToggled()){
                        if (WorkoutData.get(mContext).getToggledExercise() == e){
                            WorkoutData.get(mContext).clearToggledExercise();
                        }
                    }
                }
            });

            return convertView;
        }
    }

    }

