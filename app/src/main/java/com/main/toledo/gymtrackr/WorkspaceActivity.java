package com.main.toledo.gymtrackr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends FragmentActivity {

    WorkspaceExpandableListAdapterMKII listAdapter;
    WorkspaceListFragment ListFragment;
    WorkspaceHeaderFragment HeaderFragment;
    String planName;

    int courseOfAction;

    public static boolean isEditable = true;

    final int EDIT = 1, WORKOUT = 2;

    boolean toBrowse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gets relevant data from load activity, plan name and whether we are editing or working out.
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            planName = extras.getString("EXTRA_PLAN_NAME");
            DatabaseWrapper db = new DatabaseWrapper();
            Plan planList = db.loadEntirePlan("legs");
            WorkoutData.get(this).eatPlan(planList);
            //Log.d("W_HEADER_DEBUG", "Plan name: " + planName);
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

        listAdapter = new WorkspaceExpandableListAdapterMKII(
                        this, WorkoutData.get(this).getWorkout());

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
    public void onDestroy(){
        Log.d("CLEAR WORKOUTDATA TEST", "onDestroy() called.");
        if(!toBrowse) {
            WorkoutData.get(this).getWorkout().clear();
            WorkoutData.get(this).initialize();
        }
        super.onDestroy();
    }

    public WorkspaceExpandableListAdapterMKII getAdapter(){
        return this.listAdapter;
    }

}
