package com.main.toledo.gymtrackr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Adam on 2/10/2015.
 * This is where the code for our browse filtering options lives.
 */
public class BrowseFilterFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    //this is the test button that shows up at the top of the list
    //code for our checkboxes or whatever is going to go here.
    private EditText mSearchField;
    private String mSearchText = "";
    private String mFilterOption;

    final private String[] mFilterOptions = {
            "None",
            "Shoulders",
            "Legs",
            "Back",
            "Abs",
            "Arms",
            "Chest",
            "Barbell",
            "Body",
            "Cable",
            "Dumbbell",
            "Machine",
            "Other"};

            /*
            "              None",
            "Muscle Group: Shoulders",
            "              Legs",
            "              Back",
            "              Abs",
            "              Arms",
            "              Chest",
            "Equipment:    Barbell",
            "              Body",
            "              Cable",
            "              Dumbbell",
            "              Machine",
            "              Other"};*/

    final private String[] mLegs = {"None", "Hamstrings", "Glutes", "Quadriceps", "Calves"};
    final private String[] mArms = {"None", "Bicep", "Triceps", "Forearm"};
    //private String[] mAbs = {"Abs"};
    //private String[] mShoulders = {"Shoulders"};
    //private String[] mChest = {"Chest"};
    final private String[] mBack = {"None", "Middle Back", "Lower Back", "Lats"};
    //final private String[] mEquip = {"Barbell", "Body",  "Cable", "Dumbbell", "Machine", "Other"};

    private String[] mSpecMuscleFilterArray;

    private ArrayAdapter<String> muscleAdapter;
    private ArrayAdapter<String> mFilterAdapter;

    private Spinner muscleSpinner;
    private Spinner filterSpinner;

    private final int NONE = 0, MUSCLE_GROUP = 1, EQUIPMENT_TYPE = 2, SPECIFIC_MUSCLE = 3;
    private int mSearchFilter = NONE;

    final int mMuscleSpinnerId = View.generateViewId();

    private String mLastMuscleGroup;
    private LinearLayout mFilterLayoutHandle;

    private final static int NOT_FROM_CREATE = 0, ADDED_EXERCISE_IN_CREATE = 1;
    @Override
    public void onAttach(Activity activity){
        Log.d("4/20." , "Browsefrag.onAttach");
        super.onAttach(activity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("4/20." , "Browsefrag.oncreate");
                super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        Log.d("4/20." , "Browsefrag.oncreateview");
        View v = inflater.inflate(R.layout.b_frag_filters, null);
        mSearchField = (EditText)v.findViewById(R.id.exercise_name_search);
        mSearchField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                String search_value;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    setSearchText();
                    queryDb();
                    handled = true;
                }
                return handled;
            }
        });

        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setSearchText();
                queryDb();
            }
        });

        filterSpinner = (Spinner) v.findViewById(R.id.filter_spinner);
        mFilterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mFilterOptions);
        mFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(mFilterAdapter);
        filterSpinner.setOnItemSelectedListener(this);

        mFilterLayoutHandle = (LinearLayout)v.findViewById(R.id.filterLayoutHandle);

        mSearchField.clearComposingText();
        queryDb();
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Log.d("4/20." , "Browsefrag.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        int type = WorkoutData.get(context).getBrowseTransition();
        if(type == ADDED_EXERCISE_IN_CREATE) {
            mSearchText = WorkoutData.get(context).getExerciseCreated();
            mSearchFilter = NONE;
            mSearchField.setText(mSearchText);
            mSearchField.setSelection(mSearchField.getText().length());
            queryDb();
        }
        //set last filter
        WorkoutData.get(context).setBrowseTransition(NOT_FROM_CREATE);
        WorkoutData.get(context).setExerciseCreated(null);
        String lastFilter1;
        String lastFilter2;
        if(WorkoutData.get(context).getLastFilter1()!=null) {
            lastFilter1 = WorkoutData.get(context).getLastFilter1();
            filterSpinner.setSelection(mFilterAdapter.getPosition(lastFilter1));
            //setFilterOptionsPrimary(lastFilter1);

        }
        //set search?
    }
    private void setSearchText(){
        mSearchText = mSearchField.getText().toString();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        String selectedItem;
        Log.d("4/4", "" + parent.getId());
        switch(parent.getId()){

            case R.id.filter_spinner:
                selectedItem = mFilterOptions[pos];
                //SET LAST FILTER
                setFilterOptionsPrimary(selectedItem);
                Log.d("4/4", "mgroup spin");
                break;
            default:
                selectedItem = mSpecMuscleFilterArray[pos];
                WorkoutData.get(getActivity()).setLastFilter2(selectedItem);
                if (selectedItem.equals("None")){
                    mSearchFilter = MUSCLE_GROUP;
                    mFilterOption = mLastMuscleGroup;
                } else {
                    mSearchFilter = SPECIFIC_MUSCLE;
                    mFilterOption = selectedItem;
                }
                queryDb();
                Log.d("4/4", "mspecgroup spin");
                break;
            //case R.id.muscle_spinner:
            //    break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("4/20." , "Browsefrag.onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("4/20." , "Browsefrag.onStop");
    }

    public void setFilterOptionsPrimary(String filterSelection){
        boolean addSpinners = false;
        Context context = getActivity();
        WorkoutData.get(context).setLastFilter1(filterSelection);
        switch(filterSelection){
            case "None":

                mSearchFilter = NONE;
                break;

            case "Shoulders":

                mFilterOption = "Shoulders";
                mSearchFilter = MUSCLE_GROUP;
                break;
            case "Legs":

                mFilterOption = "Legs";
                mSearchFilter = MUSCLE_GROUP;
                mSpecMuscleFilterArray = mLegs;
                addSpinners = true;
                break;
            case "Back":

                mFilterOption = "Back";
                mSearchFilter = MUSCLE_GROUP;
                mSpecMuscleFilterArray = mBack;
                addSpinners = true;
                break;
            case "Abs":

                mFilterOption = "Abs";
                mSearchFilter = MUSCLE_GROUP;
                break;
            case "Arms":

                mFilterOption = "Arms";
                mSearchFilter = MUSCLE_GROUP;
                mSpecMuscleFilterArray = mArms;
                addSpinners = true;
                break;
            case "Chest":

                mFilterOption = "Chest";
                mSearchFilter = MUSCLE_GROUP;
                break;
            case "Barbell":

                mFilterOption = "Barbell";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "Body":

                mFilterOption = "Body";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "Cable":

                mFilterOption = "Cable";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "Dumbbell":

                mFilterOption = "Dumbell";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "Machine":

                mFilterOption = "Machine";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "Other":

                mFilterOption = "Other";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
        }
        if(mSearchFilter == MUSCLE_GROUP){
            mLastMuscleGroup = mFilterOption;
        }

        if(addSpinners){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

            mFilterLayoutHandle.removeAllViewsInLayout();

            TextView v = new TextView(getActivity());
            v.setText("Select specific muscles: ");
            v.setLayoutParams(params);
            mFilterLayoutHandle.addView(v);

            if(muscleSpinner == null) {
                muscleSpinner = new Spinner(getActivity());
            }

            muscleAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mSpecMuscleFilterArray);
            muscleSpinner.setAdapter(muscleAdapter);
            muscleSpinner.setId(mMuscleSpinnerId);
            muscleSpinner.setOnItemSelectedListener(this);
            muscleSpinner.setLayoutParams(params);

            mFilterLayoutHandle.addView(muscleSpinner);

            String lastFilter2;

            if(WorkoutData.get(context).getLastFilter2()!=null) {
                lastFilter2 = WorkoutData.get(context).getLastFilter2();
                muscleSpinner.setSelection(muscleAdapter.getPosition(lastFilter2));
            }

        } else {
            mFilterLayoutHandle.removeAllViewsInLayout();
            WorkoutData.get(context).setLastFilter2(null);
        }

        queryDb();
    }

    private void queryDb(){
        DatabaseWrapper db = new DatabaseWrapper();
        Exercise[] exercises;
        switch(mSearchFilter){
            case NONE:
                exercises = db.browseExercisesByName(mSearchText);
                ((BrowseActivity)getActivity()).putBrowseExerciseList(exercises);
                break;
            case EQUIPMENT_TYPE:
                exercises = db.browseExerciseByEquipmentType(mFilterOption, mSearchText);
                ((BrowseActivity)getActivity()).putBrowseExerciseList(exercises);
                break;
            case MUSCLE_GROUP:
                exercises = db.browseExerciseByMuscleGroup(mFilterOption, mSearchText);
                ((BrowseActivity)getActivity()).putBrowseExerciseList(exercises);
                break;
            case SPECIFIC_MUSCLE:
                exercises = db.browseExerciseByTargetMuscle(mFilterOption, mSearchText);
                ((BrowseActivity)getActivity()).putBrowseExerciseList(exercises);
                break;
        }
    }
}
