package com.main.toledo.gymtrackr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends FragmentActivity {

    WorkspaceExpandableListAdapterMKII listAdapter;
    WorkspaceListFragment ListFragment;
    WorkspaceHeaderFragment HeaderFragment;
    String planName;
    int courseOfAction;

    final int EDIT = 1, WORKOUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gets relevant data from load activity, plan name and whether we are editing or working out.
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            planName = extras.getString("EXTRA_PLAN_NAME");
            Log.d("W_HEADER_DEBUG", "Plan name: " + planName);
            courseOfAction = extras.getInt("EXTRA_COURSE_OF_ACTION");
            Log.d("W_HEADER_DEBUG", "CourseOfAction: " + courseOfAction);
        }



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
                this,  WorkoutData.get(this).getWorkout());

        HeaderFragment = new WorkspaceHeaderFragment();
        ListFragment = new WorkspaceListFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.WorkspaceHeaderContainer, HeaderFragment);
        transaction.add(R.id.WorkspaceListContainer, ListFragment);
        transaction.commit();
    }

    public String getPlanName(){return planName;}

    public int getCourseOfAction(){return courseOfAction;}

    public void testMethod(){
        ListFragment.collapseLists(listAdapter);
    }

    public WorkspaceExpandableListAdapterMKII getAdapter(){
        return this.listAdapter;
    }

}
