package com.main.toledo.gymtrackr;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * Created by Adam on 2/25/2015.
 */
public class WorkspaceListFragment extends Fragment {
    final int NOT_BROWSE = 0, BROWSE_WORKOUT = 1, WORKOUT_BROWSE = 2;
    final int FROM_DETAIL = 6, FROM_WORKSPACE = 7;
    private Context mContext = getActivity();
        WorkspaceExpandableListView workspaceListView;
        boolean mDragInProgress;
        @Override
   public void onCreate(Bundle savedInstanceState) {
        //Log.d("PAD BUGS", "ONCREATE() CALLED IN WLFRAG");
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //sets the view for the fragment
        //Log.d("PAD BUGS", "ON CREATE VIEW CALLED IN WLFRAG");
        View v = inflater.inflate(R.layout.w_frag_list, null);

        workspaceListView = (WorkspaceExpandableListView)
                v.findViewById(R.id.workspaceListView);

        workspaceListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Log.d("PAD BUGS", "ON GROUP COLLAPSE CALLED");
                if (!mDragInProgress)
                    WorkoutData.get(getActivity()).getWorkout().get(groupPosition).setExpanded(false);
                //((WorkspaceActivity)getActivity()).getAdapter().hideKeypad();
            }
        });

        workspaceListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Log.d("PAD BUGS", "ON GROUP COLLAPSE CALLED");
                WorkoutData.get(getActivity()).getWorkout().get(groupPosition).setExpanded(true);
            }
        });

        workspaceListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("4/4", "Hide Keypad Called.");
                ((WorkspaceActivity)getActivity()).getAdapter().hideKeypad();
                return false;
            }
        });
        workspaceListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if(!WorkoutData.get(getActivity())
                        .getWorkout().get(groupPosition).isOpen()){
                    return true;
                } else {
                    return false;
                }
            }
        });

        workspaceListView.setDropListener(mDropListener);

        return v;
    }

    @Override
    public void onResume(){
        Log.d("PAD BUGS", "ONRESUME() CALLED IN WLFRAG");
        workspaceListView.setAdapter(((WorkspaceActivity)getActivity()).getAdapter());
        restoreListExpansion();
        //need a better way to do this
        workspaceListView.setGroupIndicator(null);
        //final Rect hitRect = new Rect();
        //workspaceListView.getHitRect(hitRect);
        /*
        workspaceListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("PAD BUGS", "HIT RECT CALLED: " + hitRect.flattenToString());
                ((WorkspaceActivity) getActivity()).getAdapter().cleanView(hitRect);
            }
        });

        */

        int browseMode = WorkoutData.get(mContext).getBrowseState();
        switch(browseMode){
            case NOT_BROWSE:
                break;
            case BROWSE_WORKOUT:
                WorkoutData.get(mContext).setBrowseState(NOT_BROWSE);
                int circuitVal = WorkoutData.get(mContext).getStateCircuit();

                focusItem(circuitVal, -1);

                //focus right thing
                break;
        }

        int detailMode = WorkoutData.get(mContext).getDetailTransition();
        switch(detailMode){
            case FROM_DETAIL:
                WorkoutData.get(mContext).setDetailTransition(FROM_WORKSPACE);
                int circuit = WorkoutData.get(mContext).getDetailCircuit();
                int exercise = WorkoutData.get(mContext).getDetailExercise();
                focusItem(circuit, exercise);

                break;
        }
        super.onResume();
    }

    private void focusItem(int circuitVal, int exercise){

        boolean circuitOpenStatus = WorkoutData.get(mContext).isStateCircuitOpen();
        int child;
        if(exercise == -1) {
            if (circuitOpenStatus)
                exercise = WorkoutData.get(mContext).getWorkout().get(circuitVal).getExercises().size() - 2;
            else
                exercise = 0;
        }
        workspaceListView.setSelectedChild(circuitVal, exercise, false);
        workspaceListView.post(new Runnable() {
            @Override
            public void run() {
                workspaceListView.smoothScrollBy(-300, 0);
            }
        });

    }

    public void restoreListExpansion(){
        int length = WorkoutData.get(getActivity()).getWorkout().size();
        for (int i = 0; i < length; i++){
            if(WorkoutData.get(getActivity()).getWorkout().get(i).isExpanded()){
                workspaceListView.expandGroup(i);
            } else {
                workspaceListView.collapseGroup(i);
            }
        }
    }

    public void collapseAllGroups(){
        int length = WorkoutData.get(getActivity()).getWorkout().size();
        for(int i = 0; i < length; i++){
            if (WorkoutData.get(getActivity()).getWorkout().get(i).isOpen())
                workspaceListView.collapseGroup(i);
        }
    }

    public void onItemDrop(){
        int length = WorkoutData.get(getActivity()).getWorkout().size();
        for(int i = 0; i < length; i++){
            if (WorkoutData.get(getActivity()).getWorkout().get(i).isExpanded()){
                workspaceListView.expandGroup(i);
            }
        }
    }

    public void setDragInProgress(boolean b){
        mDragInProgress = b;
    }

    private DropListener mDropListener =
            new DropListener() {
                public void onDrop(int type, int toX, int toY) {
                    ExpandableListAdapter adapter = workspaceListView.getExpandableListAdapter();
                    if (adapter instanceof WorkspaceExpandableListAdapterMKII) {
                        //Log.d("TOUCH TESTS", "ITEM DROPPED");
                        ((WorkspaceExpandableListAdapterMKII)adapter).onDrop(type, toX, toY);
                        //((WorkspaceActivity)getActivity()).getAdapter().notifyDataSetChanged();
                    }
                }
            };

}

