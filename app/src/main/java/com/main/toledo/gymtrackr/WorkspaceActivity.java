package com.main.toledo.gymtrackr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends ActionBarActivity {

    WorkspaceExpandableListAdapterMKIII listAdapter;
    WorkspaceListFragment ListFragment;
    private WorkspacePalletFragment PalletFragment ;
    String planName;
    Menu mOptionsMenu;
    private int mToggledExercise = -1;
    private int mToggledCircuit = -1;

    int mode;

    public static boolean isEditable = false;
    private boolean toEdit;
    //LOAD/START STATES
    final int PLAN = 1, WORKOUT = 2, WORKOUT_WITH_PLAN = 4, LOAD_PLAN = 5, FROM_DETAIL = 6;

    //BROWSE STATES
    final int NOT_BROWSE = 0, BROWSE_WORKOUT = 1, WORKOUT_BROWSE = 2;

    boolean workout_from_plan_flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                WorkoutData.get(this).setWorkoutState(WORKOUT);
                break;
        }

        setContentView(R.layout.w_activity_main);

        PalletFragment = new WorkspacePalletFragment();
        ListFragment = new WorkspaceListFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.WorkspacePalletContainer, PalletFragment);
        transaction.add(R.id.WorkspaceListContainer, ListFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_workspace, menu);
        /*
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
        */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            /*
            case R.id.action_toggle_edit:
                toggleEdit();
                return true;
                */
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
            removeChecked();
        }
    }

    public int getAppMode(){return mode;}
    /*Not used at the moment
    public void testMethod(){
        ListFragment.collapseLists(listAdapter);
    }
    */
    private void removeChecked(){
        //ListFragment.workspaceListView.removeCheckedItems();

    }
    //I feel like this should be in the header fragment...
    /*
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
    */

    @Override
    public void onResume(){

        //THIS FIXES A BUG WHERE THE ADAPTER WONT BE UPDATED WHEN THE
        //ACTIVITY IS RESUMED AFTER BROWSE


        super.onResume();
        if(listAdapter==null)
            listAdapter = new WorkspaceExpandableListAdapterMKIII(this);

        listAdapter.hideKeypad();
    }
    @Override
    public void onPause(){
        super.onPause();
        WorkoutData.get(this).setBrowseState(NOT_BROWSE);
    }

    public WorkspaceExpandableListAdapterMKIII getAdapter(){
        return this.listAdapter;
    }

}
