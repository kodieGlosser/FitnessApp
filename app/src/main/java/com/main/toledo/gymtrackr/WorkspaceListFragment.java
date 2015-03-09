package com.main.toledo.gymtrackr;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

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
        View v = inflater.inflate(R.layout.w_frag_list, null);
        workspaceListView = (DragNDropExpandableListView)
                v.findViewById(R.id.workspaceListView);

        workspaceListView.setAdapter(((WorkspaceActivity)getActivity()).getAdapter());
        expandLists(((WorkspaceActivity)getActivity()).getAdapter());

        workspaceListView.setDropListener(mDropListener);

        //((DragNDropExpandableListView) listView).setRemoveListener(mRemoveListener);

        workspaceListView.setDragListener(mDragListener);

        workspaceListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startEdit(groupPosition, childPosition);
                return true;
            }
        });
        return v;
    }

    private void startEdit(int group, int child){
        Intent i = new Intent(getActivity(), EditActivity.class);
        i.putExtra("CIRCUIT_VALUE", group);
        i.putExtra("EXERCISE_VALUE", child);
        startActivity(i);
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
                        //Log.d("TOUCH TESTS", "ITEM DROPPED");
                        ((WorkspaceExpandableListAdapterMKII)adapter).onDrop(fromX, fromY, toX, toY);
                        ((WorkspaceActivity)getActivity()).getAdapter().notifyDataSetChanged();
                    }
                }
            };

    //TO GREG - pretty sure the drag color bug originates from here
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

