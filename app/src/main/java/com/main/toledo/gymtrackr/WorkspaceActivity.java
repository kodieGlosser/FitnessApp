package com.main.toledo.gymtrackr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends ActionBarActivity {

    WorkspaceExpandableListAdapterMKII listAdapter;
    WorkspaceListFragment ListFragment;
    WorkspaceTabFragment TabFragment;
    String planName;
    Menu mOptionsMenu;
    private int mToggledExercise = -1;
    private int mToggledCircuit = -1;

    int mode;

    public static boolean isEditable = false;
    boolean toBrowse, toEdit;

    final int PLAN = 1, WORKOUT = 2, WORKOUT_WITH_PLAN = 4, LOAD_PLAN = 5;

    boolean workout_from_plan_flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            planName = extras.getString("EXTRA_PLAN_NAME");
            mode = extras.getInt("EXTRA_MODE");
            switch(mode){
                case PLAN:
                    DatabaseWrapper db = new DatabaseWrapper();
                    Plan planList = db.loadEntirePlan(planName);
                    WorkoutData.get(this).eatPlan(planList, workout_from_plan_flag);
                    break;
                case WORKOUT:
                    break;
                case WORKOUT_WITH_PLAN:
                    workout_from_plan_flag = true;
                    DatabaseWrapper db2 = new DatabaseWrapper();
                    Plan planList2 = db2.loadEntirePlan(planName);
                    WorkoutData.get(this).eatPlan(planList2, workout_from_plan_flag);
                    break;
            }
        }
        */
        mode = WorkoutData.get(this).getState();
        switch(mode){
            case PLAN:
                break;
            case LOAD_PLAN:
                DatabaseWrapper db = new DatabaseWrapper();
                planName = WorkoutData.get(this).getWorkoutPlanName();
                Plan planList = db.loadEntirePlan(planName);
                WorkoutData.get(this).eatPlan(planList, workout_from_plan_flag);
                WorkoutData.get(this).setWorkoutState(PLAN);
                mode = PLAN;
                break;
            case WORKOUT:
                break;
            case WORKOUT_WITH_PLAN:
                workout_from_plan_flag = true;
                DatabaseWrapper db2 = new DatabaseWrapper();
                planName = WorkoutData.get(this).getWorkoutPlanName();
                Plan planList2 = db2.loadEntirePlan(planName);
                WorkoutData.get(this).eatPlan(planList2, workout_from_plan_flag);
                break;
        }
        /*
        if (extras != null){
            planName = extras.getString("EXTRA_PLAN_NAME");
            mode = extras.getInt("EXTRA_MODE");
            if (extras.getBoolean("WORKOUT_FROM_PLAN_FLAG")){
                workout_from_plan_flag = true;
            } else {
                workout_from_plan_flag = false;
            }
            DatabaseWrapper db = new DatabaseWrapper();
            Plan planList = db.loadEntirePlan(planName);
            WorkoutData.get(this).eatPlan(planList, workout_from_plan_flag);
        }
        */
        setContentView(R.layout.w_activity_main);

        TabFragment = new WorkspaceTabFragment();
        ListFragment = new WorkspaceListFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.WorkspaceHeaderContainer, TabFragment);
        transaction.add(R.id.WorkspaceListContainer, ListFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_workspace, menu);

        if(!workout_from_plan_flag){
            isEditable = true;
            listAdapter.setEditable(isEditable);
            ListFragment.workspaceListView.toggleListeners(isEditable);
            ListFragment.workspaceListView.clearHandle();
            mOptionsMenu.findItem(R.id.action_toggle_edit).setIcon(R.drawable.unlocked);
        } else {
            isEditable = false;
            listAdapter.setEditable(isEditable);
            ListFragment.workspaceListView.toggleListeners(isEditable);
            ListFragment.workspaceListView.clearHandle();
            mOptionsMenu.findItem(R.id.action_toggle_edit).setIcon(R.drawable.locked);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_toggle_edit:
                toggleEdit();
                return true;
            case R.id.save_changes:
                WorkspaceConfirmDialog dialog = new WorkspaceConfirmDialog();
                dialog.show(getFragmentManager(), "NameDialogFragment");
                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getPlanName(){return planName;}
    //flow control for workspace state
    public void setToBrowse(boolean b){toBrowse = b;}

    public void setToEdit(boolean b){toEdit = b; }

    public void save(){
        if (mode == PLAN) {
            //CODE FOR PLAN SAVE
            Plan p = WorkoutData.get(this).crapNewPlan();
            DatabaseWrapper db = new DatabaseWrapper();
            db.saveEntirePlan(p);
        }
        if (mode == WORKOUT || mode == WORKOUT_WITH_PLAN) {
            //CODE FOR WORKOUT SAVE, EG EXPORT TO HISTORY
            DatabaseWrapper db = new DatabaseWrapper();
            ExerciseHistory[] eh = WorkoutData.get(this).crapHistory();
            db.addExerciseToHistory(eh);
        }
    }
    public boolean workoutFromPlan(){return workout_from_plan_flag;}

    public int getAppMode(){return mode;}
    /*Not used at the moment
    public void testMethod(){
        ListFragment.collapseLists(listAdapter);
    }
    */
    //I feel like this should be in the header fragment...
    public void toggleEdit(){
        isEditable = !isEditable;

        if (isEditable) {
            mOptionsMenu.findItem(R.id.action_toggle_edit).setIcon(R.drawable.unlocked);
        } else {
            mOptionsMenu.findItem(R.id.action_toggle_edit).setIcon(R.drawable.locked);
        }

        listAdapter.hideKeypad();
        listAdapter.setEditable(isEditable);
        ListFragment.workspaceListView.toggleListeners(isEditable);
        ListFragment.workspaceListView.clearHandle();
    }

    @Override
    public void onResume(){
        Log.d("4/4", "ON RESUME CALLED IN WORKSPACE ACTIVITY");
        //THIS FIXES A BUG WHERE THE ADAPTER WONT BE UPDATED WHEN THE
        //ACTIVITY IS RESUMED AFTER BROWSE
        toBrowse = false;
        if(listAdapter==null)
            listAdapter = new WorkspaceExpandableListAdapterMKII(this);

        listAdapter.hideKeypad();

        super.onResume();
    }
    /*
    @Override
    public void onDestroy(){
        //Log.d("CLEAR WORKOUTDATA TEST", "onDestroy() called.");

        if(!toBrowse || !toEdit) {
            //WorkoutData.get(this).clear();
            WorkoutData.get(this).initialize();
        }

        super.onDestroy();
    }
    @Override
    public void onStart(){

        Log.d("APP FLOW TESTS", "ON start CALLED IN WORKSPACE ACTIVITY");
        for (Circuit c : WorkoutData.get(this).getWorkout()){
            Log.d("APP FLOW TESTS", "CIRCUIT: " + c.getName());
        }

        super.onStart();
    }
    */
    public void putToggledExerciseCircuit(int exercise, int circuit){
        mToggledExercise = exercise;
        mToggledCircuit = circuit;
    }

    public int getToggledExercise(){return mToggledExercise;}
    public int getToggledCircuit(){return mToggledCircuit;}

    /*
    @Override
    public void onDialogPositiveClick(DialogFragment dialog){
        Plan p = WorkoutData.get(this).crapNewPlan();
        DatabaseWrapper db = new DatabaseWrapper();
        db.saveEntirePlan(p);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog){

    }
    */

    public WorkspaceExpandableListAdapterMKII getAdapter(){
        return this.listAdapter;
    }

}
