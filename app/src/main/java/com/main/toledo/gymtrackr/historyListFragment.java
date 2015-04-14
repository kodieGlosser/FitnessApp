package com.main.toledo.gymtrackr;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Adam on 4/6/2015.
 */
public class historyListFragment extends ListFragment{
    private int mMode;
    private ArrayList<Date> mDateArray;
    private ArrayList<ExerciseHistory> mExerciseHistoryArray;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //sets the list adapter to the one we made in the browse activity


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        switch (mMode){
            case historyActivity.TOTAL_HISTORY:
                ((historyActivity)getActivity()).implementWorkoutHistoryAdapter(position);
                break;
            case historyActivity.DAY_HISTORY:
                ((historyActivity)getActivity()).implementExerciseHistoryAdapter(position);
                break;
            case historyActivity.EXERCISE_HISTORY:

                break;
        }
        super.onListItemClick(l, v, position, id);
    }

    public void setAdapter(ArrayAdapter adapter, int mode){
        mMode = mode;
        setListAdapter(adapter);
    }
}
