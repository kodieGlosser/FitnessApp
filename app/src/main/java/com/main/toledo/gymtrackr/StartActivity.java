package com.main.toledo.gymtrackr;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_activity_main);



        Button workoutNoPlanButton = (Button)findViewById(R.id.workoutNoPlanButton);

        workoutNoPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(StartActivity.this, WorkspaceActivity.class);
                startActivity(i);
            }
        });

        Button workoutFromPlanButton = (Button)findViewById(R.id.workoutFromPlanButton);

        workoutFromPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(StartActivity.this, LoadActivity.class);
                startActivity(i);
            }
        });

        /* Disabling these two buttons for now, we can always change them back later

        Button createPlanButton = (Button)findViewById(R.id.createPlanButton);

        createPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(StartActivity.this, WorkspaceActivity.class);
                startActivity(i);
            }
        });

        Button modifyPlanButton = (Button)findViewById(R.id.modifyPlanButton);

        modifyPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });

        */

        Button viewHistoryButton = (Button)findViewById(R.id.viewHistoryButton);

        viewHistoryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //our history call will go here
            }
        });
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
