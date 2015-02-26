package com.main.toledo.gymtrackr;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends FragmentActivity {

    WorkspaceExpandableListAdapterMKII listAdapter;
    WorkspaceListFragment ListFragment;
    WorkspaceHeaderFragment HeaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w_activity_test);

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

    @Override
    public void onResume(){
        super.onResume();
    }

    public WorkspaceExpandableListAdapterMKII getAdapter(){
        return this.listAdapter;
    }

}
