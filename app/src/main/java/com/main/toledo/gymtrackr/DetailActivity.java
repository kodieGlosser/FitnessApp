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
    //private ArrayList<Boolean> mFragmentsCreated = new ArrayList<>();
    private int mTotalValidExercises;
    private int mIdPointer;


    //private LinearLayout previousLayout;
    //private LinearLayout currentLayout;
    //private LinearLayout nextLayout;
    private LinearLayout mainLayoutHandle;

    private Context mContext;
    private boolean mFirst;
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
        mContext = this;

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
                mIdPointer++;
            } else {
                previousLayout = getTerminalLayout(FIRST);
            }

            mFirst = true;
            e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
            currentLayout = createFragments(e);
            currentLayout.setLayoutParams(mParams);
            mFirst = false;

            if(mIdPointer + 1 < mTotalValidExercises){
                mIdPointer++;
                e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
                nextLayout = createFragments(e);
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
                //layout.setTag(FIRST);
                implementSwipeListener(layout);
                break;
            case LAST:
                t.setText("END OF WORKOUT!");
                //layout.setTag(LAST);
                implementSwipeListener(layout);
                break;
            case EMPTY:
                t.setText("ADD AN EXERCISE IN THE WORKSPACE TO GET STARTED!");
                //layout.setTag(EMPTY);
                break;
            default:
                t.setText("AN ERROR OCCURRED!");
                //layout.setTag(EMPTY);
                break;
        }
        return layout;
    }

    private boolean mapWorkout(int startCircuit, int startExercise){
        boolean emptyDataSet = true;
        int counter = -1;
        mIdPointer = 0;
        currentExerciseValue = 0;
        currentCircuitValue = 0;

        if (!Workout.get(currentCircuitValue).getExercise(currentExerciseValue).getName().equals("test")){
            mListIds.add(View.generateViewId());
            mDetailIds.add(View.generateViewId());
            mExerciseVals.add(currentExerciseValue);
            mCircuitVals.add(currentCircuitValue);
           // mFragmentsCreated.add(false);
            counter++;
            emptyDataSet = false;
            Log.d("4/12", "ID: " + counter + " -- EXERCISE: " + Workout.get(currentCircuitValue).getExercise(currentExerciseValue).getName() +
                " -- " + mExerciseVals + " -- " + mCircuitVals);
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
         //   mFragmentsCreated.add(false);
            Log.d("4/12", "ID: " + counter + " -- EXERCISE: " + Workout.get(currentCircuitValue).getExercise(currentExerciseValue).getName() +
                    " -- " + mExerciseVals + " -- " + mCircuitVals);
            hasExercises = findNextExerciseAscending();
        }

        mTotalValidExercises = counter + 1;
        Log.d("4/1", "Start Pointer: " + mIdPointer + " -- Total valid Exercises: " + mTotalValidExercises);
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
      //  mFragmentsCreated.set(mIdPointer, true);

        Log.d("4/12", "CREATING FRAG FOR EXERCISE: " + e.getName() + " -- PTR: " + mIdPointer);
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
        historyFragment.setAnimationDataSet(history, mContext, e.getName());

        if(mFirst) {
            historyFragment.setFirstFragment();
            detailsFragment.setFirstFragment();
        }
        //return layout
        return fragmentLayout;
    }

    private LinearLayout createDynamicFragmentLayout(){

        LinearLayout fragmentLayout = new LinearLayout(this);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mainParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

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
        //LinearLayout currentLayout = (LinearLayout)mainLayoutHandle.getChildAt(1);
        //int viewType = (int)currentLayout.getTag();
        if(mIdPointer != mTotalValidExercises){
            incrementUI();
        } else {
            //do nothing, at last thing
        }
    }

    public void previous(){
        if(mIdPointer != -1){
            decrementUI();
        } else {
            //do nothing
        }
    }

    private void incrementUI(){
       int list_id;
       if(mIdPointer != -1) {
           list_id = mListIds.get(mIdPointer);
           ((EditExerciseHistoryFragment) getSupportFragmentManager().findFragmentById(list_id)).animateOut();
       }
       new android.os.Handler().postDelayed(new Runnable() {

            public void run() {
                if(mIdPointer<=mTotalValidExercises-1)
                    increment();
            }

        }, 800);
    }

    private void decrementUI(){
        int list_id;
        if(mIdPointer != mTotalValidExercises) {
            list_id = mListIds.get(mIdPointer);
            ((EditExerciseHistoryFragment) getSupportFragmentManager().findFragmentById(list_id)).animateOut();
        }
        new android.os.Handler().postDelayed(new Runnable() {

            public void run() {
                if(mIdPointer>=0)
                    decrement();
            }

        }, 800);

    }
    private void increment(){

        mIdPointer++;

        LinearLayout nextLayout;
        if (mIdPointer != 0){
            mainLayoutHandle.removeViewAt(0);
        }

        mainLayoutHandle.getChildAt(0).setVisibility(View.GONE);
        mainLayoutHandle.getChildAt(1).setVisibility(View.VISIBLE);
        mainLayoutHandle.getChildAt(1).setLayoutParams(mParams);

        if(mIdPointer != mTotalValidExercises){ //animate view
            int list_id = mListIds.get(mIdPointer);
            ((EditExerciseHistoryFragment) getSupportFragmentManager().findFragmentById(list_id)).animateIn();
            new android.os.Handler().postDelayed(new Runnable() {

                public void run() {
                    setDetailEditFocus();
                }

            }, 800);
        }

        if (mIdPointer == mTotalValidExercises){
            //we are at end, add nothing
        } else {
            //add something
            if(mIdPointer+1 == mTotalValidExercises){
                //preload end message
                nextLayout = getTerminalLayout(LAST);
                nextLayout.setVisibility(View.GONE);
            } else {
                //preload next exercise
                mIdPointer++;
                Exercise e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
                nextLayout = createFragments(e);
                nextLayout.setVisibility(View.GONE);
                mIdPointer--;
            }
            //add the corrected something
            mainLayoutHandle.addView(nextLayout);
        }
    }

    private void decrement(){
        mIdPointer--;

        LinearLayout prevLayout;

        if(mIdPointer!=mTotalValidExercises-1){
            mainLayoutHandle.removeViewAt(2);
        }


        mainLayoutHandle.getChildAt(0).setVisibility(View.VISIBLE);
        mainLayoutHandle.getChildAt(0).setLayoutParams(mParams);
        mainLayoutHandle.getChildAt(1).setVisibility(View.GONE);

        if(mIdPointer != -1){ //animate view
            int list_id = mListIds.get(mIdPointer);
            ((EditExerciseHistoryFragment) getSupportFragmentManager().findFragmentById(list_id)).animateIn();

            new android.os.Handler().postDelayed(new Runnable() {

                public void run() {
                    setDetailEditFocus();
                }

            }, 800);
        }

        if(mIdPointer == -1){
            //we are at the very end, do nothing
        }else{
            //load next item
            if(mIdPointer-1 == -1){
                //next item is very end
                prevLayout = getTerminalLayout(FIRST);
                prevLayout.setVisibility(View.GONE);
                prevLayout.setTag(FIRST);
            } else {
                //next item is exercise
                mIdPointer--;
                Exercise e = Workout.get(mCircuitVals.get(mIdPointer)).getExercise(mExerciseVals.get(mIdPointer));
                prevLayout = createFragments(e);
                prevLayout.setVisibility(View.GONE);

                //   mFragmentsCreated.set(mIdPointer, true);
                mIdPointer++;
            }
            mainLayoutHandle.addView(prevLayout, 0);
        }
    }

    private void setDetailEditFocus(){
        if(mIdPointer < mTotalValidExercises) {
            int detail_id = mDetailIds.get(mIdPointer);
            ((EditExerciseDetailsFragment) getSupportFragmentManager()
                    .findFragmentById(detail_id)).focusFirstEdit();
            ((EditExerciseDetailsFragment) getSupportFragmentManager()
                    .findFragmentById(detail_id)).implementSwipeListener();
        }
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
