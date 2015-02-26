package com.main.toledo.gymtrackr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    //this is the stub list
    private static ArrayList<Plan> workoutPlans = new ArrayList<>();
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

         */
        //populates our display initially
        //initiates filter
        HeaderFragment = new LoadHeaderFragment();
        ListFragment = new LoadListFragment();

        //creates a list adapter for our stub exercises
        adapter = new LoadAdapter(this, 0, workoutPlans);

        //adds fragments to layout/b_activity.xml
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.loadHeaderContainer, HeaderFragment);
        transaction.add(R.id.loadListContainer, ListFragment);
        transaction.commit();

    }
    //Adam:  This is a stub list of exercises used for the browse menu
    //in the format "Name, Muscle group, last used, equip used"
    //used to fetch the adapter from this activity in fragments.  the list fragment gets the adapter
    //via this method, then uses it to populate its list view.

    public LoadAdapter getAdapter(){
        return this.adapter;
    }

    //used to addToOpenCircuit data to the display list from fragments, this method is called from from the filter
    //fragment.  queries to populate the the list could go here.  we could set addItem to take some
    //params to specify things.

    //this is the actual adapter class used to populate layout/b_frag_exercise_list.xml
    public class LoadAdapter extends ArrayAdapter<Plan> {

        public LoadAdapter(Context context, int resource, ArrayList<Plan> plans){
            super(context, resource, plans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.l_frag_list_plan, null);

            }

            Plan p = getItem(position);

            TextView nameTextView =
                    (TextView)convertView.findViewById(R.id.planName);
            nameTextView.setText(p.getName());


            return convertView;
        }

    }
}