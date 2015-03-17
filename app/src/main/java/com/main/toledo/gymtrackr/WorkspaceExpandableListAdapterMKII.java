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

    //private ArrayList<Circuit> WorkoutData.get(_context).getWorkout();

    private boolean editable = true;

    private EditText m_editTextHandle;

    private Runnable mShowImeRunnable;

    private boolean THETHING;

    final int PLAN = 1, WORKOUT = 2;

    public WorkspaceExpandableListAdapterMKII(Context context){//}, ArrayList<Circuit> workout){
        this._context = context;

        mShowImeRunnable = new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        _context.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(m_editTextHandle,0);
                }
            }
        };
        //this.WorkoutData.get(_context).getWorkout() = workout;
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
                Log.d("PAD BUGS", "CLEAN VIEW: SHOULD HIDE KEYPAD");
                hideKeypad();
            } else {
                // NONE of the imageView is within the visible window

            }
        }
    }
    public void setEditable(boolean b){
        Log.d("EDITABLE TEST", "setEditable() called in adapter.  editable: " + b);
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
                if (convertView == null || (convertView.getTag() != "End Button")) {
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
                            notifyDataSetChanged();
                            ((WorkspaceActivity) _context).ListFragment
                                    .expandLists(((WorkspaceActivity) _context).getAdapter());
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
                }
            } else {
                if (convertView == null || (convertView.getTag() != "Blank")) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_empty, null);
                    convertView.setTag("Blank");
                }
            }
        }else {
            if (childPosition < (WorkoutData.get(_context).getWorkout().get(group).getSize() - 1)
                    || !(WorkoutData.get(_context).getWorkout().get(group).isOpen())) {
                //for the not last items in the list
                if (convertView == null || (convertView.getTag() != "Data")) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    convertView.setTag("Data");
                }
                LinearLayout mainLayout = (LinearLayout) convertView.findViewById(R.id.exerciseMainLayout);
                //mainLayout.removeAllViewsInLayout();
                //WORKAROUND, MAKES LIST ITEMS CLICKABLE, EDIT TEXTS DISABLED NORMAL FUNCTIONALITY
                //CODE GOES HERE
                mainLayout.setOnClickListener(null); //Resets the layouts on click listener
                mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //NOTIFY TESTS FOR FLOW
                        Log.d("FLOW TESTS", "NOTIFY CALLED IN ADAPTER");
                        notifyDataSetChanged();
                        //END TESTS
                        Intent i = new Intent(_context, EditActivity.class);
                        ((WorkspaceActivity) _context).setToEdit(true);
                        i.putExtra("CIRCUIT_VALUE", group);
                        i.putExtra("EXERCISE_VALUE", child);
                        //Log.d("LAST THING", groupPosition + " " + childPosition);
                        _context.startActivity(i);
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
                //code for delete button
                Button deleteButton = (Button) convertView.findViewById(R.id.removeExerciseButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkoutData.get(_context).removeExercise(child, group);
                        //if circuit is closed remove it as it is no longer necessary
                        //Log.d("REMOVE BUG", "Remove clicked.  Child Position: " + childPosition + " Group Position: " + groupPosition);
                        if(!WorkoutData.get(_context).getWorkout().get(group).isOpen()){
                            WorkoutData.get(_context).getWorkout().remove(group);
                        }
                        notifyDataSetChanged();
                    }
                });
                //toggles button for edit mode
                if(!editable){
                    deleteButton.setVisibility(View.INVISIBLE);
                }else{
                    deleteButton.setVisibility(View.VISIBLE);
                }
            } else {
                //for the last items
                //switch will go here when we have an edit toggle to hide buttons
                if(editable) {
                    if (convertView == null || (convertView.getTag() != "Button")) {
                        LayoutInflater inflater = (LayoutInflater) this._context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.w_circuit_menu_buttons, null);
                        convertView.setTag("Button");

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
                    }
                }else{
                    if (convertView == null || (convertView.getTag() != "Blank")) {
                        LayoutInflater inflater = (LayoutInflater) this._context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.w_empty, null);
                        convertView.setTag("Blank");
                    }
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

        }else {
            //if (groupPosition < (workout.size() - 1)) {
                //for the not last items in the list
                if (convertView == null || convertView.getTag() != "Data") {
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_circuit_group, null);
                    convertView.setTag("Data");
                }
                //values for circuit header stuff
                TextView textView = (TextView) convertView.findViewById(R.id.circuitNameHeader);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setText(WorkoutData.get(_context).getWorkout().get(groupPosition).getName());

                Button removeCircuitButton = (Button) convertView.findViewById(R.id.removeCircuitButton);

                removeCircuitButton.setFocusable(false);
                removeCircuitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        WorkoutData.get(_context).getWorkout().remove(group);
                        notifyDataSetChanged();
                    }
                });

                if(!editable){
                    removeCircuitButton.setVisibility(View.INVISIBLE);
                } else {
                    removeCircuitButton.setVisibility(View.VISIBLE);
                }
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
/*
    private void setImeVisibility(final boolean visible) {
        if (visible) {
            m_editTextHandle.post(mShowImeRunnable);
        } else {
            m_editTextHandle.removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = (InputMethodManager) _context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(m_editTextHandle.getWindowToken(), 0);
            }
        }
    }

     BASE EXAMPLES
    public void onRemove(int which) {
        if (which < 0 || which > mContent.size()) return;
        mContent.remove(which);
    }

    public void onDrop(int from, int to) {
        String temp = mContent.get(from);
        mContent.remove(from);
        mContent.addToOpenCircuit(to,temp);
    }
    */

    /* MODIFIED EXAMPLES */
    /*we'll mess with this later, not sure how the remove functions from this example
    public void onRemove(int which) {
        if (which < 0 || which > mContent.size()) return;
        mContent.remove(which);
    }

    private void doWeCloseKeypad(boolean b){
        InputMethodManager imm = (InputMethodManager)_context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_editTextHandle.getWindowToken(), 0);
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = ((WorkspaceActivity) _context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
*/
    public void hideKeypad(){
        if (m_editTextHandle != null) {
            InputMethodManager imm = (InputMethodManager) _context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(m_editTextHandle.getWindowToken(), 0);
        }
        m_editTextHandle.clearFocus();
        Log.d("PAD BUGS", "HIDE KEYPAD - CLEAR FOCUS");
    }

    private void showKeypad(){

        InputMethodManager imm = (InputMethodManager)_context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(m_editTextHandle, InputMethodManager.SHOW_IMPLICIT);
    }
    //this needs fixed a lot


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
        //SHITTY EDIT TEXT CODE REPLACE WITH METHOD
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
                                }      //
                                // notifyDataSetChanged();
                                // notifyDataSetInvalidated();
                                return true;
                            }
                            return false;
                        }
                    });

                    timeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){

                                Log.d("WORKSPACELISTFOCUS", "EDIT FOCUSED" + timeEdit.getText());
                                        /*
                                        m_editTextHandle = (EditText) v;
                                        mShowImeRunnable = new Runnable() {
                                            public void run() {
                                                InputMethodManager imm = (InputMethodManager)
                                                        _context.getSystemService(Context.INPUT_METHOD_SERVICE);

                                                if (imm != null) {
                                                    imm.showSoftInput(m_editTextHandle,0);
                                                }
                                            }
                                        };
                                        */
                                //setImeVisibility(true);
                                //notifyDataSetChanged();
                                //m_editTextHandle.requestFocus();
                                //showKeypad();
                            } else {
                                Log.d("WORKSPACELISTFOCUS", "EDIT LOST FOCUS" + timeEdit.getText());
                                if(((EditText) v).getText().toString().equals("")) {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(0);
                                } else {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                                //notifyDataSetChanged();
                                //setImeVisibility(false);
                                //hideKeypad();
                                //notifyDataSetInvalidated();
                            }
                        }
                    });
                    //timeEdit.setFocusable(false);

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
                    //repEdit.setFocusable(false);
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
                                Log.d("WORKSPACELISTFOCUS", "EDIT FOCUSED" + repEdit.getText());

                                m_editTextHandle = (EditText) v;

                                //THETHING = true;
                                        /*
                                        mShowImeRunnable = new Runnable() {
                                            public void run() {
                                                InputMethodManager imm = (InputMethodManager)
                                                        _context.getSystemService(Context.INPUT_METHOD_SERVICE);

                                                if (imm != null) {
                                                    imm.showSoftInput(m_editTextHandle,0);
                                                }
                                            }
                                        };
                                        */
                                //setImeVisibility(true);
                                //notifyDataSetChanged();
                                //m_editTextHandle.requestFocus();
                                //showKeypad();
                            } else {
                                //if( i == 2) {
                                    Log.d("WORKSPACELISTFOCUS", "EDIT LOST FOCUS" + repEdit.getText());
                                    if (((EditText) v).getText().toString().equals("")) {
                                        WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                                .getMetrics().get(j).setMetricIntValue(0);
                                    } else {
                                        WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                                .getMetrics().get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                    }
                                   // v.setTag(0);
                                   // hideKeypad();
                                   // notifyDataSetChanged();
                                //}
                                //notifyDataSetInvalidated();
                                //hideKeypad();
                                //setImeVisibility(false);
                                //notifyDataSetChanged();

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
                    //EditText otherEdit = new EditText(getActivity());

                    LinearLayout otherRow = new LinearLayout(_context);
                    otherRow.setOrientation(LinearLayout.HORIZONTAL);
                    otherRow.addView(otherText);
                    //otherRow.addView(otherEdit);
                    break;
                case WEIGHT:
                    TextView wtText = new TextView(_context);
                    wtText.setText("Weight: ");

                    final EditText wtEdit = new EditText(_context);
                    wtEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    wtEdit.setText("" + metrics.get(i).getMetricIntValue());
                    wtEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    //wtEdit.setFocusable(false);
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
                                //notifyDataSetInvalidated();
                                hideKeypad();
                                //setImeVisibility(false);
                                //notifyDataSetChanged();
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
                                Log.d("WORKSPACELISTFOCUS", "EDIT FOCUSED: " + wtEdit.getText());

                                m_editTextHandle = (EditText) v;

                                        /*
                                        mShowImeRunnable = new Runnable() {
                                            public void run() {
                                                InputMethodManager imm = (InputMethodManager)
                                                        _context.getSystemService(Context.INPUT_METHOD_SERVICE);

                                                if (imm != null) {
                                                    imm.showSoftInput(m_editTextHandle,0);
                                                }
                                            }
                                        };
                                        */
                                //setImeVisibility(true);
                                //notifyDataSetChanged();
                                //m_editTextHandle.requestFocus();
                                //showKeypad();
                            } else {
                                Log.d("WORKSPACELISTFOCUS", "EDIT LOST FOCUS" + wtEdit.getText());
                                if(((EditText) v).getText().toString().equals("")) {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(0);
                                } else {
                                    WorkoutData.get(_context).getWorkout().get(group).getExercise(child)
                                            .getMetrics().get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                                // WorkoutData.get(_context).getWorkout().get(group).getExercise(child).setSaveToHistory(true);
                                //notifyDataSetInvalidated();
                                //hideKeypad();

                                //setImeVisibility(false);
                                //notifyDataSetChanged();
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
    public void onDrop(int homeExerciseIndex, int homeCircuitIndex,
                        int destinationExerciseIndex ,int destinationCircuitIndex) {
        synchronized (WorkoutData.get(_context).getWorkout()) {//Save to temp, remove from workout


            if (destinationCircuitIndex < WorkoutData.get(_context).getWorkout().size() - 1) { //if the destination circuit is not the last or later

                if (destinationExerciseIndex > WorkoutData.get(_context).getWorkout().get(destinationCircuitIndex).getExercises().size() - 2) //if the destination exercise is
                    destinationExerciseIndex = WorkoutData.get(_context).getWorkout().get(destinationCircuitIndex).getExercises().size() - 2;
                if (destinationExerciseIndex < 0)
                    destinationExerciseIndex = 0;

                switch (homeExerciseIndex) {
                    case -1:  //passed location is a group header
                        //if ((homeCircuitIndex < destinationCircuitIndex)
                        //        || (homeCircuitIndex > destinationCircuitIndex + 1)) {

                        Circuit tempCircuit = WorkoutData.get(_context).getWorkout().get(homeCircuitIndex);
                        WorkoutData.get(_context).getWorkout().remove(homeCircuitIndex);

                        WorkoutData.get(_context).getWorkout().add(destinationCircuitIndex, tempCircuit);

                        ///}
                        break;
                    default:  //passed location is a group child
                        if(WorkoutData.get(_context).getWorkout().get(homeCircuitIndex)
                                .getExercise(homeExerciseIndex).getName() != "test") { //fixes bug where button placeholder item is moved occasionally

                            Exercise tempExercise = WorkoutData.get(_context).getWorkout().get(homeCircuitIndex)
                                    .getExercise(homeExerciseIndex);
                            WorkoutData.get(_context).getWorkout().get(homeCircuitIndex).removeExercise(homeExerciseIndex);

                            Log.d("TOUCH TEST", "DEST CIRC INDEX: " + destinationCircuitIndex
                                    + " DEST EX INDEX: " + destinationExerciseIndex);
                            //Add to location
                         WorkoutData.get(_context).getWorkout().get(destinationCircuitIndex).addExerciseAtIndex(destinationExerciseIndex,
                                    tempExercise);
                        }
                        break;
                }
            }
        }
    }
}