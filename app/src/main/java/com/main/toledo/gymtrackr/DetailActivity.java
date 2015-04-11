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
import java.util.zip.Inflater;

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

    private int mScreenHeight;

    private Exercise previousExercise;
    private Exercise currentExercise;
    private Exercise nextExercise;

    private Circuit circuit;
    //private LinearLayout previousLayout;
    //private LinearLayout currentLayout;
    //private LinearLayout nextLayout;
    private LinearLayout mainLayoutHandle;

    final int FIRST = 0, LAST = 1, EXERCISE = 2, EMPTY = 3;
    final int LIST_ID = 0, EDIT_ID = 1, VIEW_TYPE = 2;
    private ArrayList<Circuit> Workout = WorkoutData.get(this).getWorkout();
    private int totalLayoutHeight;

    LinearLayout.LayoutParams mParams = new LinearLayout
            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            0, 9f);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        mScreenHeight = metrics.heightPixels;
        //Get total view height.
        /*
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }


        totalLayoutHeight = height - actionBarHeight;

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        */


        if (extras != null){
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
        /*
        FrameLayout tabHandle = (FrameLayout) findViewById(R.id.tabHandle);
        int tabBottom = getRelativeBottom(tabHandle);

        int fragmentLayoutHeight = height - tabBottom;

        Log.d("4/11", "Remaining height: " + fragmentLayoutHeight);
        */
        //figure out where we are at...
        LinearLayout currentLayout = new LinearLayout(this);
        LinearLayout previousLayout;
        LinearLayout nextLayout;
        if(Workout.get(currentCircuitValue).getExercise(currentExerciseValue).getName().equals("test")){ //we start at 0 0 and it's test...
            Log.d("4/5", "0, 0 is test");
            boolean isNextExercise = findNextExerciseAscending();
            if(isNextExercise){ //if these are the same, we're at the end
                //empty data set screen
                currentLayout = getTerminalLayout(EMPTY);
            }
            mainLayoutHandle.addView(currentLayout);
        } else {//we start at a valid exercise
            currentExercise = Workout.get(currentCircuitValue).getExercise(currentExerciseValue);
            currentLayout = createFragments(currentExercise);
            currentLayout.setLayoutParams(mParams);


            if(findNextExerciseAscending()){
                nextExercise = Workout.get(nextCircuitValue).getExercise(nextExerciseValue);
                nextLayout = createFragments(nextExercise);
                nextLayout.setTag(EXERCISE);
            } else {
                nextLayout = getTerminalLayout(LAST);
            }
            nextLayout.setVisibility(View.GONE);


            if(findNextExerciseDescending()){
                previousExercise = Workout.get(previousCircuitValue).getExercise(previousExerciseValue);
                previousLayout = createFragments(previousExercise);
                previousLayout.setTag(EXERCISE);
            } else {
                previousLayout = getTerminalLayout(FIRST);
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

    private LinearLayout getTerminalLayout(int type){
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.d_terminal, null);
        TextView t = (TextView)layout.findViewById(R.id.terminalViewText);

        switch (type){
            case FIRST:
                t.setText("SWIPE UP TO BEGIN");
                layout.setTag(FIRST);
                break;
            case LAST:
                t.setText("END OF WORKOUT!");
                layout.setTag(LAST);
                break;
            case EMPTY:
                t.setText("ADD AN EXERCISE IN THE WORKSPACE TO GET STARTED!");
                layout.setTag(EMPTY);
                break;
            default:
                t.setText("AN ERROR OCCURRED!");
                layout.setTag(EMPTY);
                break;
        }
        return layout;
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
        FrameLayout tabHandle = (FrameLayout) findViewById(R.id.tabHandle);
        int tabBottom = getRelativeBottom(tabHandle);

        int fragmentLayoutHeight = mScreenHeight - tabBottom;
        Log.d("4/11", "Remaining height: " + fragmentLayoutHeight);


        LinearLayout fragmentLayout = new LinearLayout(this);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mainParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        /*
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
        */
        FrameLayout editFrame = new FrameLayout(this);

        LinearLayout.LayoutParams editParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                0, 1f);

        editFrame.setLayoutParams(editParams);

        FrameLayout listFrame = new FrameLayout(this);
        LinearLayout.LayoutParams listParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                0, 1f);
        listFrame.setLayoutParams(listParams);
        editFrame.setId(first_id);
        listFrame.setId(second_id);

        fragmentLayout.setLayoutParams(mainParams);
        fragmentLayout.addView(editFrame);
        fragmentLayout.addView(listFrame);

        fragmentLayout.setTag(EXERCISE);
        return fragmentLayout;
    }

    public void next(){
        LinearLayout currentLayout = (LinearLayout)mainLayoutHandle.getChildAt(1);
        int viewType = (int)currentLayout.getTag();
        if (currentLayout.getTag() == null)
            Log.d("4.11", "currentLayout.getTag() = null");


        switch (viewType){
            case FIRST:
                Log.d("4.11", "incUIcalled");
                incrementUI();
                break;
            case LAST:
                break;
            case EMPTY:
                break;
            case EXERCISE:
                Log.d("4.11", "incUIcalled");
                incrementUI();
                break;
        }
    }

    public void previous(){
        LinearLayout currentLayout = (LinearLayout)mainLayoutHandle.getChildAt(1);
        int viewType = (int)currentLayout.getTag();
        switch (viewType){
            case FIRST:
                break;
            case LAST:
                Log.d("4.11", "decUIcalled");
                decrementUI();
                break;
            case EMPTY:
                break;
            case EXERCISE:
                Log.d("4.11", "decUIcalled");
                decrementUI();
                break;
        }
    }

    private void incrementUI(){
        //remove previous
        //mainLayoutHandle.removeView(previousLayout);
        //animate transition bottom expands, top shrinks
        Log.d("4.11", "INCREMENTING EX: " + currentExerciseValue + " -- CIR: " + currentCircuitValue);
        LinearLayout nextLayout;
        mainLayoutHandle.getChildAt(1).setVisibility(View.GONE);
        mainLayoutHandle.getChildAt(2).setVisibility(View.VISIBLE);
        mainLayoutHandle.getChildAt(2).setLayoutParams(mParams);
        mainLayoutHandle.invalidate();
        boolean foundExercise = findNextExerciseAscending();
        if(foundExercise) {
            nextExercise = Workout.get(nextCircuitValue).getExercise(nextExerciseValue);
            nextLayout = createFragments(nextExercise);
            currentExercise = nextExercise;
            currentExerciseValue = nextExerciseValue;
            currentCircuitValue = nextCircuitValue;
        } else {
            nextLayout = getTerminalLayout(LAST);
        }
        mainLayoutHandle.removeViewAt(0);
        nextLayout.setVisibility(View.GONE);
        mainLayoutHandle.addView(nextLayout);

        Log.d("4.11", "incUI post invalidate");
        //MyCustomAnimation a = new MyCustomAnimation(currentLayout, nextLayout, 1000, totalLayoutHeight);
        //currentLayout.startAnimation(a);
        //previous = current
        //current = next
        //find new next
        //add new next to layout
    }

    private void decrementUI(){
        //remove next
        //mainLayoutHandle.removeView(nextLayout);
        Log.d("4.11", "DECREMENTING EX: " + currentExerciseValue + " -- CIR: " + currentCircuitValue);
        LinearLayout prevLayout;
        mainLayoutHandle.getChildAt(1).setVisibility(View.GONE);
        mainLayoutHandle.getChildAt(0).setVisibility(View.VISIBLE);
        mainLayoutHandle.getChildAt(0).setLayoutParams(mParams);
        mainLayoutHandle.invalidate();
        boolean foundExercise = findNextExerciseDescending();
        if(foundExercise) {
            previousExercise = Workout.get(previousCircuitValue).getExercise(previousExerciseValue);
            prevLayout = createFragments(previousExercise);
            currentExercise = previousExercise;
            currentExerciseValue = previousExerciseValue;
            currentCircuitValue = previousCircuitValue;
        } else {
            prevLayout = getTerminalLayout(FIRST);
        }
        mainLayoutHandle.removeViewAt(2);
        prevLayout.setVisibility(View.GONE);
        mainLayoutHandle.addView(prevLayout, 0);
        Log.d("4.11", "decUI post invalidate");
        //animate transition, top expands, bottom shrinks
        //MyCustomAnimation a = new MyCustomAnimation(currentLayout, previousLayout, 1000, totalLayoutHeight);
        //previousLayout.startAnimation(a);
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

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
    private int getRelativeBottom(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getBottom();
        else
            return myView.getBottom() + getRelativeBottom((View) myView.getParent());
    }
}
