package com.main.toledo.gymtrackr;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditActivity extends ActionBarActivity {
    //EditWorkoutMapFragment mapFragment;
    EditExerciseDetailsFragment detailsFragment;
    EditExerciseHistoryFragment historyFragment;
    WorkspaceTabFragment tabFragment;
    //public static EditWorkoutMapAdapter mapAdapter;
    public static EditExerciseHistoryAdapter historyAdapter;
    //public static EditExerciseDetailsAdapter detailsAdapter;
    private int circuitValue;
    private int exerciseValue;
    private Exercise exercise;
    private Circuit circuit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            circuitValue = extras.getInt("CIRCUIT_VALUE");
            exerciseValue = extras.getInt("EXERCISE_VALUE");
            if (circuitValue == -1 && exerciseValue == -1){
                circuitValue = 0;
                exerciseValue = 0;
            }

            exercise = WorkoutData.get(this).getWorkout()
                    .get(circuitValue).getExercise(exerciseValue);

            circuit = WorkoutData.get(this).getWorkout()
                    .get(circuitValue);
        }

        setContentView(R.layout.e_activity_main);

        detailsFragment = new EditExerciseDetailsFragment();
        //mapFragment = new EditWorkoutMapFragment();
        historyFragment = new EditExerciseHistoryFragment();
        tabFragment = new WorkspaceTabFragment();
        /*
        //Eats the workoutdata singleton
        mapAdapter = new EditWorkoutMapAdapter(this, 0, )

        eats an arraylist of exercise history elements from the db
        */
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] history = db.loadHistoryByExerciseName(exercise.getName());
        ExerciseHistory[] historyStub = {new ExerciseHistory(new Date(), 666, 666, 136, 1,1, 1),
                                         new ExerciseHistory(new Date(), 666, 666, 137, 1,1,1)};
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
        transaction.add(R.id.tabFragment, tabFragment);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //public EditWorkoutMapAdapter getMapAdapter(){ return this.mapAdapter; }

    public EditExerciseHistoryAdapter getHistoryAdapter(){ return this.historyAdapter; }

    //public EditExerciseDetailsAdapter getDetailsAdapter(){ return this.detailsAdapter; }

    public Exercise getExercise(){
        return exercise;
    }

    public Circuit getCircuit() { return circuit; }
}
