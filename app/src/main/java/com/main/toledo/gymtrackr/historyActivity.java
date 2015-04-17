package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Adam on 4/6/2015.
 */
public class historyActivity extends ActionBarActivity{
    final static int TOTAL_HISTORY = 0, DAY_HISTORY = 1, EXERCISE_HISTORY = 2;
    private HistoryAdapter mHistoryAdapter;
    private HistoricExerciseAdapter mDayHistoryAdapter;
    private EditExerciseHistoryAdapter mExerciseHistoryAdapter;
    private ArrayList<Date> mHistoryDates;
    private ArrayList<ExerciseHistory> mWorkoutHistory;
    private ArrayList<ExerciseHistory> mExerciseHistory;

    private int mCurrentView;

    private int mLastSelectedPos;

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
        if(mHistoryDates == null) {
            DatabaseWrapper db = new DatabaseWrapper();
            mHistoryDates = db.getSpecificDaysFromHistory();
            mHistoryAdapter = new HistoryAdapter(this, 0, mHistoryDates);
        }

        mListFragment.setAdapter(mHistoryAdapter, TOTAL_HISTORY);

        mHeaderFragment.tryTotalHistoryText();

        mCurrentView = TOTAL_HISTORY;
    }

    public void implementWorkoutHistoryAdapter(int pos){
        Date selectedDate = mHistoryDates.get(pos);
        mLastSelectedPos = pos;
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] eh = db.loadExercisesByDate(selectedDate);

        if (mWorkoutHistory == null)
            mWorkoutHistory = new ArrayList<>();
        else
            mWorkoutHistory.clear();

        for(ExerciseHistory e : eh)
            mWorkoutHistory.add(e);

        mDayHistoryAdapter = new HistoricExerciseAdapter(this, 0, mWorkoutHistory);

        mListFragment.setAdapter(mDayHistoryAdapter, DAY_HISTORY);

        mHeaderFragment.setDayHistoryText(selectedDate);

        mCurrentView = DAY_HISTORY;
    }

    public void implementExerciseHistoryAdapter(int pos){
        String exerciseName = mWorkoutHistory.get(pos).getExerciseName();
        DatabaseWrapper db = new DatabaseWrapper();
        ExerciseHistory[] eh = db.loadHistoryByExerciseName(exerciseName);

        if (mExerciseHistory == null)
            mExerciseHistory = new ArrayList<>();
        else
            mExerciseHistory.clear();

        for(ExerciseHistory e : eh)
            mExerciseHistory.add(e);
        mExerciseHistoryAdapter =
                new EditExerciseHistoryAdapter(this, 0, mExerciseHistory);

        mListFragment.setAdapter(mExerciseHistoryAdapter, EXERCISE_HISTORY);

        mHeaderFragment.setExerciseHistoryText(exerciseName);

        mCurrentView = EXERCISE_HISTORY;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                switch(mCurrentView){
                    case TOTAL_HISTORY:
                        super.onBackPressed();
                        break;
                    case DAY_HISTORY:
                        implementHistoryAdapter();
                        break;
                    case EXERCISE_HISTORY:
                        implementWorkoutHistoryAdapter(mLastSelectedPos);
                        break;
                    default:
                        super.onBackPressed();
                        break;
                }
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    public HistoryAdapter getHistoryAdapter(){return mHistoryAdapter;}
}

