package com.main.toledo.gymtrackr;


        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

        import android.content.Context;
        import android.graphics.Typeface;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.TextView;

public class WorkspaceExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    //private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    //private HashMap<String, List<String>> _listDataChild;

    private ArrayList<Circuit> workout;

    //public WorkspaceExpandableListAdapter(Context context, List<String> listDataHeader,
    //                             HashMap<String, List<String>> listChildData) {
    //    this._context = context;
    //    this._listDataHeader = listDataHeader;
    //    this._listDataChild = listChildData;
    //}

    public WorkspaceExpandableListAdapter(Context context, ArrayList<Circuit> workout){
        this._context = context;
        this.workout = workout;
    }

    @Override
    public Exercise getChild(int groupPosition, int childPosition) {
        //return this._listDataChild.get(this._listDataHeader.get(groupPosition))
        //        .get(childPosition);
        return workout.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //final String childText = (String) getChild(groupPosition, childPosition);
        final String exerciseName = (String) getChild(groupPosition, childPosition).getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.w_exercise, null);
        }
        //
        //TextView txtListChild = (TextView) convertView
        //        .findViewById(R.id.workspaceExerciseNameView);

        //txtListChild.setText(childText);

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.workspaceExerciseNameView);

        txtListChild.setText(exerciseName);

        return convertView;
    }

    //@Override
    //public int getChildrenCount(int groupPosition) {
    //   return this._listDataChild.get(this._listDataHeader.get(groupPosition))
    //            .size();
    //}

    @Override
    public int getChildrenCount(int groupPosition){
        return workout.get(groupPosition).getSize();
    }

    //@Override
    //public Object getGroup(int groupPosition) {
    //    return this._listDataHeader.get(groupPosition);
    //}

    @Override
    public Object getGroup(int groupPosition){
        return this.workout.get(groupPosition).getName();
    }

    //@Override
    //public int getGroupCount() {
    //    return this._listDataHeader.size();
    //}

    @Override
    public int getGroupCount() {
        return this.workout.size();
    }

    //@Override
    //public long getGroupId(int groupPosition) {
    //    return groupPosition;
    //}

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.w_circuit_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

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
}