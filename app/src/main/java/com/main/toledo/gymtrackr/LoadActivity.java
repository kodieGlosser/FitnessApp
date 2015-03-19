package com.main.toledo.gymtrackr;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/10/2015.
 */
public class LoadActivity extends FragmentActivity {
    //fragments needed for the load activity
    public String[] planArray; //stubs = {"CHEST", "BACK", "ARMS"};
    static public ArrayList<String> planList;
    LoadHeaderFragment HeaderFragment;
    LoadListFragment ListFragment;
    private int actionToPerform;
    //Program State Constants
    final int EDIT = 1, WORKOUT = 2, WORKOUT_FROM_PLAN_FLAG = 3;
    //Error constants
    final int OTHER = 10, INVALID_NAME_VALUE = 11, TAKEN_NAME_VALUE = 12;
    //
    int errorType;
    int slideVal = -650;

    //this is the stub list
    //private static ArrayList<Plan> workoutPlans = new ArrayList<>();
    //the adapter is responsible for populating the load list
    public static LoadAdapter adapter;
    private String planName;
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

        planList = new ArrayList<String>();
        DatabaseWrapper db = new DatabaseWrapper();
        planArray = db.loadPlanNames();
        //convert array to list for dynamic stuffs
        for (String s : planArray){
            planList.add(s);
        }
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
    /*
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

    public void setToWorkout(){
        actionToPerform = WORKOUT;
        this.findViewById(R.id.loadMainWindow).setBackgroundColor(Color.RED);
    }

    public void setToEdit(){
        actionToPerform = EDIT;
        this.findViewById(R.id.loadMainWindow).setBackgroundColor(Color.GREEN);
    }

    public int getActionToPerform(){
        return actionToPerform;
    }
    */

    public void setPlanName(String s){planName = s;};

    public ArrayList<String> getPlanList(){return planList; }

    public void createNewPlan(){
        //might be able to just create a new plan
        Plan p = WorkoutData.get(this).crapNewPlan();
        p.setName(planName);

        DatabaseWrapper db = new DatabaseWrapper();
        db.saveEntirePlan(p);   //NOTE TO SELF: DELETES IF THERE

        planList.clear();
        String[] planArray = db.loadPlanNames();
        //convert array to list for dynamic stuffs
        for (String s : planArray){
            planList.add(s);
        }
    }

    public void showNameDialog(){
        LoadNamePlanDialog dialog = new LoadNamePlanDialog();
        dialog.show(getSupportFragmentManager(), "NameDialogFragment");
    }

    public void showErrorDialog(int error){
        errorType = error;
        LoadErrorDialog dialog = new LoadErrorDialog();
        dialog.show(getSupportFragmentManager(), "ErrorDialogFragment");
    }

    public int getError(){
        return errorType;
    }
    /*
    public void editSelect(View view){
        Log.d("LOADTESTS", "EDIT SELECTED");
    }

    public void workoutSelect(View view){
        Log.d("LOADTEST", "WORKOUT SELECTED");
    }

    public void deleteSelect(View view){
        Log.d("LOADTEST", "DELETE SELECTED");
    }
    */



    public class LoadAdapter extends ArrayAdapter implements swipeListener{

        float mStartX;
        float mStartY;
        float mLeftX;
        float mRightX;

        public LoadAdapter(Context context, int resource, ArrayList<String> plans){
            super(context, resource, plans);
        }


        @Override
        public void onSwipe(int index){
            Log.d("LOAD SWIPE TESTS", "ON SWIPE CALLED");
            notifyDataSetChanged();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.l_frag_list_item, null);
            }

            final String planName = (String)getItem(position);
            /*
            LoadTextView nameTextView =
                    (LoadTextView)convertView.findViewById(R.id.planName);
            nameTextView.setText(planName);
            */
            final LoadTextView nameTextView =
                    (LoadTextView)convertView.findViewById(R.id.planName);
            nameTextView.setText(planName);
            /*
            nameTextView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    int action = event.getAction();
                    Log.d("MORE MOVE TESTS", "----------------------------ON GENERIC MOTION CALLED--------------------------");
                    switch (action) {
                        case MotionEvent.ACTION_DOWN: //mouse button is initially pressed
                            //Log.d("TOUCH TESTS", "MOTION EVENT IS ACTION_DOWN");
                            //maps a point to an integer position on list
                            Log.d("MORE MOVE TESTS", "----------------------------ON-DOWN--------------------------");
                            mStartX = event.getX();
                            mLeftX = mStartX - 100;
                            mRightX = mStartX + 100;
                            //mStartY = event.getY();
                            /*
                            mStartPosition = pointToPosition(x, y); //mstartposition is the TRUE position
                            if (mStartPosition != INVALID_POSITION) {
                                //first item visible
                                //get firstvisible position returns integer pointing to first
                                //thing displayed on screen
                                int mItemPosition = mStartPosition - getFirstVisiblePosition();

                                mDragPointOffset = y - getChildAt(mItemPosition).getTop(); //returns top position of this view relative to parent in pixels
                                mDragPointOffset -= ((int) ev.getRawY()) - y;
                                startDrag(mItemPosition, y);
                                //mItemPosition is the RELATIVE position on the list, 2nd item ON SCREEN vs 12th item
                                drag(x, y);// replace 0 with x if desired
                            }

                            break;
                        case MotionEvent.ACTION_MOVE: //mose if moved
                            Log.d("MORE MOVE TESTS", "-----------------------------------------------------------------------");
                            Log.d("MORE MOVE TESTS", "Start X: " + mStartX + " -- LEFT X: " + mLeftX + " -- RIGHT X: " + mRightX);
                            Log.d("MORE MOVE TESTS", "-----------------------------------------------------------------------");
                            if (event.getX() < mLeftX) {
                                ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(nameTextView, "translationX", slideVal);
                                mSlidInAnimator.setDuration(200);
                                mSlidInAnimator.start();
                            }
                            if (event.getX() > mRightX) {
                                ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(nameTextView, "translationX", -slideVal);
                                mSlidInAnimator.setDuration(200);
                                mSlidInAnimator.start();
                            }

                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP: //mouse button is released
                        default:
                            break;
                    }
                    return false;
                }
            });
            */
            /*
            nameTextView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onClick(View v) {
                    ObjectAnimator mSlidInAnimator = ObjectAnimator.ofFloat(nameTextView, "translationX", slideVal);
                    mSlidInAnimator.setDuration(200);
                    mSlidInAnimator.start();
                }
            });
            ?8

            */
            Button delete = (Button) convertView.findViewById(R.id.deleteButton);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DELETE TESTS", "plan name: " + planName);
                    DatabaseWrapper db = new DatabaseWrapper();
                    db.deletePlan(planName);
                    planList.remove(planName);

                    notifyDataSetChanged();
                }
            });

            Button edit = (Button) convertView.findViewById(R.id.editButton);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), WorkspaceActivity.class);
                    //gettag should return plan name of clicked item
                    Log.d("W_HEADER_DEBUG", "Plan name: " + planName);
                    i.putExtra("EXTRA_PLAN_NAME", planName);
                    //puts actiontoperform (EDIT or WORKOUT) into the intent
                    i.putExtra("EXTRA_MODE", EDIT);
                    startActivity(i);
                }
            });

            Button workout = (Button) convertView.findViewById(R.id.workoutButton);
            workout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), WorkspaceActivity.class);
                    //gettag should return plan name of clicked item
                    Log.d("W_HEADER_DEBUG", "Plan name: " + planName);
                    i.putExtra("EXTRA_PLAN_NAME", planName);
                    //puts actiontoperform (EDIT or WORKOUT) into the intent
                    i.putExtra("EXTRA_MODE", WORKOUT);
                    //another flag used for ui stuff
                    i.putExtra("WORKOUT_FROM_PLAN_FLAG", true);
                    startActivity(i);
                }
            });

            /*didn't work, may revisit (tried to pass plan name from here -> loadListFragment -> workspaceActivity)
            Log.d("W_HEADER_DEBUG", "Setting tag: " + planName);
            nameTextView.setTag(planName);
            */
            return convertView;
        }
    }
}