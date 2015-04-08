package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
            "              Other"};

    final private String[] mLegs = {"None", "Hamstrings", "Glutes", "Quadriceps", "Calves"};
    final private String[] mArms = {"None", "Bicep", "Tricep", "Forearm"};
    //private String[] mAbs = {"Abs"};
    //private String[] mShoulders = {"Shoulders"};
    //private String[] mChest = {"Chest"};
    final private String[] mBack = {"None", "Middle Back", "Lower Back", "Lats"};
    //final private String[] mEquip = {"Barbell", "Body",  "Cable", "Dumbbell", "Machine", "Other"};

    private String[] mSpecMuscleFilterArray;

    private ArrayAdapter<String> muscleAdapter;
    private ArrayAdapter<String> mFilterAdapter;

    private Spinner muscleSpinner;

    private final int NONE = 0, MUSCLE_GROUP = 1, EQUIPMENT_TYPE = 2, SPECIFIC_MUSCLE = 3;
    private int mSearchFilter = NONE;

    final int mMuscleSpinnerId = View.generateViewId();

    private String mLastMuscleGroup;
    private LinearLayout mFilterLayoutHandle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.b_frag_filters, null);
        mSearchField = (EditText)v.findViewById(R.id.exercise_name_search);

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

        Spinner equipmentSpinner = (Spinner) v.findViewById(R.id.filter_spinner);
        mFilterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mFilterOptions);
        mFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equipmentSpinner.setAdapter(mFilterAdapter);
        equipmentSpinner.setOnItemSelectedListener(this);

        mFilterLayoutHandle = (LinearLayout)v.findViewById(R.id.filterLayoutHandle);

        queryDb();
        return v;
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
                setFilterOptionsPrimary(selectedItem);
                Log.d("4/4", "mgroup spin");
                break;
            default:
                selectedItem = mSpecMuscleFilterArray[pos];
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

    public void onNothingSelected(AdapterView<?> parent){

    }

    public void setFilterOptionsPrimary(String filterSelection){
        boolean addSpinners = false;

        switch(filterSelection){
            case "              None":

                mSearchFilter = NONE;
                break;

            case "Muscle Group: Shoulders":

                mFilterOption = "Shoulders";
                mSearchFilter = MUSCLE_GROUP;
                break;
            case "              Legs":

                mFilterOption = "Legs";
                mSearchFilter = MUSCLE_GROUP;
                mSpecMuscleFilterArray = mLegs;
                addSpinners = true;
                break;
            case "              Back":

                mFilterOption = "Back";
                mSearchFilter = MUSCLE_GROUP;
                mSpecMuscleFilterArray = mBack;
                addSpinners = true;
                break;
            case "              Abs":

                mFilterOption = "Abs";
                mSearchFilter = MUSCLE_GROUP;
                break;
            case "              Arms":

                mFilterOption = "Arms";
                mSearchFilter = MUSCLE_GROUP;
                mSpecMuscleFilterArray = mArms;
                addSpinners = true;
                break;
            case "              Chest":

                mFilterOption = "Chest";
                mSearchFilter = MUSCLE_GROUP;
                break;
            case "Equipment:    Barbell":

                mFilterOption = "Barbell";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "              Body":

                mFilterOption = "Body";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "              Cable":

                mFilterOption = "Cable";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "              Dumbbell":

                mFilterOption = "Dumbell";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "              Machine":

                mFilterOption = "Machine";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
            case "              Other":

                mFilterOption = "Other";
                mSearchFilter = EQUIPMENT_TYPE;
                break;
        }
        if(mSearchFilter == MUSCLE_GROUP){
            mLastMuscleGroup = mFilterOption;
        }

        if(addSpinners){
            mFilterLayoutHandle.removeAllViewsInLayout();
            TextView v = new TextView(getActivity());
            v.setText("Select specific muscles: ");
            mFilterLayoutHandle.addView(v);

            if(muscleSpinner == null) {
                muscleSpinner = new Spinner(getActivity());
            }

            muscleAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mSpecMuscleFilterArray);
            muscleSpinner.setAdapter(muscleAdapter);
            muscleSpinner.setId(mMuscleSpinnerId);
            muscleSpinner.setOnItemSelectedListener(this);

            mFilterLayoutHandle.addView(muscleSpinner);
        } else {
            mFilterLayoutHandle.removeAllViewsInLayout();
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
        db = null;
    }
}
