package com.main.toledo.gymtrackr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Adam on 4/2/2015.
 */
public class CreateExerciseActivity extends ActionBarActivity {
    private boolean mWeight = false;
    private boolean mReps = false;
    private boolean mTime = false;
    private boolean mOther = false;
    private boolean nameIsTaken;

    private String mExerciseName;
    private String mOtherValue;

    private TextView mOtherTextView;
    private EditText mOtherEditText;
    private EditText mExerciseNameText;

    //PASSED FROM BROWSE, PROBABLY  a better way to do this
    private int circuitNumber;
    private boolean circuitOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MONKEY CODE
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            circuitNumber = extras.getInt("EXTRA_CIRCUIT_NUMBER");
            circuitOpen = extras.getBoolean("EXTRA_CIRCUIT_OPEN");
        }
        //END MONKEY CODE
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ce_activity);
        mExerciseNameText = (EditText) findViewById(R.id.exerciseNameField);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_exercise, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("BROWSE FLOW", "UP");
                //MONKEY CODE
                Intent i = new Intent(this, BrowseActivity.class);
                i.putExtra("EXTRA_CIRCUIT_NUMBER", circuitNumber);
                i.putExtra("EXTRA_CIRCUIT_OPEN", circuitOpen);
                startActivity(i);
                //END MONKEY CODE
                return true;
            case R.id.save_changes:
                performSaveCheck();
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.weight:
                if (checked)
                    mWeight=true;
                else
                    mWeight=false;
                break;
            case R.id.reps:
                if (checked)
                    mReps=true;
                else
                    mReps=false;
            case R.id.time:
                if (checked)
                    mTime=true;
                else
                    mTime=false;
                break;
            case R.id.other:
                if (checked) {
                    mOther = true;
                    updateUI();
                } else {
                    mOther = false;
                    updateUI();
                }

                break;
        }
    }

    private void updateUI(){
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLayout);

        if (mOther){
            if(mOtherEditText == null)
                buildEditText();
            if(mOtherTextView == null)
                buildTextView();

            mainLayout.addView(mOtherTextView);
            mainLayout.addView(mOtherEditText);
        }else{
            mainLayout.removeView(mOtherTextView);
            mainLayout.removeView(mOtherEditText);
        }
    }

    private void buildEditText(){
        mOtherEditText = new EditText(this);

        LinearLayout.LayoutParams editTextParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        mOtherEditText.setLayoutParams(editTextParams);
    }

    private void buildTextView(){
        mOtherTextView = new TextView(this);
        mOtherTextView.setText("Input note text below");
        LinearLayout.LayoutParams textViewParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        mOtherEditText.setLayoutParams(textViewParams);
    }


    private void performSaveCheck(){

        String exerciseName = mExerciseNameText.getText().toString();

        DatabaseWrapper db = new DatabaseWrapper();
        Exercise[] exercises = db.browseExercisesByExactName(exerciseName);

        if(exercises.length == 0){
            nameIsTaken = false;
        } else {
            for (Exercise e : exercises){
                Log.d("CE ACT TEST", "COMPARING DB NAME: "
                        + e.getName() + " WITH NEW NAME: " + exerciseName);
                if (e.getName().equals(exerciseName)){
                    nameIsTaken = true;
                    break;
                } else {
                    nameIsTaken = false;
                }
            }
        }

        CreateExerciseDialog dialog = new CreateExerciseDialog();
        dialog.show(getFragmentManager(), "Add Exercise Dialog.");
    }

    public boolean getError(){
        return nameIsTaken;
    }

    public void save(){

    }


    /*
        public void addExerciseToExerciseTable(Exercise exercise) {
        ContentValues exerciseValues = new ContentValues();
        exerciseValues.put(COLUMN_EQUIPMENT_TYPE, exercise.getEquipment());
        exerciseValues.put(COLUMN_NAME, exercise.getName());
        exerciseValues.put(COLUMN_TARGET_MUSCLE, exercise.getTargetMuscle());
        exerciseValues.put(COLUMN_MUSCLE_GROUP, exercise.getMuscleGroup());
        myDatabase.insert(EXERCISE_TABLE, null, exerciseValues);

     */

}
