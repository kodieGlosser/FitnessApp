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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WorkspaceExpandableListAdapterMKIII extends BaseExpandableListAdapter implements DropListener{

    private Context _context;

    private boolean editable = true;

    private EditText m_editTextHandle;

    final int CIRCUIT = 1;
    final int EXERCISE = 2;
    final float mCheckedIndentation = 100;
    private ArrayList<Circuit> Workout = new ArrayList<>();

    private LinearLayout.LayoutParams params;


    //BROWSE STATES
    final int NOT_BROWSE = 0, BROWSE_WORKOUT = 1, WORKOUT_BROWSE = 2;
    //HEADER TYPES
    final static int REGULAR_HEADER = 0, BLANK_HEADER = 1, PADDED_BLANK_HEADER =2;
    final static int NUM_HEADERS = 3;
    //CHILD TYPES
    final static int EXERCISE_ITEM_1 = 0, EXERCISE_ITEM_2 = 1, EXERCISE_ITEM_3 = 2,
            EXERCISE_ITEM_1_M = 3, EXERCISE_ITEM_2_M = 4, EXERCISE_ITEM_3_M = 5, EMPTY_BUTTONS = 6,
            CIRCUIT_BUTTONS = 7, WORKOUT_BUTTONS = 8, TERRIBLE_THINGS = 9, EXERCISE_ITEM_1C = 10, EXERCISE_ITEM_2C = 11, EXERCISE_ITEM_3C = 12,
            EXERCISE_ITEM_1_MC = 13, EXERCISE_ITEM_2_MC = 14, EXERCISE_ITEM_3_MC = 15;
    final static int NUM_CHILDREN = 16;

    //private ChildViewHolder holderHandle;

    public WorkspaceExpandableListAdapterMKIII(Context context){
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        this._context = context;
        Workout = WorkoutData.get(_context).getWorkout();
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        return Workout.get(groupPosition).getExercise(childPosition);
    }

    @Override
    public int getChildTypeCount(){
        return NUM_CHILDREN;
    }
    @Override
    public int getGroupTypeCount(){
        return NUM_HEADERS;
    }
    @Override
    public int getChildType(int group, int child){
        int type;
        Exercise e = Workout.get(group).getExercise(child);
        if(e.getName().equals("test")){
            if(Workout.size()-1 == group){
                //group is last, and test means end buttons
                if(editable)
                    type = WORKOUT_BUTTONS;
                else
                    type = EMPTY_BUTTONS;
            } else {
                //if not last group and test name, type is rego buttons
                if(editable)
                    type = CIRCUIT_BUTTONS;
                else
                    type = EMPTY_BUTTONS;
            }
        } else {
            //we have reggo exercise
            ArrayList<Metric> metrics = e.getMetrics();
            int numMetrics = metrics.size();
            switch(numMetrics){
                case 1:
                    if (e.hasPlanMetrics()){
                        if(e.isSaveToHistorySet())
                            type = EXERCISE_ITEM_1_MC;
                        else
                            type = EXERCISE_ITEM_1_M;
                    }else{
                        if(e.isSaveToHistorySet())
                            type = EXERCISE_ITEM_1C;
                        else
                            type = EXERCISE_ITEM_1;
                    }
                    break;
                case 2:
                    if (e.hasPlanMetrics()){
                        if(e.isSaveToHistorySet())
                            type = EXERCISE_ITEM_2_MC;
                        else
                            type = EXERCISE_ITEM_2_M;
                    }else{
                        if(e.isSaveToHistorySet())
                            type = EXERCISE_ITEM_2C;
                        else
                            type = EXERCISE_ITEM_2;
                    }
                    break;
                case 3:
                    if (e.hasPlanMetrics()){
                        if(e.isSaveToHistorySet())
                            type = EXERCISE_ITEM_3_MC;
                        else
                            type = EXERCISE_ITEM_3_M;
                    }else{
                        if(e.isSaveToHistorySet())
                            type = EXERCISE_ITEM_3C;
                        else
                            type = EXERCISE_ITEM_3;
                    }
                    break;
                default:
                    type = TERRIBLE_THINGS;
                    break;
            }
        }

        return type;
    }

    public int getGroupType(int group){
        int type;
        Circuit c = Workout.get(group);
        if(c.isOpen()){
            type = REGULAR_HEADER;
        } else {
            if(group != Workout.size()-1) {
                type = PADDED_BLANK_HEADER;
            }else{
                type = BLANK_HEADER;
            }
        }
        return type;
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
        Log.d("4/20", "Group" + groupPosition + "Child" + childPosition);
        LayoutInflater inflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout dynamicViewLayout;
        final int group = groupPosition;
        final int child = childPosition;
        Exercise e = Workout.get(group).getExercise(child);
        boolean emptyFlag = false;
        ChildViewHolder holder = null;
        int type = getChildType(groupPosition, childPosition);


        if(convertView == null) {
            holder = new ChildViewHolder();
            switch (type) {
                case CIRCUIT_BUTTONS:
                    convertView = inflater.inflate(R.layout.w_circuit_menu_buttons, null);

                    holder.plusButton = (Button) convertView.findViewById(R.id.PlusButton);
                    holder.browseButton = (Button) convertView.findViewById(R.id.BrowseButton);
                    break;
                case WORKOUT_BUTTONS:
                    convertView = inflater.inflate(R.layout.w_workout_menu_buttons, null);
                    //add circuit code
                    holder.browseButton = (Button) convertView.findViewById(R.id.BrowseButton);
                    holder.addCircuitButton = (Button) convertView.findViewById(R.id.AddCircuitButton);
                    holder.plusButton = (Button) convertView.findViewById(R.id.PlusButton);
                    break;
                case EMPTY_BUTTONS:
                    convertView = inflater.inflate(R.layout.w_empty_wopadding, null);
                    break;
                case EXERCISE_ITEM_1:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_1_M:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);
                    break;
                case EXERCISE_ITEM_2:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_2_M:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_3:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_3_M:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_1C:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_1_MC:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_2C:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_2_MC:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_3C:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_3_MC:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.relativeLayoutHandle = (RelativeLayout) convertView.findViewById(R.id.exercise_relative_layout_handle);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case TERRIBLE_THINGS:
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder)convertView.getTag();
        }

        switch (type) {
            case CIRCUIT_BUTTONS:
                emptyFlag = true;
                holder.browseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(_context, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);
                            /*
                            i.putExtra("EXTRA_CIRCUIT_OPEN", true);
                            i.putExtra("EXTRA_CIRCUIT_NUMBER", group);
                            */
                        WorkoutData.get(_context).setStateCircuitOpenStatus(true);
                        WorkoutData.get(_context).setStateCircuit(group);
                        WorkoutData.get(_context).setBrowseState(WORKOUT_BROWSE);

                        ((WorkspaceActivity) _context).ListFragment.workspaceListView.clearHandle();
                        _context.startActivity(i);
                    }
                });
                //}

                holder.plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WorkoutData.get(_context).isAnExerciseToggled()){
                            int CircuitSize = Workout.get(group).getSize();
                            Exercise e = WorkoutData.get(_context).getToggledExerciseCopy();
                            e.setAnimate(true);
                            Workout.get(group).addExerciseAtIndex(CircuitSize - 1, e);
                            notifyDataSetChanged();
                                /*
                                ((WorkspaceActivity) _context).ListFragment
                                        .workspaceListView.setSelectedChild(group, child, false);
                                        */
                            ((WorkspaceActivity) _context).ListFragment                                                     ///////////////////////////THIS NEEDS TO SCALE FOR RESOLUTIONS
                                    .workspaceListView.smoothScrollBy(300, 800);
                        }
                    }
                });
                break;
            case WORKOUT_BUTTONS:
                emptyFlag = true;
                holder.addCircuitButton.setOnClickListener(new View.OnClickListener() {
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


                holder.browseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(_context, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);
                        /*
                        i.putExtra("EXTRA_CIRCUIT_NUMBER", group);
                        i.putExtra("EXTRA_CIRCUIT_OPEN", false);
                        */
                        WorkoutData.get(_context).setStateCircuitOpenStatus(false);
                        WorkoutData.get(_context).setStateCircuit(group);
                        WorkoutData.get(_context).setBrowseState(WORKOUT_BROWSE);

                        ((WorkspaceActivity) _context).ListFragment.workspaceListView.clearHandle();
                        _context.startActivity(i);
                    }
                });


                holder.plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WorkoutData.get(_context).isAnExerciseToggled()){
                            int WorkoutSize = Workout.size();
                            Exercise e = WorkoutData.get(_context).getToggledExerciseCopy();
                            e.setAnimate(true);
                            WorkoutData.get(_context).addClosedCircuit(e, WorkoutSize -1);
                            notifyDataSetChanged();
                            /*
                            ((WorkspaceActivity) _context).ListFragment
                                    .workspaceListView.setSelectedChild(group, child, false);
                                    */
                            ((WorkspaceActivity) _context).ListFragment                               ///////////////////////////THIS NEEDS TO SCALE FOR RESOLUTIONS
                                    .workspaceListView.smoothScrollBy(300, 800);
                        }
                    }
                });
                break;
            case EMPTY_BUTTONS:
                emptyFlag = true;
                break;
            case EXERCISE_ITEM_1:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_1_M:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2_M:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3_M:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_1C:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_1_MC:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2C:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2_MC:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3C:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3_MC:
                holder.exerciseNameText.setText(Workout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case TERRIBLE_THINGS:
                break;
        }


        if(Workout.get(group).getExercise(child).isToggled()){
            convertView.findViewById(R.id.exercise_relative_layout_handle).setBackgroundResource(R.drawable.circuit_selected);
            //convertView.findViewById(R.id.exercise_relative_layout_handle).setPressed(true);
        } else if(!emptyFlag){
            if(convertView.findViewById(R.id.exercise_relative_layout_handle) != null)
                convertView.findViewById(R.id.exercise_relative_layout_handle).setBackgroundResource(R.drawable.circuit_notselected);
            //convertView.findViewById(R.id.exerciseMainLayout).setBackgroundColor(Color.BLUE);

        } else {

        }

        if(Workout.get(group).getExercise(child).doAnimation()){
            Animation animation = AnimationUtils.loadAnimation(_context, R.anim.slide_right);
            animation.setDuration(500);
            convertView.startAnimation(animation);
            Workout.get(group).getExercise(child).setAnimate(false);
        }
        if (convertView.getTranslationX() != 0)
            convertView.setTranslationX(0);

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
        LayoutInflater inflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GroupViewHolder holder = null;
        int type = getGroupType(groupPosition);
        if(convertView == null) {
            holder = new GroupViewHolder();
            switch (type) {
                case REGULAR_HEADER:
                    convertView = inflater.inflate(R.layout.w_circuit_group, null);
                    holder.circuitNameText = (TextView) convertView.findViewById(R.id.circuitNameHeader);
                    holder.arrow = (ImageView) convertView.findViewById(R.id.Arrow);
                    break;
                case BLANK_HEADER:
                    convertView = inflater.inflate(R.layout.w_empty_wopadding, null);
                    ((WorkspaceActivity) _context).ListFragment.workspaceListView.expandGroup(groupPosition);
                    break;
                case PADDED_BLANK_HEADER:
                    convertView = inflater.inflate(R.layout.w_empty_wpadding, null);
                    ((WorkspaceActivity) _context).ListFragment.workspaceListView.expandGroup(groupPosition);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder)convertView.getTag();
        }
        switch (type) {
            case REGULAR_HEADER:
                holder.circuitNameText.setText(Workout.get(groupPosition).getName());
                int imageResourceId = isExpanded ? R.drawable.ic_collapse_arrow_50
                        : R.drawable.ic_expand_arrow_50;
                holder.arrow.setImageResource(imageResourceId);
                break;
            case BLANK_HEADER:
                if(!editable)
                    convertView.setPadding(0,0,0,500);
                break;
            case PADDED_BLANK_HEADER:
                break;
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

    private View createGoalLayout(int group, int child, ChildViewHolder holder) {
        final LinearLayout goalLayout = new LinearLayout(_context);
        ArrayList<Metric> plan_metrics = Workout.get(group).getExercise(child).getPlanMetrics();
        goalLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < plan_metrics.size(); i++) {
            TextView metricGoalTextView = new TextView(_context);
            metricGoalTextView.setLayoutParams(params);
            goalLayout.addView(metricGoalTextView);
            holder.metricGoalText.add(metricGoalTextView);
        }
        return goalLayout;
    }

    private View createMetricLayout(int group, int child, ChildViewHolder holder){
        final LinearLayout metricLayout = new LinearLayout(_context);
        ArrayList<Metric> metrics = Workout.get(group).getExercise(child).getMetrics();
        metricLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < metrics.size(); i++){
            TextView metricText = new TextView(_context);

            EditText metricEdit = new EditText(_context);
            metricEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
            metricEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            LinearLayout metricRow = new LinearLayout(_context);
            metricRow.setOrientation(LinearLayout.HORIZONTAL);
            metricRow.addView(metricText);
            metricRow.addView(metricEdit);
            metricRow.setLayoutParams(params);

            metricLayout.addView(metricRow);
            holder.metricEditHolder.add(metricEdit);
            holder.metricTextHolder.add(metricText);
        }
        return metricLayout;
    }

    private void setValues(int group, int child, ChildViewHolder holder){
        Exercise e = Workout.get(group).getExercise(child);
        final int numMetrics = e.getMetrics().size();
        boolean hasGoalMetrics = e.hasPlanMetrics();



        if(hasGoalMetrics){
            ArrayList<Metric> goalMetrics = e.getPlanMetrics();
            for(int i = 0; i < numMetrics; i++){
                holder.metricGoalText.get(i).setText(
                        goalMetrics.get(i).getType() + ": " + goalMetrics.get(i).getMetricIntValue());
            }
        }

        ArrayList<Metric> metrics = e.getMetrics();
        for(int i = 0; i < numMetrics; i++) {
            final int j = i;
            final Metric metric = metrics.get(i);
            holder.metricTextHolder.get(i).setText(metric.getType() + ": ");
            final EditText metricEditText = holder.metricEditHolder.get(i);

            metricEditText.setText("" + metric.getMetricIntValue());

            int k;

            if(j<numMetrics-1){
                k = i+1;
            } else {
                k = i;
            }

            final EditText nextEdit = holder.metricEditHolder.get(k);


            metricEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (v.getText().toString().equals("")) {
                            metric.setMetricIntValue(0);
                            metricEditText.setText("0");
                        } else {
                            metric.setMetricIntValue(Integer.parseInt(v.getText().toString()));
                        }
                        if(j<numMetrics-1){
                            nextEdit.requestFocus();
                        } else {
                            //TODO: CODE TO DIRECT FOCUS TO NEXT WORKSPACE ROW GOES HERE
                            //v.focusSearch(View.FOCUS_DOWN);
                        }
                        return true;
                    }
                    return false;
                }
            });

            metricEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((WorkspaceActivity) _context).ListFragment.workspaceListView.toggleListeners(false);
                        metricEditText.setSelection(metricEditText.getText().toString().length());
                        m_editTextHandle = (EditText) v;
                        if (metricEditText.getText().toString().equals("0")) {
                            //timeEdit.setText("");
                        }
                        //Log.d("WORKSPACELISTFOCUS", "PLAN FOCUSED" + timeEdit.getText());
                    } else {
                        ((WorkspaceActivity) _context).ListFragment.workspaceListView.toggleListeners(editable);
                        //Log.d("WORKSPACELISTFOCUS", "PLAN LOST FOCUS" + timeEdit.getText());
                        if (((EditText) v).getText().toString().equals("")) {
                            metric.setMetricIntValue(0);
                        } else {
                            metric.setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                        }
                    }
                }
            });

        }
    }
    /*
    private void focusNextEdit(int j){
        holderHandle.metricEditHolder.get(j+1).requestFocus();
    }
    */
    private void doCheck(ChildViewHolder holder){
        LayoutInflater inflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        holder.exerciseCheckImage = (ImageView) inflater.inflate(R.layout.w_check, null);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        holder.exerciseCheckImage.setLayoutParams(params);

        holder.relativeLayoutHandle.addView(holder.exerciseCheckImage);
        holder.dataLayoutHandle.setTranslationX(mCheckedIndentation);
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

    public static class ChildViewHolder {
        public Button plusButton;
        public Button browseButton;
        public Button addCircuitButton;

        public TextView exerciseNameText;

        public RelativeLayout relativeLayoutHandle;

        public LinearLayout dataLayoutHandle;

        public ArrayList<EditText> metricEditHolder = new ArrayList<>();

        public ArrayList<TextView> metricTextHolder = new ArrayList<>();

        public ArrayList<TextView> metricGoalText = new ArrayList<>();

        public ImageView exerciseCheckImage;
    }

    public static class GroupViewHolder {
        public TextView circuitNameText;

        public ImageView arrow;
    }
}
