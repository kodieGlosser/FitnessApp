package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditActivity extends FragmentActivity {
    EditWorkoutMapFragment mapFragment;
    EditExerciseDetailsFragment detailsFragment;
    EditExerciseHistoryFragment historyFragment;
    public static EditWorkoutMapAdapter mapAdapter;
    public static EditExerciseHistoryAdapter historyAdapter;
    public static EditExerciseDetailsAdapter detailsAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            //get exercise extra
        }
        setContentView(R.layout.e_activity_main);
        //Log.d("test", " Looking for exercise for circuit" + circuitNumber);

        detailsFragment = new EditExerciseDetailsFragment();
        mapFragment = new EditWorkoutMapFragment();
        historyFragment = new EditExerciseHistoryFragment();
        /*
        //Eats the workoutdata singleton
        mapAdapter = new EditWorkoutMapAdapter(this, 0, )

        //eats an arraylist of exercise history elements from the db
        historyAdapter = new EditExerciseHistoryAdapter(this, 0, )

        //Eats an arraylist of metrcis from exercise it is sent
        detailsAdapter = new  EditExerciseDetailsAdapter(this, 0, )

        */
        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.editMapFragment, mapFragment);
        transaction.add(R.id.editDetailsFragment, detailsFragment);
        transaction.add(R.id.editHistoryFragment, historyFragment);
        transaction.commit();


    }

    public EditWorkoutMapAdapter getMapAdapter(){ return this.mapAdapter; }

    public EditExerciseHistoryAdapter getHistoryAdapter(){ return this.historyAdapter; }

    public EditExerciseDetailsAdapter getDetailsAdapter(){ return this.detailsAdapter; }

}
