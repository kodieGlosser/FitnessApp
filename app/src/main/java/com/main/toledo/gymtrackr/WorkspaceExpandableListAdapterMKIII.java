package com.main.toledo.gymtrackr;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Point;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WorkspaceExpandableListAdapterMKIII extends BaseExpandableListAdapter{

    private String logTag = "WRKSPACEADPTR";
    private Context mContext;

    private EditText mEditTextHandle;

    //private boolean editable = true;

    final int CIRCUIT = 1;
    final int EXERCISE = 2;
    final float mCheckedIndentation = 100;
    private ArrayList<Circuit> mWorkout; //6/23 UNEEDED? = new ArrayList<>();
    private LinearLayout.LayoutParams params;

    /*
     *  STABLE ID THINGS
     */
    final long INVALID_ID = -1;
    public static Long AVAILABLE_STABLE_ID;
    //KEEP AN EYE ON THESE, ENCAPSULATED PRIMITIVE
    private HashMap<Circuit, Long> mGroupIdMap = new HashMap<Circuit, Long>();
    private HashMap<Exercise, Long> mChildIdMap = new HashMap<Exercise, Long>();



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
    private int SCREENWIDTH;

    public WorkspaceExpandableListAdapterMKIII(Context context){
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        this.mContext = context;

        //Set initial stable id's ADD IDS TO SOME MAP
        AVAILABLE_STABLE_ID = 0L;
        mWorkout = WorkoutData.get(mContext).getWorkout();
        for(Circuit circuit : mWorkout){
            mGroupIdMap.put(circuit, AVAILABLE_STABLE_ID);
            AVAILABLE_STABLE_ID++;
            for(Exercise exercise : circuit.getExercises()){
                mChildIdMap.put(exercise, AVAILABLE_STABLE_ID);
                AVAILABLE_STABLE_ID++;
            }
        }


        //Used for display things THERE IS PROBABLY A BETTER WAY
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREENWIDTH = size.x;
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        return mWorkout.get(groupPosition).getExercise(childPosition);
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
        Exercise e = mWorkout.get(group).getExercise(child);
        if(e.getName().equals("test")){
            if(mWorkout.size()-1 == group){
                //group is last, and test means end buttons

                //if(editable)
                    type = WORKOUT_BUTTONS;
                /*
                else
                    type = EMPTY_BUTTONS;
                    */
            } else {
                //if not last group and test name, type is rego buttons
                //if(editable)
                    type = CIRCUIT_BUTTONS;
                /*
                else
                    type = EMPTY_BUTTONS;
                    */
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
        Circuit c = mWorkout.get(group);
        if(c.isOpen()){
            type = REGULAR_HEADER;
        } else {
            if(group != mWorkout.size()-1) {
                type = PADDED_BLANK_HEADER;
            }else{
                type = BLANK_HEADER;
            }
        }
        return type;
    }
    /*
    public void setEditable(boolean b){
        //Log.d("EDITABLE TEST", "setEditable() called in adapter.  editable: " + b);
        editable = b;
        notifyDataSetChanged();
    }
    */

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout dynamicViewLayout;
        final int group = groupPosition;
        final int child = childPosition;
        Exercise e = mWorkout.get(group).getExercise(child);
        boolean emptyFlag = false;
        ChildViewHolder holder = null;
        int type = getChildType(groupPosition, childPosition);


        if(convertView == null) {
            holder = new ChildViewHolder();
            switch (type) {
                case CIRCUIT_BUTTONS:
                    convertView = inflater.inflate(R.layout.w_circuit_menu_buttons, null);

                    //holder.plusButton = (ImageButton) convertView.findViewById(R.id.PlusButton);
                    //holder.browseButton = (ImageButton) convertView.findViewById(R.id.BrowseButton);
                    break;
                case WORKOUT_BUTTONS:
                    convertView = inflater.inflate(R.layout.w_workout_menu_buttons, null);
                    //add circuit code
                    //holder.browseButton = (ImageButton) convertView.findViewById(R.id.BrowseButton);
                    //holder.addCircuitButton = (ImageButton) convertView.findViewById(R.id.AddCircuitButton);
                    //holder.plusButton = (ImageButton) convertView.findViewById(R.id.PlusButton);
                    break;
                case EMPTY_BUTTONS:
                    convertView = inflater.inflate(R.layout.w_empty_wopadding, null);
                    break;
                case EXERCISE_ITEM_1:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_1_M:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);
                    break;
                case EXERCISE_ITEM_2:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_2_M:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_3:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_3_M:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    break;
                case EXERCISE_ITEM_1C:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_1_MC:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_2C:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_2_MC:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_3C:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

                    holder.dataLayoutHandle = (LinearLayout) convertView.findViewById(R.id.exercise_data_layout);

                    doCheck(holder);
                    break;
                case EXERCISE_ITEM_3_MC:
                    convertView = inflater.inflate(R.layout.w_exercise, null);
                    dynamicViewLayout = (LinearLayout) convertView.findViewById(R.id.dynamicViewLayout);
                    dynamicViewLayout.addView(createGoalLayout(group, child, holder));
                    dynamicViewLayout.addView(createMetricLayout(group, child, holder));

                    holder.exerciseNameText = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);

                    holder.layoutHandle = (LinearLayout) convertView.findViewById(R.id.LAYOUT_HANDLE);

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
        final int height = convertView.getMeasuredHeight();
        switch (type) {
            case CIRCUIT_BUTTONS:
                emptyFlag = true;

                /*
                holder.browseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, BrowseActivity.class);
                        WorkoutData.get(mContext).setStateCircuitOpenStatus(true);
                        WorkoutData.get(mContext).setStateCircuit(group);
                        WorkoutData.get(mContext).setBrowseState(WORKOUT_BROWSE);

                        ((WorkspaceActivity) mContext).ListFragment.workspaceListView.clearHandle();
                        mContext.startActivity(i);
                    }
                });


                holder.plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WorkoutData.get(mContext).isAnExerciseToggled()){
                            int CircuitSize = mWorkout.get(group).getSize();
                            Exercise e = WorkoutData.get(mContext).getToggledExerciseCopy();
                            e.setAnimate(true);
                            mWorkout.get(group).addExerciseAtIndex(CircuitSize - 1, e);
                            notifyDataSetChanged();
                            ((WorkspaceActivity) mContext).ListFragment                                                     ///////////////////////////THIS NEEDS TO SCALE FOR RESOLUTIONS
                                    .workspaceListView.smoothScrollBy(height, 800);
                        }
                    }
                });
                */

                break;
            case WORKOUT_BUTTONS:
                emptyFlag = true;
                /*
                holder.addCircuitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkoutData.get(mContext).addCircuit(group);
                        ((WorkspaceActivity) mContext).ListFragment.workspaceListView.expandGroup(group);

                        notifyDataSetChanged();

                        ((WorkspaceActivity) mContext).ListFragment                                                     ///////////////////////////THIS NEEDS TO SCALE FOR RESOLUTIONS
                                .workspaceListView.smoothScrollBy(height*2, 800);
                    }
                });


                holder.browseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, BrowseActivity.class);
                        WorkoutData.get(mContext).setStateCircuitOpenStatus(false);
                        WorkoutData.get(mContext).setStateCircuit(group);
                        WorkoutData.get(mContext).setBrowseState(WORKOUT_BROWSE);

                        ((WorkspaceActivity) mContext).ListFragment.workspaceListView.clearHandle();
                        mContext.startActivity(i);
                    }
                });


                holder.plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WorkoutData.get(mContext).isAnExerciseToggled()){
                            int WorkoutSize = mWorkout.size();
                            Exercise e = WorkoutData.get(mContext).getToggledExerciseCopy();
                            e.setAnimate(true);
                            WorkoutData.get(mContext).addClosedCircuit(e, WorkoutSize -1);
                            notifyDataSetChanged();
                            ((WorkspaceActivity) mContext).ListFragment                               ///////////////////////////THIS NEEDS TO SCALE FOR RESOLUTIONS
                                    .workspaceListView.smoothScrollBy(height, 800);
                        }
                    }
                });
                */

                break;
            case EMPTY_BUTTONS:
                emptyFlag = true;
                break;
            case EXERCISE_ITEM_1:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_1_M:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2_M:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3_M:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_1C:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_1_MC:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2C:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_2_MC:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3C:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case EXERCISE_ITEM_3_MC:
                holder.exerciseNameText.setText(mWorkout.get(group).getExercise(childPosition).getName());
                setValues(group, child, holder);
                break;
            case TERRIBLE_THINGS:
                break;
        }


        if(mWorkout.get(group).getExercise(child).isToggled()){
            convertView.findViewById(R.id.LAYOUT_HANDLE).setBackgroundResource(R.drawable.circuit_selected);
            //convertView.findViewById(R.id.exercise_relative_layout_handle).setPressed(true);
        } else if(!emptyFlag){
            if(convertView.findViewById(R.id.LAYOUT_HANDLE) != null)
                convertView.findViewById(R.id.LAYOUT_HANDLE).setBackgroundResource(R.drawable.circuit_notselected);
            //convertView.findViewById(R.id.exerciseMainLayout).setBackgroundColor(Color.BLUE);

        } else {

        }

        if(mWorkout.get(group).getExercise(child).doAnimation()){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_right);
            animation.setDuration(500);
            convertView.startAnimation(animation);
            mWorkout.get(group).getExercise(child).setAnimate(false);
        }
        if (convertView.getTranslationX() != 0)
            convertView.setTranslationX(0);

        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition){
        return mWorkout.get(groupPosition).getSize();
    }


    @Override
    public Object getGroup(int groupPosition){
        return mWorkout.get(groupPosition).getName();
    }

    @Override
    public int getGroupCount() {
        return mWorkout.size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //Log.d("TEST", "DOING STUFF FOR GROUP: " + groupPosition);
        LayoutInflater inflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GroupViewHolder holder = null;
        int type = getGroupType(groupPosition);
        if(convertView == null) {
            holder = new GroupViewHolder();
            switch (type) {
                case REGULAR_HEADER:
                    convertView = inflater.inflate(R.layout.w_group_header, null);
                    holder.circuitNameText = (TextView) convertView.findViewById(R.id.circuitNameHeader);
                    holder.arrow = (ImageView) convertView.findViewById(R.id.Arrow);
                    holder.footerView = (View)convertView.findViewById(R.id.circuitFooter);
                    break;
                case BLANK_HEADER:
                    convertView = inflater.inflate(R.layout.w_empty_wopadding, null);

                    break;
                case PADDED_BLANK_HEADER:
                    convertView = inflater.inflate(R.layout.w_empty_wpadding, null);

                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder)convertView.getTag();
        }
        switch (type) {
            case REGULAR_HEADER:
                holder.circuitNameText.setText(mWorkout.get(groupPosition).getName());
                int imageResourceId = isExpanded ? R.drawable.ic_collapse_arrow_50
                        : R.drawable.ic_expand_arrow_50;
                //todo Add code to hide footer view
                holder.arrow.setImageResource(imageResourceId);

                if (isExpanded)
                    holder.footerView.setVisibility(View.GONE);
                else
                    holder.footerView.setVisibility(View.VISIBLE);


                break;
            case BLANK_HEADER:
                //((WorkspaceActivity) mContext).ListFragment.workspaceListView.expandGroup(groupPosition);
                //if(!editable)
                    convertView.setPadding(0,0,0,SCREENWIDTH/2);
                /*
                else
                    convertView.setPadding(0,0,0,0);
                    */
                break;
            case PADDED_BLANK_HEADER:
                //((WorkspaceActivity) mContext).ListFragment.workspaceListView.expandGroup(groupPosition);
                break;
        }

        if (convertView.getTranslationX() != 0)
            convertView.setTranslationX(0);

        convertView.setPressed(false);

        return convertView;
    }


    /*
     *
     *
     * Stable Id stuff
     *
     *
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    //will return reference to map
    @Override
    public long getGroupId(int groupPosition){

        if(groupPosition < 0 || groupPosition >= mGroupIdMap.size()){
            Log.d(logTag, "*****************************ATTENTION*******************************");
            Log.d(logTag, "getGroupId() returned INVALID_ID for groupPosition: " + groupPosition
                    + "GROUPPOSITION OUT OF HASH MAP BOUNDS");
            Log.d(logTag, "*********************************************************************");
            return INVALID_ID;
        }

        Circuit circuit = mWorkout.get(groupPosition);

        if(!mGroupIdMap.containsKey(circuit)) {
            Log.d(logTag, "*****************************ATTENTION*******************************");
            Log.d(logTag, "getGroupId() returned INVALID_ID for groupPosition: "
                    + groupPosition + "NO ID FOR KEY CONTAINED IN GROUPHASHMAP");
            Log.d(logTag, "*********************************************************************");
            return INVALID_ID;
        }

        return mGroupIdMap.get(circuit);

    }



    @Override
    public long getChildId(int groupPosition, int childPosition){
        if(groupPosition < 0 || groupPosition >= mGroupIdMap.size()){
            Log.d(logTag, "*****************************ATTENTION*******************************");
            Log.d(logTag, "getChildId() returned INVALID_ID for groupPosition: "
                    + groupPosition + "GROUP OUT OF BOUNDS");
            Log.d(logTag, "*********************************************************************");
            return INVALID_ID;
        }
        
        Circuit circuit = mWorkout.get(groupPosition);

        if(childPosition < 0 || childPosition >= circuit.getExercises().size()){
            Log.d(logTag, "*****************************ATTENTION*******************************");
            Log.d(logTag, "getChildId() returned INVALID_ID for groupPosition: "
                    + groupPosition + " childPosition: " + childPosition + "CHILD OUT OF GROUP BOUNDS");
            Log.d(logTag, "*********************************************************************");
            return INVALID_ID;
        }

        Exercise exercise = circuit.getExercise(childPosition);

        if(!mChildIdMap.containsKey(exercise)) {
            Log.d(logTag, "*****************************ATTENTION*******************************");
            Log.d(logTag, "getChildId() returned INVALID_ID for groupPosition: "
                    + groupPosition + " childPosition: " + childPosition + "NO ID FOR KEY CONTAINED IN HASHMAP");
            Log.d(logTag, "*********************************************************************");
            return INVALID_ID;
        }
        return mChildIdMap.get(exercise);
    }

    public void addChild(int groupPosition, int childPosition, Exercise exercise){
        //adds child at specified location
        mWorkout.get(groupPosition).add(childPosition, exercise);
        mChildIdMap.put(exercise, AVAILABLE_STABLE_ID);
        AVAILABLE_STABLE_ID++;
    }

    public void removeChild(int groupPosition, int childPosition){
        mChildIdMap.remove(
                mWorkout.get(groupPosition).getExercise(childPosition)
        );
        mWorkout.get(groupPosition).removeExercise(childPosition);
    }

    public void addGroup(int groupPostion, Circuit circuit){
        mWorkout.add(groupPostion, circuit);
        mGroupIdMap.put(circuit, AVAILABLE_STABLE_ID);
        AVAILABLE_STABLE_ID++;
    }


    public void removeGroup(int groupPosition){
        mGroupIdMap.remove(
            mWorkout.get(groupPosition)
        );
        mWorkout.remove(groupPosition);
    }
    /*
     * These MAY be needed
     *
    @Override
    public long getCombinedChildId(long groupId, long childId){

    }

    @Override
    public long getCombinedGroupId(long groupId){

    }
    *
    */

    /*
     *END OF STABLE ID STUFF
     *
     *
     *
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void hideKeypad(){
        if (mEditTextHandle != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditTextHandle.getWindowToken(), 0);
            mEditTextHandle.clearFocus();
        }
    }

    private View createGoalLayout(int group, int child, ChildViewHolder holder) {
        final LinearLayout goalLayout = new LinearLayout(mContext);
        ArrayList<Metric> plan_metrics = mWorkout.get(group).getExercise(child).getPlanMetrics();
        goalLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < plan_metrics.size(); i++) {
            TextView metricGoalTextView = new TextView(mContext);
            metricGoalTextView.setLayoutParams(params);
            goalLayout.addView(metricGoalTextView);
            holder.metricGoalText.add(metricGoalTextView);
        }
        return goalLayout;
    }

    private View createMetricLayout(int group, int child, ChildViewHolder holder){
        final LinearLayout metricLayout = new LinearLayout(mContext);
        ArrayList<Metric> metrics = mWorkout.get(group).getExercise(child).getMetrics();
        metricLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < metrics.size(); i++){
            TextView metricText = new TextView(mContext);

            EditText metricEdit = new EditText(mContext);
            metricEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
            metricEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            metricEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

            LinearLayout metricRow = new LinearLayout(mContext);
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
        Exercise e = mWorkout.get(group).getExercise(child);
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
                        } //else {
                            //TODO: CODE TO DIRECT FOCUS TO NEXT WORKSPACE ROW GOES HERE
                            //v.focusSearch(View.FOCUS_DOWN);
                        //}
                        return true;
                    }
                    return false;
                }
            });

            metricEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        //((WorkspaceActivity) mContext).ListFragment.workspaceListView.toggleListeners(false);
                        metricEditText.setSelection(metricEditText.getText().toString().length());
                        mEditTextHandle = (EditText) v;
                        if (metricEditText.getText().toString().equals("0")) {
                            //timeEdit.setText("");
                        }
                        //Log.d("WORKSPACELISTFOCUS", "PLAN FOCUSED" + timeEdit.getText());
                    } else {
                        //((WorkspaceActivity) mContext).ListFragment.workspaceListView.toggleListeners(editable);
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
        LayoutInflater inflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        holder.exerciseCheckImage = (ImageView) inflater.inflate(R.layout.w_check, null);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        holder.exerciseCheckImage.setLayoutParams(params);

        holder.layoutHandle.addView(holder.exerciseCheckImage);
        holder.dataLayoutHandle.setTranslationX(mCheckedIndentation);
    }

    public static class ChildViewHolder {

        public TextView exerciseNameText;

        public LinearLayout layoutHandle;

        public LinearLayout dataLayoutHandle;

        public ArrayList<EditText> metricEditHolder = new ArrayList<>();

        public ArrayList<TextView> metricTextHolder = new ArrayList<>();

        public ArrayList<TextView> metricGoalText = new ArrayList<>();

        public ImageView exerciseCheckImage;
    }

    public static class GroupViewHolder {
        public TextView circuitNameText;

        public ImageView arrow;

        public View footerView;
    }

}
