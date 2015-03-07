package com.main.toledo.gymtrackr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/25/2015.
 */
public class WorkspaceListFragment extends Fragment {

        //ArrayList<Circuit> workout = new ArrayList<Circuit>();
        //ArrayList<Circuit> singletonWorkout = new ArrayList<Circuit>();
        DragNDropExpandableListView workspaceListView;
        //WorkspaceExpandableListAdapterMKII listAdapter;

        @Override
   public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //sets the view for the fragment
        View v = inflater.inflate(R.layout.w_activity_main, null);
        workspaceListView = (DragNDropExpandableListView)
                v.findViewById(R.id.lvExp);

        workspaceListView.setAdapter(((WorkspaceActivity)getActivity()).getAdapter());
        expandLists(((WorkspaceActivity)getActivity()).getAdapter());

        workspaceListView.setDropListener(mDropListener);

        //((DragNDropExpandableListView) listView).setRemoveListener(mRemoveListener);

        workspaceListView.setDragListener(mDragListener);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        //if (listAdapter != null) {
        //    listAdapter.notifyDataSetChanged();
        //}
    }

    public void expandLists(WorkspaceExpandableListAdapterMKII listAdapter){
        int count = listAdapter.getGroupCount();
        for (int position = 1; position <= count; position++){
            workspaceListView.expandGroup(position - 1);
        }
    }

    public void collapseLists(WorkspaceExpandableListAdapterMKII listAdapter){
        int count = listAdapter.getGroupCount();
        for (int position = 1; position < count; position++){
            workspaceListView.collapseGroup(position - 1);
        }
    }

    private DropListener mDropListener =
            new DropListener() {
                public void onDrop(int fromX, int fromY, int toX, int toY) {
                    ExpandableListAdapter adapter = workspaceListView.getExpandableListAdapter();
                    if (adapter instanceof WorkspaceExpandableListAdapterMKII) {
                        Log.d("TOUCH TESTS", "ITEM DROPPED");
                        ((WorkspaceExpandableListAdapterMKII)adapter).onDrop(fromX, fromY, toX, toY);
                        ((WorkspaceActivity)getActivity()).getAdapter().notifyDataSetChanged();
                    }
                }
            };

    private DragListener mDragListener =
            new DragListener() {

                int backgroundColor = 0xe0103010;
                int defaultBackgroundColor;

                public void onDrag(int x, int y, ExpandableListView listView) {
                    //Log.d("TOUCH TESTS", "ON DRAG CALLED");
                    // TODO Auto-generated method stub
                }

                public void onStartDrag(View itemView) {
                    //Log.d("TOUCH TESTS", "ON START DRAG CALLED");
                    itemView.setVisibility(View.INVISIBLE);
                    defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
                    itemView.setBackgroundColor(backgroundColor);
                    //test
                    //ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
                    //if (iv != null) iv.setVisibility(View.INVISIBLE);
                }

                public void onStopDrag(View itemView) {
                    //Log.d("TOUCH TESTS", "ON STOP DRAG CALLED");
                    itemView.setVisibility(View.VISIBLE);
                    itemView.setBackgroundColor(defaultBackgroundColor);
                    //test
                    //ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
                    //if (iv != null) iv.setVisibility(View.VISIBLE);
                }

            };


}

