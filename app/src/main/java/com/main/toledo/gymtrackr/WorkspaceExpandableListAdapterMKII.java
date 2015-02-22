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

public class WorkspaceExpandableListAdapterMKII extends BaseExpandableListAdapter {

    private Context _context;

    private ArrayList<Circuit> workout;

    public WorkspaceExpandableListAdapterMKII(Context context, ArrayList<Circuit> workout){
        this._context = context;
        this.workout = workout;
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        return workout.get(groupPosition).getExercise(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

//        Log.d("TEST", "getChildView called:");
//        Log.d("TEST", "group position: " + groupPosition + " group.size(): "
//               + workout.get(groupPosition).getExercises().size());

        if ( childPosition < (workout.get(groupPosition).getSize()-1) ) {
            //for the not last items in the list
            if (convertView == null || ( convertView.getTag() != "Data" ) ) {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_exercise, null);
                convertView.setTag("Data");
            }

            TextView textView = (TextView) convertView.findViewById(R.id.workspaceExerciseNameView);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(workout.get(groupPosition).getExercise(childPosition).getName());

        } else {
            //for the last items
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_end_buttons, null);
                convertView.setTag("Button");

            Button browseButton = (Button)convertView.findViewById(R.id.BrowseButton);
            browseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent i = new Intent(_context, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);

                    i.putExtra("EXTRA_CIRCUIT_NUMBER", groupPosition);
                    _context.startActivity(i);
                }
            });

            //ConvertViewIsButton = true;
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

//        Log.d("TEST", "getGroupView called:");
//        Log.d("TEST", "group position: " + groupPosition + " workout.size(): " + workout.size());

        if ( groupPosition < (workout.size()-1 ) ) {
            //for the not last items in the list
            if (convertView == null || convertView.getTag() != "Data") {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.w_circuit_group, null);
                convertView.setTag("Data");
            }

            TextView textView = (TextView) convertView.findViewById(R.id.lblListHeader);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(workout.get(groupPosition).getName());

        } else {
            //for the last items
            //Log.d("TEST", "Should now make buttons!");
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.w_end_buttons, null);
            convertView.setTag("Button");

            Button browseButton = (Button)convertView.findViewById(R.id.BrowseButton);
            browseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent i = new Intent(_context, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);
                    i.putExtra("EXTRA_CIRCUIT_NUMBER", groupPosition);
                    _context.startActivity(i);
                }
            });

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

    public static class ViewHolder {
        public TextView textView;
        public Button browseButton;
        public Button button2;
        public Button button3;
        public String browseButtonText;
    }

}