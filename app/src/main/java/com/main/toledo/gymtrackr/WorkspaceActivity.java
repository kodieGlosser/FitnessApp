package com.main.toledo.gymtrackr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;

import java.util.List;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends FragmentActivity{

    WorkspaceExpandableListAdapterMKII listAdapter;
    WorkspaceListFragment ListFragment;
    WorkspaceHeaderFragment HeaderFragment;
    String planName;

    int courseOfAction;

    public static boolean isEditable = true;
    boolean toBrowse;

    final int EDIT = 1, WORKOUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("APP FLOW TESTS", "ON CREATE CALLED IN WORKSPACE ACTIVITY");
        super.onCreate(savedInstanceState);
        //gets relevant data from load activity, plan name and whether we are editing or working out.
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            planName = extras.getString("EXTRA_PLAN_NAME");
            //Log.d("W_HEADER_DEBUG", "Plan name: " + planName);
            DatabaseWrapper db = new DatabaseWrapper();
            Plan planList = db.loadEntirePlan(planName);
            //Log.d("W_HEADER_DEBUG", "DB CALL COMPLETED");
            WorkoutData.get(this).eatPlan(planList);
            //Log.d("W_HEADER_DEBUG", "EATPLAN CALL COMPLETED");
            courseOfAction = extras.getInt("EXTRA_COURSE_OF_ACTION");
            //Log.d("W_HEADER_DEBUG", "CourseOfAction: " + courseOfAction);
        }

        toBrowse = false;

        setContentView(R.layout.w_activity_main);

        switch(courseOfAction){
            case EDIT:
                this.findViewById(R.id.mainLayoutHandle).setBackgroundColor(Color.GREEN);
                break;
            case WORKOUT:
                this.findViewById(R.id.mainLayoutHandle).setBackgroundColor(Color.RED);
                break;
            default:
                break;
        }

        //listAdapter = new WorkspaceExpandableListAdapterMKII(this);

        HeaderFragment = new WorkspaceHeaderFragment();
        ListFragment = new WorkspaceListFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.WorkspaceHeaderContainer, HeaderFragment);
        transaction.add(R.id.WorkspaceListContainer, ListFragment);
        transaction.commit();

    }

    public String getPlanName(){return planName;}

    public void setToBrowse(boolean b){toBrowse = b;}

    public int getCourseOfAction(){return courseOfAction;}
    /*Not used at the moment
    public void testMethod(){
        ListFragment.collapseLists(listAdapter);
    }
    */
    //I feel like this should be in the header fragment...
    public void toggleEdit(){
        Button toggleButton = (Button) HeaderFragment.getView().findViewById(R.id.toggleEdit);

        Log.d("EDITABLE TEST", "toggleEdit() called in workspace activity");
        isEditable = !isEditable;
        listAdapter.setEditable(isEditable);
        ListFragment.workspaceListView.toggleListeners(isEditable);
        if(isEditable){
            toggleButton.setBackgroundColor(Color.BLUE);
            toggleButton.setTextColor(Color.WHITE);
        }else{
            toggleButton.setBackgroundColor(android.R.drawable.btn_default_small);
            toggleButton.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
        }
    }
    @Override
    public void onResume(){
        //Log.d("APP FLOW TESTS", "ON RESUME CALLED IN WORKSPACE ACTIVITY");
        listAdapter = new WorkspaceExpandableListAdapterMKII(this);
        super.onResume();
    }
    @Override
    public void onDestroy(){
        Log.d("CLEAR WORKOUTDATA TEST", "onDestroy() called.");
        if(!toBrowse) {
            WorkoutData.get(this).getWorkout().clear();
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
