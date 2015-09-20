package com.main.toledo.gymtrackr;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * Created by Adam on 2/25/2015.
 */
public class WorkspaceListFragment extends Fragment {
    final int NOT_BROWSE = 0, BROWSE_WORKOUT = 1, WORKOUT_BROWSE = 2;
    final int FROM_DETAIL = 6, FROM_WORKSPACE = 7;

    private static final String logTag = "wrkspcLstFrg";
    private WorkspaceExpandableListAdapterMKIII mListAdapter;

    private Context mContext;
    private WorkspaceExpandableListView workspaceListView;
    @Override

    public void onCreate(Bundle savedInstanceState) {
        //Log.d("PAD BUGS", "ONCREATE() CALLED IN WLFRAG");
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //sets the view for the fragment
        //Log.d("PAD BUGS", "ON CREATE VIEW CALLED IN WLFRAG");
        View v = inflater.inflate(R.layout.w_frag_list, null);

        workspaceListView = (WorkspaceExpandableListView)
                v.findViewById(R.id.workspaceListView);
        /*
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
                Log.d("PAD BUGS", "ON GROUP EXPAND CALLED");
                WorkoutData.get(getActivity()).getWorkout().get(groupPosition).setExpanded(true);
            }
        });
        */
        workspaceListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mListAdapter.hideKeypad();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        /*
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
         */
        workspaceListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if(workspaceListView.isGroupExpanded(groupPosition)){
                    workspaceListView.collapseGroupWithAnimation(groupPosition);
                    WorkoutData.get(getActivity()).getWorkout().get(groupPosition).setExpanded(false);
                } else {
                    workspaceListView.expandGroupWithAnimation(groupPosition);
                    WorkoutData.get(getActivity()).getWorkout().get(groupPosition).setExpanded(true);
                }
                return true;
            }
        });

        //workspaceListView.setDropListener(mDropListener);

        return v;
    }

    @Override
    public void onResume(){
        Log.d(logTag, "listfrag resume");
        if(mListAdapter == null)
            mListAdapter = new WorkspaceExpandableListAdapterMKIII(mContext);
        workspaceListView.setAdapter(mListAdapter);
        mListAdapter.hideKeypad();
        workspaceListView.init();

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
}

