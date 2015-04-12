package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;
import java.util.zip.Inflater;

/**
 * Created by Adam on 4/9/2015.
 */
public class DetailActivity extends ActionBarActivity{

    private int currentCircuitValue;
    private int currentExerciseValue;
    private int nextCircuitValue;
    private int nextExerciseValue;
    //private int previousCircuitValue = -1;
   // private int previousExerciseValue= -1;

    private int mScreenHeight;

    private ArrayList<Integer> mExerciseVals = new ArrayList<>();
    private ArrayList<Integer> mCircuitVals = new ArrayList<>();
    private ArrayList<Integer> mListIds = new ArrayList<>();
    private ArrayList<Integer> mDetailIds = new ArrayList<>();
    private ArrayList<Boolean> mFragmentsCreated = new ArrayList<>();
    private int mTotalValidExercises;
    private int mIdPointer;

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
        setContentView(R.layout.d_activity_main);
        mainLayoutHandle = (LinearLayout) findViewById(R.id.mainLayoutHandle);
        Bundle extras = getIntent().getExtras();

        //Get total view height.
        /*
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        mScreenHeight = metrics.heightPixels;
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
        /*
        for( Circuit c : WorkoutData.get(this).getWorkout())
            for(Exercise e : c.getExercises())
                Log.d("pointerTests", e.getName());
        */
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

        boolean emptyDataSet = mapWorkout(currentCircuitValue, currentExerciseValue);


        if(!emptyDataSet){//we start at a valid exercise
            Exercise e;
            if(mIdPointer > 0){
                mIdPointer--;
                e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
                previousLayout = createFragments(e);
                previousLayout.setTag(EXERCISE);
                mIdPointer++;
            } else {
                previousLayout = getTerminalLayout(FIRST);
            }

            e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
            currentLayout = createFragments(e);
            currentLayout.setLayoutParams(mParams);
            currentLayout.setTag(EXERCISE);

            if(mIdPointer + 1 <= mTotalValidExercises){
                mIdPointer++;
                e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
                nextLayout = createFragments(e);
                nextLayout.setTag(EXERCISE);
                mIdPointer--;
            } else {
                nextLayout = getTerminalLayout(LAST);
            }
            nextLayout.setVisibility(View.GONE);

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
                implementSwipeListener(layout);
                break;
            case LAST:
                t.setText("END OF WORKOUT!");
                layout.setTag(LAST);
                implementSwipeListener(layout);
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

    private boolean mapWorkout(int startCircuit, int startExercise){
        boolean emptyDataSet = true;
        int counter = 0;
        mIdPointer = 0;
        currentExerciseValue = 0;
        currentCircuitValue = 0;

        if (!Workout.get(currentCircuitValue).getExercise(currentExerciseValue).getName().equals("test")){
            mListIds.add(View.generateViewId());
            mDetailIds.add(View.generateViewId());
            mExerciseVals.add(currentExerciseValue);
            mCircuitVals.add(currentCircuitValue);
            mFragmentsCreated.add(false);
            counter++;
            emptyDataSet = false;
        }

        boolean hasExercises = findNextExerciseAscending();

        while(hasExercises){
            emptyDataSet = false;
            counter++;
            currentExerciseValue = nextExerciseValue;
            currentCircuitValue = nextCircuitValue;

            if(currentExerciseValue == startExercise && currentCircuitValue == startCircuit){
                mIdPointer = counter;
            }

            mListIds.add(View.generateViewId());
            mDetailIds.add(View.generateViewId());
            mExerciseVals.add(currentExerciseValue);
            mCircuitVals.add(currentCircuitValue);
            mFragmentsCreated.add(false);
            hasExercises = findNextExerciseAscending();
        }
        mTotalValidExercises = counter;
        return emptyDataSet;
    }


    private LinearLayout createFragments(Exercise e){

        LinearLayout fragmentLayout = createDynamicFragmentLayout();
        //return fragment layout
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        EditExerciseDetailsFragment detailsFragment = new EditExerciseDetailsFragment();
        EditExerciseHistoryFragment historyFragment = new EditExerciseHistoryFragment();

        transaction.add(mDetailIds.get(mIdPointer), detailsFragment);
        transaction.add(mListIds.get(mIdPointer), historyFragment);
        mFragmentsCreated.set(mIdPointer, true);

        //Log.d("pointer tests", "CREATING FRAG FOR EXERCISE: " + e.getName() + " -- @ ID: " + mCurrentListID + " -- PTR: " + mIdPointer);
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

    private LinearLayout createDynamicFragmentLayout(){
        FrameLayout tabHandle = (FrameLayout) findViewById(R.id.tabHandle);
        //int fragmentLayoutHeight = mScreenHeight - tabBottom;
        //Log.d("4/11", "Remaining height: " + fragmentLayoutHeight);


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
        editFrame.setId(mDetailIds.get(mIdPointer));
        listFrame.setId(mListIds.get(mIdPointer));

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
        LinearLayout currentLayout = (LinearLayout)mainLayoutHandle.getChildAt(1);
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

    private void removeList(){

    }

    private void incrementUI(){
        //remove previous
        //mainLayoutHandle.removeView(previousLayout);
        //animate transition bottom expands, top shrinks
        //Log.d("4.11", "INCREMENTING EX: " + currentExerciseValue + " -- CIR: " + currentCircuitValue);

       // int list_id = mListIds.get(mIdPointer);
      //  ((EditExerciseHistoryFragment)getSupportFragmentManager().findFragmentById(list_id)).animateOut();
       // new android.os.Handler().postDelayed(new Runnable() {

        //    public void run() {
                increment();
          //  }

       // }, 1000);


        //Log.d("4.11", "incUI post invalidate");
        //MyCustomAnimation a = new MyCustomAnimation(currentLayout, nextLayout, 1000, totalLayoutHeight);
        //currentLayout.startAnimation(a);
        //previous = current
        //current = next
        //find new next
        //add new next to layout
    }

    private void increment(){
        if(mIdPointer != mTotalValidExercises)
            mIdPointer++;

        LinearLayout nextLayout;
        mainLayoutHandle.getChildAt(1).setVisibility(View.GONE);
        mainLayoutHandle.getChildAt(2).setVisibility(View.VISIBLE);
        mainLayoutHandle.getChildAt(2).setLayoutParams(mParams);

        if(mIdPointer+1 <= mTotalValidExercises) {
            if(!mFragmentsCreated.get(mIdPointer+1)) {
                mIdPointer++;
                Exercise e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
                nextLayout = createFragments(e);
                nextLayout.setVisibility(View.GONE);
                mainLayoutHandle.addView(nextLayout);
                mFragmentsCreated.set(mIdPointer+1, true);
                mIdPointer--;
            }
        } else {
            nextLayout = getTerminalLayout(LAST);
            nextLayout.setVisibility(View.GONE);
            mainLayoutHandle.addView(nextLayout);
        }
        mainLayoutHandle.removeViewAt(0);
    }

    private void decrementUI(){
        //remove next
        //mainLayoutHandle.removeView(nextLayout);
        //Log.d("4.11", "DECREMENTING EX: " + currentExerciseValue + " -- CIR: " + currentCircuitValue);
        if(mIdPointer != 0)
            mIdPointer--;
        
        LinearLayout prevLayout;
        mainLayoutHandle.getChildAt(1).setVisibility(View.GONE);
        mainLayoutHandle.getChildAt(0).setVisibility(View.VISIBLE);
        mainLayoutHandle.getChildAt(0).setLayoutParams(mParams);

        if(foundExercise) {

            previousExercise = Workout.get(previousCircuitValue).getExercise(previousExerciseValue);
            prevLayout = createFragments(previousExercise);

        } else {
            prevLayout = getTerminalLayout(FIRST);
        }
        mainLayoutHandle.removeViewAt(2);
        prevLayout.setVisibility(View.GONE);
        mainLayoutHandle.addView(prevLayout, 0);


        //Log.d("4.11", "decUI post invalidate");
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
        //int counter = mCircuitVals.get(mIdPointer);
        //int counter = 0;
        int forCounter = currentExerciseValue + 1;
        //int forCounter = mExerciseVals.get(mIdPointer) + 1;
        //int forCounter = 1;

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
    /*
    private boolean findNextExerciseAscending(){
        ArrayList<Circuit> workout = WorkoutData.get(this).getWorkout();
        int workoutSize = workout.size();
        boolean breakFlag = false;
        //int counter = currentCircuitValue;
        int counter = mCircuitVals.get(mIdPointer);
        //int counter = 0;
        //int forCounter = currentExerciseValue + 1;
        int forCounter = mExerciseVals.get(mIdPointer) + 1;
        //int forCounter = 1;

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
        //int loopCircuitVal = currentCircuitValue;
        int loopCircuitVal = mCircuitVals.get(mIdPointer);
        //int exerciseLoopVal = currentExerciseValue - 1;
        int exerciseLoopVal = mExerciseVals.get(mIdPointer) - 1;

        boolean foundExercise = false;
        Circuit prevCircuit =
                WorkoutData.get(this).getWorkout().get(loopCircuitVal);
        while(loopCircuitVal >= 0 && !foundExercise){
            for(int i = exerciseLoopVal; i>= 0; i--){
                //Log.d("4/6", "IN FOR: CIRCUIT VAL " + loopCircuitVal + " -- EXERCISE VAL" + i);
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
                    //Log.d("4/6", "END OF WHILE: CIRCUIT VAL " + loopCircuitVal + " -- EXERCISE VAL" + exerciseLoopVal);
                }
            }
        }
        return foundExercise;
    }


    private void initializePointer(){
        mIdPointer = 1;
        mListIds.add(View.generateViewId());
        mListIds.add(View.generateViewId());
        mListIds.add(View.generateViewId());
        mDetailIds.add(View.generateViewId());
        mDetailIds.add(View.generateViewId());
        mDetailIds.add(View.generateViewId());

        mCurrentDetailID = mDetailIds.get(1);
        mCurrentListID = mListIds.get(1);
        //Log.d("pointer tests", "Initialize, pointer ID: " + mIdPointer);
        //for(Integer i : mListIds)
            //Log.d("pointer tests", "" + i);
    }

    private void incrementPointer(){
        mIdPointer++;
        int nextPointer = mIdPointer + 1;
        if (nextPointer == mListIds.size()){
            mListIds.add(View.generateViewId());
            mDetailIds.add(View.generateViewId());
        }

        mCurrentDetailID = mDetailIds.get(mIdPointer);
        mCurrentListID = mListIds.get(mIdPointer);
        Log.d("pointer tests", "INC, pointer ID: " + mIdPointer);
        //for(Integer i : mListIds)
         //   Log.d("pointer tests", "" + i);
    }

    private void decrementPointer(){
        if (mIdPointer == 1){
            mListIds.add(0, View.generateViewId());
            mDetailIds.add(0, View.generateViewId());
            mCurrentDetailID = mDetailIds.get(1);
            mCurrentListID = mListIds.get(1);
        } else {
            mIdPointer--;
            mCurrentDetailID = mDetailIds.get(mIdPointer);
            mCurrentListID = mListIds.get(mIdPointer);
        }
        Log.d("pointer tests", "DEC, pointer ID: " + mIdPointer);
        //for(Integer i : mListIds)
            //Log.d("pointer tests", "" + i);
    }
    */


    private void implementSwipeListener(View v){

        v.setOnTouchListener(new View.OnTouchListener() {
            private int swipeThreshold = 150;
            private float currentY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();

                switch (action){
                    case MotionEvent.ACTION_DOWN: {
                        //Log.d("DETAIL SWIPE TESTS", "ACTION_DOWN");
                        final int pointerIndex = MotionEventCompat.getActionIndex(event);
                        final float startY = MotionEventCompat.getY(event, pointerIndex);

                        v.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if(currentY > startY + swipeThreshold){

                                    previous();

                                }
                                if(currentY < startY - swipeThreshold){
                                    next();

                                }
                            }
                        }, 500);

                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        currentY = event.getY();
                        break;
                    }

                    case MotionEvent.ACTION_UP: {

                        break;
                    }

                    case MotionEvent.ACTION_CANCEL: {

                        break;
                    }

                    case MotionEvent.ACTION_POINTER_UP: {

                        break;
                    }
                }
                return true;
            }
        });

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
