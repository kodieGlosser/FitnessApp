package com.main.toledo.gymtrackr;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditActivity extends ActionBarActivity {

    private EditExerciseDetailsFragment detailsFragment;
    private EditExerciseHistoryFragment historyFragment;
    private WorkspaceTabFragment tabFragment;

    private static EditExerciseHistoryAdapter historyAdapter;

    private int circuitValue;
    private int exerciseValue;
    private Exercise exercise;
    private Circuit circuit;

    ArrayList<ExerciseHistory> history;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            Log.d("4/5", "EXTRAS NOT NULL");
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


        Log.d("4/5", circuitValue + " " + exerciseValue);
        setContentView(R.layout.e_activity_main);
        int aquaColor = Color.parseColor("#26d6cf");
        //this.findViewById(R.id.detailActivityMainView).setBackgroundColor(aquaColor);
        this.findViewById(R.id.detailFragmentView).setBackgroundColor(aquaColor);

        detailsFragment = new EditExerciseDetailsFragment();
        historyFragment = new EditExerciseHistoryFragment();
        tabFragment = new WorkspaceTabFragment();

        setHistoryAdapter(exercise.getName());
        historyAdapter = new EditExerciseHistoryAdapter(this, 0, history);
        historyFragment.setAdapter(historyAdapter);


        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.editDetailsFragment, detailsFragment);
        transaction.add(R.id.editHistoryFragment, historyFragment);
        transaction.add(R.id.tabFragment, tabFragment);
        transaction.commit();
    }

    public void setHistoryAdapter(String exerciseName){
        if(history == null)
            history = new ArrayList<>();

        history.clear();

        DatabaseWrapper db = new DatabaseWrapper();

        ExerciseHistory[] exerciseHistories = db.loadHistoryByExerciseName(exerciseName);

        if (exerciseHistories.length != 0) {
            for (ExerciseHistory e : exerciseHistories)
                history.add(e);
        } else {
            history.add(new ExerciseHistory(new Date(), 666, 666, 136, 1, 1, 1));
            history.add(new ExerciseHistory(new Date(), 666, 666, 137, 1, 1, 1));
        }

        if(historyAdapter != null)
            historyAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public ImageView createTransitionScreen(){


        View v = this.findViewById(R.id.detailFragmentView);
        v.setDrawingCacheEnabled(true);


        //if (mDragListener != null)
        //   mDragListener.onStartDrag(item);


        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());

        Context context = this;

        ImageView imageView = new ImageView(context);

        Drawable d = new BitmapDrawable(getResources(), bitmap);
        imageView.setImageDrawable(d);

        imageView.setAdjustViewBounds(false);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    //public EditWorkoutMapAdapter getMapAdapter(){ return this.mapAdapter; }

    public EditExerciseHistoryAdapter getHistoryAdapter(){ return this.historyAdapter; }

    //public EditExerciseDetailsAdapter getDetailsAdapter(){ return this.detailsAdapter; }

    public int getExercise(){
        return exerciseValue;
    }

    public int getCircuit() { return circuitValue; }

}
