package com.main.toledo.gymtrackr;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class WorkspaceExpandableListAdapterMKII extends BaseExpandableListAdapter implements DropListener{

    private Context _context;

    private boolean editable = true;

    private EditText m_editTextHandle;

    final int CIRCUIT = 1;
    final int EXERCISE = 2;

    public WorkspaceExpandableListAdapterMKII(Context context){
        this._context = context;
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        return WorkoutData.get(_context).getWorkout().get(groupPosition).getExercise(childPosition);
    }

    public void cleanView(Rect viewHitRect){

        Log.d("PAD BUGS", "CLEAN VIEW: CALLED");
        if (m_editTextHandle != null) {
            //Log.d("PAD BUGS", "CLEAN VIEW: EDIT TEXT IS NOT NULL");
            if (!m_editTextHandle.getLocalVisibleRect(viewHitRect)
                    || viewHitRect.height() < m_editTextHandle.getHeight()) {
                //Log.d("PAD BUGS", "CLEAN VIEW: SHOULD HIDE KEYPAD");
                hideKeypad();
            } else {
                // NONE of the imageView is within the visible window

            }
        }
    }
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

        //Log.d("TEST", "group position: " + groupPosition + " child position "
        //       + childPosition);
        //if workout is closed, prints last item as an exercise value, otherwise last item is buttons
        final int group = groupPosition;
        final int child = childPosition;
        //Log.d("FLOW TEST", "TEST groupPosition >= WorkoutData.get(_context).getWorkout().size()-1");
        //Log.d("FLOW TEST", "groupPosition: " + group + "-- WorkoutData.get(_context).getWorkout().size(): " + WorkoutData.get(_context).getWorkout().size());
        if (group >= WorkoutData.get(_context).getWorkout().size()-1){
        //    Log.d("test", "should make buttons");
            if(editable){
                //Log.d("FLOW TEST", "DRAW BUTTONS SHOULD BE CALLED NOW");
                //if (convertView == null || (convertView.getTag() != "End Button")) {
                    //Log.d("FLOW TEST", "DRAW BUTTONS CALLED");
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
                            //WorkoutData.get(_context).getWorkout()
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
                            _context.startActivity(i);
                        }
                    });
                    convertView.setTag("End Button");
                //}
            } else {
                //if (convertView == null || (convertView.getTag() != "Blank")) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_empty, null);
                    convertView.setTag("Blank");
                //}
            }
        }else {
            if (childPosition < (WorkoutData.get(_context).getWorkout().get(group).getSize() - 1)
                    || !(WorkoutData.get(_context).getWorkout().get(group).isOpen())) {
                //for the not last items in the list
                //if (convertView == null || (convertView.getTag() != "Data")) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    convertView.setTag("Data");
                    //code to set color
                    convertView.setBackgroundColor(R.color.material_blue_grey_800);
                //}

                LinearLayout mainLayout = (LinearLayout) convertView.findViewById(R.id.exerciseMainLayout);
                mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("PLUS FUNCTIONALITY", "BUTTON SHOULD BE WHITE");
                        v.setBackgroundColor(Color.WHITE);
                    }
                });

                LinearLayout dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                dynamicViewLayout.removeAllViewsInLayout();
                if (((WorkspaceActivity) _context).workoutFromPlan()){
                    dynamicViewLayout.addView(createGoalLayout(group, child));
                }

                dynamicViewLayout.addView(createMetricEditTextLayout(group, child));


                //sets text for name
                TextView textView = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setText(WorkoutData.get(_context).getWorkout().get(group).getExercise(childPosition).getName());
            } else {
                //for the last items
                //switch will go here when we have an edit toggle to hide buttons
                if(editable) {
                    //if (convertView == null || (convertView.getTag() != "Button")) {
                        LayoutInflater inflater = (LayoutInflater) this._context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.w_circuit_menu_buttons, null);
                        convertView.setTag("Button");
                        convertView.findViewById(R.id.inCircuitButtonHandle)
                                .setBackgroundColor(R.color.material_blue_grey_800);
                    //}
                        Button browseButton = (Button) convertView.findViewById(R.id.BrowseButton);
                        browseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(_context, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);
                                i.putExtra("EXTRA_CIRCUIT_OPEN", true);
                                i.putExtra("EXTRA_CIRCUIT_NUMBER", group);
                                ((WorkspaceActivity) _context).setToBrowse(true);
                                _context.startActivity(i);
                            }
                        });

                }else{
                    //if (convertView == null || (convertView.getTag() != "Blank")) {
                        LayoutInflater inflater = (LayoutInflater) this._context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.w_empty, null);
                        convertView.setTag("Blank");
                    //}
                }
                //ConvertViewIsButton = true;
            }
        }
        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition){
        return WorkoutData.get(_context).getWorkout().get(groupPosition).getSize();
    }


    @Override
    public Object getGroup(int groupPosition){
        return WorkoutData.get(_context).getWorkout().get(groupPosition).getName();
    }

    @Override
    public int getGroupCount() {
        return WorkoutData.get(_context).getWorkout().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //Log.d("TEST", "DOING STUFF FOR GROUP: " + groupPosition);
        final int group = groupPosition;
        if (!(WorkoutData.get(_context).getWorkout().get(groupPosition).isOpen())) {

            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.w_empty, null);
            convertView.setTag("Blank");
            ((WorkspaceActivity) _context).ListFragment.workspaceListView.expandGroup(groupPosition);
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
                    convertView.findViewById(R.id.groupHandle).setBackgroundColor(R.color.material_blue_grey_800);
                    //LinearLayout main = (LinearLayout) convertView.findViewById(R.id.groupHandle);
                    //main.setPadding(100, 100, 0, 100);

               // }
                //values for circuit header stuff
                TextView textView = (TextView) convertView.findViewById(R.id.circuitNameHeader);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setText(WorkoutData.get(_context).getWorkout().get(groupPosition).getName());
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
        if (m_editTextHandle != null) {
            InputMethodManager imm = (InputMethodManager) _context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(m_editTextHandle.getWindowToken(), 0);
            m_editTextHandle.clearFocus();
        }
    }

    private View createGoalLayout(int group, int child){
        final LinearLayout goalLayout = new LinearLayout(_context);
        ArrayList<Metric> plan_metrics = WorkoutData.get(_context).getWorkout().get(group).getExercise(child).getPlanMetrics();
        goalLayout.setOrientation(LinearLayout.HORIZONTAL);
        for(int i = 0; i < plan_metrics.size(); i++) {
            switch(plan_metrics.get(i).getType()){
                case TIME:
                    TextView timeGoal = new TextView(_context);
                    timeGoal.setText("Time: " + plan_metrics.get(i).getMetricIntValue());
                    goalLayout.addView(timeGoal);
                    break;
                case REPETITIONS:
                    TextView repGoal = new TextView(_context);
                    repGoal.setText("Reps: " + plan_metrics.get(i).getMetricIntValue());
                    goalLayout.addView(repGoal);
                    break;
                case OTHER:
                    TextView otherGoal = new TextView(_context);
                    break;
                case WEIGHT:
                    TextView weightGoal = new TextView(_context);
                    weightGoal.setText("Weight: " + plan_metrics.get(i).getMetricIntValue());
                    goalLayout.addView(weightGoal);
                    break;
            }
        }
        return goalLayout;
    }

    private View createMetricEditTextLayout(final int group, final int child){
        final LinearLayout layout = new LinearLayout(_context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //layout.removeAllViewsInLayout();
        ArrayList<Metric> metrics = WorkoutData.get(_context).getWorkout().get(group).getExercise(child).getMetrics();
        for(int i = 0; i < metrics.size(); i++){
            final int j = i;
            switch(metrics.get(i).getType()){
                case TIME:
                    TextView timeText = new TextView(_context);
                    timeText.setText("Time: ");
                    final EditText timeEdit = new EditText(_context);
                    timeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    timeEdit.setText("" + metrics.get(i).getMetricIntValue());
                    timeEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    timeEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                            if (actionId == EditorInfo.IME_ACTION_DONE){
                                if(v.getText().toString().equals("")){
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(0);
                                } else {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
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
                                //Log.d("WORKSPACELISTFOCUS", "EDIT FOCUSED" + timeEdit.getText());
                            } else {
                                //Log.d("WORKSPACELISTFOCUS", "EDIT LOST FOCUS" + timeEdit.getText());
                                if(((EditText) v).getText().toString().equals("")) {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(0);
                                } else {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                            }
                        }
                    });

                    LinearLayout timeRow = new LinearLayout(_context);
                    timeRow.setOrientation(LinearLayout.HORIZONTAL);
                    timeRow.addView(timeText);
                    timeRow.addView(timeEdit);

                    layout.addView(timeRow);
                    break;
                case REPETITIONS:
                    TextView repText = new TextView(_context);
                    repText.setText("Reps: ");
                    final EditText repEdit = new EditText(_context);
                    repEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    repEdit.setText("" + metrics.get(i).getMetricIntValue());
                    repEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    repEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if(v.getText().toString().equals("")){
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(0);
                                } else {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                }
                                hideKeypad();
                                //notifyDataSetInvalidated();
                                notifyDataSetChanged();
                                return true;
                            }
                            return false;
                        }
                    });


                    //time

                    repEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                //Log.d("WORKSPACELISTFOCUS", "EDIT FOCUSED" + repEdit.getText());

                                m_editTextHandle = (EditText) v;

                            } else {
                                    //Log.d("WORKSPACELISTFOCUS", "EDIT LOST FOCUS" + repEdit.getText());
                                    if (((EditText) v).getText().toString().equals("")) {
                                        WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                                .getMetrics().get(j).setMetricIntValue(0);
                                    } else {
                                        WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                                .getMetrics().get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                    }
                            }
                        }
                    });
                    LinearLayout repRow = new LinearLayout(_context);
                    repRow.setOrientation(LinearLayout.HORIZONTAL);
                    repRow.addView(repText);
                    repRow.addView(repEdit);

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
                    TextView wtText = new TextView(_context);
                    wtText.setText("Weight: ");

                    final EditText wtEdit = new EditText(_context);
                    wtEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    wtEdit.setText("" + metrics.get(i).getMetricIntValue());
                    wtEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    wtEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if(v.getText().toString().equals("")){
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(0);
                                } else {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                }
                                hideKeypad();
                                return true;
                            }
                            return false;
                        }
                    });
                    //time

                    wtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                //Log.d("WORKSPACELISTFOCUS", "EDIT FOCUSED: " + wtEdit.getText());

                                m_editTextHandle = (EditText) v;

                            } else {
                                //Log.d("WORKSPACELISTFOCUS", "EDIT LOST FOCUS" + wtEdit.getText());
                                if(((EditText) v).getText().toString().equals("")) {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(0);
                                } else {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                            }
                        }
                    });
                    LinearLayout wtRow = new LinearLayout(_context);
                    wtRow.setOrientation(LinearLayout.HORIZONTAL);
                    wtRow.addView(wtText);
                    wtRow.addView(wtEdit);

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
        synchronized (WorkoutData.get(_context).getWorkout()) {//Save to temp, remove from workout

            if (destinationCircuitIndex >= WorkoutData.get(_context).getWorkout().size() - 1) {             //if the destination circuit is the last or later
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