package com.main.toledo.gymtrackr;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WorkspaceExpandableListAdapterMKII extends BaseExpandableListAdapter implements DropListener{

    private Context _context;

    private ArrayList<Circuit> workout;

    private boolean editable = true;

    public WorkspaceExpandableListAdapterMKII(Context context, ArrayList<Circuit> workout){
        this._context = context;
        this.workout = workout;
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        return workout.get(groupPosition).getExercise(childPosition);
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
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //Log.d("TEST", "group position: " + groupPosition + " child position "
        //       + childPosition);
        //if workout is closed, prints last item as an exercise value, otherwise last item is buttons

        if (groupPosition >= workout.size()-1){
        //    Log.d("test", "should make buttons");
            if(editable){
                if (convertView == null || (convertView.getTag() != "End Button")) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_workout_menu_buttons, null);
                    //add circuit code

                    Button addCircuitButton = (Button) convertView.findViewById(R.id.AddCircuitButton);
                    addCircuitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WorkoutData.get(_context).addCircuit(groupPosition);
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
                            i.putExtra("EXTRA_CIRCUIT_NUMBER", groupPosition);
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
            if (childPosition < (workout.get(groupPosition).getSize() - 1) || !(workout.get(groupPosition).isOpen())) {
                //for the not last items in the list
                if (convertView == null || (convertView.getTag() != "Data")) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.w_exercise, null);


                    convertView.setTag("Data");
                }

                LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.exerciseLayout);
                ArrayList<Metric> metrics = workout.get(groupPosition).getExercise(childPosition).getMetrics();
                layout.removeAllViewsInLayout();
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
                                       workout.get(groupPosition).getExercise(childPosition)
                                                .getMetrics().get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                       notifyDataSetChanged();
                                       return true;
                                    }
                                    return false;
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

                            EditText repEdit = new EditText(_context);
                            repEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                            repEdit.setText("" + metrics.get(i).getMetricIntValue());
                            repEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            //repEdit.setFocusable(false);
                            repEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                                    if (actionId == EditorInfo.IME_ACTION_DONE){
                                        workout.get(groupPosition).getExercise(childPosition)
                                                .getMetrics().get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                        notifyDataSetChanged();
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            //time

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

                            EditText wtEdit = new EditText(_context);
                            wtEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                            wtEdit.setText("" + metrics.get(i).getMetricIntValue());
                            wtEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            //wtEdit.setFocusable(false);
                            wtEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                                    if (actionId == EditorInfo.IME_ACTION_DONE){
                                        workout.get(groupPosition).getExercise(childPosition)
                                                .getMetrics().get(j).setMetricIntValue(Integer.parseInt(v.getText().toString()));
                                        notifyDataSetChanged();
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            //time

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

                TextView textView = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setText(workout.get(groupPosition).getExercise(childPosition).getName());

                Button deleteButton = (Button) convertView.findViewById(R.id.removeExerciseButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkoutData.get(_context).removeExercise(childPosition, groupPosition);
                        //if circuit is closed remove it as it is no longer necessary
                        //Log.d("REMOVE BUG", "Remove clicked.  Child Position: " + childPosition + " Group Position: " + groupPosition);

                        if(!WorkoutData.get(_context).getWorkout().get(groupPosition).isOpen()){
                            WorkoutData.get(_context).getWorkout().remove(groupPosition);
                        }
                        notifyDataSetChanged();
                    }
                });

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
                                i.putExtra("EXTRA_CIRCUIT_NUMBER", groupPosition);
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
        return workout.get(groupPosition).getSize();
    }


    @Override
    public Object getGroup(int groupPosition){
        return this.workout.get(groupPosition).getName();
    }

    @Override
    public int getGroupCount() {
        return this.workout.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //Log.d("TEST", "DOING STUFF FOR GROUP: " + groupPosition);
        if (!(workout.get(groupPosition).isOpen())) {

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
                textView.setText(workout.get(groupPosition).getName());

                Button removeCircuitButton = (Button) convertView.findViewById(R.id.removeCircuitButton);

                removeCircuitButton.setFocusable(false);
                removeCircuitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        WorkoutData.get(_context).getWorkout().remove(groupPosition);
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

    /* BASE EXAMPLES
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
    */
    //this needs fixed a lot
    @Override
    public void onDrop(int homeExerciseIndex, int homeCircuitIndex,
                        int destinationExerciseIndex ,int destinationCircuitIndex) {
        synchronized (workout) {//Save to temp, remove from workout


            if (destinationCircuitIndex < workout.size() - 1) { //if the destination circuit is not the last or later

                if (destinationExerciseIndex > workout.get(destinationCircuitIndex).getExercises().size() - 2) //if the destination exercise is
                    destinationExerciseIndex = workout.get(destinationCircuitIndex).getExercises().size() - 2;
                if (destinationExerciseIndex < 0)
                    destinationExerciseIndex = 0;

                switch (homeExerciseIndex) {
                    case -1:  //passed location is a group header
                        //if ((homeCircuitIndex < destinationCircuitIndex)
                        //        || (homeCircuitIndex > destinationCircuitIndex + 1)) {

                        Circuit tempCircuit = workout.get(homeCircuitIndex);
                        workout.remove(homeCircuitIndex);

                        workout.add(destinationCircuitIndex, tempCircuit);

                        ///}
                        break;
                    default:  //passed location is a group child
                        if(workout.get(homeCircuitIndex)
                                .getExercise(homeExerciseIndex).getName() != "test") { //fixes bug where button placeholder item is moved occasionally

                            Exercise tempExercise = workout.get(homeCircuitIndex)
                                    .getExercise(homeExerciseIndex);
                            workout.get(homeCircuitIndex).removeExercise(homeExerciseIndex);

                            Log.d("TOUCH TEST", "DEST CIRC INDEX: " + destinationCircuitIndex
                                    + " DEST EX INDEX: " + destinationExerciseIndex);
                            //Add to location
                            workout.get(destinationCircuitIndex).addExerciseAtIndex(destinationExerciseIndex,
                                    tempExercise);
                        }
                        break;
                }
            }
        }
    }
}