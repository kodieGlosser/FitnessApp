package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Adam on 2/10/2015.
 */
public class LoadActivity extends FragmentActivity {
    //fragments needed for the load activity
    LoadHeaderFragment HeaderFragment;
    LoadListFragment ListFragment;
    private int actionToPerform;
    final int LOAD = 1, WORKOUT = 2;
    //this is the stub list
    //private static ArrayList<Plan> workoutPlans = new ArrayList<>();
    //the adapter is responsible for populating the load list
    public static LoadAdapter adapter;

    //testvals
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.l_activity_load);
        /*

        PLAN DB CALL WILL GO HERE, GIVE US OUR INITIAL LIST OF PLANS
        MAKE WORKOUTPLANS REFLECT OUR DB STUFF AND WE'RE GOOD

         */
        //populates our display initially
        //initiates filter
        HeaderFragment = new LoadHeaderFragment();
        ListFragment = new LoadListFragment();

        //DatabaseWrapper db = new DatabaseWrapper();
        //String[] planList = db.loadPlanNames();
        //db.loadPlanNames();
        String[] planList = {"CHEST", "BACK", "ARMS"};
        //creates a list adapter for our stub exercises
        adapter = new LoadAdapter(this, 0, planList);

        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.loadHeaderContainer, HeaderFragment);
        transaction.add(R.id.loadListContainer, ListFragment);
        transaction.commit();

    }

    public LoadAdapter getAdapter(){
        return this.adapter;
    }

    public void setToWorkout(){
        actionToPerform = WORKOUT;
        this.findViewById(R.id.loadMainWindow).setBackgroundColor(Color.RED);
    }

    public void setToEdit(){
        actionToPerform = LOAD;
        this.findViewById(R.id.loadMainWindow).setBackgroundColor(Color.GREEN);
    }

    public void onRadioButtonClicked(View view){

        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {

            case R.id.load_radio_edit:
                if (checked)
                    setToEdit();
                break;
            case R.id.load_radio_workout:
                if (checked)
                    setToWorkout();
                break;
        }
    }

    public class LoadAdapter extends ArrayAdapter{

        public LoadAdapter(Context context, int resource, String[] plans){
            super(context, resource, plans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if (convertView == null) {

                convertView = getLayoutInflater()
                        .inflate(R.layout.l_frag_list_plan, null);

            }

            String planName = (String)getItem(position);

            TextView nameTextView =
                    (TextView)convertView.findViewById(R.id.planName);
            nameTextView.setText(planName);


            return convertView;
        }

    }
}