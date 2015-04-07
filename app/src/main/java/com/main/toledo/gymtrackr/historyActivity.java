package com.main.toledo.gymtrackr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

/**
 * Created by Adam on 4/6/2015.
 */
public class historyActivity extends ActionBarActivity{

    HistoryAdapter mHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        historyHeaderFragment HeaderFragment = new historyHeaderFragment();
        historyListFragment ListFragment = new historyListFragment();

        //creates a list adapter for our stub exercises
        //adapter = new BrowseAdapter(this, 0, StubExercises);

        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.historyFiltersContainer, HeaderFragment);
        transaction.add(R.id.historyListContainer, ListFragment);
        transaction.commit();
    }

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


    public class HistoryAdapter extends ArrayAdapter<Exercise> {

        public HistoryAdapter(Context context, int resource, ArrayList<Exercise> exercises) {
            super(context, resource, exercises);

        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){

            if ((convertView == null)) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.h_history_item, null);
            }

            return convertView;
        }
    }
}

