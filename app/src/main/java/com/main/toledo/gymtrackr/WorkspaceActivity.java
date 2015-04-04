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

    final int PLAN = 1, WORKOUT = 2;

    boolean workout_from_plan_flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("APP FLOW TESTS", "ON CREATE CALLED IN WORKSPACE ACTIVITY");
        super.onCreate(savedInstanceState);
        //gets relevant data from load activity, plan name and whether we are editing or working out.
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            planName = extras.getString("EXTRA_PLAN_NAME");
            mode = extras.getInt("EXTRA_MODE");
            if (extras.getBoolean("WORKOUT_FROM_PLAN_FLAG")){
                workout_from_plan_flag = true;
            } else {
                workout_from_plan_flag = false;
            }
            //Log.d("W_HEADER_DEBUG", "Plan name: " + planName);
            DatabaseWrapper db = new DatabaseWrapper();
            Plan planList = db.loadEntirePlan(planName);
            //Log.d("DB INTEGRATION TESTS", "PLAN ID: "+ planList.getPlanId() + " -- PLAN NAME: " + planList.getName());
            //Log.d("W_HEADER_DEBUG", "DB CALL COMPLETED");
            WorkoutData.get(this).eatPlan(planList, workout_from_plan_flag);
            //Log.d("W_HEADER_DEBUG", "EATPLAN CALL COMPLETED");
        }

        setContentView(R.layout.w_activity_main);

        /*
        int aquaColor = Color.parseColor("#26d6cf");
        int greenColor = Color.parseColor("#00B800");
        */


        //listAdapter = new WorkspaceExpandableListAdapterMKII(this);

        TabFragment = new WorkspaceTabFragment();
        ListFragment = new WorkspaceListFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.WorkspaceHeaderContainer, TabFragment);
        transaction.add(R.id.WorkspaceListContainer, ListFragment);
        transaction.commit();
        /*
        switch(mode){
            case PLAN:
                this.findViewById(R.id.mainLayoutHandle).setBackgroundColor(greenColor);
                break;
            case WORKOUT:
                this.findViewById(R.id.mainLayoutHandle).setBackgroundColor(aquaColor);
                break;
            default:
                break;
        }
        */


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_workspace, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_toggle_edit:
                toggleEdit(false);
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
        if (mode == WORKOUT) {
            //CODE FOR WORKOUT SAVE, EG EXPORT TO HISTORY
            DatabaseWrapper db = new DatabaseWrapper();
            ExerciseHistory[] eh = WorkoutData.get(this).crapHistory();
            for (ExerciseHistory e : eh){
                Log.d("SAVE TESTS", e.getExerciseId() + " IS BEING DIGESTED INTO EH");
            }
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
    public void toggleEdit(boolean fromOnResume){


        //if(fromOnResume)
        //    isEditable = false;

        if(!fromOnResume) {
            isEditable = !isEditable;


            if (isEditable) {
                mOptionsMenu.findItem(R.id.action_toggle_edit).setIcon(R.drawable.unlocked);
            /*
            toggleButton.setBackgroundColor(Color.BLUE);
            toggleButton.setTextColor(Color.WHITE);
            */
            } else {
                mOptionsMenu.findItem(R.id.action_toggle_edit).setIcon(R.drawable.locked);
            /*
            toggleButton.setBackgroundColor(android.R.drawable.btn_default_small);
            toggleButton.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
            */
            }
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
        toggleEdit(true);
        listAdapter.hideKeypad();


        super.onResume();
    }
    @Override
    public void onDestroy(){
        //Log.d("CLEAR WORKOUTDATA TEST", "onDestroy() called.");
        if(!toBrowse || !toEdit) {
            WorkoutData.get(this).clear();
            WorkoutData.get(this).initialize();
        }
        super.onDestroy();
    }
    @Override
    public void onStart(){
        /*
        Log.d("APP FLOW TESTS", "ON start CALLED IN WORKSPACE ACTIVITY");
        for (Circuit c : WorkoutData.get(this).getWorkout()){
            Log.d("APP FLOW TESTS", "CIRCUIT: " + c.getName());
        }
        */
        super.onStart();
    }

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
