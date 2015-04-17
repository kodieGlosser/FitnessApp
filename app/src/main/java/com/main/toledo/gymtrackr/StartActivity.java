package com.main.toledo.gymtrackr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Date;


public class StartActivity extends Activity {
    final int PLAN = 1, WORKOUT = 2, LOAD_PLAN = 5;

    private Button resumeWorkoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_activity_main);

        resumeWorkoutButton = (Button)findViewById(R.id.newWorkout);
        resumeWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(StartActivity.this, WorkspaceActivity.class);
                startActivity(i);
            }
        });

        Button workoutNoPlanButton = (Button)findViewById(R.id.workoutNow);
        workoutNoPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent i = new Intent(StartActivity.this, WorkspaceActivity.class);
                WorkoutData.get(getApplicationContext()).clear();
                WorkoutData.get(getApplicationContext()).initialize();
                WorkoutData.get(getApplicationContext()).setWorkoutState(WORKOUT);
                startActivity(i);

            }
        });

        Button workoutFromPlanButton = (Button)findViewById(R.id.workoutFromPlan);

        workoutFromPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(StartActivity.this, LoadActivity.class);
                startActivity(i);
            }
        });

        Button viewHistoryButton = (Button)findViewById(R.id.viewHistory);

        viewHistoryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(StartActivity.this, historyActivity.class);
                startActivity(i);
            }
        });

        if(WorkoutData.get(this).isEmpty()){
            resumeWorkoutButton.setVisibility(View.GONE);
        } else {
            resumeWorkoutButton.setVisibility(View.VISIBLE);
            if (WorkoutData.get(getApplicationContext()).getState() == PLAN){
                resumeWorkoutButton.setText("Resume planning");
            } else {
                resumeWorkoutButton.setText("Resume workout");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
