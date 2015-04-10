package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Adam on 4/9/2015.
 */
public class DetailActivity extends ActionBarActivity{

    private int currentCircuitValue;
    private int currentExerciseValue;
    private int nextCircuitValue;
    private int nextExerciseValue;
    private int previousCircuitValue;
    private int previousExerciseValue;

    private Exercise previousExercise;
    private Exercise currentExercise;
    private Exercise nextExercise;

    private Circuit circuit;
    private LinearLayout previousLayout;
    private LinearLayout currentLayout;
    private LinearLayout nextLayout;
    private LinearLayout mainLayoutHandle;

    final int FIRST = 0, LAST = 1, EXERCISE = 2, EMPTY = 3;
    final int LIST_ID = 0, EDIT_ID = 1, VIEW_TYPE = 2;
    private ArrayList<Circuit> Workout = WorkoutData.get(this).getWorkout();
    private int totalLayoutHeight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        //Get total view height.
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        totalLayoutHeight = height - actionBarHeight;

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (extras != null){
            Log.d("4/5", "EXTRAS NOT NULL");
            currentCircuitValue = extras.getInt("CIRCUIT_VALUE");
            currentExerciseValue = extras.getInt("EXERCISE_VALUE");
            if (currentCircuitValue == -1 && currentExerciseValue == -1){
                currentCircuitValue = 0;
                currentExerciseValue = 0;
            }
        } else {
            currentCircuitValue = 0;
            currentExerciseValue = 0;
        }

        //first look at passed values if exercise name equals test then we have an empty dataset
        //display special screen
        //otherwise initialize

        setContentView(R.layout.d_activity_main);
        mainLayoutHandle = (LinearLayout) findViewById(R.id.mainLayoutHandle);

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        WorkspaceTabFragment tabFragment = new WorkspaceTabFragment();
        transaction.add(R.id.tabHandle, tabFragment);
        transaction.commit();
        //figure out where we are at...
        if(Workout.get(currentCircuitValue).getExercise(currentExerciseValue).getName().equals("test")){ //we start at 0 0 and it's test...
            Log.d("4/5", "0, 0 is test");
            boolean isNextExercise = findNextExerciseAscending();
            if(isNextExercise){ //if these are the same, we're at the end
                //empty data set screen
                currentLayout = (LinearLayout)inflater.inflate(R.layout.d_terminal, null);
                TextView t = (TextView)currentLayout.findViewById(R.id.terminalViewText);
                t.setText("ADD AN EXERCISE IN THE WORKSPACE TO GET STARTED!");
                currentLayout.setTag(VIEW_TYPE, EMPTY);
            }
            mainLayoutHandle.addView(currentLayout);
        } else {//we start at a valid exercise
            currentExercise = Workout.get(currentCircuitValue).getExercise(currentExerciseValue);
            currentLayout = createFragments(currentExercise);

            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            currentLayout.setLayoutParams(params);

            if(findNextExerciseAscending()){
                nextExercise = Workout.get(nextCircuitValue).getExercise(nextExerciseValue);
                nextLayout = createFragments(nextExercise);

            } else {
                //nextLayout = last layout
                nextLayout = (LinearLayout)inflater.inflate(R.layout.d_terminal, null);
                TextView t = (TextView)nextLayout.findViewById(R.id.terminalViewText);
                t.setText("END OF WORKOUT!");
                nextLayout.setTag(VIEW_TYPE, LAST);
            }
            nextLayout.setVisibility(View.GONE);

            if(findNextExerciseDescending()){
                previousExercise = Workout.get(previousCircuitValue).getExercise(previousExerciseValue);
                previousLayout = createFragments(previousExercise);
                previousLayout.setTag(VIEW_TYPE, EXERCISE);
            } else {
                previousLayout = (LinearLayout)inflater.inflate(R.layout.d_terminal, null);
                TextView t = (TextView)previousLayout.findViewById(R.id.terminalViewText);
                t.setText("SWIPE UP TO BEGIN");
                previousLayout.setTag(VIEW_TYPE, FIRST);
            }
            previousLayout.setVisibility(View.GONE);

            mainLayoutHandle.addView(previousLayout);
            mainLayoutHandle.addView(currentLayout);
            mainLayoutHandle.addView(nextLayout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private LinearLayout createFragments(Exercise e){
        //create 3 ids
        int detail_id = View.generateViewId();
        int list_id = View.generateViewId();

        //pass three ids to make fragment layout
        LinearLayout fragmentLayout = createDynamicFragmentLayout(detail_id, list_id);
        //return fragment layout
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        EditExerciseDetailsFragment detailsFragment = new EditExerciseDetailsFragment();
        EditExerciseHistoryFragment historyFragment = new EditExerciseHistoryFragment();

        transaction.add(detail_id, detailsFragment);
        transaction.add(list_id, historyFragment);
        //put fragments in layout
        transaction.commit();

        //put exercise in detail
        detailsFragment.putExercise(e);
        //set adapter for list
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] exerciseHistories = db.loadHistoryByExerciseName(e.getName());
        ArrayList<ExerciseHistory> history = new ArrayList<>();
        for (ExerciseHistory eh : exerciseHistories)
            history.add(eh);
        EditExerciseHistoryAdapter adapter = new EditExerciseHistoryAdapter(this, 0, history);
        historyFragment.setAdapter(adapter);
        //return layout
        return fragmentLayout;
    }

    private LinearLayout createDynamicFragmentLayout(int first_id, int second_id){
        LinearLayout fragmentLayout = new LinearLayout(this);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout editFrame = new LinearLayout(this);
        LinearLayout.LayoutParams editParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        editFrame.setLayoutParams(editParams);

        LinearLayout listFrame = new LinearLayout(this);
        LinearLayout.LayoutParams listParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        listFrame.setLayoutParams(listParams);

        editFrame.setId(first_id);
        listFrame.setId(second_id);

        fragmentLayout.addView(editFrame);
        fragmentLayout.addView(listFrame);

        fragmentLayout.setTag(VIEW_TYPE, EXERCISE);
        fragmentLayout.setTag(EDIT_ID, first_id);
        fragmentLayout.setTag(LIST_ID, second_id);
        return fragmentLayout;
    }

    public void next(){
        int viewType = (int)currentLayout.getTag();
        switch (viewType){
            case FIRST:
                incrementUI();
                break;
            case LAST:
                break;
            case EMPTY:
                break;
            case EXERCISE:
                incrementUI();
                break;
        }
    }

    public void previous(){
        int viewType = (int)currentLayout.getTag();
        switch (viewType){
            case FIRST:
                break;
            case LAST:
                decrementUI();
                break;
            case EMPTY:
                break;
            case EXERCISE:
                decrementUI();
                break;
        }
    }

    private void incrementUI(){
        //remove previous
        mainLayoutHandle.removeView(previousLayout);
        //animate transition bottom expands, top shrinks

        MyCustomAnimation a = new MyCustomAnimation(currentLayout, nextLayout, 1000, totalLayoutHeight);
        currentLayout.startAnimation(a);
        //previous = current
        //current = next
        //find new next
        //add new next to layout
    }

    private void decrementUI(){
        //remove next
        mainLayoutHandle.removeView(nextLayout);
        //animate transition, top expands, bottom shrinks
        MyCustomAnimation a = new MyCustomAnimation(currentLayout, previousLayout, 1000, totalLayoutHeight);
        previousLayout.startAnimation(a);
        //next = current
        //current = previous
        //find new previous
        //add new previous to layout
    }

    private boolean findNextExerciseAscending(){
        ArrayList<Circuit> workout = WorkoutData.get(this).getWorkout();
        int workoutSize = workout.size();
        boolean breakFlag = false;
        int counter = currentCircuitValue;
        int forCounter = currentExerciseValue + 1;


        while((!breakFlag) && (counter < workoutSize)){
            Circuit c = workout.get(counter);
            int circuitSize = c.getSize();
            for(int i = forCounter; i < circuitSize; i++){
                if(!c.getExercise(i).getName().equals("test")){
                    breakFlag = true;
                    nextExerciseValue = i;
                    nextCircuitValue = counter;
                    break;
                }
            }
            forCounter = 0;
            counter++;
        }
        return breakFlag;
    }

    private boolean findNextExerciseDescending(){
        int loopCircuitVal = currentCircuitValue;
        int exerciseLoopVal = currentExerciseValue - 1;
        boolean foundExercise = false;
        Circuit prevCircuit =
                WorkoutData.get(this).getWorkout().get(loopCircuitVal);
        while(loopCircuitVal >= 0 && !foundExercise){
            for(int i = exerciseLoopVal; i>= 0; i--){
                Log.d("4/6", "IN FOR: CIRCUIT VAL " + loopCircuitVal + " -- EXERCISE VAL" + i);
                Exercise e = prevCircuit.getExercise(i);
                if(!e.getName().equals("test")){
                    previousExerciseValue = i;
                    previousCircuitValue = loopCircuitVal;
                    foundExercise = true;
                    break;
                }
            }
            if(!foundExercise) {
                loopCircuitVal--;
                if(loopCircuitVal>=0) {
                    prevCircuit =
                            WorkoutData.get(this).getWorkout().get(loopCircuitVal);
                    exerciseLoopVal = prevCircuit.getExercises().size() - 1;
                    Log.d("4/6", "END OF WHILE: CIRCUIT VAL " + loopCircuitVal + " -- EXERCISE VAL" + exerciseLoopVal);
                }
            }
        }
        return foundExercise;
    }
}
