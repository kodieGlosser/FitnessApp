package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditActivity extends FragmentActivity {
    //EditWorkoutMapFragment mapFragment;
    EditExerciseDetailsFragment detailsFragment;
    EditExerciseHistoryFragment historyFragment;
    //public static EditWorkoutMapAdapter mapAdapter;
    public static EditExerciseHistoryAdapter historyAdapter;
    //public static EditExerciseDetailsAdapter detailsAdapter;
    int circuitValue;
    int exerciseValue;
    Exercise exercise;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            circuitValue = extras.getInt("CIRCUIT_VALUE");
            exerciseValue = extras.getInt("EXERCISE_VALUE");
            exercise = WorkoutData.get(this).getWorkout()
                    .get(circuitValue).getExercise(exerciseValue);
        }

        setContentView(R.layout.e_activity_main);

        detailsFragment = new EditExerciseDetailsFragment();
        //mapFragment = new EditWorkoutMapFragment();
        historyFragment = new EditExerciseHistoryFragment();
        /*
        //Eats the workoutdata singleton
        mapAdapter = new EditWorkoutMapAdapter(this, 0, )

        eats an arraylist of exercise history elements from the db
        */
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] history = db.loadHistoryByExerciseName(exercise.getName());
        ExerciseHistory[] historyStub = {new ExerciseHistory(new Date(), 666, 666, 136, 1),
                                         new ExerciseHistory(new Date(), 666, 666, 137, 1)};
        if (history.length != 0) {
            historyAdapter = new EditExerciseHistoryAdapter(this, 0, history);
        } else {
            historyAdapter = new EditExerciseHistoryAdapter(this, 0, historyStub);
        }
        /*
        //Eats an arraylist of metrcis from exercise it is sent
        detailsAdapter = new  EditExerciseDetailsAdapter(this, 0, )

        */
        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        //transaction.add(R.id.editMapFragment, mapFragment);
        transaction.add(R.id.editDetailsFragment, detailsFragment);
        transaction.add(R.id.editHistoryFragment, historyFragment);
        transaction.commit();

    }

    //public EditWorkoutMapAdapter getMapAdapter(){ return this.mapAdapter; }

    public EditExerciseHistoryAdapter getHistoryAdapter(){ return this.historyAdapter; }

    //public EditExerciseDetailsAdapter getDetailsAdapter(){ return this.detailsAdapter; }

    public Exercise getExercise(){
        return exercise;
    }
}
