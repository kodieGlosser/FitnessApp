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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Adam on 4/2/2015.
 */
public class CreateExerciseActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    //Values used to write exercise to db
    private boolean mWeight = false;
    private boolean mReps = false;
    private boolean mTime = false;
    private boolean mOther = false;
    private String mMuscleGroup = "Shoulders";          //THIS IS PROBABLY TERRIBLE
    private String mSpecificMuscle = "Shoulders";       //ASSUMED INITIAL SPINNER VALUES
    private String mEquipment = "Barbell";              //SHOULDN'T BE DIFFERENT, BUT THINGS HAPPEN
    private String mExerciseName;
    private String mOtherValue;

    private boolean nameIsTaken;

    final private String[] mMuscleGroups = {"Shoulders", "Legs", "Back", "Abs", "Arms", "Chest"};
    final private String[] mLegs = {"Hamstrings", "Glutes", "Quadriceps", "Calves"};
    final private String[] mArms = {"Bicep", "Tricep", "Forearm"};
    //private String[] mAbs = {"Abs"};
    //private String[] mShoulders = {"Shoulders"};
    //private String[] mChest = {"Chest"};
    final private String[] mBack = {"Middle Back", "Lower Back", "Lats"};
    final private String[] mEquip = {"Barbell", "Body",  "Cable", "Dumbbell", "Machine", "Other"};
    private String[] mSpecMuscleArray;

    private ArrayAdapter<String> muscleAdapter;
    private ArrayAdapter<String> spinnerEquipAdapter;
    private ArrayAdapter<String> muscleGroupAdapter;

    private Spinner muscleSpinner;
    private TextView mOtherTextView;
    private EditText mOtherEditText;
    private EditText mExerciseNameText;

    final int mMuscleSpinnerId = View.generateViewId();

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
        /*
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        */
        Spinner equipmentSpinner = (Spinner) findViewById(R.id.equipment_spinner);
        spinnerEquipAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mEquip);
        spinnerEquipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equipmentSpinner.setAdapter(spinnerEquipAdapter);
        equipmentSpinner.setOnItemSelectedListener(this);

        Spinner muscleGroupSpinner = (Spinner) findViewById(R.id.muscle_group_spinner);
        muscleGroupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mMuscleGroups);
        muscleGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleGroupSpinner.setAdapter(muscleGroupAdapter);
        muscleGroupSpinner.setOnItemSelectedListener(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_exercise, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        Log.d("4/4", "" + parent.getId());
        switch(parent.getId()){

            case R.id.equipment_spinner:
                Log.d("4/4", "equipspin");
                mEquipment = mEquip[pos];
                break;
            case R.id.muscle_group_spinner:
                mMuscleGroup = mMuscleGroups[pos];
                addAdditionalSpinner(mMuscleGroup);
                Log.d("4/4", "mgroup spin");
                break;
            default:
                mSpecificMuscle = mSpecMuscleArray[pos];
                Log.d("4/4", "mspecgroup spin");
                break;
            //case R.id.muscle_spinner:
            //    break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent){

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
        LinearLayout metricLayout = (LinearLayout)findViewById(R.id.metricLayout);

        if (mOther){
            if(mOtherEditText == null)
                buildEditText();
            if(mOtherTextView == null)
                buildTextView();

            metricLayout.addView(mOtherTextView);
            metricLayout.addView(mOtherEditText);
        }else{
            metricLayout.removeView(mOtherTextView);
            metricLayout.removeView(mOtherEditText);
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

    public void addAdditionalSpinner(String muscleGroup){
        boolean addSpinners = false;
        LinearLayout layout = (LinearLayout)findViewById(R.id.muscle_spinner_layout);

        switch(muscleGroup){
            case "Abs":
                mSpecificMuscle = "Abs";
                if(muscleSpinner!=null)
                    layout.removeView(muscleSpinner);
                layout.removeAllViewsInLayout();
                break;
            case "Chest":
                mSpecificMuscle = "Chest";
                if(muscleSpinner!=null)
                    layout.removeView(muscleSpinner);
                layout.removeAllViewsInLayout();
                break;
            case "Shoulders":
                mSpecificMuscle = "Shoulders";
                Log.d("4/4", "Will not add spinner");
                if(muscleSpinner!=null)
                    layout.removeView(muscleSpinner);
                layout.removeAllViewsInLayout();
                layout.invalidate();
                break;
            case "Legs":
                mSpecMuscleArray = mLegs;
                addSpinners = true;
                break;
            case "Arms":
                mSpecMuscleArray = mArms;
                addSpinners = true;
                break;
            case "Back":
                mSpecMuscleArray = mBack;
                addSpinners = true;
                break;
        }
        if(addSpinners){
            layout.removeAllViewsInLayout();
            TextView v = new TextView(this);
            v.setText("Select specific muscles: ");
            layout.addView(v);

            if(muscleSpinner == null) {
                muscleSpinner = new Spinner(this);
            }

            muscleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSpecMuscleArray);
            muscleSpinner.setAdapter(muscleAdapter);
            muscleSpinner.setId(mMuscleSpinnerId);
            muscleSpinner.setOnItemSelectedListener(this);

            layout.addView(muscleSpinner);
        }
    }

    public boolean getError(){
        return nameIsTaken;
    }

    public void save(){
        //code to save to db goes here
    }
}