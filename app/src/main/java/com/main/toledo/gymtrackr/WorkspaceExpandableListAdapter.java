package com.main.toledo.gymtrackr;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

public class WorkspaceExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;

    private ArrayList<Circuit> workout;

    public WorkspaceExpandableListAdapter(Context context, ArrayList<Circuit> workout){
        this._context = context;
        this.workout = workout;
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        return workout.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    /**
     @Override
     public View getChildView(int groupPosition, final int childPosition,
     boolean isLastChild, View convertView, ViewGroup parent) {

     //Handles output of our terminal add thing
     if ( (groupPosition + 1) < workout.size()) {
     //final String childText = (String) getChild(groupPosition, childPosition);
     final String exerciseName = (String) getChild(groupPosition, childPosition).getName();
     if (convertView == null) {
     LayoutInflater inflater = (LayoutInflater) this._context
     .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     convertView = inflater.inflate(R.layout.w_exercise, null);
     }
     //
     //TextView txtListChild = (TextView) convertView
     //        .findViewById(R.id.workspaceExerciseNameView);

     //txtListChild.setText(childText);

     TextView txtListChild = (TextView) convertView
     .findViewById(R.id.workspaceExerciseNameView);

     txtListChild.setText(exerciseName);
     }
     return convertView;
     }
     **/
    //bigass test #2
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

//        Log.d("TEST", "Calling get group view on " + groupPosition);

        if (convertView == null) {
//            Log.d("TEST", "Calling inflate view on " + groupPosition);
            //Note: says convert view isn't used, but if I alter the method to return
            //void it breaks and starts barfing out null pointers
            convertView = workout.get(groupPosition).get(childPosition).initiateView(_context);
        }

        convertView = workout.get(groupPosition).get(childPosition).refreshView(_context, groupPosition);

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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
//        Log.d("TEST", "Calling get group view on " + groupPosition);
        if (convertView == null) {
//            Log.d("TEST", "Calling inflate view on " + groupPosition);
            //Note: says convert view isn't used, but if I alter the method to return
            //void it breaks and starts barfing out null pointers
            convertView = workout.get(groupPosition).initiateView(_context);
        }

        convertView = workout.get(groupPosition).refreshView(_context);
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

    public static class ViewHolder {
        public TextView textView;
        public Button browseButton;
        public Button button2;
        public Button button3;
        public String browseButtonText;
    }

}