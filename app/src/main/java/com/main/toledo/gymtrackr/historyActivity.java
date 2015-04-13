package com.main.toledo.gymtrackr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Adam on 4/6/2015.
 */
public class historyActivity extends ActionBarActivity{
    final static int TOTAL_HISTORY = 0, DAY_HISTORY = 1, EXERCISE_HISTORY = 2;
    private HistoryAdapter mHistoryAdapter;
    private EditExerciseHistoryAdapter mExerciseHistoryAdapter;
    private ArrayList<Date> mHistoryDates;
    private ArrayList<ExerciseHistory> mWorkoutHistory;
    private ArrayList<ExerciseHistory> mExerciseHistory;

    historyHeaderFragment mHeaderFragment;
    historyListFragment mListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mHeaderFragment = new historyHeaderFragment();
        mListFragment = new historyListFragment();

        setContentView(R.layout.h_activity_main);
        //creates a list adapter for our stub exercises
        //adapter = new BrowseAdapter(this, 0, StubExercises);

        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.historyFiltersContainer, mHeaderFragment);
        transaction.add(R.id.historyListContainer, mListFragment);
        transaction.commit();

        implementHistoryAdapter();
    }

    private void implementHistoryAdapter(){
        DatabaseWrapper db = new DatabaseWrapper();
        mHistoryDates = db.getSpecificDaysFromHistory();
        mHistoryAdapter = new HistoryAdapter(this, 0, mHistoryDates);
        mListFragment.setAdapter(mHistoryAdapter, TOTAL_HISTORY);
    }

    public void implementWorkoutHistoryAdapter(int pos){
        Date selectedDate = mHistoryDates.get(pos);
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] eh = db.loadExercisesByDate(selectedDate);

        if (mWorkoutHistory == null)
            mWorkoutHistory = new ArrayList<>();
        else
            mWorkoutHistory.clear();

        for(ExerciseHistory e : eh)
            mWorkoutHistory.add(e);
        mExerciseHistoryAdapter = new EditExerciseHistoryAdapter(this, 0, mWorkoutHistory);
        mListFragment.setAdapter(mExerciseHistoryAdapter, DAY_HISTORY);
    }
    /*
    public void implementExerciseHistoryAdapter(int pos){
        String exerciseName = mWorkoutHistory.get(pos).;
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] eh = db.loadHistoryByExerciseName(exerciseName);

        if (mExerciseHistory == null)
            mExerciseHistory = new ArrayList<>();
        else
            mExerciseHistory.clear();

        for(ExerciseHistory e : eh)
            mExerciseHistory.add(e);
        mExerciseHistoryAdapter = new EditExerciseHistoryAdapter(this, 0, mExerciseHistory);
        mListFragment.setAdapter(mExerciseHistoryAdapter, EXERCISE_HISTORY);
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_browse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public HistoryAdapter getHistoryAdapter(){return mHistoryAdapter;}
}

