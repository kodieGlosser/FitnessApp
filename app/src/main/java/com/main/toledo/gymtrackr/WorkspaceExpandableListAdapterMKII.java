package com.main.toledo.gymtrackr;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WorkspaceExpandableListAdapterMKII extends BaseExpandableListAdapter implements DropListener{

    private Context _context;

    private boolean editable = true;

    private EditText m_editTextHandle;

    final int CIRCUIT = 1;
    final int EXERCISE = 2;
    final float mCheckedIndentation = 100;
    private ArrayList<Circuit> Workout = new ArrayList<>();

    private LinearLayout.LayoutParams params;

    public WorkspaceExpandableListAdapterMKII(Context context){
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        this._context = context;
        Workout = WorkoutData.get(_context).getWorkout();
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        return Workout.get(groupPosition).getExercise(childPosition);
    }
    /*
    public void cleanView(Rect viewHitRect){

        Log.d("PAD BUGS", "CLEAN VIEW: CALLED");
        if (m_editTextHandle != null) {
            //Log.d("PAD BUGS", "CLEAN VIEW: PLAN TEXT IS NOT NULL");
            if (!m_editTextHandle.getLocalVisibleRect(viewHitRect)
                    || viewHitRect.height() < m_editTextHandle.getHeight()) {
                //Log.d("PAD BUGS", "CLEAN VIEW: SHOULD HIDE KEYPAD");
                hideKeypad();
            } else {
                // NONE of the imageView is within the visible window

            }
        }
    }
    */

    public void setEditable(boolean b){
        //Log.d("EDITABLE TEST", "setEditable() called in adapter.  editable: " + b);
        editable = b;
        notifyDataSetChanged();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final int group = groupPosition;
        final int child = childPosition;
        boolean emptyFlag = false;

        if (group >= Workout.size()-1){
        //    Log.d("test", "should make buttons");
            if(editable){
                //Log.d("FLOW TEST", "DRAW BUTTONS SHOULD BE CALLED NOW");
                //if (convertView == null || (convertView.getTag() != "End Button")) {
                    //Log.d("FLOW TEST", "DRAW BUTTONS CALLED");
                emptyFlag = true;
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_workout_menu_buttons, null);
                //add circuit code

                Button addCircuitButton = (Button) convertView.findViewById(R.id.AddCircuitButton);
                addCircuitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkoutData.get(_context).addCircuit(group);
                        ((WorkspaceActivity) _context).ListFragment.workspaceListView.expandGroup(group);

                        notifyDataSetChanged();
                            /*
                            ((WorkspaceActivity) _context).ListFragment
                                    .expandLists(((WorkspaceActivity) _context).getAdapter());
                                    */
                    }
                });

                Button browseButton = (Button) convertView.findViewById(R.id.BrowseButton);
                browseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(_context, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);
                        i.putExtra("EXTRA_CIRCUIT_NUMBER", group);
                        i.putExtra("EXTRA_CIRCUIT_OPEN", false);
                        ((WorkspaceActivity) _context).setToBrowse(true);
                        ((WorkspaceActivity) _context).ListFragment.workspaceListView.clearHandle();
                        _context.startActivity(i);
                    }
                });

                Button plusButton = (Button) convertView.findViewById(R.id.PlusButton);
                plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WorkoutData.get(_context).isAnExerciseToggled()){
                            int WorkoutSize = Workout.size();
                            Exercise e = WorkoutData.get(_context).getToggledExerciseCopy();
                            WorkoutData.get(_context).addClosedCircuit(e, WorkoutSize -1);
                            notifyDataSetChanged();
                        }
                    }
                });
                //}
            } else {
                //if (convertView == null || (convertView.getTag() != "Blank")) {
                emptyFlag = true;
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_empty_wopadding, null);
                //convertView.setTag("Blank");
                //}
            }
        }else {
            if (childPosition < (Workout.get(group).getSize() - 1)
                    || !(Workout.get(group).isOpen())) {
                //for the not last items in the list
                //if (convertView == null || (convertView.getTag() != "Data")) {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_exercise, null);
                //convertView.setTag("Data");
                //code to set color

                //}

                LinearLayout dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                dynamicViewLayout.removeAllViewsInLayout();
                /*
                if (((WorkspaceActivity) _context).workoutFromPlan()){
                    dynamicViewLayout.addView(createGoalLayout(group, child));
                }
                */
                if (Workout.get(group).getExercise(childPosition).hasPlanMetrics()){
                    dynamicViewLayout.addView(createGoalLayout(group, child));
                }

                RelativeLayout exerciseViewHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);
                dynamicViewLayout.addView(createMetricEditTextLayout(group, child, exerciseViewHandle));


                //sets text for name
                TextView textView = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setText(Workout.get(group).getExercise(childPosition).getName());

                if(Workout.get(group).getExercise(childPosition).isSaveToHistorySet()){
                    RelativeLayout relativeLayout = (RelativeLayout) convertView
                            .findViewById(R.id.exercise_relative_layout_handle);

                    ImageView mChecked;

                    mChecked = (ImageView) inflater.inflate(R.layout.w_check, null);

                    relativeLayout.addView(mChecked);

                    RelativeLayout.LayoutParams params =
                            (RelativeLayout.LayoutParams) mChecked.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);
                    layout.setTranslationX(mCheckedIndentation);
                }

            } else {
                //for the last items
                //switch will go here when we have an edit toggle to hide buttons
                if(editable) {
                    //if (convertView == null || (convertView.getTag() != "Button")) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_circuit_menu_buttons, null);
                    //convertView.setTag("Button");
                    Button browseButton = (Button) convertView.findViewById(R.id.BrowseButton);
                    browseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(_context, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);
                            i.putExtra("EXTRA_CIRCUIT_OPEN", true);
                            i.putExtra("EXTRA_CIRCUIT_NUMBER", group);
                            ((WorkspaceActivity) _context).setToBrowse(true);
                            ((WorkspaceActivity) _context).ListFragment.workspaceListView.clearHandle();
                            _context.startActivity(i);
                        }
                    });
                    //}
                    Button plusButton = (Button) convertView.findViewById(R.id.PlusButton);
                    plusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (WorkoutData.get(_context).isAnExerciseToggled()){
                                int CircuitSize = Workout.get(group).getSize();
                                Exercise e = WorkoutData.get(_context).getToggledExerciseCopy();
                                Workout.get(group).addExerciseAtIndex(CircuitSize - 1, e);
                                notifyDataSetChanged();
                            }
                        }
                    });

                }else{
                    //if (convertView == null || (convertView.getTag() != "Blank")) {
                    emptyFlag = true;
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_empty_wopadding, null);
                    //convertView.setTag("Blank");
                    //}
                }
                //ConvertViewIsButton = true;
            }
        }

        if(Workout.get(group).getExercise(child).isToggled()){
            //convertView.findViewById(R.id.exerciseMainLayout).setBackgroundColor(Color.RED);
            convertView.findViewById(R.id.exercise_relative_layout_handle).setPressed(true);
        } else if(!emptyFlag){
            //convertView.findViewById(R.id.exerciseMainLayout).setBackgroundColor(Color.BLUE);
            //convertView.setBackgroundColor(mBackgroundColor);
        } else {

        }

        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition){
        return Workout.get(groupPosition).getSize();
    }


    @Override
    public Object getGroup(int groupPosition){
        return Workout.get(groupPosition).getName();
    }

    @Override
    public int getGroupCount() {
        return Workout.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //Log.d("TEST", "DOING STUFF FOR GROUP: " + groupPosition);

        if (!(Workout.get(groupPosition).isOpen())) {
            if( groupPosition != Workout.size()-1) {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_empty_wpadding, null);
                convertView.setTag("Blank");
                ((WorkspaceActivity) _context).ListFragment.workspaceListView.expandGroup(groupPosition);
            } else {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_empty_wopadding, null);
                convertView.setTag("Blank");
                ((WorkspaceActivity) _context).ListFragment.workspaceListView.expandGroup(groupPosition);
                if(!editable)
                convertView.setPadding(0,0,0,500);
            }
            //LinearLayout main = (LinearLayout) convertView.findViewById(R.id.empty);
            //main.setPadding(100, 100, 0, 100);

        }else {
            //if (groupPosition < (workout.size() - 1)) {
            //for the not last items in the list
            //if (convertView == null || convertView.getTag() != "Data") {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.w_circuit_group, null);
            convertView.setTag("Data");
            //convertView.findViewById(R.id.groupHandle).setBackgroundColor(mBackgroundColor);
            //LinearLayout main = (LinearLayout) convertView.findViewById(R.id.groupHandle);
            //main.setPadding(100, 100, 0, 100);

            // }
            //values for circuit header stuff
            TextView textView = (TextView) convertView.findViewById(R.id.circuitNameHeader);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(Workout.get(groupPosition).getName());
        }

        if (convertView != null) {
            ImageView img_selection = (ImageView) convertView.findViewById(R.id.Arrow);
            int imageResourceId = isExpanded ? R.drawable.ic_collapse_arrow_50
                    : R.drawable.ic_expand_arrow_50;

            if (img_selection != null)
                img_selection.setImageResource(imageResourceId);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void hideKeypad(){
        Log.d("4/4 Tests", "HIDEKEYPAD()");
        if (m_editTextHandle != null) {
            InputMethodManager imm = (InputMethodManager) _context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(m_editTextHandle.getWindowToken(), 0);
            m_editTextHandle.clearFocus();
        }
    }

    private View createGoalLayout(int group, int child){
        final LinearLayout goalLayout = new LinearLayout(_context);
        ArrayList<Metric> plan_metrics = Workout.get(group).getExercise(child).getPlanMetrics();
        goalLayout.setOrientation(LinearLayout.HORIZONTAL);
        for(int i = 0; i < plan_metrics.size(); i++) {
            switch(plan_metrics.get(i).getType()){
                case TIME:
                    TextView timeGoal = new TextView(_context);
                    timeGoal.setText("Time: " + plan_metrics.get(i).getMetricIntValue());
                    timeGoal.setLayoutParams(params);

                    goalLayout.addView(timeGoal);
                    break;
                case REPETITIONS:
                    TextView repGoal = new TextView(_context);
                    repGoal.setText("Reps: " + plan_metrics.get(i).getMetricIntValue());
                    repGoal.setLayoutParams(params);

                    goalLayout.addView(repGoal);
                    break;
                case OTHER:
                    TextView otherGoal = new TextView(_context);
                    break;
                case WEIGHT:
                    TextView weightGoal = new TextView(_context);
                    weightGoal.setText("Weight: " + plan_metrics.get(i).getMetricIntValue());
                    weightGoal.setLayoutParams(params);

                    goalLayout.addView(weightGoal);
                    break;
            }
        }
        return goalLayout;
    }

    private View createMetricEditTextLayout(final int group, final int child, final RelativeLayout frameLayout){
        final LinearLayout layout = new LinearLayout(_context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //layout.removeAllViewsInLayout();

        final ArrayList<Metric> metrics = Workout.get(group).getExercise(child).getMetrics();
        Log.d("4/9", "Adding metricis for: " + Workout.get(group).getExercise(child).getName() + " -- METRIC LIST SIZE = " + metrics.size() + " -- from not final: " + Workout.get(group).getExercise(child).getMetrics().size());
        for(int i = 0; i < metrics.size(); i++){
            Log.d("4/9", "Looking at metric of TYPE: " + metrics.get(i).getType());
            final int j = i;
            switch(metrics.get(i).getType()){
                case TIME:
                    Log.d("4/9", "Adding metricis for: " + Workout.get(group).getExercise(child).getName() + " -- TIME: " + metrics.get(i).getType());
                    TextView timeText = new TextView(_context);
                    timeText.setText("Time: ");
                    final EditText timeEdit = new EditText(_context);
                    timeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    timeEdit.setText("" + metrics.get(i).getMetricIntValue());
                    timeEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    timeEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                            if (actionId == EditorInfo.IME_ACTION_NEXT){
                                if(v.getText().toString().equals("")){
                                    metrics.get(j).setMetricIntValue(0);
                                    timeEdit.setText("0");
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                }
                                return true;
                            }
                            return false;
                        }
                    });

                    timeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                ((WorkspaceActivity)_context).ListFragment.workspaceListView.toggleListeners(false);
                                timeEdit.setSelection(timeEdit.getText().toString().length());
                                if(timeEdit.getText().toString().equals("0")){
                                    //timeEdit.setText("");
                                }
                                //Log.d("WORKSPACELISTFOCUS", "PLAN FOCUSED" + timeEdit.getText());
                            } else {
                                ((WorkspaceActivity)_context).ListFragment.workspaceListView.toggleListeners(editable);
                                //Log.d("WORKSPACELISTFOCUS", "PLAN LOST FOCUS" + timeEdit.getText());
                                    if (((EditText) v).getText().toString().equals("")) {
                                        metrics.get(j).setMetricIntValue(0);
                                    } else {
                                        metrics.get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                    }
                            }
                        }
                    });

                    LinearLayout timeRow = new LinearLayout(_context);
                    timeRow.setOrientation(LinearLayout.HORIZONTAL);
                    timeRow.addView(timeText);
                    timeRow.addView(timeEdit);
                    timeRow.setLayoutParams(params);

                    layout.addView(timeRow);
                    break;
                case REPETITIONS:
                    Log.d("4/9", "Adding metricis for: " + Workout.get(group).getExercise(child).getName() + " -- REPS: " + metrics.get(i).getType());
                    TextView repText = new TextView(_context);
                    repText.setText("Reps: ");
                    final EditText repEdit = new EditText(_context);
                    repEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    repEdit.setText("" + metrics.get(i).getMetricIntValue());
                    repEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                    repEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                if(v.getText().toString().equals("")){
                                    metrics.get(j).setMetricIntValue(0);
                                    repEdit.setText("0");
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                }
                                //hideKeypad();
                            }
                            return false;
                        }
                    });


                    //time

                    repEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                //Log.d("WORKSPACELISTFOCUS", "PLAN FOCUSED" + repEdit.getText());
                                ((WorkspaceActivity)_context).ListFragment.workspaceListView.toggleListeners(false);
                                repEdit.setSelection(repEdit.getText().toString().length());

                                m_editTextHandle = (EditText) v;

                                if(repEdit.getText().toString().equals("0")){
                                    Log.d("4/4", "DOOR 1");
                                    //repEdit.setText("");
                                }

                            } else {
                                ((WorkspaceActivity)_context).ListFragment.workspaceListView.toggleListeners(editable);
                                    //Log.d("WORKSPACELISTFOCUS", "PLAN LOST FOCUS" + repEdit.getText());
                                    if (((EditText) v).getText().toString().equals("")) {
                                        metrics.get(j).setMetricIntValue(0);
                                        Log.d("4/4", "DOOR 2");
                                    } else {
                                        metrics.get(j).setMetricIntValue(Integer
                                                .parseInt(((EditText) v).getText().toString()));
                                        Log.d("4/4", "DOOR 3");

                                }
                                Log.d("CHECKED TESTS", "PERFORMING CHECK TESTS");
                            }
                        }
                    });
                    LinearLayout repRow = new LinearLayout(_context);
                    repRow.setOrientation(LinearLayout.HORIZONTAL);
                    repRow.addView(repText);
                    repRow.addView(repEdit);
                    repRow.setLayoutParams(params);

                    layout.addView(repRow);
                    break;
                case OTHER:
                    TextView otherText = new TextView(_context);
                    otherText.setText(metrics.get(i).getMetricStringValue());

                    LinearLayout otherRow = new LinearLayout(_context);
                    otherRow.setOrientation(LinearLayout.HORIZONTAL);
                    otherRow.addView(otherText);

                    break;
                case WEIGHT:
                    Log.d("4/9", "Adding metricis for: " + Workout.get(group).getExercise(child).getName() + " -- WEIGHT: " + metrics.get(i).getType());
                    TextView wtText = new TextView(_context);
                    wtText.setText("Weight: ");

                    final EditText wtEdit = new EditText(_context);
                    wtEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    wtEdit.setText("" + metrics.get(i).getMetricIntValue());
                    wtEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                    wtEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                if(v.getText().toString().equals("")){
                                    metrics.get(j).setMetricIntValue(0);
                                    wtEdit.setText("0");
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                }
                                //hideKeypad();

                            }
                            return false;
                        }
                    });

                    wtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                ((WorkspaceActivity)_context).ListFragment.workspaceListView.toggleListeners(false);
                                m_editTextHandle = (EditText) v;
                                wtEdit.setSelection(wtEdit.getText().toString().length());
                                if(wtEdit.getText().toString().equals("0")){
                                    //wtEdit.setText("");
                                    //wtEdit.
                                }

                            } else {
                                ((WorkspaceActivity)_context).ListFragment.workspaceListView.toggleListeners(editable);
                                if(((EditText) v).getText().toString().equals("")) {
                                    metrics.get(j).setMetricIntValue(0);
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                            }
                        }
                    });

                    LinearLayout wtRow = new LinearLayout(_context);
                    wtRow.setOrientation(LinearLayout.HORIZONTAL);
                    wtRow.addView(wtText);
                    wtRow.addView(wtEdit);
                    wtRow.setLayoutParams(params);

                    layout.addView(wtRow);
                    break;
                default:
                    break;
            }
        }
        return layout;
    }

    @Override
    public void onDrop(int type, int destinationExerciseIndex ,int destinationCircuitIndex) {
        synchronized (Workout) {//Save to temp, remove from workout

            if (destinationCircuitIndex >= Workout.size() - 1) {             //if the destination circuit is the last or later
                destinationExerciseIndex = -1;

            }
                switch (type) {
                    case CIRCUIT:
                        WorkoutData.get(_context).placeTempCircuit(destinationCircuitIndex);

                        break;
                    case EXERCISE:  //passed location is a group child
                        switch (destinationExerciseIndex){
                            case -1:
                                WorkoutData.get(_context).addClosedCircuitWithTempExercise(destinationCircuitIndex);
                                break;
                            default:
                                WorkoutData.get(_context).placeTempExercise(destinationCircuitIndex, destinationExerciseIndex);
                                break;
                        }
                        break;
                }
            notifyDataSetChanged();
            ((WorkspaceActivity) _context).ListFragment.restoreListExpansion();
        }
    }
}